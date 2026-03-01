package com.h1b.lottery.web.controller;

import com.h1b.lottery.service.RegistrationService;
import com.h1b.lottery.web.dto.RegistrationResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/registrations")
public class RegistrationController {

    private final RegistrationService registrationService;

    public RegistrationController(RegistrationService registrationService) {
        this.registrationService = registrationService;
    }

    @PatchMapping("/{registrationId}/submit")
    public RegistrationResponse submit(@PathVariable Long registrationId) {
        return registrationService.submit(registrationId);
    }

    @PatchMapping("/{registrationId}/withdraw")
    public RegistrationResponse withdraw(@PathVariable Long registrationId) {
        return registrationService.withdraw(registrationId);
    }

    @GetMapping
    public List<RegistrationResponse> listByFiscalYear(@RequestParam Integer fiscalYear) {
        return registrationService.listByFiscalYear(fiscalYear);
    }
}
