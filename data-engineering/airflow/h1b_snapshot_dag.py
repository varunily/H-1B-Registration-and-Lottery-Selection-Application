from datetime import datetime
from airflow import DAG
from airflow.operators.bash import BashOperator

with DAG(
    dag_id="h1b_snapshot_pipeline",
    start_date=datetime(2025, 1, 1),
    schedule="0 2 * * *",
    catchup=False,
    tags=["h1b", "analytics"],
) as dag:
    export_snapshot = BashOperator(
        task_id="export_snapshot",
        bash_command=(
            "curl -s -X POST 'http://h1b-backend:8080/api/analytics/exports?fiscalYear=2027' "
            "-H 'accept: application/json'"
        ),
    )

    transform_duckdb = BashOperator(
        task_id="transform_duckdb",
        bash_command=(
            "cd /opt/airflow/dags/duckdb && "
            "duckdb analytics.duckdb < transformations.sql"
        ),
    )

    export_snapshot >> transform_duckdb
