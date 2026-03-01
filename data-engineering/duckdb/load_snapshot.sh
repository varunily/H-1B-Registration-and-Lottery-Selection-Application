#!/usr/bin/env bash
set -euo pipefail

if [ "$#" -ne 1 ]; then
  echo "Usage: $0 <path-to-registration-csv>"
  exit 1
fi

CSV_PATH="$1"
DB_FILE="analytics.duckdb"

if ! command -v duckdb >/dev/null 2>&1; then
  echo "duckdb CLI is required"
  exit 1
fi

duckdb "$DB_FILE" <<SQL
CREATE TABLE IF NOT EXISTS stg_registrations AS
SELECT * FROM read_csv_auto('${CSV_PATH}', header=true);
SQL

echo "Loaded snapshot into ${DB_FILE}: stg_registrations"
