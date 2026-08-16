// ============================================================
// STUDENTHUB — TASK 06
// AUTOMATED LOGGING USING TRIGGERS & VIEWS
// script.js
// ============================================================


// ============================================================
// 1. API CONFIGURATION
// ============================================================

const API_BASE_URL = "http://localhost:5000/api";


// ============================================================
// 2. DOM ELEMENTS
// ============================================================

// Statistics

const totalLogsElement =
    document.getElementById("totalLogs");

const insertOperationsElement =
    document.getElementById("insertOperations");

const updateOperationsElement =
    document.getElementById("updateOperations");

const todayActivityElement =
    document.getElementById("todayActivity");


// Tables

const auditTableBody =
    document.getElementById("auditTableBody");

const activityTableBody =
    document.getElementById("activityTableBody");


// Refresh button

const refreshButton =
    document.getElementById("refreshLogs");


// Loading / error elements

const loadingElement =
    document.getElementById("loading");

const errorMessageElement =
    document.getElementById("errorMessage");

const statusMessageElement =
    document.getElementById("statusMessage");


// ============================================================
// 3. HELPER — SHOW ERROR
// ============================================================

function showError(message) {

    if (errorMessageElement) {

        errorMessageElement.textContent =
            message;

        errorMessageElement.style.display =
            "block";

    }

}


// ============================================================
// 4. HELPER — HIDE ERROR
// ============================================================

function hideError() {

    if (errorMessageElement) {

        errorMessageElement.textContent =
            "";

        errorMessageElement.style.display =
            "none";

    }

}


// ============================================================
// 5. HELPER — FORMAT DATE
// ============================================================

function formatDate(dateValue) {

    if (!dateValue) {

        return "—";

    }

    const date =
        new Date(dateValue);

    if (isNaN(date.getTime())) {

        return escapeHTML(
            String(dateValue)
        );

    }

    return date.toLocaleString(
        "en-IN",
        {
            dateStyle: "medium",
            timeStyle: "short"
        }
    );

}


// ============================================================
// 6. HELPER — FORMAT ACTIVITY DATE
// ============================================================

function formatActivityDate(dateValue) {

    if (!dateValue) {

        return "—";

    }

    const date =
        new Date(dateValue);

    if (isNaN(date.getTime())) {

        return escapeHTML(
            String(dateValue)
        );

    }

    return date.toLocaleDateString(
        "en-IN"
    );

}


// ============================================================
// 7. LOAD AUDIT LOGS
// ============================================================

async function loadAuditLogs() {

    try {

        if (auditTableBody) {

            auditTableBody.innerHTML = `
                <tr>
                    <td colspan="5">
                        Loading audit activity...
                    </td>
                </tr>
            `;

        }

        hideError();


        console.log(
            "Loading audit logs from:",
            `${API_BASE_URL}/audit-logs`
        );


        const response =
            await fetch(
                `${API_BASE_URL}/audit-logs`
            );


        if (!response.ok) {

            throw new Error(
                `Server returned ${response.status}`
            );

        }


        const result =
            await response.json();


        console.log(
            "Audit logs response:",
            result
        );


        // ====================================================
        // IMPORTANT
        // API RESPONSE:
        //
        // {
        //     success: true,
        //     data: [...]
        // }
        // ====================================================

        let logs = [];


        if (Array.isArray(result)) {

            // Supports direct array response

            logs = result;

        }

        else if (
            result &&
            Array.isArray(result.data)
        ) {

            // Supports { success: true, data: [...] }

            logs = result.data;

        }

        else {

            throw new Error(
                result?.message ||
                "Invalid audit log data received from server"
            );

        }


        console.log(
            "Processed audit logs:",
            logs
        );


        displayAuditLogs(logs);

        updateStatistics(logs);


    }

    catch (error) {

        console.error(
            "Audit log error:",
            error
        );


        if (auditTableBody) {

            auditTableBody.innerHTML = `
                <tr>
                    <td colspan="5">
                        Unable to load audit logs.
                    </td>
                </tr>
            `;

        }


        updateStatistics([]);


        showError(
            "Unable to connect to the StudentHub API. Make sure the Node.js server is running on port 5000."
        );

    }

}


// ============================================================
// 8. DISPLAY AUDIT LOGS
// ============================================================

function displayAuditLogs(logs) {

    if (!auditTableBody) {

        return;

    }


    auditTableBody.innerHTML =
        "";


    if (
        !logs ||
        logs.length === 0
    ) {

        auditTableBody.innerHTML = `
            <tr>
                <td colspan="5">
                    No audit records found.
                </td>
            </tr>
        `;

        return;

    }


    logs.forEach(log => {

        const row =
            document.createElement("tr");


        // ----------------------------------------------------
        // LOG ID
        // ----------------------------------------------------

        const logId =
            log.log_id ??
            log.id ??
            "-";


        // ----------------------------------------------------
        // TABLE NAME
        // ----------------------------------------------------

        const tableName =
            log.table_name ??
            log.table ??
            "students";


        // ----------------------------------------------------
        // OPERATION
        // ----------------------------------------------------

        const operation =
            String(
                log.action_type ??
                log.operation ??
                "UNKNOWN"
            ).toUpperCase();


        const operationClass =
            operation === "INSERT"
                ? "insert"
                : operation === "UPDATE"
                    ? "update"
                    : "";


        // ----------------------------------------------------
        // RECORD ID
        // ----------------------------------------------------

        const recordId =
            log.record_id ??
            "-";


        // ----------------------------------------------------
        // DESCRIPTION
        // ----------------------------------------------------

        const description =
            log.description ??
            "Database activity";


        // ----------------------------------------------------
        // TIMESTAMP
        // ----------------------------------------------------

        const actionTime =
            log.action_time ??
            log.created_at ??
            log.timestamp;


        // ----------------------------------------------------
        // CREATE ROW
        // ----------------------------------------------------

        row.innerHTML = `

            <td>
                ${escapeHTML(logId)}
            </td>

            <td>
                ${escapeHTML(tableName)}
            </td>

            <td>

                <span
                    class="operation-badge ${operationClass}"
                >
                    ${escapeHTML(operation)}
                </span>

            </td>

            <td>
                ${escapeHTML(recordId)}
            </td>

            <td>

                ${escapeHTML(description)}

                <br>

                <small>
                    ${formatDate(actionTime)}
                </small>

            </td>

        `;


        auditTableBody.appendChild(row);

    });

}


// ============================================================
// 9. UPDATE DASHBOARD STATISTICS
// ============================================================

function updateStatistics(logs) {

    if (!Array.isArray(logs)) {

        logs = [];

    }


    // --------------------------------------------------------
    // TOTAL LOGS
    // --------------------------------------------------------

    const totalLogs =
        logs.length;


    // --------------------------------------------------------
    // INSERT COUNT
    // --------------------------------------------------------

    const insertCount =
        logs.filter(
            log =>
                String(
                    log.action_type ??
                    log.operation ??
                    ""
                ).toUpperCase() === "INSERT"
        ).length;


    // --------------------------------------------------------
    // UPDATE COUNT
    // --------------------------------------------------------

    const updateCount =
        logs.filter(
            log =>
                String(
                    log.action_type ??
                    log.operation ??
                    ""
                ).toUpperCase() === "UPDATE"
        ).length;


    // --------------------------------------------------------
    // UPDATE UI
    // --------------------------------------------------------

    if (totalLogsElement) {

        totalLogsElement.textContent =
            totalLogs;

    }


    if (insertOperationsElement) {

        insertOperationsElement.textContent =
            insertCount;

    }


    if (updateOperationsElement) {

        updateOperationsElement.textContent =
            updateCount;

    }


    // --------------------------------------------------------
    // TODAY'S ACTIVITY
    // --------------------------------------------------------

    updateTodayActivityFromLogs(logs);

}


// ============================================================
// 10. UPDATE TODAY'S ACTIVITY FROM AUDIT LOGS
// ============================================================

function updateTodayActivityFromLogs(logs) {

    if (!todayActivityElement) {

        return;

    }


    if (
        !Array.isArray(logs) ||
        logs.length === 0
    ) {

        todayActivityElement.textContent =
            "0";

        return;

    }


    const now =
        new Date();


    const todayYear =
        now.getFullYear();

    const todayMonth =
        now.getMonth();

    const todayDate =
        now.getDate();


    let todayCount = 0;


    logs.forEach(log => {

        const timeValue =
            log.action_time ??
            log.created_at ??
            log.timestamp;


        if (!timeValue) {

            return;

        }


        const logDate =
            new Date(timeValue);


        if (
            isNaN(
                logDate.getTime()
            )
        ) {

            return;

        }


        if (
            logDate.getFullYear() === todayYear &&
            logDate.getMonth() === todayMonth &&
            logDate.getDate() === todayDate
        ) {

            todayCount++;

        }

    });


    todayActivityElement.textContent =
        todayCount;

}


// ============================================================
// 11. LOAD DAILY ACTIVITY REPORT
// ============================================================

async function loadDailyActivityReport() {

    try {

        if (activityTableBody) {

            activityTableBody.innerHTML = `
                <tr>
                    <td colspan="3">
                        Loading daily report...
                    </td>
                </tr>
            `;

        }


        console.log(
            "Loading daily report from:",
            `${API_BASE_URL}/daily-activity`
        );


        const response =
            await fetch(
                `${API_BASE_URL}/daily-activity`
            );


        if (!response.ok) {

            throw new Error(
                `Server returned ${response.status}`
            );

        }


        const result =
            await response.json();


        console.log(
            "Daily report response:",
            result
        );


        // ====================================================
        // SUPPORT BOTH:
        //
        // [...]
        //
        // AND:
        //
        // {
        //     success: true,
        //     data: [...]
        // }
        // ====================================================

        let report = [];


        if (Array.isArray(result)) {

            report = result;

        }

        else if (
            result &&
            Array.isArray(result.data)
        ) {

            report = result.data;

        }

        else {

            throw new Error(
                result?.message ||
                "Invalid daily activity data received"
            );

        }


        console.log(
            "Processed daily report:",
            report
        );


        displayDailyActivity(report);


        updateTodayActivityFromReport(
            report
        );


    }

    catch (error) {

        console.error(
            "Daily report error:",
            error
        );


        if (activityTableBody) {

            activityTableBody.innerHTML = `
                <tr>
                    <td colspan="3">
                        Unable to load daily report.
                    </td>
                </tr>
            `;

        }

    }

}


// ============================================================
// 12. DISPLAY DAILY ACTIVITY REPORT
// ============================================================

function displayDailyActivity(report) {

    if (!activityTableBody) {

        return;

    }


    activityTableBody.innerHTML =
        "";


    if (
        !report ||
        report.length === 0
    ) {

        activityTableBody.innerHTML = `
            <tr>
                <td colspan="3">
                    No daily activity found.
                </td>
            </tr>
        `;

        return;

    }


    report.forEach(item => {

        const row =
            document.createElement("tr");


        // ----------------------------------------------------
        // OPERATION
        // ----------------------------------------------------

        const operation =
            String(
                item.action_type ??
                item.operation ??
                "UNKNOWN"
            ).toUpperCase();


        const operationClass =
            operation === "INSERT"
                ? "insert"
                : operation === "UPDATE"
                    ? "update"
                    : "";


        // ----------------------------------------------------
        // TOTAL ACTIONS
        // ----------------------------------------------------

        const totalActions =
            Number(
                item.total_actions ??
                item.total_operations ??
                item.count ??
                0
            );


        // ----------------------------------------------------
        // ACTIVITY DATE
        // ----------------------------------------------------

        const activityDate =
            item.activity_date ??
            item.date ??
            item.action_date;


        // ----------------------------------------------------
        // CREATE ROW
        // ----------------------------------------------------

        row.innerHTML = `

            <td>
                ${formatActivityDate(
                    activityDate
                )}
            </td>

            <td>

                <span
                    class="operation-badge ${operationClass}"
                >
                    ${escapeHTML(operation)}
                </span>

            </td>

            <td>
                ${totalActions}
            </td>

        `;


        activityTableBody.appendChild(row);

    });

}


// ============================================================
// 13. UPDATE TODAY ACTIVITY FROM SQL VIEW
// ============================================================

function updateTodayActivityFromReport(report) {

    if (!todayActivityElement) {

        return;

    }


    if (
        !Array.isArray(report) ||
        report.length === 0
    ) {

        todayActivityElement.textContent =
            "0";

        return;

    }


    const now =
        new Date();


    let todayTotal = 0;


    report.forEach(item => {

        const dateValue =
            item.activity_date ??
            item.date ??
            item.action_date;


        if (!dateValue) {

            return;

        }


        const activityDate =
            new Date(dateValue);


        if (
            isNaN(
                activityDate.getTime()
            )
        ) {

            return;

        }


        if (
            activityDate.getFullYear() ===
                now.getFullYear() &&

            activityDate.getMonth() ===
                now.getMonth() &&

            activityDate.getDate() ===
                now.getDate()
        ) {

            todayTotal +=
                Number(
                    item.total_actions ??
                    item.total_operations ??
                    item.count ??
                    0
                );

        }

    });


    todayActivityElement.textContent =
        todayTotal;

}


// ============================================================
// 14. ESCAPE HTML
// ============================================================

function escapeHTML(value) {

    if (
        value === null ||
        value === undefined
    ) {

        return "";

    }


    return String(value)

        .replace(
            /&/g,
            "&amp;"
        )

        .replace(
            /</g,
            "&lt;"
        )

        .replace(
            />/g,
            "&gt;"
        )

        .replace(
            /"/g,
            "&quot;"
        )

        .replace(
            /'/g,
            "&#039;"
        );

}


// ============================================================
// 15. REFRESH DASHBOARD
// ============================================================

async function refreshDashboard() {

    console.log(
        "Refreshing Task 06 dashboard..."
    );


    await Promise.all([
        loadAuditLogs(),
        loadDailyActivityReport()
    ]);

}


// ============================================================
// 16. REFRESH BUTTON
// ============================================================

if (refreshButton) {

    refreshButton.addEventListener(
        "click",
        async function () {

            refreshButton.disabled =
                true;


            const originalText =
                refreshButton.innerHTML;


            refreshButton.innerHTML =
                "↻ Refreshing...";


            try {

                await refreshDashboard();

            }

            finally {

                refreshButton.disabled =
                    false;

                refreshButton.innerHTML =
                    originalText;

            }

        }
    );

}


// ============================================================
// 17. AUTOMATIC REFRESH
// ============================================================

// Refresh every 10 seconds

setInterval(
    refreshDashboard,
    10000
);


// ============================================================
// 18. INITIAL LOAD
// ============================================================

document.addEventListener(
    "DOMContentLoaded",
    function () {

        console.log(
            "========================================"
        );

        console.log(
            "StudentHub Task 06 loaded"
        );

        console.log(
            "API:",
            API_BASE_URL
        );

        console.log(
            "========================================"
        );


        refreshDashboard();

    }
);


// ============================================================
// TASK 06 COMPLETE
// ============================================================