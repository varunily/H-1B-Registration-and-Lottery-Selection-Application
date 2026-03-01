package com.h1b.lottery.service;

import com.h1b.lottery.domain.model.Beneficiary;
import com.h1b.lottery.domain.model.Employer;
import com.h1b.lottery.domain.model.Registration;
import com.h1b.lottery.domain.model.enums.RegistrationStatus;
import com.h1b.lottery.exception.BadRequestException;
import com.h1b.lottery.exception.NotFoundException;
import com.h1b.lottery.repository.RegistrationRepository;
import com.h1b.lottery.web.dto.RegistrationCreateRequest;
import com.h1b.lottery.web.dto.RegistrationResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;

@Service
public class RegistrationService {

    private final EmployerService employerService;
    private final BeneficiaryService beneficiaryService;
    private final RegistrationRepository registrationRepository;

    public RegistrationService(EmployerService employerService,
                               BeneficiaryService beneficiaryService,
                               RegistrationRepository registrationRepository) {
        this.employerService = employerService;
        this.beneficiaryService = beneficiaryService;
        this.registrationRepository = registrationRepository;
    }

    @Transactional
    public RegistrationResponse create(Long employerId, RegistrationCreateRequest request) {
        Employer employer = employerService.getEntity(employerId);
        Beneficiary beneficiary = beneficiaryService.getEntity(request.beneficiaryId());

        if (!beneficiary.getEmployer().getId().equals(employerId)) {
            throw new BadRequestException("Beneficiary does not belong to employer " + employerId);
        }

        registrationRepository.findByEmployerIdAndBeneficiaryIdAndFiscalYear(
                employerId,
                request.beneficiaryId(),
                request.fiscalYear()
        ).ifPresent(existing -> {
            throw new BadRequestException("Registration already exists for this beneficiary and fiscal year");
        });

        Registration registration = new Registration();
        registration.setEmployer(employer);
        registration.setBeneficiary(beneficiary);
        registration.setFiscalYear(request.fiscalYear());
        registration.setOfferedSalary(request.offeredSalary());
        registration.setWorkLocation(request.workLocation().trim());

        RegistrationStatus requestedStatus = request.status();
        RegistrationStatus finalStatus = requestedStatus == null ? RegistrationStatus.DRAFT : requestedStatus;
        registration.setStatus(finalStatus);

        if (finalStatus == RegistrationStatus.SUBMITTED) {
            registration.setSubmittedAt(OffsetDateTime.now());
        }

        Registration saved = registrationRepository.save(registration);
        return DtoMapper.toRegistrationResponse(saved);
    }

    @Transactional
    public RegistrationResponse submit(Long registrationId) {
        Registration registration = registrationRepository.findById(registrationId)
                .orElseThrow(() -> new NotFoundException("Registration not found: " + registrationId));

        if (registration.getStatus() != RegistrationStatus.DRAFT) {
            throw new BadRequestException("Only draft registrations can be submitted");
        }

        registration.setStatus(RegistrationStatus.SUBMITTED);
        registration.setSubmittedAt(OffsetDateTime.now());

        return DtoMapper.toRegistrationResponse(registration);
    }

    @Transactional
    public RegistrationResponse withdraw(Long registrationId) {
        Registration registration = registrationRepository.findById(registrationId)
                .orElseThrow(() -> new NotFoundException("Registration not found: " + registrationId));

        if (registration.getStatus() == RegistrationStatus.SELECTED) {
            throw new BadRequestException("Selected registrations cannot be withdrawn from the system");
        }

        registration.setStatus(RegistrationStatus.WITHDRAWN);
        return DtoMapper.toRegistrationResponse(registration);
    }

    @Transactional(readOnly = true)
    public List<RegistrationResponse> listByEmployer(Long employerId) {
        employerService.getEntity(employerId);
        return registrationRepository.findByEmployerIdOrderByCreatedAtDesc(employerId)
                .stream()
                .map(DtoMapper::toRegistrationResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<RegistrationResponse> listByFiscalYear(Integer fiscalYear) {
        return registrationRepository.findByFiscalYearOrderByCreatedAtDesc(fiscalYear)
                .stream()
                .map(DtoMapper::toRegistrationResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<Registration> submittedForYear(Integer fiscalYear) {
        return registrationRepository.findByFiscalYearAndStatus(fiscalYear, RegistrationStatus.SUBMITTED);
    }

    @Transactional
    public void saveAll(List<Registration> registrations) {
        registrationRepository.saveAll(registrations);
    }
}
