CREATE OR REPLACE TABLE fct_lottery_outcomes AS
SELECT
    registration_id,
    employer_id,
    employer_name,
    beneficiary_id,
    beneficiary_name,
    fiscal_year,
    status,
    masters_cap_eligible,
    offered_salary,
    work_location,
    submitted_at,
    selected_at,
    created_at,
    CASE WHEN status = 'SELECTED' THEN 1 ELSE 0 END AS selected_flag
FROM stg_registrations;

CREATE OR REPLACE TABLE dim_employer_metrics AS
SELECT
    fiscal_year,
    employer_id,
    employer_name,
    COUNT(*) AS total_registrations,
    SUM(selected_flag) AS selected_registrations,
    ROUND(100.0 * SUM(selected_flag) / NULLIF(COUNT(*), 0), 2) AS selection_rate
FROM fct_lottery_outcomes
GROUP BY 1, 2, 3;
