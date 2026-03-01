CREATE TABLE employers (
    id BIGSERIAL PRIMARY KEY,
    legal_name VARCHAR(255) NOT NULL,
    fein VARCHAR(9) NOT NULL UNIQUE,
    contact_email VARCHAR(255) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE beneficiaries (
    id BIGSERIAL PRIMARY KEY,
    employer_id BIGINT NOT NULL REFERENCES employers(id),
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    email VARCHAR(255) NOT NULL,
    country_of_citizenship VARCHAR(120) NOT NULL,
    highest_education VARCHAR(30) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE registrations (
    id BIGSERIAL PRIMARY KEY,
    employer_id BIGINT NOT NULL REFERENCES employers(id),
    beneficiary_id BIGINT NOT NULL REFERENCES beneficiaries(id),
    fiscal_year INT NOT NULL,
    offered_salary NUMERIC(12,2) NOT NULL,
    work_location VARCHAR(255) NOT NULL,
    status VARCHAR(30) NOT NULL,
    submitted_at TIMESTAMPTZ,
    selected_at TIMESTAMPTZ,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT uq_registration UNIQUE (employer_id, beneficiary_id, fiscal_year)
);

CREATE TABLE lottery_runs (
    id BIGSERIAL PRIMARY KEY,
    fiscal_year INT NOT NULL,
    regular_cap INT NOT NULL,
    masters_cap INT NOT NULL,
    seed BIGINT NOT NULL,
    total_submitted INT NOT NULL,
    selected_regular INT NOT NULL,
    selected_masters INT NOT NULL,
    status VARCHAR(30) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    completed_at TIMESTAMPTZ
);

CREATE INDEX idx_beneficiaries_employer_id ON beneficiaries(employer_id);
CREATE INDEX idx_registrations_employer_id ON registrations(employer_id);
CREATE INDEX idx_registrations_beneficiary_id ON registrations(beneficiary_id);
CREATE INDEX idx_registrations_fiscal_year_status ON registrations(fiscal_year, status);
CREATE INDEX idx_lottery_runs_fiscal_year ON lottery_runs(fiscal_year);

CREATE OR REPLACE VIEW vw_employer_selection_metrics AS
SELECT
    r.fiscal_year,
    e.id AS employer_id,
    e.legal_name AS employer_name,
    COUNT(*) AS total_registrations,
    SUM(CASE WHEN r.status = 'SELECTED' THEN 1 ELSE 0 END) AS selected_registrations,
    ROUND(
        100.0 * SUM(CASE WHEN r.status = 'SELECTED' THEN 1 ELSE 0 END)::NUMERIC / NULLIF(COUNT(*), 0),
        2
    ) AS selection_rate
FROM registrations r
JOIN employers e ON e.id = r.employer_id
GROUP BY r.fiscal_year, e.id, e.legal_name;
