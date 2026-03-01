package com.h1b.lottery.service;

import com.h1b.lottery.domain.model.Registration;
import com.h1b.lottery.domain.model.enums.RegistrationStatus;
import com.h1b.lottery.repository.BeneficiaryRepository;
import com.h1b.lottery.repository.EmployerRepository;
import com.h1b.lottery.repository.LotteryRunRepository;
import com.h1b.lottery.repository.RegistrationRepository;
import com.h1b.lottery.web.dto.DashboardMetricsResponse;
import com.h1b.lottery.web.dto.EmployerAnalyticsResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AnalyticsService {

    private final EmployerRepository employerRepository;
    private final BeneficiaryRepository beneficiaryRepository;
    private final RegistrationRepository registrationRepository;
    private final LotteryRunRepository lotteryRunRepository;

    public AnalyticsService(EmployerRepository employerRepository,
                            BeneficiaryRepository beneficiaryRepository,
                            RegistrationRepository registrationRepository,
                            LotteryRunRepository lotteryRunRepository) {
        this.employerRepository = employerRepository;
        this.beneficiaryRepository = beneficiaryRepository;
        this.registrationRepository = registrationRepository;
        this.lotteryRunRepository = lotteryRunRepository;
    }

    @Transactional(readOnly = true)
    public DashboardMetricsResponse dashboard(Integer fiscalYear) {
        return new DashboardMetricsResponse(
                fiscalYear,
                employerRepository.count(),
                beneficiaryRepository.count(),
                registrationRepository.countByFiscalYearAndStatus(fiscalYear, RegistrationStatus.DRAFT),
                registrationRepository.countByFiscalYearAndStatus(fiscalYear, RegistrationStatus.SUBMITTED),
                registrationRepository.countByFiscalYearAndStatus(fiscalYear, RegistrationStatus.SELECTED),
                registrationRepository.countByFiscalYearAndStatus(fiscalYear, RegistrationStatus.NOT_SELECTED),
                lotteryRunRepository.countByFiscalYear(fiscalYear)
        );
    }

    @Transactional(readOnly = true)
    public List<EmployerAnalyticsResponse> employerAnalytics(Integer fiscalYear) {
        List<Registration> registrations = registrationRepository.findByFiscalYear(fiscalYear);

        Map<Long, Stats> byEmployer = new HashMap<>();
        for (Registration registration : registrations) {
            Long employerId = registration.getEmployer().getId();
            Stats stats = byEmployer.computeIfAbsent(employerId,
                    ignored -> new Stats(registration.getEmployer().getLegalName()));
            stats.total++;
            if (registration.getStatus() == RegistrationStatus.SELECTED) {
                stats.selected++;
            }
        }

        return byEmployer.entrySet().stream()
                .map(entry -> {
                    Long employerId = entry.getKey();
                    Stats stats = entry.getValue();
                    double rate = stats.total == 0 ? 0.0 : ((double) stats.selected / stats.total) * 100.0;
                    return new EmployerAnalyticsResponse(
                            employerId,
                            stats.employerName,
                            fiscalYear,
                            stats.total,
                            stats.selected,
                            Math.round(rate * 100.0) / 100.0
                    );
                })
                .sorted(Comparator.comparing(EmployerAnalyticsResponse::selectionRate).reversed())
                .toList();
    }

    private static final class Stats {
        private final String employerName;
        private long total;
        private long selected;

        private Stats(String employerName) {
            this.employerName = employerName;
        }
    }
}
