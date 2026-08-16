// ============================================================
// STUDENTHUB — TASK 07
// INTERACTIVE WEB FORM WITH EVENTS & FUNCTIONS
// MySQL + Node.js + Express
// ============================================================


// ============================================================
// 1. API CONFIGURATION
// ============================================================

const API_BASE_URL = "http://localhost:5000/api";


// ============================================================
// 2. GET FORM ELEMENTS
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
// 3. DATABASE DISPLAY ELEMENTS
// ============================================================

const totalFeedbackElement =
    document.getElementById("totalFeedback");

const averageRatingElement =
    document.getElementById("averageRating");

const feedbackTableBody =
    document.getElementById("feedbackTableBody");


// ============================================================
// 4. PAGE LOAD
// ============================================================

document.addEventListener(
    "DOMContentLoaded",
    function () {

        updateCharacterCount();

        setupInputEvents();

        setupMouseEvents();

        setupSubmitEvent();

        setupResetEvent();

        setupKeyboardEvents();

        loadFeedback();

    }
);


// ============================================================
// 5. INPUT EVENTS
// ============================================================

function setupInputEvents() {

    if (nameInput) {

        nameInput.addEventListener(
            "input",
            function () {

                validateName();

            }
        );

    }


    if (emailInput) {

        emailInput.addEventListener(
            "input",
            function () {

                validateEmail();

            }
        );

    }


    if (categoryInput) {

        categoryInput.addEventListener(
            "change",
            function () {

                validateCategory();

            }
        );

    }


    if (ratingInput) {

        ratingInput.addEventListener(
            "change",
            function () {

                validateRating();

            }
        );

    }


    if (feedbackInput) {

        feedbackInput.addEventListener(
            "input",
            function () {

                validateFeedback();

                updateCharacterCount();

            }
        );

    }

}


// ============================================================
// 6. MOUSE EVENTS
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

            if (!field) {
                return;
            }


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
// 7. NAME VALIDATION
// ============================================================

function validateName() {

    if (!nameInput) {
        return false;
    }


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
// 8. EMAIL VALIDATION
// ============================================================

function validateEmail() {

    if (!emailInput) {
        return false;
    }


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
// 9. CATEGORY VALIDATION
// ============================================================

function validateCategory() {

    if (!categoryInput) {
        return false;
    }


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
// 10. RATING VALIDATION
// ============================================================

function validateRating() {

    if (!ratingInput) {
        return false;
    }


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


    const rating =
        Number(value);


    if (
        rating < 1 ||
        rating > 5
    ) {

        setInvalid(
            ratingInput,
            message,
            "Rating must be between 1 and 5."
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
// 11. FEEDBACK VALIDATION
// ============================================================

function validateFeedback() {

    if (!feedbackInput) {
        return false;
    }


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


    if (value.length > 250) {

        setInvalid(
            feedbackInput,
            message,
            "Feedback cannot exceed 250 characters."
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
// 12. SET VALID STATE
// ============================================================

function setValid(
    field,
    messageElement,
    message
) {

    if (!field) {
        return;
    }


    field.classList.remove(
        "input-invalid"
    );

    field.classList.add(
        "input-valid"
    );


    if (messageElement) {

        messageElement.textContent =
            message;

        messageElement.className =
            "field-message success";

    }

}


// ============================================================
// 13. SET INVALID STATE
// ============================================================

function setInvalid(
    field,
    messageElement,
    message
) {

    if (!field) {
        return;
    }


    field.classList.remove(
        "input-valid"
    );

    field.classList.add(
        "input-invalid"
    );


    if (messageElement) {

        messageElement.textContent =
            message;

        messageElement.className =
            "field-message error";

    }

}


// ============================================================
// 14. COMPLETE FORM VALIDATION
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
// 15. DOUBLE-CLICK SUBMIT EVENT
// ============================================================

function setupSubmitEvent() {

    if (!submitButton) {
        return;
    }


    submitButton.addEventListener(
        "dblclick",
        function (event) {

            event.preventDefault();

            handleSubmit();

        }
    );

}


// ============================================================
// 16. HANDLE SUBMIT
// ============================================================

async function handleSubmit() {

    const isValid =
        validateForm();


    if (!isValid) {

        showStatus(
            "Please correct the highlighted fields before submitting.",
            "error"
        );

        return;

    }


    if (!submitButton) {
        return;
    }


    submitButton.disabled = true;

    submitButton.textContent =
        "Submitting...";


    try {

        const feedbackData = {

            name:
                nameInput.value.trim(),

            email:
                emailInput.value
                    .trim()
                    .toLowerCase(),

            category:
                categoryInput.value,

            rating:
                Number(
                    ratingInput.value
                ),

            message:
                feedbackInput.value.trim()

        };


        console.log(
            "Sending feedback:",
            feedbackData
        );


        const response =
            await fetch(
                `${API_BASE_URL}/feedback`,
                {

                    method: "POST",

                    headers: {

                        "Content-Type":
                            "application/json"

                    },

                    body:
                        JSON.stringify(
                            feedbackData
                        )

                }
            );


        let result;

        try {

            result =
                await response.json();

        }

        catch {

            result = {};

        }


        if (!response.ok) {

            throw new Error(
                result.message ||
                `Server returned status ${response.status}`
            );

        }


        // ----------------------------------------------------
        // SUCCESS
        // ----------------------------------------------------

        showStatus(
            "Feedback submitted successfully!",
            "success"
        );


        if (confirmationSection) {

            confirmationSection.classList.remove(
                "hidden"
            );

        }


        console.log(
            "Feedback stored in MySQL:",
            result
        );


        // ----------------------------------------------------
        // REFRESH DATABASE DATA
        // ----------------------------------------------------

        await loadFeedback();


        if (confirmationSection) {

            confirmationSection.scrollIntoView({

                behavior: "smooth",

                block: "center"

            });

        }

    }


    catch (error) {

        console.error(
            "Feedback submission error:",
            error
        );


        showStatus(
            error.message ||
            "Unable to submit feedback. Make sure the Node.js server is running on port 5000.",
            "error"
        );

    }


    finally {

        submitButton.disabled = false;

        submitButton.textContent =
            "Submit Feedback";

    }

}


// ============================================================
// 17. LOAD FEEDBACK FROM MYSQL
// ============================================================

async function loadFeedback() {

    try {

        console.log(
            "Loading feedback from MySQL..."
        );


        const response =
            await fetch(
                `${API_BASE_URL}/feedback`
            );


        let result;

        try {

            result =
                await response.json();

        }

        catch {

            result = {};

        }


        if (!response.ok) {

            throw new Error(
                result.message ||
                `Server returned status ${response.status}`
            );

        }


        const feedback =
            Array.isArray(result.data)
                ? result.data
                : [];


        // ----------------------------------------------------
        // UPDATE STATISTICS
        // ----------------------------------------------------

        updateStatistics(
            feedback
        );


        // ----------------------------------------------------
        // UPDATE TABLE
        // ----------------------------------------------------

        displayFeedback(
            feedback
        );


        console.log(
            `Loaded ${feedback.length} feedback records`
        );

    }


    catch (error) {

        console.error(
            "Feedback loading error:",
            error
        );


        if (feedbackTableBody) {

            feedbackTableBody.innerHTML = `

                <tr>

                    <td colspan="6">

                        Unable to load feedback.
                        Make sure the Node.js server is running.

                    </td>

                </tr>

            `;

        }

    }

}


// ============================================================
// 18. UPDATE STATISTICS
// ============================================================

function updateStatistics(
    feedback
) {

    const total =
        feedback.length;


    let average =
        0;


    if (total > 0) {

        const totalRating =
            feedback.reduce(
                function (sum, item) {

                    return (
                        sum +
                        Number(
                            item.rating || 0
                        )
                    );

                },
                0
            );


        average =
            totalRating / total;

    }


    if (totalFeedbackElement) {

        totalFeedbackElement.textContent =
            total;

    }


    if (averageRatingElement) {

        averageRatingElement.textContent =
            average.toFixed(1);

    }

}


// ============================================================
// 19. DISPLAY FEEDBACK TABLE
// ============================================================

function displayFeedback(
    feedback
) {

    if (!feedbackTableBody) {
        return;
    }


    if (
        !Array.isArray(feedback) ||
        feedback.length === 0
    ) {

        feedbackTableBody.innerHTML = `

            <tr>

                <td colspan="6">

                    No feedback records found.

                </td>

            </tr>

        `;

        return;

    }


    feedbackTableBody.innerHTML =
        feedback.map(
            function (item) {

                const date =
                    formatDate(
                        item.submitted_at ||
                        item.created_at ||
                        item.feedback_date
                    );


                const id =
                    item.feedback_id ||
                    item.id ||
                    "";


                const name =
                    item.name ||
                    "";


                const category =
                    item.category ||
                    "";


                const rating =
                    item.rating ||
                    "";


                const message =
                    item.message ||
                    item.feedback ||
                    "";


                return `

                    <tr>

                        <td>
                            ${escapeHTML(id)}
                        </td>

                        <td>
                            ${escapeHTML(name)}
                        </td>

                        <td>
                            ${escapeHTML(category)}
                        </td>

                        <td>
                            ⭐ ${escapeHTML(rating)}
                        </td>

                        <td>
                            ${escapeHTML(message)}
                        </td>

                        <td>
                            ${escapeHTML(date)}
                        </td>

                    </tr>

                `;

            }
        ).join("");

}


// ============================================================
// 20. FORMAT DATE
// ============================================================

function formatDate(
    dateValue
) {

    if (!dateValue) {

        return "—";

    }


    const date =
        new Date(dateValue);


    if (
        Number.isNaN(
            date.getTime()
        )
    ) {

        return String(
            dateValue
        );

    }


    return date.toLocaleString(
        "en-IN",
        {

            day: "2-digit",

            month: "short",

            year: "numeric",

            hour: "2-digit",

            minute: "2-digit"

        }
    );

}


// ============================================================
// 21. HTML SECURITY FUNCTION
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
// 22. RESET EVENT
// ============================================================

function setupResetEvent() {

    if (!resetButton) {
        return;
    }


    resetButton.addEventListener(
        "click",
        function (event) {

            event.preventDefault();

            resetForm();

        }
    );

}


// ============================================================
// 23. RESET FORM
// ============================================================

function resetForm() {

    if (feedbackForm) {

        feedbackForm.reset();

    }


    const fields = [

        nameInput,
        emailInput,
        categoryInput,
        ratingInput,
        feedbackInput

    ];


    fields.forEach(
        function (field) {

            if (!field) {
                return;
            }


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


            if (element) {

                element.textContent =
                    "";

                element.className =
                    "field-message";

            }

        }
    );


    updateCharacterCount();


    if (confirmationSection) {

        confirmationSection.classList.add(
            "hidden"
        );

    }


    showStatus(
        "Form has been reset.",
        "warning"
    );

}


// ============================================================
// 24. CHARACTER COUNTER
// ============================================================

function updateCharacterCount() {

    if (
        !feedbackInput ||
        !characterCount
    ) {

        return;

    }


    const length =
        feedbackInput.value.length;


    const maxLength =
        feedbackInput.maxLength || 250;


    characterCount.textContent =
        `${length} / ${maxLength}`;


    if (
        length >= maxLength * 0.9
    ) {

        characterCount.style.fontWeight =
            "800";

    }

    else {

        characterCount.style.fontWeight =
            "600";

    }

}


// ============================================================
// 25. SHOW STATUS
// ============================================================

function showStatus(
    message,
    type
) {

    if (!statusMessage) {
        return;
    }


    statusMessage.textContent =
        message;


    statusMessage.className =
        `status-message ${type}`;


    statusMessage.classList.remove(
        "hidden"
    );


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
// 26. KEYBOARD EVENTS
// ============================================================

function setupKeyboardEvents() {

    document.addEventListener(
        "keydown",
        function (event) {

            // Ctrl + Enter submits the form

            if (
                event.key === "Enter" &&
                event.ctrlKey
            ) {

                event.preventDefault();

                handleSubmit();

            }

        }
    );

}


// ============================================================
// 27. PREVENT NORMAL FORM SUBMISSION
// ============================================================

if (feedbackForm) {

    feedbackForm.addEventListener(
        "submit",
        function (event) {

            event.preventDefault();

            showStatus(
                "Please double-click the Submit Feedback button.",
                "warning"
            );

        }
    );

}


// ============================================================
// 28. CONSOLE INFORMATION
// ============================================================

console.log(
    "=========================================="
);

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
    "Ctrl + Enter submit event: ACTIVE"
);

console.log(
    "Reusable validation functions: ACTIVE"
);

console.log(
    "Character counter: ACTIVE"
);

console.log(
    "Form reset event: ACTIVE"
);

console.log(
    "MySQL API integration: ACTIVE"
);

console.log(
    "Feedback loading: ACTIVE"
);

console.log(
    "Statistics: ACTIVE"
);

console.log(
    "HTML security escaping: ACTIVE"
);

console.log(
    "=========================================="
);