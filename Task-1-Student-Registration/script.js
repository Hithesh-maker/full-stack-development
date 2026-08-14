// ============================================================
// STUDENTHUB — STUDENT REGISTRATION
// Task 01: Student Registration & Data Storage
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
// DOM ELEMENTS
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

const dobInput =
    document.getElementById("dob");

const departmentInput =
    document.getElementById("department");

const phoneInput =
    document.getElementById("phone");


// ============================================================
// APPLICATION CHECK
// ============================================================

if (
    !form ||
    !message ||
    !submitBtn ||
    !nameInput ||
    !emailInput ||
    !dobInput ||
    !departmentInput ||
    !phoneInput
) {

    console.error(
        "StudentHub: Required form elements were not found."
    );

}


// ============================================================
// PREVENT FUTURE DATE SELECTION
// ============================================================

if (dobInput) {

    const today =
        new Date()
            .toISOString()
            .split("T")[0];

    dobInput.max = today;

}


// ============================================================
// FORM SUBMISSION
// ============================================================

if (form) {

    form.addEventListener(
        "submit",
        handleRegistration
    );

}


// ============================================================
// REGISTRATION FUNCTION
// ============================================================

async function handleRegistration(event) {

    event.preventDefault();


    // --------------------------------------------------------
    // GET VALUES
    // --------------------------------------------------------

    const name =
        nameInput.value.trim();

    const email =
        emailInput.value
            .trim()
            .toLowerCase();

    const dob =
        dobInput.value;

    const department =
        departmentInput.value;

    const phone =
        phoneInput.value.trim();


    // --------------------------------------------------------
    // CLEAR PREVIOUS MESSAGE
    // --------------------------------------------------------

    clearMessage();


    // --------------------------------------------------------
    // VALIDATE FORM
    // --------------------------------------------------------

    const validationError =
        validateStudentData(
            name,
            email,
            dob,
            department,
            phone
        );


    if (validationError) {

        showMessage(
            validationError,
            "error"
        );

        return;

    }


    // --------------------------------------------------------
    // DISABLE SUBMIT BUTTON
    // --------------------------------------------------------

    setLoadingState(true);


    try {


        // ====================================================
        // INSERT INTO SUPABASE
        // ====================================================
        //
        // IMPORTANT:
        // We intentionally DO NOT use .select()
        //
        // This keeps the public browser operation
        // INSERT-only for privacy.
        //
        // ====================================================

        const { error } =
            await supabaseClient
                .from("students")
                .insert([
                    {
                        name: name,
                        email: email,
                        dob: dob,
                        department: department,
                        phone: phone
                    }
                ]);


        // ----------------------------------------------------
        // DATABASE ERROR
        // ----------------------------------------------------

        if (error) {

            console.error(
                "StudentHub Supabase Error:",
                error
            );


            handleDatabaseError(error);

            return;

        }


        // ----------------------------------------------------
        // SUCCESS
        // ----------------------------------------------------

        console.log(
            "Student registration successful."
        );


        showMessage(
            `Student "${name}" registered successfully.`,
            "success"
        );


        // ----------------------------------------------------
        // RESET FORM
        // ----------------------------------------------------

        form.reset();


        // Restore today's maximum DOB
        if (dobInput) {

            dobInput.max =
                new Date()
                    .toISOString()
                    .split("T")[0];

        }


    } catch (error) {


        // ----------------------------------------------------
        // UNEXPECTED ERROR
        // ----------------------------------------------------

        console.error(
            "StudentHub Unexpected Error:",
            error
        );


        showMessage(
            "Something went wrong. Please try again.",
            "error"
        );


    } finally {


        // ----------------------------------------------------
        // ENABLE BUTTON
        // ----------------------------------------------------

        setLoadingState(false);

    }

}


// ============================================================
// VALIDATION
// ============================================================

function validateStudentData(
    name,
    email,
    dob,
    department,
    phone
) {


    // --------------------------------------------------------
    // REQUIRED FIELDS
    // --------------------------------------------------------

    if (
        !name ||
        !email ||
        !dob ||
        !department ||
        !phone
    ) {

        return (
            "Please complete all required fields."
        );

    }


    // --------------------------------------------------------
    // NAME
    // --------------------------------------------------------

    const namePattern =
        /^[A-Za-z\s.'-]{2,100}$/;


    if (!namePattern.test(name)) {

        return (
            "Please enter a valid full name."
        );

    }


    // --------------------------------------------------------
    // EMAIL
    // --------------------------------------------------------

    const emailPattern =
        /^[^\s@]+@[^\s@]+\.[^\s@]+$/;


    if (!emailPattern.test(email)) {

        return (
            "Please enter a valid email address."
        );

    }


    // --------------------------------------------------------
    // DATE OF BIRTH
    // --------------------------------------------------------

    const selectedDate =
        new Date(dob);

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


    if (selectedDate > today) {

        return (
            "Date of birth cannot be in the future."
        );

    }


    // --------------------------------------------------------
    // DEPARTMENT
    // --------------------------------------------------------

    if (!department) {

        return (
            "Please select a department."
        );

    }


    // --------------------------------------------------------
    // PHONE
    // --------------------------------------------------------

    const phonePattern =
        /^[0-9]{10}$/;


    if (!phonePattern.test(phone)) {

        return (
            "Please enter a valid 10-digit phone number."
        );

    }


    return null;

}


// ============================================================
// DATABASE ERROR HANDLER
// ============================================================

function handleDatabaseError(error) {

    const errorMessage =
        (error.message || "")
            .toLowerCase();


    // --------------------------------------------------------
    // DUPLICATE RECORD
    // --------------------------------------------------------

    if (
        error.code === "23505" ||
        errorMessage.includes("duplicate") ||
        errorMessage.includes("unique")
    ) {

        showMessage(
            "This email is already registered.",
            "error"
        );

        return;

    }


    // --------------------------------------------------------
    // RLS ERROR
    // --------------------------------------------------------

    if (
        errorMessage.includes(
            "row-level security"
        )
    ) {

        showMessage(
            "Registration was blocked by the database security policy.",
            "error"
        );

        return;

    }


    // --------------------------------------------------------
    // PERMISSION ERROR
    // --------------------------------------------------------

    if (
        error.code === "42501" ||
        errorMessage.includes(
            "permission denied"
        )
    ) {

        showMessage(
            "Database permission denied. Please check the security policy.",
            "error"
        );

        return;

    }


    // --------------------------------------------------------
    // NETWORK / CONNECTION ERROR
    // --------------------------------------------------------

    if (
        errorMessage.includes(
            "network"
        ) ||
        errorMessage.includes(
            "fetch"
        )
    ) {

        showMessage(
            "Unable to connect to the database. Please check your internet connection.",
            "error"
        );

        return;

    }


    // --------------------------------------------------------
    // DEFAULT DATABASE ERROR
    // --------------------------------------------------------

    showMessage(
        "Registration failed. Please try again.",
        "error"
    );

}


// ============================================================
// BUTTON LOADING STATE
// ============================================================

function setLoadingState(isLoading) {

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

    } else {

        buttonText.textContent =
            "Register Student";

        submitBtn.classList.remove(
            "loading"
        );

    }

}


// ============================================================
// SHOW MESSAGE
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


    // Accessibility
    message.setAttribute(
        "role",
        type === "error"
            ? "alert"
            : "status"
    );


    // --------------------------------------------------------
    // AUTO HIDE
    // --------------------------------------------------------

    clearMessage.timeout =
        setTimeout(
            clearMessage,
            6000
        );

}


// ============================================================
// CLEAR MESSAGE
// ============================================================

function clearMessage() {

    if (!message) {
        return;
    }


    if (clearMessage.timeout) {

        clearTimeout(
            clearMessage.timeout
        );

    }


    message.textContent =
        "";

    message.className =
        "message";

}


// ============================================================
// PHONE INPUT CLEANUP
// ============================================================

if (phoneInput) {

    phoneInput.addEventListener(
        "input",
        function () {

            // Keep only numbers
            this.value =
                this.value.replace(
                    /\D/g,
                    ""
                );

            // Maximum 10 digits
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
// NAME INPUT CLEANUP
// ============================================================

if (nameInput) {

    nameInput.addEventListener(
        "input",
        function () {

            // Prevent accidental leading spaces
            this.value =
                this.value.replace(
                    /^\s+/,
                    ""
                );

        }
    );

}


// ============================================================
// EMAIL INPUT CLEANUP
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
// END OF STUDENTHUB SCRIPT
// ============================================================