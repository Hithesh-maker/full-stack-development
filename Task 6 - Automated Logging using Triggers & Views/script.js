// ============================================================
// STUDENTHUB — TASK 06
// AUTOMATED LOGGING USING TRIGGERS & VIEWS
// ============================================================


// ============================================================
// SUPABASE CONFIGURATION
// ============================================================

const SUPABASE_URL =
    "https://cppjrfaftlwlzmwgicxj.supabase.co";

const SUPABASE_KEY =
    "sb_publishable_-uuWKMVV61MAgUIdP21cQg_BDwtyIcb";

const supabaseClient =
    window.supabase.createClient(
        SUPABASE_URL,
        SUPABASE_KEY
    );


// ============================================================
// PAGE LOAD
// ============================================================

document.addEventListener(
    "DOMContentLoaded",
    async function () {

        console.log(
            "StudentHub Task 06 dashboard loaded."
        );

        await refreshDashboard();

    }
);


// ============================================================
// INSERT RECORD
// ============================================================

async function insertRecord() {

    const name =
        document
            .getElementById("recordName")
            .value
            .trim();

    const department =
        document
            .getElementById("department")
            .value
            .trim();


    // --------------------------------------------------------
    // VALIDATION
    // --------------------------------------------------------

    if (!name || !department) {

        showStatus(
            "Please enter both record name and department.",
            "error"
        );

        return;
    }


    try {

        console.log(
            "Attempting to insert record..."
        );

        console.log(
            "Name:",
            name
        );

        console.log(
            "Department:",
            department
        );


        // ----------------------------------------------------
        // INSERT INTO RECORDS
        // ----------------------------------------------------

        const {
            data,
            error
        } =
            await supabaseClient
                .from("records")
                .insert([
                    {
                        name: name,
                        department: department
                    }
                ])
                .select();


        // ----------------------------------------------------
        // DATABASE ERROR
        // ----------------------------------------------------

        if (error) {

            console.error(
                "================================="
            );

            console.error(
                "SUPABASE INSERT ERROR"
            );

            console.error(
                "Code:",
                error.code
            );

            console.error(
                "Message:",
                error.message
            );

            console.error(
                "Details:",
                error.details
            );

            console.error(
                "Hint:",
                error.hint
            );

            console.error(
                "Full error:",
                error
            );

            console.error(
                "================================="
            );


            showStatus(
                `Insert failed: ${error.message}`,
                "error"
            );

            return;
        }


        // ----------------------------------------------------
        // SUCCESS
        // ----------------------------------------------------

        console.log(
            "Record inserted successfully:",
            data
        );


        showStatus(
            "Record inserted successfully. The audit trigger created an INSERT log.",
            "success"
        );


        clearForm();


        // Refresh entire dashboard

        await refreshDashboard();

    }
    catch (error) {

        console.error(
            "Unexpected INSERT error:",
            error
        );


        showStatus(
            `Unexpected error: ${error.message}`,
            "error"
        );

    }

}


// ============================================================
// UPDATE RECORD
// ============================================================

async function updateRecord() {

    const name =
        document
            .getElementById("recordName")
            .value
            .trim();

    const department =
        document
            .getElementById("department")
            .value
            .trim();


    // --------------------------------------------------------
    // VALIDATION
    // --------------------------------------------------------

    if (!name || !department) {

        showStatus(
            "Enter the record name and new department.",
            "error"
        );

        return;
    }


    try {

        console.log(
            "Searching for record:",
            name
        );


        // ----------------------------------------------------
        // FIND RECORD
        // ----------------------------------------------------

        const {
            data,
            error
        } =
            await supabaseClient
                .from("records")
                .select("*")
                .eq("name", name)
                .limit(1);


        if (error) {

            console.error(
                "Find record error:",
                error
            );


            showStatus(
                `Unable to find record: ${error.message}`,
                "error"
            );

            return;
        }


        // ----------------------------------------------------
        // RECORD NOT FOUND
        // ----------------------------------------------------

        if (
            !data ||
            data.length === 0
        ) {

            showStatus(
                "No record found with that name.",
                "warning"
            );

            return;
        }


        const record =
            data[0];


        console.log(
            "Record found:",
            record
        );


        // ----------------------------------------------------
        // UPDATE RECORD
        // ----------------------------------------------------

        const {
            data: updatedData,
            error: updateError
        } =
            await supabaseClient
                .from("records")
                .update({
                    department: department
                })
                .eq(
                    "id",
                    record.id
                )
                .select();


        // ----------------------------------------------------
        // UPDATE ERROR
        // ----------------------------------------------------

        if (updateError) {

            console.error(
                "================================="
            );

            console.error(
                "SUPABASE UPDATE ERROR"
            );

            console.error(
                "Code:",
                updateError.code
            );

            console.error(
                "Message:",
                updateError.message
            );

            console.error(
                "Details:",
                updateError.details
            );

            console.error(
                "Hint:",
                updateError.hint
            );

            console.error(
                "Full error:",
                updateError
            );

            console.error(
                "================================="
            );


            showStatus(
                `Update failed: ${updateError.message}`,
                "error"
            );

            return;
        }


        // ----------------------------------------------------
        // SUCCESS
        // ----------------------------------------------------

        console.log(
            "Record updated successfully:",
            updatedData
        );


        showStatus(
            "Record updated successfully. The audit trigger created an UPDATE log.",
            "success"
        );


        clearForm();


        await refreshDashboard();

    }
    catch (error) {

        console.error(
            "Unexpected UPDATE error:",
            error
        );


        showStatus(
            `Unexpected error: ${error.message}`,
            "error"
        );

    }

}


// ============================================================
// LOAD RECORDS
// ============================================================

async function loadRecords() {

    const table =
        document.getElementById(
            "recordsTable"
        );


    if (!table) {

        return;
    }


    try {

        const {
            data,
            error
        } =
            await supabaseClient
                .from("records")
                .select("*")
                .order(
                    "id",
                    {
                        ascending: false
                    }
                )
                .limit(10);


        if (error) {

            console.error(
                "Records error:",
                error
            );


            table.innerHTML = `
                <tr>
                    <td
                        colspan="5"
                        class="empty-state"
                    >
                        Unable to load records:
                        ${escapeHTML(error.message)}
                    </td>
                </tr>
            `;

            return;
        }


        if (
            !data ||
            data.length === 0
        ) {

            table.innerHTML = `
                <tr>
                    <td
                        colspan="5"
                        class="empty-state"
                    >
                        No records available
                    </td>
                </tr>
            `;

            return;
        }


        table.innerHTML = "";


        data.forEach(
            record => {

                const row =
                    document.createElement(
                        "tr"
                    );


                row.innerHTML = `

                    <td>
                        #${record.id}
                    </td>

                    <td>
                        ${escapeHTML(
                            record.name
                        )}
                    </td>

                    <td>
                        ${escapeHTML(
                            record.department
                        )}
                    </td>

                    <td>
                        ${formatDate(
                            record.created_at
                        )}
                    </td>

                    <td>
                        ${formatDate(
                            record.updated_at
                        )}
                    </td>

                `;


                table.appendChild(
                    row
                );

            }
        );

    }
    catch (error) {

        console.error(
            "Unexpected records error:",
            error
        );

    }

}


// ============================================================
// LOAD AUDIT LOGS
// ============================================================

async function loadAuditLogs() {

    const table =
        document.getElementById(
            "logsTable"
        );


    if (!table) {

        return;
    }


    try {

        const {
            data,
            error
        } =
            await supabaseClient
                .from("audit_logs")
                .select("*")
                .order(
                    "changed_at",
                    {
                        ascending: false
                    }
                )
                .limit(10);


        if (error) {

            console.error(
                "Audit log error:",
                error
            );


            table.innerHTML = `
                <tr>
                    <td
                        colspan="6"
                        class="empty-state"
                    >
                        Unable to load audit logs:
                        ${escapeHTML(error.message)}
                    </td>
                </tr>
            `;

            return;
        }


        if (
            !data ||
            data.length === 0
        ) {

            table.innerHTML = `
                <tr>
                    <td
                        colspan="6"
                        class="empty-state"
                    >
                        No audit logs available
                    </td>
                </tr>
            `;

            return;
        }


        table.innerHTML = "";


        data.forEach(
            log => {

                const row =
                    document.createElement(
                        "tr"
                    );


                const operation =
                    String(
                        log.operation || ""
                    ).toUpperCase();


                const badgeClass =
                    operation === "INSERT"
                        ? "operation-insert"
                        : "operation-update";


                row.innerHTML = `

                    <td>
                        #${log.id}
                    </td>

                    <td>
                        #${log.record_id}
                    </td>

                    <td>

                        <span
                            class="operation-badge ${badgeClass}"
                        >
                            ${escapeHTML(
                                operation
                            )}
                        </span>

                    </td>

                    <td>
                        ${formatJSON(
                            log.old_data
                        )}
                    </td>

                    <td>
                        ${formatJSON(
                            log.new_data
                        )}
                    </td>

                    <td>
                        ${formatDate(
                            log.changed_at
                        )}
                    </td>

                `;


                table.appendChild(
                    row
                );

            }
        );

    }
    catch (error) {

        console.error(
            "Unexpected audit log error:",
            error
        );

    }

}


// ============================================================
// LOAD DAILY ACTIVITY REPORT
// ============================================================

async function loadDailyReport() {

    const table =
        document.getElementById(
            "reportTable"
        );


    if (!table) {

        return;
    }


    try {

        const {
            data,
            error
        } =
            await supabaseClient
                .from(
                    "daily_activity_report"
                )
                .select("*")
                .order(
                    "activity_date",
                    {
                        ascending: false
                    }
                );


        if (error) {

            console.error(
                "Daily report error:",
                error
            );


            table.innerHTML = `
                <tr>
                    <td
                        colspan="3"
                        class="empty-state"
                    >
                        Unable to load daily report:
                        ${escapeHTML(error.message)}
                    </td>
                </tr>
            `;

            return;
        }


        if (
            !data ||
            data.length === 0
        ) {

            table.innerHTML = `
                <tr>
                    <td
                        colspan="3"
                        class="empty-state"
                    >
                        No daily activity available
                    </td>
                </tr>
            `;

            return;
        }


        table.innerHTML = "";


        data.forEach(
            report => {

                const row =
                    document.createElement(
                        "tr"
                    );


                const operation =
                    String(
                        report.operation || ""
                    ).toUpperCase();


                const badgeClass =
                    operation === "INSERT"
                        ? "operation-insert"
                        : "operation-update";


                row.innerHTML = `

                    <td>
                        ${formatDateOnly(
                            report.activity_date
                        )}
                    </td>

                    <td>

                        <span
                            class="operation-badge ${badgeClass}"
                        >
                            ${escapeHTML(
                                operation
                            )}
                        </span>

                    </td>

                    <td>

                        <strong>
                            ${report.total_operations ?? 0}
                        </strong>

                    </td>

                `;


                table.appendChild(
                    row
                );

            }
        );

    }
    catch (error) {

        console.error(
            "Unexpected daily report error:",
            error
        );

    }

}


// ============================================================
// LOAD STATISTICS
// ============================================================

async function loadStatistics() {

    try {

        const {
            data,
            error
        } =
            await supabaseClient
                .from("audit_logs")
                .select(
                    "id, operation, changed_at"
                );


        if (error) {

            console.error(
                "Statistics error:",
                error
            );

            return;
        }


        const logs =
            data || [];


        // ----------------------------------------------------
        // TOTAL ACTIVITIES
        // ----------------------------------------------------

        const totalActivities =
            logs.length;


        // ----------------------------------------------------
        // INSERT COUNT
        // ----------------------------------------------------

        const insertCount =
            logs.filter(
                log =>
                    String(
                        log.operation || ""
                    ).toUpperCase() ===
                    "INSERT"
            ).length;


        // ----------------------------------------------------
        // UPDATE COUNT
        // ----------------------------------------------------

        const updateCount =
            logs.filter(
                log =>
                    String(
                        log.operation || ""
                    ).toUpperCase() ===
                    "UPDATE"
            ).length;


        // ----------------------------------------------------
        // TODAY'S ACTIVITY
        // ----------------------------------------------------

        const today =
            new Date()
                .toLocaleDateString(
                    "en-CA"
                );


        const todayCount =
            logs.filter(
                log => {

                    if (!log.changed_at) {

                        return false;
                    }


                    const logDate =
                        new Date(
                            log.changed_at
                        ).toLocaleDateString(
                            "en-CA"
                        );


                    return (
                        logDate === today
                    );

                }
            ).length;


        // ----------------------------------------------------
        // UPDATE UI
        // ----------------------------------------------------

        const totalElement =
            document.getElementById(
                "totalActivities"
            );


        const insertElement =
            document.getElementById(
                "insertCount"
            );


        const updateElement =
            document.getElementById(
                "updateCount"
            );


        const todayElement =
            document.getElementById(
                "todayCount"
            );


        if (totalElement) {

            totalElement.textContent =
                totalActivities;

        }


        if (insertElement) {

            insertElement.textContent =
                insertCount;

        }


        if (updateElement) {

            updateElement.textContent =
                updateCount;

        }


        if (todayElement) {

            todayElement.textContent =
                todayCount;

        }

    }
    catch (error) {

        console.error(
            "Unexpected statistics error:",
            error
        );

    }

}


// ============================================================
// REFRESH DASHBOARD
// ============================================================

async function refreshDashboard() {

    console.log(
        "Refreshing Task 06 dashboard..."
    );


    await Promise.all([
        loadStatistics(),
        loadRecords(),
        loadAuditLogs(),
        loadDailyReport()
    ]);


    console.log(
        "Dashboard refresh complete."
    );

}


// ============================================================
// CLEAR FORM
// ============================================================

function clearForm() {

    const nameInput =
        document.getElementById(
            "recordName"
        );


    const departmentInput =
        document.getElementById(
            "department"
        );


    if (nameInput) {

        nameInput.value = "";

    }


    if (departmentInput) {

        departmentInput.value = "";

    }

}


// ============================================================
// SHOW STATUS
// ============================================================

function showStatus(
    message,
    type
) {

    const status =
        document.getElementById(
            "statusMessage"
        );


    if (!status) {

        return;
    }


    status.textContent =
        message;


    status.className =
        `status-message ${type}`;


    status.classList.remove(
        "hidden"
    );


    // Hide after 6 seconds

    setTimeout(
        () => {

            status.classList.add(
                "hidden"
            );

        },
        6000
    );

}


// ============================================================
// FORMAT DATE
// ============================================================

function formatDate(
    date
) {

    if (!date) {

        return "-";
    }


    const parsedDate =
        new Date(date);


    if (
        Number.isNaN(
            parsedDate.getTime()
        )
    ) {

        return "-";
    }


    return parsedDate.toLocaleString(
        "en-IN",
        {
            dateStyle: "medium",
            timeStyle: "short"
        }
    );

}


// ============================================================
// FORMAT DATE ONLY
// ============================================================

function formatDateOnly(
    date
) {

    if (!date) {

        return "-";
    }


    const parsedDate =
        new Date(date);


    if (
        Number.isNaN(
            parsedDate.getTime()
        )
    ) {

        return "-";
    }


    return parsedDate.toLocaleDateString(
        "en-IN",
        {
            dateStyle: "medium"
        }
    );

}


// ============================================================
// FORMAT JSON
// ============================================================

function formatJSON(
    value
) {

    if (
        value === null ||
        value === undefined
    ) {

        return "-";
    }


    if (
        typeof value === "object"
    ) {

        return escapeHTML(
            JSON.stringify(
                value
            )
        );

    }


    return escapeHTML(
        String(value)
    );

}


// ============================================================
// ESCAPE HTML
// ============================================================

function escapeHTML(
    value
) {

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
// TEST SUPABASE CONNECTION
// ============================================================

async function testSupabaseConnection() {

    console.log(
        "Testing Supabase connection..."
    );


    const {
        data,
        error
    } =
        await supabaseClient
            .from("records")
            .select("id")
            .limit(1);


    if (error) {

        console.error(
            "Supabase connection/table test failed:",
            error
        );

        return false;
    }


    console.log(
        "Supabase connection successful."
    );

    console.log(
        "Records table is accessible."
    );


    return true;

}