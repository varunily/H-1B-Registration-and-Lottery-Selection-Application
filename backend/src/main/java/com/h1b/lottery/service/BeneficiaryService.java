package com.h1b.lottery.service;

import com.h1b.lottery.domain.model.Beneficiary;
import com.h1b.lottery.domain.model.Employer;
import com.h1b.lottery.repository.BeneficiaryRepository;
import com.h1b.lottery.web.dto.BeneficiaryCreateRequest;
import com.h1b.lottery.web.dto.BeneficiaryResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class BeneficiaryService {

    private final EmployerService employerService;
    private final BeneficiaryRepository beneficiaryRepository;

    public BeneficiaryService(EmployerService employerService, BeneficiaryRepository beneficiaryRepository) {
        this.employerService = employerService;
        this.beneficiaryRepository = beneficiaryRepository;
    }

    @Transactional
    public BeneficiaryResponse create(Long employerId, BeneficiaryCreateRequest request) {
        Employer employer = employerService.getEntity(employerId);

        Beneficiary beneficiary = new Beneficiary();
        beneficiary.setEmployer(employer);
        beneficiary.setFirstName(request.firstName().trim());
        beneficiary.setLastName(request.lastName().trim());
        beneficiary.setEmail(request.email().trim().toLowerCase());
        beneficiary.setCountryOfCitizenship(request.countryOfCitizenship().trim());
        beneficiary.setHighestEducation(request.highestEducation());

        Beneficiary saved = beneficiaryRepository.save(beneficiary);
        return DtoMapper.toBeneficiaryResponse(saved);
    }

    @Transactional(readOnly = true)
    public Beneficiary getEntity(Long beneficiaryId) {
        return beneficiaryRepository.findById(beneficiaryId)
                .orElseThrow(() -> new com.h1b.lottery.exception.NotFoundException("Beneficiary not found: " + beneficiaryId));
    }

    @Transactional(readOnly = true)
    public List<BeneficiaryResponse> listByEmployer(Long employerId) {
        employerService.getEntity(employerId);
        return beneficiaryRepository.findByEmployerIdOrderByCreatedAtDesc(employerId)
                .stream()
                .map(DtoMapper::toBeneficiaryResponse)
                .toList();
    }
}
