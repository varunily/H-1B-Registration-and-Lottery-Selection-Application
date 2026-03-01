# H-1B Employer Registration and Lottery Application

Production-style end-to-end application for employers to:
- register their company,
- onboard foreign national beneficiaries,
- submit H-1B cap registrations,
- execute a reproducible lottery workflow,
- monitor outcomes with analytics and data exports.

## 1. What This Includes

- Java backend (`Spring Boot 3`, `PostgreSQL`, `Flyway`, `JPA`, `OpenAPI/Swagger`)
- Frontend workflow portal (`HTML/CSS/JS`, mobile-friendly)
- Deterministic lottery engine (seed-based for audit/replay)
- Data engineering layer (CSV export + DuckDB transforms + sample Airflow DAG)
- Deployment assets (`Dockerfile`, `docker-compose`, Kubernetes manifests)
- CI pipeline (`GitHub Actions`)

## 2. Core Business Workflow

1. Employer creates account/profile (legal name, FEIN, contact email)
2. Employer adds beneficiaries (education level determines masters-cap eligibility)
3. Employer creates registration per beneficiary per fiscal year
4. Registration moves from `DRAFT` -> `SUBMITTED`
5. Admin runs lottery for fiscal year with:
   - regular cap (default 65,000)
   - masters cap (default 20,000)
   - optional random seed
6. System marks each submitted registration as:
   - `SELECTED`
   - `NOT_SELECTED`
7. Team reviews dashboard analytics and exports CSV snapshots for reporting

## 3. Lottery Picking Logic (How Selection Happens)

For a requested fiscal year:

1. Read all `SUBMITTED` registrations.
2. Build **masters pool** from beneficiaries with advanced education (`MASTERS`, `PHD`, `PROFESSIONAL`).
3. Randomly select up to `mastersCap` from the masters pool.
4. Build **regular pool** using all remaining submitted registrations (including masters not selected in step 3).
5. Randomly select up to `regularCap` from the regular pool.
6. Mark selected IDs as `SELECTED`; all other submitted registrations become `NOT_SELECTED`.
7. Persist run metadata (`seed`, selected counts, timestamps, status).

The random seed allows exact replay for audits.

## 4. Project Structure

```text
.
├── backend/
│   ├── src/main/java/com/h1b/lottery
│   │   ├── domain/model        # Employer, Beneficiary, Registration, LotteryRun
│   │   ├── repository          # JPA repositories
│   │   ├── service             # Business logic + lottery engine + reporting
│   │   ├── web/controller      # REST APIs
│   │   └── exception           # API exception handling
│   ├── src/main/resources
│   │   ├── application.yml
│   │   └── db/migration/V1__init_schema.sql
│   └── Dockerfile
├── frontend/
│   ├── index.html
│   ├── styles.css
│   ├── app.js
│   └── Dockerfile
├── data-engineering/
│   ├── duckdb/load_snapshot.sh
│   ├── duckdb/transformations.sql
│   └── airflow/h1b_snapshot_dag.py
├── deploy/k8s/
│   ├── namespace.yaml
│   ├── postgres.yaml
│   ├── backend.yaml
│   └── frontend.yaml
├── docker-compose.yml
└── .github/workflows/ci.yml
```

## 5. Run Locally (Recommended: Docker Compose)

### Prerequisites
- Docker + Docker Compose

### Start

```bash
docker compose up --build
```

### Access
- Frontend: `http://localhost:3000`
- Backend API: `http://localhost:8080`
- Swagger UI: `http://localhost:8080/swagger-ui.html`

## 6. Run Backend Without Docker

### Prerequisites
- Java 21
- Maven 3.9+
- PostgreSQL 16+

### Environment
Copy `.env.example` values into your shell.

### Start backend

```bash
cd backend
mvn spring-boot:run
```

## 7. Main API Endpoints

### Employers
- `GET /api/employers`
- `POST /api/employers`
- `GET /api/employers/{employerId}/beneficiaries`
- `POST /api/employers/{employerId}/beneficiaries`
- `GET /api/employers/{employerId}/registrations`
- `POST /api/employers/{employerId}/registrations`

### Registrations
- `PATCH /api/registrations/{registrationId}/submit`
- `PATCH /api/registrations/{registrationId}/withdraw`
- `GET /api/registrations?fiscalYear=2027`

### Lottery
- `POST /api/lottery/runs`
- `GET /api/lottery/runs?fiscalYear=2027`

### Analytics / Data Engineering
- `GET /api/analytics/dashboard?fiscalYear=2027`
- `GET /api/analytics/employers?fiscalYear=2027`
- `POST /api/analytics/exports?fiscalYear=2027`

## 8. Sample API Calls

Create employer:

```bash
curl -X POST http://localhost:8080/api/employers \
  -H "Content-Type: application/json" \
  -d '{
    "legalName": "Acme Consulting LLC",
    "fein": "123456789",
    "contactEmail": "immigration@acme.com"
  }'
```

Run lottery:

```bash
curl -X POST http://localhost:8080/api/lottery/runs \
  -H "Content-Type: application/json" \
  -d '{
    "fiscalYear": 2027,
    "regularCap": 65000,
    "mastersCap": 20000,
    "seed": 2027001
  }'
```

## 9. Data Engineering Usage

1. Trigger export:

```bash
curl -X POST "http://localhost:8080/api/analytics/exports?fiscalYear=2027"
```

2. Load CSV into DuckDB:

```bash
cd data-engineering/duckdb
./load_snapshot.sh ../../backend/exports/registrations_fy2027_YYYYMMDD_HHMMSS.csv
```

3. Build fact/dimension tables:

```bash
duckdb analytics.duckdb < transformations.sql
```

## 10. Kubernetes Deployment

Update images in:
- `deploy/k8s/backend.yaml`
- `deploy/k8s/frontend.yaml`

Apply manifests:

```bash
kubectl apply -f deploy/k8s/namespace.yaml
kubectl apply -f deploy/k8s/postgres.yaml
kubectl apply -f deploy/k8s/backend.yaml
kubectl apply -f deploy/k8s/frontend.yaml
```

## 11. Testing and CI

Backend unit tests:

```bash
cd backend
mvn test
```

CI (`.github/workflows/ci.yml`) runs:
- backend tests
- frontend artifact presence smoke check

## 12. Notes

- This system models a robust employer workflow and lottery simulation.
- Real USCIS policy/legal operations should be validated with immigration counsel and official USCIS updates.
