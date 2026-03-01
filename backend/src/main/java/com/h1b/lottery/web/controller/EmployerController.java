package com.h1b.lottery.web.controller;

import com.h1b.lottery.service.BeneficiaryService;
import com.h1b.lottery.service.EmployerService;
import com.h1b.lottery.service.RegistrationService;
import com.h1b.lottery.web.dto.BeneficiaryCreateRequest;
import com.h1b.lottery.web.dto.BeneficiaryResponse;
import com.h1b.lottery.web.dto.EmployerCreateRequest;
import com.h1b.lottery.web.dto.EmployerResponse;
import com.h1b.lottery.web.dto.RegistrationCreateRequest;
import com.h1b.lottery.web.dto.RegistrationResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/employers")
public class EmployerController {

    private final EmployerService employerService;
    private final BeneficiaryService beneficiaryService;
    private final RegistrationService registrationService;

    public EmployerController(EmployerService employerService,
                              BeneficiaryService beneficiaryService,
                              RegistrationService registrationService) {
        this.employerService = employerService;
        this.beneficiaryService = beneficiaryService;
        this.registrationService = registrationService;
    }

    @GetMapping
    public List<EmployerResponse> listEmployers() {
        return employerService.list();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EmployerResponse createEmployer(@Valid @RequestBody EmployerCreateRequest request) {
        return employerService.create(request);
    }

    @GetMapping("/{employerId}/beneficiaries")
    public List<BeneficiaryResponse> listBeneficiaries(@PathVariable Long employerId) {
        return beneficiaryService.listByEmployer(employerId);
    }

    @PostMapping("/{employerId}/beneficiaries")
    @ResponseStatus(HttpStatus.CREATED)
    public BeneficiaryResponse createBeneficiary(@PathVariable Long employerId,
                                                 @Valid @RequestBody BeneficiaryCreateRequest request) {
        return beneficiaryService.create(employerId, request);
    }

    @GetMapping("/{employerId}/registrations")
    public List<RegistrationResponse> listRegistrations(@PathVariable Long employerId) {
        return registrationService.listByEmployer(employerId);
    }

    @PostMapping("/{employerId}/registrations")
    @ResponseStatus(HttpStatus.CREATED)
    public RegistrationResponse createRegistration(@PathVariable Long employerId,
                                                   @Valid @RequestBody RegistrationCreateRequest request) {
        return registrationService.create(employerId, request);
    }
}
