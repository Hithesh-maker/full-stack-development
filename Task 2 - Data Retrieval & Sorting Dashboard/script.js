// ============================================================
// STUDENTHUB — TASK 02
// DATA RETRIEVAL & SORTING DASHBOARD
// Node.js + Express + MySQL
// ============================================================


// ============================================================
// 1. API CONFIGURATION
// ============================================================

const API_URL =
    "http://localhost:5000/api/students";


// ============================================================
// 2. GLOBAL DATA
// ============================================================

let students = [];


// ============================================================
// 3. DOM ELEMENTS
// ============================================================

const tableBody =
    document.getElementById("studentTableBody");

const departmentFilter =
    document.getElementById("departmentFilter");

const sortOption =
    document.getElementById("sortOption");

const totalStudents =
    document.getElementById("totalStudents");

const cseCount =
    document.getElementById("cseCount");

const eceCount =
    document.getElementById("eceCount");

const eeeCount =
    document.getElementById("eeeCount");

const recordCount =
    document.getElementById("recordCount");

const refreshBtn =
    document.getElementById("refreshBtn");

const connectionStatus =
    document.getElementById("connectionStatus");


// ============================================================
// 4. CONNECTION STATUS
// ============================================================

function setConnectionStatus(text, type) {

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
// 5. FETCH STUDENTS FROM MYSQL API
// ============================================================

async function fetchStudents() {

    showLoading();

    console.log(
        "StudentHub: Fetching students from MySQL..."
    );

    try {

        const response =
            await fetch(API_URL);


        if (!response.ok) {

            throw new Error(
                `HTTP ${response.status}`
            );

        }


        const result =
            await response.json();


        console.log(
            "StudentHub API Response:",
            result
        );


        // ----------------------------------------------------
        // CHECK API RESPONSE
        // ----------------------------------------------------

        if (!result.success) {

            throw new Error(
                result.message ||
                "Failed to retrieve student records."
            );

        }


        // ----------------------------------------------------
        // STORE STUDENT DATA
        // ----------------------------------------------------

        students =
            Array.isArray(result.data)
                ? result.data
                : [];


        console.log(
            `StudentHub: ${students.length} students loaded`
        );


        // ----------------------------------------------------
        // UPDATE DASHBOARD
        // ----------------------------------------------------

        setConnectionStatus(
            "Connected to MySQL",
            "success-status"
        );

        updateDepartmentFilter();

        updateCounts();

        displayStudents();


    } catch (error) {

        console.error(
            "StudentHub connection error:",
            error
        );

        showUnexpectedError(error);

    }

}


// ============================================================
// 6. LOADING STATE
// ============================================================

function showLoading() {

    setConnectionStatus(
        "Connecting to MySQL...",
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
// 7. ERROR STATE
// ============================================================

function showUnexpectedError(error) {

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
                        Unable to retrieve student records.
                    </strong>

                    <br><br>

                    Make sure the Node.js server
                    and MySQL database are running.

                    <br><br>

                    <small>
                        ${escapeHTML(
                            error?.message ||
                            "Unknown error"
                        )}
                    </small>

                </td>

            </tr>

        `;

    }


    if (recordCount) {

        recordCount.textContent =
            "Connection error";

    }

}


// ============================================================
// 8. NORMALIZE DEPARTMENT
// ============================================================
// Converts all department formats into short codes.
//
// CSE
// ECE
// EEE
//
// Examples:
//
// CSE
// Computer Science
// Computer Science and Engineering
//       ↓
//      CSE
//
// ECE
// Electronics
// Electronics and Communication Engineering
//       ↓
//      ECE
//
// EEE
// Electrical
// Electrical and Electronics Engineering
//       ↓
//      EEE
// ============================================================

function normalizeDepartment(department) {

    const value =
        String(
            department || ""
        )
            .trim()
            .toUpperCase();


    const departmentMap = {

        // ----------------------------------------------------
        // CSE
        // ----------------------------------------------------

        "CSE":
            "CSE",

        "COMPUTER SCIENCE":
            "CSE",

        "COMPUTER SCIENCE AND ENGINEERING":
            "CSE",


        // ----------------------------------------------------
        // ECE
        // ----------------------------------------------------

        "ECE":
            "ECE",

        "ELECTRONICS":
            "ECE",

        "ELECTRONICS AND COMMUNICATION ENGINEERING":
            "ECE",


        // ----------------------------------------------------
        // EEE
        // ----------------------------------------------------

        "EEE":
            "EEE",

        "ELECTRICAL":
            "EEE",

        "ELECTRICAL AND ELECTRONICS ENGINEERING":
            "EEE"

    };


    return (
        departmentMap[value] ||
        value
    );

}


// ============================================================
// 9. UPDATE DEPARTMENT FILTER
// ============================================================

function updateDepartmentFilter() {

    if (!departmentFilter) {
        return;
    }


    // --------------------------------------------------------
    // Get unique department codes
    // --------------------------------------------------------

    const departments = [

        ...new Set(

            students
                .map(student =>
                    normalizeDepartment(
                        student.department
                    )
                )
                .filter(Boolean)

        )

    ];


    departments.sort(
        (a, b) =>
            a.localeCompare(b)
    );


    // --------------------------------------------------------
    // Reset filter
    // --------------------------------------------------------

    departmentFilter.innerHTML = `

        <option value="all">
            All Departments
        </option>

    `;


    // --------------------------------------------------------
    // Add department options
    // --------------------------------------------------------

    departments.forEach(
        department => {

            const option =
                document.createElement(
                    "option"
                );


            option.value =
                department;


            // Display SHORT CODE

            option.textContent =
                department;


            departmentFilter.appendChild(
                option
            );

        }
    );

}


// ============================================================
// 10. UPDATE DASHBOARD COUNTS
// ============================================================

function updateCounts() {

    // --------------------------------------------------------
    // TOTAL STUDENTS
    // --------------------------------------------------------

    if (totalStudents) {

        totalStudents.textContent =
            students.length;

    }


    // --------------------------------------------------------
    // CSE COUNT
    // --------------------------------------------------------

    const cse =
        students.filter(
            student =>
                normalizeDepartment(
                    student.department
                ) === "CSE"
        ).length;


    // --------------------------------------------------------
    // ECE COUNT
    // --------------------------------------------------------

    const ece =
        students.filter(
            student =>
                normalizeDepartment(
                    student.department
                ) === "ECE"
        ).length;


    // --------------------------------------------------------
    // EEE COUNT
    // --------------------------------------------------------

    const eee =
        students.filter(
            student =>
                normalizeDepartment(
                    student.department
                ) === "EEE"
        ).length;


    // --------------------------------------------------------
    // UPDATE UI
    // --------------------------------------------------------

    if (cseCount) {

        cseCount.textContent =
            cse;

    }


    if (eceCount) {

        eceCount.textContent =
            ece;

    }


    if (eeeCount) {

        eeeCount.textContent =
            eee;

    }

}


// ============================================================
// 11. DEPARTMENT IDENTIFICATION
// ============================================================

function isDepartment(
    department,
    code
) {

    return (
        normalizeDepartment(
            department
        ) === code
    );

}


// ============================================================
// 12. DISPLAY STUDENTS
// ============================================================

function displayStudents() {

    let filteredStudents =
        [...students];


    // ========================================================
    // FILTER BY DEPARTMENT
    // ========================================================

    const selectedDepartment =
        departmentFilter
            ? departmentFilter.value
            : "all";


    if (
        selectedDepartment !== "all"
    ) {

        filteredStudents =
            filteredStudents.filter(
                student =>
                    isDepartment(
                        student.department,
                        selectedDepartment
                    )
            );

    }


    // ========================================================
    // SORT
    // ========================================================

    const selectedSort =
        sortOption
            ? sortOption.value
            : "default";


    // --------------------------------------------------------
    // NAME A → Z
    // --------------------------------------------------------

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


    // --------------------------------------------------------
    // NAME Z → A
    // --------------------------------------------------------

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


    // --------------------------------------------------------
    // DATE OLDEST → NEWEST
    // --------------------------------------------------------

    else if (
        selectedSort === "dateAsc"
    ) {

        filteredStudents.sort(
            (a, b) =>
                getDateValue(
                    a.created_at ||
                    a.createdAt
                ) -
                getDateValue(
                    b.created_at ||
                    b.createdAt
                )
        );

    }


    // --------------------------------------------------------
    // DATE NEWEST → OLDEST
    // --------------------------------------------------------

    else if (
        selectedSort === "dateDesc"
    ) {

        filteredStudents.sort(
            (a, b) =>
                getDateValue(
                    b.created_at ||
                    b.createdAt
                ) -
                getDateValue(
                    a.created_at ||
                    a.createdAt
                )
        );

    }


    // ========================================================
    // NO RESULTS
    // ========================================================

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


        if (recordCount) {

            recordCount.textContent =
                "0 records found";

        }

        return;

    }


    // ========================================================
    // CLEAR TABLE
    // ========================================================

    tableBody.innerHTML = "";


    // ========================================================
    // CREATE TABLE ROWS
    // ========================================================

    filteredStudents.forEach(
        (student, index) => {

            const row =
                document.createElement(
                    "tr"
                );


            // ------------------------------------------------
            // NORMALIZE DEPARTMENT
            // ------------------------------------------------

            const department =
                normalizeDepartment(
                    student.department
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
                            department
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


    // ========================================================
    // RECORD COUNT
    // ========================================================

    if (recordCount) {

        recordCount.textContent =
            `${filteredStudents.length} ${
                filteredStudents.length === 1
                    ? "record"
                    : "records"
            } found`;

    }

}


// ============================================================
// 13. DATE VALUE
// ============================================================

function getDateValue(date) {

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
// 14. DATE FORMAT
// ============================================================

function formatDate(date) {

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
// 15. GET INITIALS
// ============================================================

function getInitials(name) {

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
// 16. ESCAPE HTML
// ============================================================

function escapeHTML(value) {

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
// 17. FILTER EVENT
// ============================================================

if (departmentFilter) {

    departmentFilter.addEventListener(
        "change",
        displayStudents
    );

}


// ============================================================
// 18. SORT EVENT
// ============================================================

if (sortOption) {

    sortOption.addEventListener(
        "change",
        displayStudents
    );

}


// ============================================================
// 19. REFRESH EVENT
// ============================================================

if (refreshBtn) {

    refreshBtn.addEventListener(
        "click",
        fetchStudents
    );

}


// ============================================================
// 20. INITIAL LOAD
// ============================================================

document.addEventListener(
    "DOMContentLoaded",
    function () {

        fetchStudents();

    }
);


// ============================================================
// 21. CONSOLE
// ============================================================

console.log(
    "StudentHub Task 02 loaded."
);

console.log(
    "Database: MySQL"
);

console.log(
    "API: http://localhost:5000/api/students"
);

console.log(
    "Departments: CSE / ECE / EEE"
);

console.log(
    "Sorting: ACTIVE"
);

console.log(
    "Filtering: ACTIVE"
);


// ============================================================
// END OF TASK 02
// ============================================================