// ============================================================
// STUDENTHUB — TASK 07
// INTERACTIVE WEB FORM WITH EVENTS & FUNCTIONS
// ============================================================


// ============================================================
// GET ELEMENTS
// ============================================================

const feedbackForm =
    document.getElementById("feedbackForm");

const nameInput =
    document.getElementById("name");

const emailInput =
    document.getElementById("email");

const categoryInput =
    document.getElementById("category");

const ratingInput =
    document.getElementById("rating");

const feedbackInput =
    document.getElementById("feedback");

const submitButton =
    document.getElementById("submitButton");

const resetButton =
    document.getElementById("resetButton");

const statusMessage =
    document.getElementById("statusMessage");

const confirmationSection =
    document.getElementById("confirmationSection");

const characterCount =
    document.getElementById("characterCount");


// ============================================================
// PAGE LOAD
// ============================================================

document.addEventListener(
    "DOMContentLoaded",
    function () {

        updateCharacterCount();

        setupInputEvents();

        setupMouseEvents();

        setupSubmitEvent();

        setupResetEvent();

    }
);


// ============================================================
// SETUP INPUT EVENTS
// ============================================================

function setupInputEvents() {

    nameInput.addEventListener(
        "input",
        function () {

            validateName();

        }
    );


    emailInput.addEventListener(
        "input",
        function () {

            validateEmail();

        }
    );


    categoryInput.addEventListener(
        "change",
        function () {

            validateCategory();

        }
    );


    ratingInput.addEventListener(
        "change",
        function () {

            validateRating();

        }
    );


    feedbackInput.addEventListener(
        "input",
        function () {

            validateFeedback();

            updateCharacterCount();

        }
    );

}


// ============================================================
// SETUP MOUSE EVENTS
// ============================================================

function setupMouseEvents() {

    const fields = [
        nameInput,
        emailInput,
        categoryInput,
        ratingInput,
        feedbackInput
    ];


    fields.forEach(
        function (field) {

            field.addEventListener(
                "mouseenter",
                function () {

                    field.classList.add(
                        "hover-active"
                    );

                }
            );


            field.addEventListener(
                "mouseleave",
                function () {

                    field.classList.remove(
                        "hover-active"
                    );

                }
            );

        }
    );

}


// ============================================================
// NAME VALIDATION
// ============================================================

function validateName() {

    const value =
        nameInput.value.trim();

    const message =
        document.getElementById(
            "nameMessage"
        );


    if (value.length === 0) {

        setInvalid(
            nameInput,
            message,
            "Name is required."
        );

        return false;

    }


    if (value.length < 3) {

        setInvalid(
            nameInput,
            message,
            "Name must contain at least 3 characters."
        );

        return false;

    }


    setValid(
        nameInput,
        message,
        "Name looks good."
    );

    return true;

}


// ============================================================
// EMAIL VALIDATION
// ============================================================

function validateEmail() {

    const value =
        emailInput.value.trim();

    const message =
        document.getElementById(
            "emailMessage"
        );


    const emailPattern =
        /^[^\s@]+@[^\s@]+\.[^\s@]+$/;


    if (value.length === 0) {

        setInvalid(
            emailInput,
            message,
            "Email address is required."
        );

        return false;

    }


    if (!emailPattern.test(value)) {

        setInvalid(
            emailInput,
            message,
            "Please enter a valid email address."
        );

        return false;

    }


    setValid(
        emailInput,
        message,
        "Valid email address."
    );

    return true;

}


// ============================================================
// CATEGORY VALIDATION
// ============================================================

function validateCategory() {

    const value =
        categoryInput.value;

    const message =
        document.getElementById(
            "categoryMessage"
        );


    if (!value) {

        setInvalid(
            categoryInput,
            message,
            "Please select a feedback category."
        );

        return false;

    }


    setValid(
        categoryInput,
        message,
        "Category selected."
    );

    return true;

}


// ============================================================
// RATING VALIDATION
// ============================================================

function validateRating() {

    const value =
        ratingInput.value;

    const message =
        document.getElementById(
            "ratingMessage"
        );


    if (!value) {

        setInvalid(
            ratingInput,
            message,
            "Please select a rating."
        );

        return false;

    }


    setValid(
        ratingInput,
        message,
        "Rating selected."
    );

    return true;

}


// ============================================================
// FEEDBACK VALIDATION
// ============================================================

function validateFeedback() {

    const value =
        feedbackInput.value.trim();

    const message =
        document.getElementById(
            "feedbackMessage"
        );


    if (value.length === 0) {

        setInvalid(
            feedbackInput,
            message,
            "Feedback is required."
        );

        return false;

    }


    if (value.length < 10) {

        setInvalid(
            feedbackInput,
            message,
            "Feedback must contain at least 10 characters."
        );

        return false;

    }


    setValid(
        feedbackInput,
        message,
        "Feedback looks good."
    );

    return true;

}


// ============================================================
// SET VALID STATE
// ============================================================

function setValid(
    field,
    messageElement,
    message
) {

    field.classList.remove(
        "input-invalid"
    );

    field.classList.add(
        "input-valid"
    );


    messageElement.textContent =
        message;

    messageElement.className =
        "field-message success";

}


// ============================================================
// SET INVALID STATE
// ============================================================

function setInvalid(
    field,
    messageElement,
    message
) {

    field.classList.remove(
        "input-valid"
    );

    field.classList.add(
        "input-invalid"
    );


    messageElement.textContent =
        message;

    messageElement.className =
        "field-message error";

}


// ============================================================
// VALIDATE COMPLETE FORM
// ============================================================

function validateForm() {

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


    return (
        nameValid &&
        emailValid &&
        categoryValid &&
        ratingValid &&
        feedbackValid
    );

}


// ============================================================
// SETUP DOUBLE CLICK SUBMIT
// ============================================================

function setupSubmitEvent() {

    submitButton.addEventListener(
        "dblclick",
        function () {

            handleSubmit();

        }
    );

}


// ============================================================
// HANDLE SUBMIT
// ============================================================

function handleSubmit() {

    const isValid =
        validateForm();


    if (!isValid) {

        showStatus(
            "Please correct the highlighted fields before submitting.",
            "error"
        );

        return;

    }


    showStatus(
        "Feedback submitted successfully!",
        "success"
    );


    confirmationSection.classList.remove(
        "hidden"
    );


    confirmationSection.scrollIntoView({
        behavior: "smooth",
        block: "center"
    });

}


// ============================================================
// RESET EVENT
// ============================================================

function setupResetEvent() {

    resetButton.addEventListener(
        "click",
        function () {

            resetForm();

        }
    );

}


// ============================================================
// RESET FORM
// ============================================================

function resetForm() {

    feedbackForm.reset();


    const fields = [
        nameInput,
        emailInput,
        categoryInput,
        ratingInput,
        feedbackInput
    ];


    fields.forEach(
        function (field) {

            field.classList.remove(
                "input-valid",
                "input-invalid"
            );

        }
    );


    const messages = [
        "nameMessage",
        "emailMessage",
        "categoryMessage",
        "ratingMessage",
        "feedbackMessage"
    ];


    messages.forEach(
        function (id) {

            const element =
                document.getElementById(id);

            element.textContent = "";

            element.className =
                "field-message";

        }
    );


    updateCharacterCount();


    confirmationSection.classList.add(
        "hidden"
    );


    showStatus(
        "Form has been reset.",
        "warning"
    );

}


// ============================================================
// CHARACTER COUNTER
// ============================================================

function updateCharacterCount() {

    const length =
        feedbackInput.value.length;

    const maxLength =
        feedbackInput.maxLength;


    characterCount.textContent =
        `${length} / ${maxLength}`;


    if (length >= maxLength * 0.9) {

        characterCount.style.fontWeight =
            "800";

    }
    else {

        characterCount.style.fontWeight =
            "600";

    }

}


// ============================================================
// SHOW STATUS
// ============================================================

function showStatus(
    message,
    type
) {

    statusMessage.textContent =
        message;


    statusMessage.className =
        `status-message ${type}`;


    setTimeout(
        function () {

            statusMessage.classList.add(
                "hidden"
            );

        },
        5000
    );

}


// ============================================================
// KEYBOARD EVENT
// ============================================================

document.addEventListener(
    "keydown",
    function (event) {

        /*
         * Pressing Enter while inside the form
         * does not submit automatically.
         *
         * This demonstrates keyboard event handling.
         */

        if (
            event.key === "Enter" &&
            event.ctrlKey
        ) {

            event.preventDefault();

            handleSubmit();

        }

    }
);


// ============================================================
// CONSOLE INFORMATION
// ============================================================

console.log(
    "StudentHub Task 07 loaded successfully."
);

console.log(
    "Input events: ACTIVE"
);

console.log(
    "Mouse hover events: ACTIVE"
);

console.log(
    "Double-click submit event: ACTIVE"
);

console.log(
    "Reusable validation functions: ACTIVE"
);