// ============================================================
// STUDENTHUB — TASK 3
// LOGIN SYSTEM WITH VALIDATION
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

const loginForm =
    document.getElementById("loginForm");

const emailInput =
    document.getElementById("email");

const passwordInput =
    document.getElementById("password");

const emailError =
    document.getElementById("emailError");

const passwordError =
    document.getElementById("passwordError");

const loginMessage =
    document.getElementById("loginMessage");

const loginButton =
    document.getElementById("loginButton");

const buttonText =
    document.getElementById("buttonText");

const togglePassword =
    document.getElementById("togglePassword");


// ============================================================
// SHOW / HIDE PASSWORD
// ============================================================

togglePassword.addEventListener("click", () => {

    if (passwordInput.type === "password") {

        passwordInput.type = "text";

        togglePassword.textContent = "Hide";

        togglePassword.setAttribute(
            "aria-label",
            "Hide password"
        );

    } else {

        passwordInput.type = "password";

        togglePassword.textContent = "Show";

        togglePassword.setAttribute(
            "aria-label",
            "Show password"
        );
    }

});


// ============================================================
// EMAIL VALIDATION
// ============================================================

function isValidEmail(email) {

    const emailPattern =
        /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

    return emailPattern.test(email);
}


// ============================================================
// CLEAR ERRORS
// ============================================================

function clearErrors() {

    emailError.textContent = "";

    passwordError.textContent = "";

    emailInput.classList.remove("input-error");

    passwordInput.classList.remove("input-error");

    loginMessage.textContent = "";

    loginMessage.className = "message";
}


// ============================================================
// SHOW MESSAGE
// ============================================================

function showMessage(message, type) {

    loginMessage.textContent = message;

    loginMessage.className =
        `message ${type}`;
}


// ============================================================
// VALIDATE FORM
// ============================================================

function validateForm() {

    let valid = true;

    const email =
        emailInput.value.trim();

    const password =
        passwordInput.value.trim();


    // --------------------------------------------------------
    // EMAIL VALIDATION
    // --------------------------------------------------------

    if (email === "") {

        emailError.textContent =
            "Please enter your email address.";

        emailInput.classList.add(
            "input-error"
        );

        valid = false;

    } else if (!isValidEmail(email)) {

        emailError.textContent =
            "Please enter a valid email address.";

        emailInput.classList.add(
            "input-error"
        );

        valid = false;
    }


    // --------------------------------------------------------
    // PASSWORD VALIDATION
    // --------------------------------------------------------

    if (password === "") {

        passwordError.textContent =
            "Please enter your password.";

        passwordInput.classList.add(
            "input-error"
        );

        valid = false;

    } else if (password.length < 6) {

        passwordError.textContent =
            "Password must contain at least 6 characters.";

        passwordInput.classList.add(
            "input-error"
        );

        valid = false;
    }


    return valid;
}


// ============================================================
// REMOVE ERROR WHILE TYPING
// ============================================================

emailInput.addEventListener("input", () => {

    emailError.textContent = "";

    emailInput.classList.remove(
        "input-error"
    );

});


passwordInput.addEventListener("input", () => {

    passwordError.textContent = "";

    passwordInput.classList.remove(
        "input-error"
    );

});


// ============================================================
// LOGIN FORM SUBMISSION
// ============================================================

loginForm.addEventListener(
    "submit",
    async (event) => {

        event.preventDefault();

        clearErrors();


        // ----------------------------------------------------
        // STEP 1: JAVASCRIPT VALIDATION
        // ----------------------------------------------------

        if (!validateForm()) {

            return;
        }


        const email =
            emailInput.value.trim();

        const password =
            passwordInput.value.trim();


        // ----------------------------------------------------
        // LOADING STATE
        // ----------------------------------------------------

        loginButton.disabled = true;

        buttonText.textContent =
            "Checking...";


        try {

            // ------------------------------------------------
            // STEP 2: CHECK DATABASE CREDENTIALS
            // ------------------------------------------------

            const { data, error } =
                await supabaseClient
                    .from("students")
                    .select(
                        "id, name, email, department"
                    )
                    .eq("email", email)
                    .eq("password", password)
                    .maybeSingle();


            // ------------------------------------------------
            // DATABASE ERROR
            // ------------------------------------------------

            if (error) {

                console.error(
                    "Supabase error:",
                    error
                );

                showMessage(
                    "Unable to connect to the database. Please try again.",
                    "error"
                );

                return;
            }


            // ------------------------------------------------
            // INVALID CREDENTIALS
            // ------------------------------------------------

            if (!data) {

                showMessage(
                    "Invalid email or password.",
                    "error"
                );

                return;
            }


            // ------------------------------------------------
            // LOGIN SUCCESS
            // ------------------------------------------------

            showMessage(
                `Login successful! Welcome, ${data.name}.`,
                "success"
            );


            // ------------------------------------------------
            // STORE BASIC SESSION INFORMATION
            // ------------------------------------------------

            sessionStorage.setItem(
                "studentName",
                data.name
            );

            sessionStorage.setItem(
                "studentEmail",
                data.email
            );

            sessionStorage.setItem(
                "studentDepartment",
                data.department || ""
            );


            // ------------------------------------------------
            // CLEAR PASSWORD FIELD
            // ------------------------------------------------

            passwordInput.value = "";


        } catch (error) {

            console.error(
                "Login error:",
                error
            );

            showMessage(
                "Something went wrong. Please try again.",
                "error"
            );

        } finally {

            loginButton.disabled = false;

            buttonText.textContent =
                "Login";
        }

    }
);