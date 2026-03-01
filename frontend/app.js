const DEFAULT_API_BASE = "http://localhost:8080";

const state = {
  apiBase: localStorage.getItem("h1b_api_base") || DEFAULT_API_BASE,
  employers: [],
  beneficiariesByEmployer: new Map(),
  registrations: []
};

const apiBaseInput = document.getElementById("apiBaseInput");
const saveApiBaseBtn = document.getElementById("saveApiBaseBtn");
const activityLog = document.getElementById("activityLog");

const employerForm = document.getElementById("employerForm");
const beneficiaryForm = document.getElementById("beneficiaryForm");
const registrationForm = document.getElementById("registrationForm");
const manageRegistrationForm = document.getElementById("manageRegistrationForm");
const lotteryForm = document.getElementById("lotteryForm");
const analyticsForm = document.getElementById("analyticsForm");

const beneficiaryEmployerSelect = document.getElementById("beneficiaryEmployerSelect");
const registrationEmployerSelect = document.getElementById("registrationEmployerSelect");
const registrationBeneficiarySelect = document.getElementById("registrationBeneficiarySelect");
const registrationSelect = document.getElementById("registrationSelect");
const loadRegistrationsBtn = document.getElementById("loadRegistrationsBtn");
const submitRegistrationBtn = document.getElementById("submitRegistrationBtn");
const withdrawRegistrationBtn = document.getElementById("withdrawRegistrationBtn");
const refreshAnalyticsBtn = document.getElementById("refreshAnalyticsBtn");
const exportSnapshotBtn = document.getElementById("exportSnapshotBtn");

const lotteryResultPanel = document.getElementById("lotteryResultPanel");
const metricsGrid = document.getElementById("metricsGrid");
const analyticsTableBody = document.getElementById("analyticsTableBody");
const registrationTableBody = document.getElementById("registrationTableBody");

apiBaseInput.value = state.apiBase;

saveApiBaseBtn.addEventListener("click", () => {
  const value = apiBaseInput.value.trim();
  state.apiBase = value || DEFAULT_API_BASE;
  localStorage.setItem("h1b_api_base", state.apiBase);
  log(`API base URL set to ${state.apiBase}`);
  bootstrap();
});

employerForm.addEventListener("submit", async (event) => {
  event.preventDefault();
  const form = new FormData(event.currentTarget);
  await api("/api/employers", {
    method: "POST",
    body: {
      legalName: form.get("legalName"),
      fein: form.get("fein"),
      contactEmail: form.get("contactEmail")
    }
  });
  event.currentTarget.reset();
  log("Employer created.");
  await refreshEmployers();
});

beneficiaryForm.addEventListener("submit", async (event) => {
  event.preventDefault();
  const form = new FormData(event.currentTarget);
  const employerId = form.get("employerId");

  await api(`/api/employers/${employerId}/beneficiaries`, {
    method: "POST",
    body: {
      firstName: form.get("firstName"),
      lastName: form.get("lastName"),
      email: form.get("email"),
      countryOfCitizenship: form.get("countryOfCitizenship"),
      highestEducation: form.get("highestEducation")
    }
  });
  event.currentTarget.reset();
  log(`Beneficiary created for employer ${employerId}.`);
  await refreshBeneficiaries(employerId);
});

registrationForm.addEventListener("submit", async (event) => {
  event.preventDefault();
  const form = new FormData(event.currentTarget);
  const employerId = Number(form.get("employerId"));
  const payload = {
    beneficiaryId: Number(form.get("beneficiaryId")),
    fiscalYear: Number(form.get("fiscalYear")),
    offeredSalary: Number(form.get("offeredSalary")),
    workLocation: form.get("workLocation"),
    status: form.get("status")
  };

  await api(`/api/employers/${employerId}/registrations`, {
    method: "POST",
    body: payload
  });

  event.currentTarget.reset();
  log(`Registration created for employer ${employerId}.`);
  await loadRegistrations(payload.fiscalYear);
});

loadRegistrationsBtn.addEventListener("click", async () => {
  const fiscalYear = Number(new FormData(manageRegistrationForm).get("fiscalYear"));
  await loadRegistrations(fiscalYear);
});

submitRegistrationBtn.addEventListener("click", async () => {
  const registrationId = Number(new FormData(manageRegistrationForm).get("registrationId"));
  if (!registrationId) {
    log("Choose a registration first.", true);
    return;
  }
  await api(`/api/registrations/${registrationId}/submit`, { method: "PATCH" });
  log(`Registration ${registrationId} submitted.`);
  await reloadCurrentFiscalYearData();
});

withdrawRegistrationBtn.addEventListener("click", async () => {
  const registrationId = Number(new FormData(manageRegistrationForm).get("registrationId"));
  if (!registrationId) {
    log("Choose a registration first.", true);
    return;
  }
  await api(`/api/registrations/${registrationId}/withdraw`, { method: "PATCH" });
  log(`Registration ${registrationId} withdrawn.`);
  await reloadCurrentFiscalYearData();
});

lotteryForm.addEventListener("submit", async (event) => {
  event.preventDefault();
  const form = new FormData(event.currentTarget);

  const payload = {
    fiscalYear: Number(form.get("fiscalYear")),
    regularCap: Number(form.get("regularCap")),
    mastersCap: Number(form.get("mastersCap"))
  };

  const seed = form.get("seed");
  if (seed) {
    payload.seed = Number(seed);
  }

  const result = await api("/api/lottery/runs", { method: "POST", body: payload });
  lotteryResultPanel.textContent = `Run #${result.id}: submitted=${result.totalSubmitted}, selected masters=${result.selectedMasters}, selected regular=${result.selectedRegular}, seed=${result.seed}`;
  log(`Lottery run completed for FY ${payload.fiscalYear}.`);
  await reloadCurrentFiscalYearData();
});

refreshAnalyticsBtn.addEventListener("click", reloadCurrentFiscalYearData);

exportSnapshotBtn.addEventListener("click", async () => {
  const fiscalYear = Number(new FormData(analyticsForm).get("fiscalYear"));
  const exportResult = await api(`/api/analytics/exports?fiscalYear=${fiscalYear}`, { method: "POST" });
  log(`CSV export created: ${exportResult.filePath} (${exportResult.rowCount} rows)`);
});

registrationEmployerSelect.addEventListener("change", async (event) => {
  const employerId = Number(event.target.value);
  await populateBeneficiarySelect(employerId);
});

async function bootstrap() {
  await refreshEmployers();
  const fiscalYear = Number(new FormData(analyticsForm).get("fiscalYear"));
  await loadRegistrations(fiscalYear);
  await reloadCurrentFiscalYearData();
}

async function refreshEmployers() {
  state.employers = await api("/api/employers");

  fillSelect(beneficiaryEmployerSelect, state.employers, "Select employer");
  fillSelect(registrationEmployerSelect, state.employers, "Select employer");

  if (state.employers.length > 0) {
    const employerId = state.employers[0].id;
    beneficiaryEmployerSelect.value = String(employerId);
    registrationEmployerSelect.value = String(employerId);
    await populateBeneficiarySelect(employerId);
  } else {
    registrationBeneficiarySelect.innerHTML = `<option value="">No beneficiaries available</option>`;
  }
}

async function refreshBeneficiaries(employerId) {
  const data = await api(`/api/employers/${employerId}/beneficiaries`);
  state.beneficiariesByEmployer.set(Number(employerId), data);

  if (Number(registrationEmployerSelect.value) === Number(employerId)) {
    await populateBeneficiarySelect(Number(employerId));
  }
}

async function populateBeneficiarySelect(employerId) {
  if (!employerId) {
    registrationBeneficiarySelect.innerHTML = `<option value="">Select employer first</option>`;
    return;
  }

  let beneficiaries = state.beneficiariesByEmployer.get(Number(employerId));
  if (!beneficiaries) {
    beneficiaries = await api(`/api/employers/${employerId}/beneficiaries`);
    state.beneficiariesByEmployer.set(Number(employerId), beneficiaries);
  }

  if (!beneficiaries.length) {
    registrationBeneficiarySelect.innerHTML = `<option value="">No beneficiaries available</option>`;
    return;
  }

  registrationBeneficiarySelect.innerHTML = beneficiaries
    .map((beneficiary) => `<option value="${beneficiary.id}">${beneficiary.firstName} ${beneficiary.lastName}</option>`)
    .join("");
}

async function loadRegistrations(fiscalYear) {
  state.registrations = await api(`/api/registrations?fiscalYear=${fiscalYear}`);

  registrationSelect.innerHTML = state.registrations.length
    ? state.registrations
      .map((registration) => `<option value="${registration.id}">#${registration.id} ${registration.beneficiaryName} (${registration.status})</option>`)
      .join("")
    : `<option value="">No registrations for FY ${fiscalYear}</option>`;

  registrationTableBody.innerHTML = state.registrations
    .map((registration) => {
      const employerName = state.employers.find((employer) => employer.id === registration.employerId)?.legalName || registration.employerId;
      return `<tr>
        <td>${registration.id}</td>
        <td>${escapeHtml(String(employerName))}</td>
        <td>${escapeHtml(registration.beneficiaryName)}</td>
        <td>${registration.status}</td>
        <td>${registration.mastersCapEligible ? "Yes" : "No"}</td>
      </tr>`;
    })
    .join("");
}

async function reloadCurrentFiscalYearData() {
  const fiscalYear = Number(new FormData(analyticsForm).get("fiscalYear"));
  await Promise.all([
    loadRegistrations(fiscalYear),
    refreshDashboard(fiscalYear),
    refreshEmployerAnalytics(fiscalYear)
  ]);
}

async function refreshDashboard(fiscalYear) {
  const metrics = await api(`/api/analytics/dashboard?fiscalYear=${fiscalYear}`);
  const metricEntries = Object.entries(metrics).filter(([key]) => key !== "fiscalYear");
  metricsGrid.innerHTML = metricEntries
    .map(([label, value]) => `<div class="metric"><div class="label">${label}</div><div class="value">${value}</div></div>`)
    .join("");
}

async function refreshEmployerAnalytics(fiscalYear) {
  const rows = await api(`/api/analytics/employers?fiscalYear=${fiscalYear}`);
  analyticsTableBody.innerHTML = rows
    .map((row) => `<tr>
      <td>${escapeHtml(row.employerName)}</td>
      <td>${row.fiscalYear}</td>
      <td>${row.totalRegistrations}</td>
      <td>${row.selected}</td>
      <td>${row.selectionRate}%</td>
    </tr>`)
    .join("");
}

function fillSelect(selectEl, items, placeholder) {
  if (!items.length) {
    selectEl.innerHTML = `<option value="">No employers available</option>`;
    return;
  }

  selectEl.innerHTML = [
    `<option value="">${placeholder}</option>`,
    ...items.map((item) => `<option value="${item.id}">${escapeHtml(item.legalName)} (${item.fein})</option>`)
  ].join("");
}

async function api(path, options = {}) {
  const url = `${state.apiBase}${path}`;
  const init = {
    method: options.method || "GET",
    headers: {
      "Content-Type": "application/json"
    }
  };

  if (options.body) {
    init.body = JSON.stringify(options.body);
  }

  const response = await fetch(url, init);
  if (!response.ok) {
    let message = `Request failed (${response.status})`;
    try {
      const problem = await response.json();
      if (problem.message) {
        message = problem.message;
      }
      if (problem.details && problem.details.length) {
        message += ` | ${problem.details.join("; ")}`;
      }
    } catch (_) {
      // ignore parse errors
    }
    log(`${init.method} ${path} failed: ${message}`, true);
    throw new Error(message);
  }

  if (response.status === 204) {
    return null;
  }

  return response.json();
}

function log(message, isError = false) {
  const timestamp = new Date().toISOString();
  const line = `[${timestamp}] ${isError ? "ERROR" : "INFO"} ${message}`;
  activityLog.textContent = `${line}\n${activityLog.textContent}`.trim();
}

function escapeHtml(value) {
  return value
    .replaceAll("&", "&amp;")
    .replaceAll("<", "&lt;")
    .replaceAll(">", "&gt;")
    .replaceAll('"', "&quot;")
    .replaceAll("'", "&#39;");
}

bootstrap().catch((error) => {
  log(`Bootstrap failed: ${error.message}`, true);
});
