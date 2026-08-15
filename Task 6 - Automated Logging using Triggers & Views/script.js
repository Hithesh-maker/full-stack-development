// ============================================================
// STUDENTHUB — TASK 07
// INTERACTIVE WEB FORM WITH EVENTS & FUNCTIONS
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
            "StudentHub Task 07 loaded."
        );

        initializeEvents();

        updateCharacterCount();

        hideConfirmation();

        await testSupabaseConnection();

    }
);


// ============================================================
// INITIALIZE EVENTS
// ============================================================

function initializeEvents() {

    const form =
        document.getElementById(
            "feedbackForm"
        );

    const submitButton =
        document.getElementById(
            "submitButton"
        );

    const resetButton =
        document.getElementById(
            "resetButton"
        );

    const nameInput =
        document.getElementById(
            "name"
        );

    const emailInput =
        document.getElementById(
            "email"
        );

    const categoryInput =
        document.getElementById(
            "category"
        );

    const ratingInput =
        document.getElementById(
            "rating"
        );

    const feedbackInput =
        document.getElementById(
            "feedback"
        );


    // ========================================================
    // FORM SUBMIT EVENT
    // ========================================================

    if (form) {

        form.addEventListener(
            "submit",
            function (event) {

                event.preventDefault();

                submitFeedback();

            }
        );

    }


    // ========================================================
    // SUBMIT BUTTON CLICK EVENT
    // ========================================================

    if (submitButton) {

        submitButton.addEventListener(
            "click",
            function () {

                if (form) {

                    form.requestSubmit();

                }
                else {

                    submitFeedback();

                }

            }
        );

    }


    // ========================================================
    // RESET BUTTON CLICK EVENT
    // ========================================================

    if (resetButton) {

        resetButton.addEventListener(
            "click",
            function () {

                resetForm();

            }
        );

    }


    // ========================================================
    // NAME EVENTS
    // ========================================================

    if (nameInput) {

        nameInput.addEventListener(
            "input",
            validateName
        );

        nameInput.addEventListener(
            "blur",
            validateName
        );

    }


    // ========================================================
    // EMAIL EVENTS
    // ========================================================

    if (emailInput) {

        emailInput.addEventListener(
            "input",
            validateEmail
        );

        emailInput.addEventListener(
            "blur",
            validateEmail
        );

    }


    // ========================================================
    // CATEGORY EVENT
    // ========================================================

    if (categoryInput) {

        categoryInput.addEventListener(
            "change",
            validateCategory
        );

    }


    // ========================================================
    // RATING EVENT
    // ========================================================

    if (ratingInput) {

        ratingInput.addEventListener(
            "change",
            validateRating
        );

    }


    // ========================================================
    // FEEDBACK EVENTS
    // ========================================================

    if (feedbackInput) {

        feedbackInput.addEventListener(
            "input",
            function () {

                validateFeedback();

                updateCharacterCount();

            }
        );

        feedbackInput.addEventListener(
            "blur",
            validateFeedback
        );

        feedbackInput.addEventListener(
            "keydown",
            function (event) {

                console.log(
                    "Keyboard event:",
                    event.key
                );

            }
        );

    }


    // ========================================================
    // MOUSE EVENTS
    // ========================================================

    const inputs =
        document.querySelectorAll(
            "#feedbackForm input, #feedbackForm select, #feedbackForm textarea"
        );

    inputs.forEach(
        function (input) {

            input.addEventListener(
                "mouseenter",
                function () {

                    input.classList.add(
                        "field-hover"
                    );

                }
            );

            input.addEventListener(
                "mouseleave",
                function () {

                    input.classList.remove(
                        "field-hover"
                    );

                }
            );

        }
    );

}


// ============================================================
// SUBMIT FEEDBACK
// ============================================================

async function submitFeedback() {

    const submitButton =
        document.getElementById(
            "submitButton"
        );


    // ========================================================
    // PREVENT DOUBLE SUBMISSION
    // ========================================================

    if (
        submitButton &&
        submitButton.disabled
    ) {

        return;

    }


    console.log(
        "Validating feedback form..."
    );


    // ========================================================
    // VALIDATE ALL FIELDS
    // ========================================================

    const nameValid =
        validateName();

    const emailValid =
        validateEmail();

    const categoryValid =
        validateCategory();

    const ratingValid =
        validateRating();

    const feedbackValid =
        validateFeedback();


    if (
        !nameValid ||
        !emailValid ||
        !categoryValid ||
        !ratingValid ||
        !feedbackValid
    ) {

        showStatus(
            "Please correct the highlighted fields before submitting.",
            "error"
        );

        return;

    }


    // ========================================================
    // GET FORM VALUES
    // ========================================================

    const name =
        document
            .getElementById("name")
            .value
            .trim();

    const email =
        document
            .getElementById("email")
            .value
            .trim();

    const category =
        document
            .getElementById("category")
            .value;

    const rating =
        Number(
            document
                .getElementById("rating")
                .value
        );

    const feedback =
        document
            .getElementById("feedback")
            .value
            .trim();


    // ========================================================
    // DISABLE SUBMIT BUTTON
    // ========================================================

    if (submitButton) {

        submitButton.disabled = true;

        submitButton.textContent =
            "Submitting...";

    }


    try {

        console.log(
            "Sending feedback to Supabase..."
        );


        // ====================================================
        // INSERT INTO SUPABASE
        // ====================================================
        //
        // IMPORTANT:
        // Database column is "message"
        // NOT "feedback"
        //
        // ====================================================

        const {
            data,
            error
        } =
            await supabaseClient
                .from("feedback")
                .insert([
                    {
                        name: name,
                        email: email,
                        category: category,
                        rating: rating,

                        // FIXED DATABASE COLUMN
                        message: feedback
                    }
                ])
                .select();


        // ====================================================
        // HANDLE DATABASE ERROR
        // ====================================================

        if (error) {

            console.error(
                "Supabase feedback error:",
                error
            );

            showStatus(
                `Submission failed: ${error.message}`,
                "error"
            );

            return;

        }


        // ====================================================
        // SUCCESS
        // ====================================================

        console.log(
            "Feedback submitted successfully:",
            data
        );


        showStatus(
            "Feedback submitted successfully!",
            "success"
        );


        // ====================================================
        // SHOW CONFIRMATION
        // ====================================================

        showConfirmation();


        // ====================================================
        // RESET FORM AFTER SUCCESS
        // ====================================================

        setTimeout(
            function () {

                resetForm(false);

            },
            1500
        );

    }
    catch (error) {

        console.error(
            "Unexpected submission error:",
            error
        );

        showStatus(
            `Unexpected error: ${error.message}`,
            "error"
        );

    }
    finally {

        // ====================================================
        // RESTORE BUTTON
        // ====================================================

        if (submitButton) {

            submitButton.disabled = false;

            submitButton.textContent =
                "✓ Submit Feedback";

        }

    }

}


// ============================================================
// VALIDATE NAME
// ============================================================

function validateName() {

    const input =
        document.getElementById(
            "name"
        );

    const message =
        document.getElementById(
            "nameMessage"
        );


    if (!input || !message) {

        return false;

    }


    const value =
        input.value.trim();


    if (!value) {

        setFieldError(
            input,
            message,
            "Full name is required."
        );

        return false;

    }


    if (value.length < 3) {

        setFieldError(
            input,
            message,
            "Name must contain at least 3 characters."
        );

        return false;

    }


    if (!/^[a-zA-Z\s.'-]+$/.test(value)) {

        setFieldError(
            input,
            message,
            "Name contains invalid characters."
        );

        return false;

    }


    setFieldSuccess(
        input,
        message,
        "✓ Name looks good."
    );

    return true;

}


// ============================================================
// VALIDATE EMAIL
// ============================================================

function validateEmail() {

    const input =
        document.getElementById(
            "email"
        );

    const message =
        document.getElementById(
            "emailMessage"
        );


    if (!input || !message) {

        return false;

    }


    const value =
        input.value.trim();


    if (!value) {

        setFieldError(
            input,
            message,
            "Email address is required."
        );

        return false;

    }


    const emailPattern =
        /^[^\s@]+@[^\s@]+\.[^\s@]+$/;


    if (!emailPattern.test(value)) {

        setFieldError(
            input,
            message,
            "Please enter a valid email address."
        );

        return false;

    }


    setFieldSuccess(
        input,
        message,
        "✓ Valid email address."
    );

    return true;

}


// ============================================================
// VALIDATE CATEGORY
// ============================================================

function validateCategory() {

    const input =
        document.getElementById(
            "category"
        );

    const message =
        document.getElementById(
            "categoryMessage"
        );


    if (!input || !message) {

        return false;

    }


    if (!input.value) {

        setFieldError(
            input,
            message,
            "Please select a feedback category."
        );

        return false;

    }


    setFieldSuccess(
        input,
        message,
        "✓ Category selected."
    );

    return true;

}


// ============================================================
// VALIDATE RATING
// ============================================================

function validateRating() {

    const input =
        document.getElementById(
            "rating"
        );

    const message =
        document.getElementById(
            "ratingMessage"
        );


    if (!input || !message) {

        return false;

    }


    if (!input.value) {

        setFieldError(
            input,
            message,
            "Please select a rating."
        );

        return false;

    }


    const rating =
        Number(input.value);


    if (
        !Number.isInteger(rating) ||
        rating < 1 ||
        rating > 5
    ) {

        setFieldError(
            input,
            message,
            "Rating must be between 1 and 5."
        );

        return false;

    }


    setFieldSuccess(
        input,
        message,
        "✓ Rating selected."
    );

    return true;

}


// ============================================================
// VALIDATE FEEDBACK
// ============================================================

function validateFeedback() {

    const input =
        document.getElementById(
            "feedback"
        );

    const message =
        document.getElementById(
            "feedbackMessage"
        );


    if (!input || !message) {

        return false;

    }


    const value =
        input.value.trim();


    if (!value) {

        setFieldError(
            input,
            message,
            "Please enter your feedback."
        );

        return false;

    }


    if (value.length < 10) {

        setFieldError(
            input,
            message,
            "Feedback must contain at least 10 characters."
        );

        return false;

    }


    if (value.length > 250) {

        setFieldError(
            input,
            message,
            "Feedback cannot exceed 250 characters."
        );

        return false;

    }


    setFieldSuccess(
        input,
        message,
        "✓ Feedback is valid."
    );

    return true;

}


// ============================================================
// SET FIELD ERROR
// ============================================================

function setFieldError(
    input,
    message,
    text
) {

    input.classList.remove(
        "input-valid"
    );

    input.classList.add(
        "input-invalid"
    );

    message.textContent =
        text;

    message.className =
        "field-message error";

}


// ============================================================
// SET FIELD SUCCESS
// ============================================================

function setFieldSuccess(
    input,
    message,
    text
) {

    input.classList.remove(
        "input-invalid"
    );

    input.classList.add(
        "input-valid"
    );

    message.textContent =
        text;

    message.className =
        "field-message success";

}


// ============================================================
// CHARACTER COUNTER
// ============================================================

function updateCharacterCount() {

    const feedback =
        document.getElementById(
            "feedback"
        );

    const counter =
        document.getElementById(
            "characterCount"
        );


    if (!feedback || !counter) {

        return;

    }


    const length =
        feedback.value.length;


    counter.textContent =
        `${length} / 250`;


    // ========================================================
    // NEAR LIMIT
    // ========================================================

    if (length >= 225) {

        counter.classList.add(
            "near-limit"
        );

    }
    else {

        counter.classList.remove(
            "near-limit"
        );

    }


    // ========================================================
    // LIMIT REACHED
    // ========================================================

    if (length >= 250) {

        counter.classList.add(
            "limit-reached"
        );

    }
    else {

        counter.classList.remove(
            "limit-reached"
        );

    }

}


// ============================================================
// RESET FORM
// ============================================================

function resetForm(
    showMessage = true
) {

    console.log(
        "Resetting feedback form..."
    );


    const form =
        document.getElementById(
            "feedbackForm"
        );


    if (form) {

        form.reset();

    }


    // ========================================================
    // CLEAR VALIDATION STATES
    // ========================================================

    const fields =
        document.querySelectorAll(
            "#feedbackForm input, #feedbackForm select, #feedbackForm textarea"
        );


    fields.forEach(
        function (field) {

            field.classList.remove(
                "input-valid",
                "input-invalid",
                "field-hover"
            );

        }
    );


    // ========================================================
    // CLEAR FIELD MESSAGES
    // ========================================================

    const messages =
        document.querySelectorAll(
            "#feedbackForm .field-message"
        );


    messages.forEach(
        function (message) {

            message.textContent =
                "";

            message.className =
                "field-message";

        }
    );


    // ========================================================
    // RESET CHARACTER COUNTER
    // ========================================================

    updateCharacterCount();


    // ========================================================
    // HIDE CONFIRMATION
    // ========================================================

    hideConfirmation();


    // ========================================================
    // SHOW RESET MESSAGE
    // ========================================================

    if (showMessage) {

        showStatus(
            "Form has been reset successfully.",
            "success"
        );

    }


    console.log(
        "Form reset complete."
    );

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

        console.error(
            "statusMessage element not found."
        );

        return;

    }


    status.textContent =
        message;


    status.className =
        `status-message ${type}`;


    status.classList.remove(
        "hidden"
    );


    // ========================================================
    // CLEAR PREVIOUS TIMER
    // ========================================================

    clearTimeout(
        window.statusTimeout
    );


    window.statusTimeout =
        setTimeout(
            function () {

                status.classList.add(
                    "hidden"
                );

            },
            5000
        );

}


// ============================================================
// SHOW CONFIRMATION
// ============================================================

function showConfirmation() {

    const section =
        document.getElementById(
            "confirmationSection"
        );


    if (!section) {

        return;

    }


    section.classList.remove(
        "hidden"
    );


    section.scrollIntoView({
        behavior: "smooth",
        block: "center"
    });

}


// ============================================================
// HIDE CONFIRMATION
// ============================================================

function hideConfirmation() {

    const section =
        document.getElementById(
            "confirmationSection"
        );


    if (!section) {

        return;

    }


    section.classList.add(
        "hidden"
    );

}


// ============================================================
// TEST SUPABASE CONNECTION
// ============================================================

async function testSupabaseConnection() {

    console.log(
        "Testing Supabase connection..."
    );


    try {

        const {
            data,
            error
        } =
            await supabaseClient
                .from("feedback")
                .select("id")
                .limit(1);


        if (error) {

            console.error(
                "Supabase connection failed:",
                error
            );

            showStatus(
                `Database connection failed: ${error.message}`,
                "error"
            );

            return false;

        }


        console.log(
            "Supabase connection successful."
        );

        console.log(
            "Feedback table is accessible."
        );


        return true;

    }
    catch (error) {

        console.error(
            "Connection exception:",
            error
        );

        showStatus(
            "Unable to connect to the database.",
            "error"
        );

        return false;

    }

}