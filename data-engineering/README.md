# Data Engineering Layer

This folder contains a lightweight analytics pipeline to support audit/reporting for H-1B registration outcomes.

## Flow

1. Trigger backend export:
   - `POST /api/analytics/exports?fiscalYear=2027`
2. Backend writes CSV under `backend/exports/`.
3. Load CSV into DuckDB for downstream analytics.
4. Run SQL transforms to generate employer-level and outcome-level datasets.

## Quickstart

```bash
cd data-engineering/duckdb
./load_snapshot.sh ../../backend/exports/registrations_fy2027_YYYYMMDD_HHMMSS.csv
```

Then run:

```bash
duckdb analytics.duckdb < transformations.sql
```

The script creates these tables:
- `stg_registrations`
- `fct_lottery_outcomes`
- `dim_employer_metrics`

## Scheduling

A sample Airflow DAG is provided in `airflow/h1b_snapshot_dag.py` to automate nightly imports and transforms.
