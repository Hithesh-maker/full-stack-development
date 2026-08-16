// ============================================================
// STUDENTHUB — TASK 03
// LOGIN SYSTEM WITH VALIDATION
// Node.js + Express + MySQL
// ============================================================


// ============================================================
// 1. API CONFIGURATION
// ============================================================

const LOGIN_API_URL = "http://localhost:5000/api/login";


// ============================================================
// 2. DOM ELEMENTS
// ============================================================

const loginForm = document.getElementById("loginForm");

const emailInput = document.getElementById("email");

const passwordInput = document.getElementById("password");

const emailError = document.getElementById("emailError");

const passwordError = document.getElementById("passwordError");

const loginMessage = document.getElementById("loginMessage");

const loginButton = document.getElementById("loginButton");

const buttonText = document.getElementById("buttonText");

const togglePassword = document.getElementById("togglePassword");


// ============================================================
// 3. SHOW / HIDE PASSWORD
// ============================================================

if (togglePassword && passwordInput) {

    togglePassword.addEventListener("click", function () {

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

}


// ============================================================
// 4. EMAIL VALIDATION
// ============================================================

function isValidEmail(email) {

    const emailPattern =
        /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

    return emailPattern.test(email);

}


// ============================================================
// 5. CLEAR FORM ERRORS
// ============================================================

function clearErrors() {

    if (emailError) {

        emailError.textContent = "";

    }

    if (passwordError) {

        passwordError.textContent = "";

    }

    if (emailInput) {

        emailInput.classList.remove("input-error");

    }

    if (passwordInput) {

        passwordInput.classList.remove("input-error");

    }

    clearMessage();

}


// ============================================================
// 6. CLEAR LOGIN MESSAGE
// ============================================================

function clearMessage() {

    if (!loginMessage) {

        return;

    }

    loginMessage.textContent = "";

    loginMessage.className = "message";

}


// ============================================================
// 7. SHOW LOGIN MESSAGE
// ============================================================

function showMessage(message, type) {

    if (!loginMessage) {

        return;

    }

    loginMessage.textContent = message;

    loginMessage.className = "message";

    loginMessage.classList.add(type);

}


// ============================================================
// 8. VALIDATE FORM
// ============================================================

function validateForm() {

    let valid = true;

    const email =
        emailInput.value
            .trim()
            .toLowerCase();

    const password =
        passwordInput.value;


    // ========================================================
    // EMAIL VALIDATION
    // ========================================================

    if (email === "") {

        emailError.textContent =
            "Please enter your email address.";

        emailInput.classList.add(
            "input-error"
        );

        valid = false;

    }

    else if (!isValidEmail(email)) {

        emailError.textContent =
            "Please enter a valid email address.";

        emailInput.classList.add(
            "input-error"
        );

        valid = false;

    }


    // ========================================================
    // PASSWORD VALIDATION
    // ========================================================

    if (password === "") {

        passwordError.textContent =
            "Please enter your password.";

        passwordInput.classList.add(
            "input-error"
        );

        valid = false;

    }

    else if (password.length < 6) {

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
// 9. CLEAR EMAIL ERROR WHILE TYPING
// ============================================================

if (emailInput) {

    emailInput.addEventListener(
        "input",
        function () {

            if (emailError) {

                emailError.textContent = "";

            }

            emailInput.classList.remove(
                "input-error"
            );

            clearMessage();

        }
    );

}


// ============================================================
// 10. CLEAR PASSWORD ERROR WHILE TYPING
// ============================================================

if (passwordInput) {

    passwordInput.addEventListener(
        "input",
        function () {

            if (passwordError) {

                passwordError.textContent = "";

            }

            passwordInput.classList.remove(
                "input-error"
            );

            clearMessage();

        }
    );

}


// ============================================================
// 11. LOGIN FORM SUBMISSION
// ============================================================

if (loginForm) {

    loginForm.addEventListener(
        "submit",
        async function (event) {

            event.preventDefault();


            // ==================================================
            // STEP 1 — CLEAR PREVIOUS ERRORS
            // ==================================================

            clearErrors();


            // ==================================================
            // STEP 2 — VALIDATE FORM
            // ==================================================

            if (!validateForm()) {

                return;

            }


            // ==================================================
            // STEP 3 — GET LOGIN VALUES
            // ==================================================

            const email =
                emailInput.value
                    .trim()
                    .toLowerCase();

            const password =
                passwordInput.value;


            // ==================================================
            // STEP 4 — LOADING STATE
            // ==================================================

            loginButton.disabled = true;

            buttonText.textContent =
                "Checking...";

            showMessage(
                "Verifying your credentials...",
                "loading"
            );


            try {


                // ==================================================
                // STEP 5 — SEND REQUEST TO NODE.JS
                // ==================================================

                const response =
                    await fetch(
                        LOGIN_API_URL,
                        {
                            method: "POST",

                            headers: {
                                "Content-Type":
                                    "application/json"
                            },

                            body: JSON.stringify({

                                email: email,

                                password: password

                            })

                        }
                    );


                // ==================================================
                // STEP 6 — READ SERVER RESPONSE
                // ==================================================

                let result;

                try {

                    result =
                        await response.json();

                }

                catch {

                    throw new Error(
                        "Server returned an invalid response."
                    );

                }


                console.log(
                    "StudentHub Login Response:",
                    result
                );


                // ==================================================
                // STEP 7 — SERVER ERROR
                // ==================================================

                if (!response.ok) {

                    showMessage(
                        result.message ||
                        "Invalid email or password.",
                        "error"
                    );

                    return;

                }


                // ==================================================
                // STEP 8 — CHECK LOGIN SUCCESS
                // ==================================================

                if (
                    result.success !== true ||
                    !result.data
                ) {

                    showMessage(
                        result.message ||
                        "Invalid email or password.",
                        "error"
                    );

                    return;

                }


                // ==================================================
                // STEP 9 — GET USER DATA
                // ==================================================

                const user =
                    result.data;


                const username =
                    user.username ||
                    user.name ||
                    user.email ||
                    email;


                // ==================================================
                // STEP 10 — SUCCESS MESSAGE
                // ==================================================

                showMessage(
                    `Login successful! Welcome, ${username}.`,
                    "success"
                );


                console.log(
                    "LOGIN SUCCESS:",
                    user
                );


                // ==================================================
                // STEP 11 — STORE SESSION INFORMATION
                // ==================================================

                sessionStorage.setItem(
                    "userId",
                    user.id || ""
                );

                sessionStorage.setItem(
                    "username",
                    username
                );

                sessionStorage.setItem(
                    "email",
                    user.email ||
                    email
                );

                sessionStorage.setItem(
                    "department",
                    user.department || ""
                );

                sessionStorage.setItem(
                    "loggedIn",
                    "true"
                );


                // ==================================================
                // STEP 12 — CLEAR PASSWORD
                // ==================================================

                passwordInput.value = "";


                // ==================================================
                // STEP 13 — SUCCESS BUTTON STATE
                // ==================================================

                buttonText.textContent =
                    "Login Successful";


            }

            catch (error) {


                // ==================================================
                // STEP 14 — CONNECTION ERROR
                // ==================================================

                console.error(
                    "StudentHub Login Error:",
                    error
                );


                showMessage(
                    "Unable to connect to the StudentHub server. Make sure Node.js is running on port 5000.",
                    "error"
                );

            }


            finally {


                // ==================================================
                // STEP 15 — RESTORE BUTTON
                // ==================================================

                setTimeout(
                    function () {

                        loginButton.disabled =
                            false;

                        buttonText.textContent =
                            "Login";

                    },
                    1500
                );

            }

        }
    );

}


// ============================================================
// 16. PAGE LOAD
// ============================================================

console.log(
    "StudentHub Task 03 Login System loaded."
);

console.log(
    "Login API:",
    LOGIN_API_URL
);

console.log(
    "Authentication: Node.js + Express + MySQL"
);