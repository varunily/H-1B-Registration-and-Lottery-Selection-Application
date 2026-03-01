package com.h1b.lottery.service;

import com.h1b.lottery.domain.model.Beneficiary;
import com.h1b.lottery.domain.model.Employer;
import com.h1b.lottery.domain.model.LotteryRun;
import com.h1b.lottery.domain.model.Registration;
import com.h1b.lottery.web.dto.BeneficiaryResponse;
import com.h1b.lottery.web.dto.EmployerResponse;
import com.h1b.lottery.web.dto.LotteryRunResponse;
import com.h1b.lottery.web.dto.RegistrationResponse;

public final class DtoMapper {

    private DtoMapper() {
    }

    public static EmployerResponse toEmployerResponse(Employer employer) {
        return new EmployerResponse(
                employer.getId(),
                employer.getLegalName(),
                employer.getFein(),
                employer.getContactEmail(),
                employer.getCreatedAt()
        );
    }

    public static BeneficiaryResponse toBeneficiaryResponse(Beneficiary beneficiary) {
        return new BeneficiaryResponse(
                beneficiary.getId(),
                beneficiary.getEmployer().getId(),
                beneficiary.getFirstName(),
                beneficiary.getLastName(),
                beneficiary.getEmail(),
                beneficiary.getCountryOfCitizenship(),
                beneficiary.getHighestEducation(),
                beneficiary.isMastersCapEligible(),
                beneficiary.getCreatedAt()
        );
    }

    public static RegistrationResponse toRegistrationResponse(Registration registration) {
        String beneficiaryName = registration.getBeneficiary().getFirstName() + " " + registration.getBeneficiary().getLastName();
        return new RegistrationResponse(
                registration.getId(),
                registration.getEmployer().getId(),
                registration.getBeneficiary().getId(),
                beneficiaryName,
                registration.getFiscalYear(),
                registration.getOfferedSalary(),
                registration.getWorkLocation(),
                registration.getBeneficiary().isMastersCapEligible(),
                registration.getStatus(),
                registration.getSubmittedAt(),
                registration.getSelectedAt(),
                registration.getCreatedAt(),
                registration.getUpdatedAt()
        );
    }

    public static LotteryRunResponse toLotteryRunResponse(LotteryRun run) {
        return new LotteryRunResponse(
                run.getId(),
                run.getFiscalYear(),
                run.getRegularCap(),
                run.getMastersCap(),
                run.getSeed(),
                run.getTotalSubmitted(),
                run.getSelectedRegular(),
                run.getSelectedMasters(),
                run.getStatus(),
                run.getCreatedAt(),
                run.getCompletedAt()
        );
    }
}
