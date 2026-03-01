package com.h1b.lottery.service;

import com.h1b.lottery.domain.model.Registration;
import com.h1b.lottery.repository.RegistrationRepository;
import com.h1b.lottery.web.dto.ExportResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class ReportingService {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");

    private final RegistrationRepository registrationRepository;

    public ReportingService(RegistrationRepository registrationRepository) {
        this.registrationRepository = registrationRepository;
    }

    @Transactional(readOnly = true)
    public ExportResponse exportFiscalYearSnapshot(Integer fiscalYear) {
        List<Registration> rows = registrationRepository.findByFiscalYearOrderByCreatedAtDesc(fiscalYear);

        try {
            Path exportDir = Path.of("exports");
            Files.createDirectories(exportDir);

            String filename = "registrations_fy" + fiscalYear + "_" + FORMATTER.format(OffsetDateTime.now()) + ".csv";
            Path file = exportDir.resolve(filename);

            StringBuilder csv = new StringBuilder();
            csv.append("registration_id,employer_id,employer_name,beneficiary_id,beneficiary_name,fiscal_year,status,masters_cap_eligible,offered_salary,work_location,submitted_at,selected_at,created_at\n");

            for (Registration row : rows) {
                csv.append(row.getId()).append(',')
                        .append(row.getEmployer().getId()).append(',')
                        .append(escape(row.getEmployer().getLegalName())).append(',')
                        .append(row.getBeneficiary().getId()).append(',')
                        .append(escape(row.getBeneficiary().getFirstName() + " " + row.getBeneficiary().getLastName())).append(',')
                        .append(row.getFiscalYear()).append(',')
                        .append(row.getStatus()).append(',')
                        .append(row.getBeneficiary().isMastersCapEligible()).append(',')
                        .append(row.getOfferedSalary()).append(',')
                        .append(escape(row.getWorkLocation())).append(',')
                        .append(nullable(row.getSubmittedAt())).append(',')
                        .append(nullable(row.getSelectedAt())).append(',')
                        .append(nullable(row.getCreatedAt())).append('\n');
            }

            Files.writeString(file, csv.toString());
            return new ExportResponse(file.toAbsolutePath().toString(), rows.size());
        } catch (IOException e) {
            throw new RuntimeException("Failed to export snapshot", e);
        }
    }

    private String escape(String value) {
        if (value == null) {
            return "";
        }
        String escaped = value.replace("\"", "\"\"");
        return "\"" + escaped + "\"";
    }

    private String nullable(Object value) {
        return value == null ? "" : value.toString();
    }
}
