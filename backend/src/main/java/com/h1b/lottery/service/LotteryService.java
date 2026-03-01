package com.h1b.lottery.service;

import com.h1b.lottery.domain.model.LotteryRun;
import com.h1b.lottery.domain.model.Registration;
import com.h1b.lottery.domain.model.enums.LotteryRunStatus;
import com.h1b.lottery.domain.model.enums.RegistrationStatus;
import com.h1b.lottery.repository.LotteryRunRepository;
import com.h1b.lottery.web.dto.LotteryRunRequest;
import com.h1b.lottery.web.dto.LotteryRunResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class LotteryService {

    private final RegistrationService registrationService;
    private final LotteryRunRepository lotteryRunRepository;
    private final LotterySelectionEngine selectionEngine;

    public LotteryService(RegistrationService registrationService, LotteryRunRepository lotteryRunRepository) {
        this.registrationService = registrationService;
        this.lotteryRunRepository = lotteryRunRepository;
        this.selectionEngine = new LotterySelectionEngine();
    }

    @Transactional
    public LotteryRunResponse runLottery(LotteryRunRequest request) {
        long seed = request.seed() == null
                ? ThreadLocalRandom.current().nextLong(1L, Long.MAX_VALUE)
                : request.seed();

        LotteryRun run = new LotteryRun();
        run.setFiscalYear(request.fiscalYear());
        run.setRegularCap(request.regularCap());
        run.setMastersCap(request.mastersCap());
        run.setSeed(seed);
        run.setStatus(LotteryRunStatus.RUNNING);
        run.setTotalSubmitted(0);
        run.setSelectedMasters(0);
        run.setSelectedRegular(0);
        lotteryRunRepository.save(run);

        try {
            List<Registration> submitted = registrationService.submittedForYear(request.fiscalYear());
            Random random = new Random(seed);

            LotterySelectionEngine.SelectionResult selectionResult = selectionEngine.run(
                    submitted,
                    request.mastersCap(),
                    request.regularCap(),
                    random
            );

            Set<Long> selectedIds = selectionResult.allSelectedIds();

            OffsetDateTime now = OffsetDateTime.now();
            for (Registration registration : submitted) {
                if (selectedIds.contains(registration.getId())) {
                    registration.setStatus(RegistrationStatus.SELECTED);
                    registration.setSelectedAt(now);
                } else {
                    registration.setStatus(RegistrationStatus.NOT_SELECTED);
                    registration.setSelectedAt(null);
                }
            }
            registrationService.saveAll(submitted);

            run.setTotalSubmitted(submitted.size());
            run.setSelectedMasters(selectionResult.mastersSelected().size());
            run.setSelectedRegular(selectionResult.regularSelected().size());
            run.setStatus(LotteryRunStatus.COMPLETED);
            run.setCompletedAt(now);
            lotteryRunRepository.save(run);

            return DtoMapper.toLotteryRunResponse(run);
        } catch (Exception ex) {
            run.setStatus(LotteryRunStatus.FAILED);
            run.setCompletedAt(OffsetDateTime.now());
            lotteryRunRepository.save(run);
            throw ex;
        }
    }

    @Transactional(readOnly = true)
    public List<LotteryRunResponse> listRuns(Integer fiscalYear) {
        return lotteryRunRepository.findByFiscalYearOrderByCreatedAtDesc(fiscalYear)
                .stream()
                .map(DtoMapper::toLotteryRunResponse)
                .toList();
    }
}
