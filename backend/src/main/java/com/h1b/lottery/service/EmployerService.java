package com.h1b.lottery.service;

import com.h1b.lottery.domain.model.Employer;
import com.h1b.lottery.exception.BadRequestException;
import com.h1b.lottery.exception.NotFoundException;
import com.h1b.lottery.repository.EmployerRepository;
import com.h1b.lottery.web.dto.EmployerCreateRequest;
import com.h1b.lottery.web.dto.EmployerResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class EmployerService {

    private final EmployerRepository employerRepository;

    public EmployerService(EmployerRepository employerRepository) {
        this.employerRepository = employerRepository;
    }

    @Transactional
    public EmployerResponse create(EmployerCreateRequest request) {
        employerRepository.findByFein(request.fein()).ifPresent(existing -> {
            throw new BadRequestException("An employer with FEIN " + request.fein() + " already exists");
        });

        Employer employer = new Employer();
        employer.setLegalName(request.legalName().trim());
        employer.setFein(request.fein().trim());
        employer.setContactEmail(request.contactEmail().trim().toLowerCase());

        Employer saved = employerRepository.save(employer);
        return DtoMapper.toEmployerResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<EmployerResponse> list() {
        return employerRepository.findAll()
                .stream()
                .map(DtoMapper::toEmployerResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public Employer getEntity(Long employerId) {
        return employerRepository.findById(employerId)
                .orElseThrow(() -> new NotFoundException("Employer not found: " + employerId));
    }
}
