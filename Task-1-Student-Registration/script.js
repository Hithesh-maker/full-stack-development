const form = document.getElementById("studentForm");
const message = document.getElementById("message");
const submitBtn = document.getElementById("submitBtn");


form.addEventListener("submit", function (event) {

    event.preventDefault();

    const name =
        document.getElementById("name").value.trim();

    const email =
        document.getElementById("email").value.trim();

    const dob =
        document.getElementById("dob").value;

    const department =
        document.getElementById("department").value;

    const phone =
        document.getElementById("phone").value.trim();


    // Phone validation

    if (!/^[0-9]{10}$/.test(phone)) {

        showMessage(
            "Please enter a valid 10-digit phone number.",
            "error"
        );

        return;
    }


    // Basic validation

    if (
        !name ||
        !email ||
        !dob ||
        !department ||
        !phone
    ) {

        showMessage(
            "Please fill in all required fields.",
            "error"
        );

        return;
    }


    // Temporary success message

    showMessage(
        `Student "${name}" is ready to be registered.`,
        "success"
    );

});


function showMessage(text, type) {

    message.textContent = text;

    message.className =
        `message ${type}`;
}