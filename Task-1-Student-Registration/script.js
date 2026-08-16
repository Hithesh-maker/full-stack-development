// ============================================================
// STUDENTHUB — TASK 01
// STUDENT REGISTRATION & DATA STORAGE
// ============================================================
// Frontend : HTML + CSS + JavaScript
// Backend  : Node.js + Express
// Database : MySQL
// ============================================================


// ============================================================
// 1. API CONFIGURATION
// ============================================================

const API_BASE_URL = "http://localhost:5000";

const STUDENTS_API =
    `${API_BASE_URL}/api/students`;


// ============================================================
// 2. DOM ELEMENTS
// ============================================================

const form =
    document.getElementById("studentForm");

const message =
    document.getElementById("message");

const submitBtn =
    document.getElementById("submitBtn");

const nameInput =
    document.getElementById("name");

const emailInput =
    document.getElementById("email");

const passwordInput =
    document.getElementById("password");

const dobInput =
    document.getElementById("dob");

const departmentInput =
    document.getElementById("department");

const phoneInput =
    document.getElementById("phone");

const yearInput =
    document.getElementById("year");


// ============================================================
// 3. CHECK REQUIRED ELEMENTS
// ============================================================

if (!form) {
    console.error(
        "StudentHub: studentForm not found."
    );
}

if (!submitBtn) {
    console.error(
        "StudentHub: submitBtn not found."
    );
}

if (!nameInput) {
    console.error(
        "StudentHub: name input not found."
    );
}

if (!emailInput) {
    console.error(
        "StudentHub: email input not found."
    );
}

if (!passwordInput) {
    console.error(
        "StudentHub: password input not found."
    );
}

if (!dobInput) {
    console.error(
        "StudentHub: dob input not found."
    );
}

if (!departmentInput) {
    console.error(
        "StudentHub: department input not found."
    );
}

if (!phoneInput) {
    console.error(
        "StudentHub: phone input not found."
    );
}

if (!yearInput) {
    console.error(
        "StudentHub: year input not found."
    );
}


// ============================================================
// 4. SET MAXIMUM DATE FOR DOB
// ============================================================

function setDobMaximumDate() {

    if (!dobInput) {
        return;
    }

    const today =
        new Date()
            .toISOString()
            .split("T")[0];

    dobInput.max = today;
}

setDobMaximumDate();


// ============================================================
// 5. FORM SUBMISSION
// ============================================================

if (form) {

    form.addEventListener(
        "submit",
        handleRegistration
    );

}


// ============================================================
// 6. HANDLE STUDENT REGISTRATION
// ============================================================

async function handleRegistration(event) {

    event.preventDefault();


    // --------------------------------------------------------
    // GET FORM VALUES
    // --------------------------------------------------------

    const name =
        nameInput.value.trim();

    const email =
        emailInput.value
            .trim()
            .toLowerCase();

    const password =
        passwordInput.value;

    const dob =
        dobInput.value;

    const department =
        departmentInput.value.trim();

    const phone =
        phoneInput.value.trim();

    const year =
        yearInput.value;


    // --------------------------------------------------------
    // CLEAR OLD MESSAGE
    // --------------------------------------------------------

    clearMessage();


    // --------------------------------------------------------
    // VALIDATE DATA
    // --------------------------------------------------------

    const validationError =
        validateStudentData(
            name,
            email,
            password,
            dob,
            department,
            phone,
            year
        );


    if (validationError) {

        showMessage(
            validationError,
            "error"
        );

        return;
    }


    // --------------------------------------------------------
    // LOADING STATE
    // --------------------------------------------------------

    setLoadingState(true);


    try {

        console.log(
            "StudentHub: Sending registration request..."
        );


        // ----------------------------------------------------
        // DATA BEING SENT
        // ----------------------------------------------------

        const studentData = {

            name: name,

            email: email,

            password: password,

            dob: dob,

            department: department,

            phone: phone,

            year: Number(year)

        };


        console.log(
            "StudentHub Request:",
            {
                ...studentData,
                password: "********"
            }
        );


        // ====================================================
        // SEND REQUEST TO NODE.JS
        // ====================================================

        const response =
            await fetch(
                STUDENTS_API,
                {

                    method: "POST",

                    headers: {

                        "Content-Type":
                            "application/json"

                    },

                    body:
                        JSON.stringify(
                            studentData
                        )

                }
            );


        // ====================================================
        // READ SERVER RESPONSE
        // ====================================================

        const responseText =
            await response.text();

        let result;


        try {

            result =
                responseText
                    ? JSON.parse(responseText)
                    : {};

        }

        catch (jsonError) {

            console.error(
                "StudentHub: Invalid JSON response.",
                jsonError
            );

            console.error(
                "Raw server response:",
                responseText
            );


            showMessage(
                `Server returned an invalid response (${response.status}). Check the Node.js terminal.`,
                "error"
            );

            return;
        }


        console.log(
            "StudentHub API Response:",
            result
        );


        // ====================================================
        // HANDLE API ERROR
        // ====================================================

        if (
            !response.ok ||
            result.success === false
        ) {

            handleDatabaseError(
                result,
                response.status
            );

            return;
        }


        // ====================================================
        // SUCCESS
        // ====================================================

        console.log(
            "✓ Student saved successfully."
        );


        showMessage(
            `Student "${name}" registered successfully.`,
            "success"
        );


        // ====================================================
        // RESET FORM
        // ====================================================

        form.reset();

        setDobMaximumDate();


    }

    catch (error) {

        // ====================================================
        // NETWORK / SERVER CONNECTION ERROR
        // ====================================================

        console.error(
            "StudentHub connection error:",
            error
        );


        if (
            error instanceof TypeError
        ) {

            showMessage(
                "Unable to connect to StudentHub. Make sure Node.js is running on http://localhost:5000.",
                "error"
            );

        }

        else {

            showMessage(
                error.message ||
                "Registration failed. Please check the Node.js server.",
                "error"
            );

        }

    }

    finally {

        setLoadingState(false);

    }

}


// ============================================================
// 7. VALIDATE STUDENT DATA
// ============================================================

function validateStudentData(
    name,
    email,
    password,
    dob,
    department,
    phone,
    year
) {


    // --------------------------------------------------------
    // REQUIRED FIELDS
    // --------------------------------------------------------

    if (
        !name ||
        !email ||
        !password ||
        !dob ||
        !department ||
        !phone ||
        !year
    ) {

        return (
            "Please complete all required fields."
        );

    }


    // --------------------------------------------------------
    // NAME VALIDATION
    // --------------------------------------------------------

    const namePattern =
        /^[A-Za-z\s.'-]{2,100}$/;


    if (
        !namePattern.test(name)
    ) {

        return (
            "Please enter a valid full name."
        );

    }


    // --------------------------------------------------------
    // EMAIL VALIDATION
    // --------------------------------------------------------

    const emailPattern =
        /^[^\s@]+@[^\s@]+\.[^\s@]+$/;


    if (
        !emailPattern.test(email)
    ) {

        return (
            "Please enter a valid email address."
        );

    }


    // --------------------------------------------------------
    // PASSWORD VALIDATION
    // --------------------------------------------------------

    if (
        password.length < 6
    ) {

        return (
            "Password must contain at least 6 characters."
        );

    }


    if (
        password.length > 255
    ) {

        return (
            "Password cannot exceed 255 characters."
        );

    }


    // --------------------------------------------------------
    // DATE OF BIRTH VALIDATION
    // --------------------------------------------------------

    const selectedDate =
        new Date(`${dob}T00:00:00`);

    const today =
        new Date();

    today.setHours(
        0,
        0,
        0,
        0
    );


    if (
        Number.isNaN(
            selectedDate.getTime()
        )
    ) {

        return (
            "Please select a valid date of birth."
        );

    }


    if (
        selectedDate > today
    ) {

        return (
            "Date of birth cannot be in the future."
        );

    }


    // --------------------------------------------------------
    // DEPARTMENT VALIDATION
    // --------------------------------------------------------

    if (
        !department
    ) {

        return (
            "Please select a department."
        );

    }


    // --------------------------------------------------------
    // PHONE VALIDATION
    // --------------------------------------------------------

    const phonePattern =
        /^[0-9]{10}$/;


    if (
        !phonePattern.test(phone)
    ) {

        return (
            "Please enter a valid 10-digit phone number."
        );

    }


    // --------------------------------------------------------
    // YEAR VALIDATION
    // --------------------------------------------------------

    const studentYear =
        Number(year);


    if (
        !Number.isInteger(studentYear) ||
        studentYear < 1 ||
        studentYear > 4
    ) {

        return (
            "Please select a valid academic year."
        );

    }


    // --------------------------------------------------------
    // VALIDATION SUCCESS
    // --------------------------------------------------------

    return null;

}


// ============================================================
// 8. HANDLE DATABASE / API ERRORS
// ============================================================

function handleDatabaseError(
    error,
    statusCode
) {

    console.error(
        "StudentHub API Error:",
        error
    );


    const errorMessage =
        String(
            error?.message ||
            error?.error ||
            error?.sqlMessage ||
            ""
        );


    const lowerMessage =
        errorMessage.toLowerCase();


    // --------------------------------------------------------
    // DUPLICATE EMAIL
    // --------------------------------------------------------

    if (
        lowerMessage.includes("duplicate") ||
        lowerMessage.includes("already registered") ||
        lowerMessage.includes("unique")
    ) {

        showMessage(
            "This email is already registered.",
            "error"
        );

        return;
    }


    // --------------------------------------------------------
    // DATABASE COLUMN / STRUCTURE ERROR
    // --------------------------------------------------------

    if (
        lowerMessage.includes("unknown column") ||
        lowerMessage.includes("column") ||
        lowerMessage.includes("doesn't exist") ||
        lowerMessage.includes("not null")
    ) {

        showMessage(
            `Database structure problem: ${errorMessage}`,
            "error"
        );

        return;
    }


    // --------------------------------------------------------
    // PASSWORD ERROR
    // --------------------------------------------------------

    if (
        lowerMessage.includes("password")
    ) {

        showMessage(
            `Password/database problem: ${errorMessage}`,
            "error"
        );

        return;
    }


    // --------------------------------------------------------
    // DATABASE CONNECTION ERROR
    // --------------------------------------------------------

    if (
        lowerMessage.includes("database") ||
        lowerMessage.includes("mysql") ||
        lowerMessage.includes("connection") ||
        lowerMessage.includes("connect")
    ) {

        showMessage(
            `MySQL error: ${errorMessage}`,
            "error"
        );

        return;
    }


    // --------------------------------------------------------
    // VALIDATION ERROR
    // --------------------------------------------------------

    if (
        statusCode === 400
    ) {

        showMessage(
            errorMessage ||
            "Invalid student data.",
            "error"
        );

        return;
    }


    // --------------------------------------------------------
    // SERVER ERROR
    // --------------------------------------------------------

    if (
        statusCode >= 500
    ) {

        showMessage(
            errorMessage ||
            "Server error. Check the Node.js terminal.",
            "error"
        );

        return;
    }


    // --------------------------------------------------------
    // DEFAULT ERROR
    // --------------------------------------------------------

    showMessage(
        errorMessage ||
        "Registration failed. Please check the Node.js server.",
        "error"
    );

}


// ============================================================
// 9. BUTTON LOADING STATE
// ============================================================

function setLoadingState(
    isLoading
) {

    if (!submitBtn) {
        return;
    }


    submitBtn.disabled =
        isLoading;


    const buttonText =
        submitBtn.querySelector(
            "span:first-child"
        );


    if (!buttonText) {
        return;
    }


    if (isLoading) {

        buttonText.textContent =
            "Registering...";

        submitBtn.classList.add(
            "loading"
        );

    }

    else {

        buttonText.textContent =
            "Register Student";

        submitBtn.classList.remove(
            "loading"
        );

    }

}


// ============================================================
// 10. SHOW MESSAGE
// ============================================================

function showMessage(
    text,
    type
) {

    if (!message) {
        return;
    }


    message.textContent =
        text;


    message.className =
        `message ${type}`;


    message.setAttribute(
        "role",
        type === "error"
            ? "alert"
            : "status"
    );


    if (
        showMessage.timeout
    ) {

        clearTimeout(
            showMessage.timeout
        );

    }


    showMessage.timeout =
        setTimeout(
            clearMessage,
            8000
        );

}


// ============================================================
// 11. CLEAR MESSAGE
// ============================================================

function clearMessage() {

    if (!message) {
        return;
    }


    if (
        showMessage.timeout
    ) {

        clearTimeout(
            showMessage.timeout
        );

        showMessage.timeout =
            null;

    }


    message.textContent =
        "";

    message.className =
        "message";

}


// ============================================================
// 12. PHONE INPUT CLEANUP
// ============================================================

if (phoneInput) {

    phoneInput.addEventListener(
        "input",
        function () {

            this.value =
                this.value.replace(
                    /\D/g,
                    ""
                );


            if (
                this.value.length > 10
            ) {

                this.value =
                    this.value.slice(
                        0,
                        10
                    );

            }

        }
    );

}


// ============================================================
// 13. NAME INPUT CLEANUP
// ============================================================

if (nameInput) {

    nameInput.addEventListener(
        "input",
        function () {

            // Remove leading spaces
            this.value =
                this.value.replace(
                    /^\s+/,
                    ""
                );

        }
    );

}


// ============================================================
// 14. EMAIL INPUT CLEANUP
// ============================================================

if (emailInput) {

    emailInput.addEventListener(
        "input",
        function () {

            this.value =
                this.value.toLowerCase();

        }
    );

}


// ============================================================
// 15. PASSWORD INPUT CLEANUP
// ============================================================

if (passwordInput) {

    passwordInput.addEventListener(
        "input",
        function () {

            // Remove spaces from beginning
            // but allow spaces inside password.

            this.value =
                this.value.replace(
                    /^\s+/,
                    ""
                );

        }
    );

}


// ============================================================
// 16. YEAR INPUT VALIDATION
// ============================================================

if (yearInput) {

    yearInput.addEventListener(
        "change",
        function () {

            const year =
                Number(this.value);


            if (
                year < 1 ||
                year > 4
            ) {

                this.value =
                    "";

            }

        }
    );

}


// ============================================================
// 17. PREVENT DOUBLE SUBMISSION
// ============================================================

let registrationInProgress =
    false;


if (form) {

    form.addEventListener(
        "submit",
        function (event) {

            if (
                registrationInProgress
            ) {

                event.preventDefault();

                return;

            }

            registrationInProgress =
                true;


            setTimeout(
                function () {

                    registrationInProgress =
                        false;

                },
                3000
            );

        },
        true
    );

}


// ============================================================
// 18. STARTUP MESSAGE
// ============================================================

console.log(
    "=========================================="
);

console.log(
    "✓ StudentHub Task 01 loaded"
);

console.log(
    "✓ Frontend: HTML + CSS + JavaScript"
);

console.log(
    "✓ Backend: Node.js + Express"
);

console.log(
    "✓ Database: MySQL"
);

console.log(
    "✓ Email field: ACTIVE"
);

console.log(
    "✓ Password field: ACTIVE"
);

console.log(
    "✓ Date of birth: ACTIVE"
);

console.log(
    "✓ Phone field: ACTIVE"
);

console.log(
    "✓ Academic year: ACTIVE"
);

console.log(
    "✓ API:",
    STUDENTS_API
);

console.log(
    "=========================================="
);


// ============================================================
// TASK 01 COMPLETE
// ============================================================