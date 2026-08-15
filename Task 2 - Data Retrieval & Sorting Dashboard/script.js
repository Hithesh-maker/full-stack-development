// ============================================================
// STUDENTHUB — TASK 02
// DATA RETRIEVAL & SORTING DASHBOARD
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
// GLOBAL DATA
// ============================================================

let students = [];


// ============================================================
// DOM ELEMENTS
// ============================================================

const tableBody =
    document.getElementById(
        "studentTableBody"
    );

const departmentFilter =
    document.getElementById(
        "departmentFilter"
    );

const sortOption =
    document.getElementById(
        "sortOption"
    );

const totalStudents =
    document.getElementById(
        "totalStudents"
    );

const cseCount =
    document.getElementById(
        "cseCount"
    );

const eceCount =
    document.getElementById(
        "eceCount"
    );

const eeeCount =
    document.getElementById(
        "eeeCount"
    );

const recordCount =
    document.getElementById(
        "recordCount"
    );

const refreshBtn =
    document.getElementById(
        "refreshBtn"
    );

const connectionStatus =
    document.getElementById(
        "connectionStatus"
    );


// ============================================================
// CONNECTION STATUS
// ============================================================

function setConnectionStatus(
    text,
    type
) {

    if (!connectionStatus) {
        return;
    }


    connectionStatus.className =
        `status ${type}`;


    connectionStatus.innerHTML = `

        <span class="status-dot"></span>

        ${escapeHTML(text)}

    `;

}


// ============================================================
// FETCH STUDENTS
// ============================================================

async function fetchStudents() {

    showLoading();


    console.log(
        "StudentHub: Fetching student records..."
    );


    try {

        const {
            data,
            error
        } =
            await supabaseClient
                .from("students")
                .select(
                    "id, name, email, dob, department, phone"
                );


        // ----------------------------------------------------
        // DATABASE ERROR
        // ----------------------------------------------------

        if (error) {

            console.error(
                "StudentHub Supabase Error:",
                error
            );


            showDatabaseError(
                error
            );


            return;

        }


        // ----------------------------------------------------
        // STORE DATA
        // ----------------------------------------------------

        students =
            Array.isArray(data)
                ? data
                : [];


        console.log(
            "StudentHub: Records retrieved:",
            students.length
        );


        // ----------------------------------------------------
        // CONNECTION SUCCESS
        // ----------------------------------------------------

        setConnectionStatus(
            "Connected",
            "success-status"
        );


        // ----------------------------------------------------
        // UPDATE DASHBOARD
        // ----------------------------------------------------

        updateDepartmentFilter();

        updateCounts();

        displayStudents();

    }


    catch (error) {

        console.error(
            "StudentHub unexpected error:",
            error
        );


        showUnexpectedError(
            error
        );

    }

}


// ============================================================
// LOADING STATE
// ============================================================

function showLoading() {

    setConnectionStatus(
        "Connecting...",
        "loading-status"
    );


    if (tableBody) {

        tableBody.innerHTML = `

            <tr>

                <td
                    colspan="6"
                    class="message"
                >

                    Loading student records...

                </td>

            </tr>

        `;

    }


    if (recordCount) {

        recordCount.textContent =
            "Loading records...";

    }

}


// ============================================================
// DATABASE ERROR
// ============================================================

function showDatabaseError(
    error
) {

    setConnectionStatus(
        "Database Error",
        "error-status"
    );


    const message =
        error?.message ||
        "Unable to retrieve records.";


    if (tableBody) {

        tableBody.innerHTML = `

            <tr>

                <td
                    colspan="6"
                    class="message"
                >

                    <strong>
                        Unable to retrieve student records.
                    </strong>

                    <br><br>

                    ${escapeHTML(message)}

                </td>

            </tr>

        `;

    }


    if (recordCount) {

        recordCount.textContent =
            "Database error";

    }

}


// ============================================================
// UNEXPECTED ERROR
// ============================================================

function showUnexpectedError(
    error
) {

    setConnectionStatus(
        "Connection Error",
        "error-status"
    );


    if (tableBody) {

        tableBody.innerHTML = `

            <tr>

                <td
                    colspan="6"
                    class="message"
                >

                    <strong>
                        Unable to connect to Supabase.
                    </strong>

                    <br><br>

                    Check the browser console
                    for more information.

                </td>

            </tr>

        `;

    }


    if (recordCount) {

        recordCount.textContent =
            "Connection error";

    }


    console.error(
        error
    );

}


// ============================================================
// DEPARTMENT FILTER
// ============================================================

function updateDepartmentFilter() {

    if (!departmentFilter) {
        return;
    }


    const departments = [

        ...new Set(

            students

                .map(
                    student =>
                        String(
                            student.department || ""
                        ).trim()
                )

                .filter(Boolean)

        )

    ];


    departments.sort(
        (a, b) =>
            a.localeCompare(b)
    );


    departmentFilter.innerHTML = `

        <option value="all">
            All Departments
        </option>

    `;


    departments.forEach(
        department => {

            const option =
                document.createElement(
                    "option"
                );


            option.value =
                department;


            option.textContent =
                department;


            departmentFilter.appendChild(
                option
            );

        }
    );

}


// ============================================================
// UPDATE COUNTS
// ============================================================

function updateCounts() {

    totalStudents.textContent =
        students.length;


    const cse =
        students.filter(
            student =>
                isDepartment(
                    student.department,
                    "CSE"
                )
        ).length;


    const ece =
        students.filter(
            student =>
                isDepartment(
                    student.department,
                    "ECE"
                )
        ).length;


    const eee =
        students.filter(
            student =>
                isDepartment(
                    student.department,
                    "EEE"
                )
        ).length;


    cseCount.textContent =
        cse;


    eceCount.textContent =
        ece;


    eeeCount.textContent =
        eee;

}


// ============================================================
// DEPARTMENT IDENTIFICATION
// ============================================================

function isDepartment(
    department,
    code
) {

    const value =
        String(
            department || ""
        )
            .trim()
            .toUpperCase();


    const target =
        code.toUpperCase();


    // Exact match

    if (
        value === target
    ) {

        return true;

    }


    // CSE

    if (
        target === "CSE" &&
        (
            value.includes(
                "COMPUTER SCIENCE"
            ) ||
            value.includes(
                "COMPUTER & SCIENCE"
            ) ||
            value.includes(
                "COMPUTER ENGINEERING"
            )
        )
    ) {

        return true;

    }


    // ECE

    if (
        target === "ECE" &&
        (
            value.includes(
                "ELECTRONICS AND COMMUNICATION"
            ) ||
            value.includes(
                "ELECTRONICS & COMMUNICATION"
            )
        )
    ) {

        return true;

    }


    // EEE

    if (
        target === "EEE" &&
        (
            value.includes(
                "ELECTRICAL AND ELECTRONICS"
            ) ||
            value.includes(
                "ELECTRICAL & ELECTRONICS"
            )
        )
    ) {

        return true;

    }


    return false;

}


// ============================================================
// DISPLAY STUDENTS
// ============================================================

function displayStudents() {

    let filteredStudents =
        [...students];


    // --------------------------------------------------------
    // FILTER
    // --------------------------------------------------------

    const selectedDepartment =
        departmentFilter.value;


    if (
        selectedDepartment !== "all"
    ) {

        filteredStudents =
            filteredStudents.filter(
                student =>
                    String(
                        student.department || ""
                    ).trim() ===
                    selectedDepartment
            );

    }


    // --------------------------------------------------------
    // SORT
    // --------------------------------------------------------

    const selectedSort =
        sortOption.value;


    // NAME A-Z

    if (
        selectedSort === "nameAsc"
    ) {

        filteredStudents.sort(
            (a, b) =>
                String(
                    a.name || ""
                ).localeCompare(
                    String(
                        b.name || ""
                    )
                )
        );

    }


    // NAME Z-A

    else if (
        selectedSort === "nameDesc"
    ) {

        filteredStudents.sort(
            (a, b) =>
                String(
                    b.name || ""
                ).localeCompare(
                    String(
                        a.name || ""
                    )
                )
        );

    }


    // DATE OLDEST -> NEWEST

    else if (
        selectedSort === "dateAsc"
    ) {

        filteredStudents.sort(
            (a, b) =>
                getDateValue(a.dob) -
                getDateValue(b.dob)
        );

    }


    // DATE NEWEST -> OLDEST

    else if (
        selectedSort === "dateDesc"
    ) {

        filteredStudents.sort(
            (a, b) =>
                getDateValue(b.dob) -
                getDateValue(a.dob)
        );

    }


    // --------------------------------------------------------
    // NO RESULTS
    // --------------------------------------------------------

    if (
        filteredStudents.length === 0
    ) {

        tableBody.innerHTML = `

            <tr>

                <td
                    colspan="6"
                    class="message"
                >

                    No student records found.

                </td>

            </tr>

        `;


        recordCount.textContent =
            "0 records found";


        return;

    }


    // --------------------------------------------------------
    // CLEAR TABLE
    // --------------------------------------------------------

    tableBody.innerHTML =
        "";


    // --------------------------------------------------------
    // CREATE ROWS
    // --------------------------------------------------------

    filteredStudents.forEach(
        (
            student,
            index
        ) => {

            const row =
                document.createElement(
                    "tr"
                );


            row.innerHTML = `

                <td>

                    <span class="row-number">

                        ${index + 1}

                    </span>

                </td>


                <td>

                    <div class="student-cell">

                        <div class="student-avatar">

                            ${getInitials(
                                student.name
                            )}

                        </div>


                        <strong>

                            ${escapeHTML(
                                student.name
                            )}

                        </strong>

                    </div>

                </td>


                <td>

                    <span class="email-text">

                        ${escapeHTML(
                            student.email
                        )}

                    </span>

                </td>


                <td>

                    ${formatDate(
                        student.dob
                    )}

                </td>


                <td>

                    <span class="department">

                        ${escapeHTML(
                            student.department
                        )}

                    </span>

                </td>


                <td>

                    ${escapeHTML(
                        student.phone
                    )}

                </td>

            `;


            tableBody.appendChild(
                row
            );

        }
    );


    // --------------------------------------------------------
    // RECORD COUNT
    // --------------------------------------------------------

    recordCount.textContent =
        `${filteredStudents.length} ${
            filteredStudents.length === 1
                ? "record"
                : "records"
        } found`;

}


// ============================================================
// DATE VALUE
// ============================================================

function getDateValue(
    date
) {

    if (!date) {
        return 0;
    }


    const value =
        new Date(date).getTime();


    return Number.isNaN(value)
        ? 0
        : value;

}


// ============================================================
// DATE FORMATTER
// ============================================================

function formatDate(
    date
) {

    if (!date) {
        return "-";
    }


    const formattedDate =
        new Date(date);


    if (
        Number.isNaN(
            formattedDate.getTime()
        )
    ) {

        return "-";

    }


    return formattedDate.toLocaleDateString(
        "en-IN",
        {
            day: "2-digit",
            month: "short",
            year: "numeric"
        }
    );

}


// ============================================================
// GET INITIALS
// ============================================================

function getInitials(
    name
) {

    if (!name) {
        return "?";
    }


    const words =
        String(name)
            .trim()
            .split(/\s+/);


    if (
        words.length === 1
    ) {

        return words[0]
            .substring(0, 2)
            .toUpperCase();

    }


    return (
        words[0][0] +
        words[words.length - 1][0]
    ).toUpperCase();

}


// ============================================================
// HTML ESCAPE
// ============================================================

function escapeHTML(
    value
) {

    if (
        value === null ||
        value === undefined ||
        value === ""
    ) {

        return "-";

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
// EVENT LISTENERS
// ============================================================

if (departmentFilter) {

    departmentFilter.addEventListener(
        "change",
        displayStudents
    );

}


if (sortOption) {

    sortOption.addEventListener(
        "change",
        displayStudents
    );

}


if (refreshBtn) {

    refreshBtn.addEventListener(
        "click",
        fetchStudents
    );

}


// ============================================================
// INITIAL LOAD
// ============================================================

fetchStudents();


// ============================================================
// END OF TASK 02
// ============================================================