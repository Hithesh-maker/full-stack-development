// ============================================================
// STUDENTHUB — TASK 05
// TRANSACTION-BASED PAYMENT SIMULATION
// Frontend JavaScript
// MYSQL + NODE.JS + EXPRESS
// ============================================================


// ============================================================
// 1. API CONFIGURATION
// ============================================================

const API_BASE_URL = "http://localhost:5000";

const ACCOUNTS_API_URL =
    `${API_BASE_URL}/api/accounts`;

const PAYMENT_API_URL =
    `${API_BASE_URL}/api/payment`;

const TRANSACTIONS_API_URL =
    `${API_BASE_URL}/api/transactions`;


// ============================================================
// 2. ACCOUNT VARIABLES
// ============================================================

let userAccount = null;
let merchantAccount = null;


// ============================================================
// 3. DOM ELEMENTS
// ============================================================

const userName =
    document.getElementById("userName");

const userBalance =
    document.getElementById("userBalance");

const merchantName =
    document.getElementById("merchantName");

const merchantBalance =
    document.getElementById("merchantBalance");

const amountInput =
    document.getElementById("amount");

const payButton =
    document.getElementById("payButton");

const statusMessage =
    document.getElementById("statusMessage");

const transactionTable =
    document.getElementById("transactionTable");


// ============================================================
// 4. PAGE LOAD
// ============================================================

document.addEventListener("DOMContentLoaded", async () => {

    console.log("========================================");
    console.log("StudentHub Task 05 loaded");
    console.log("API:", API_BASE_URL);
    console.log("========================================");

    await loadAccounts();
    await loadTransactions();

});


// ============================================================
// 5. LOAD ACCOUNTS
// ============================================================

async function loadAccounts() {

    try {

        console.log(
            "Loading accounts from:",
            ACCOUNTS_API_URL
        );


        const response =
            await fetch(ACCOUNTS_API_URL);


        if (!response.ok) {

            throw new Error(
                `HTTP ${response.status}`
            );

        }


        const result =
            await response.json();


        console.log(
            "Accounts response:",
            result
        );


        // ----------------------------------------------------
        // READ ACCOUNT DATA
        // ----------------------------------------------------

        let accounts = [];


        if (Array.isArray(result)) {

            accounts = result;

        }

        else if (
            result &&
            Array.isArray(result.data)
        ) {

            accounts = result.data;

        }

        else {

            throw new Error(
                result?.message ||
                "Invalid account response"
            );

        }


        // ----------------------------------------------------
        // NO ACCOUNTS
        // ----------------------------------------------------

        if (accounts.length === 0) {

            userAccount = null;
            merchantAccount = null;

            updateAccountDisplay();

            showStatus(
                "No accounts found in the database.",
                "error"
            );

            return;

        }


        // ====================================================
        // FIND USER ACCOUNT
        // ====================================================
        //
        // Your backend returns:
        //
        // Hithesh Account
        // Merchant Account
        // Test Account
        //
        // There is NO account_type field.
        //
        // Therefore we identify the user using the
        // account name.
        // ====================================================

        userAccount =
            accounts.find(account => {

                const name =
                    String(
                        account.name ||
                        account.account_name ||
                        ""
                    ).toLowerCase();

                return (
                    name.includes("hithesh") &&
                    !name.includes("merchant")
                );

            });


        // ====================================================
        // FALLBACK USER ACCOUNT
        // ====================================================
        //
        // If "Hithesh Account" does not exist,
        // select the first account that is NOT merchant.
        // ====================================================

        if (!userAccount) {

            userAccount =
                accounts.find(account => {

                    const name =
                        String(
                            account.name ||
                            account.account_name ||
                            ""
                        ).toLowerCase();

                    return !name.includes("merchant");

                });

        }


        // ====================================================
        // FIND MERCHANT ACCOUNT
        // ====================================================

        merchantAccount =
            accounts.find(account => {

                const name =
                    String(
                        account.name ||
                        account.account_name ||
                        ""
                    ).toLowerCase();

                return name.includes("merchant");

            });


        // ====================================================
        // UPDATE SCREEN
        // ====================================================

        updateAccountDisplay();


        // ====================================================
        // VALIDATE USER ACCOUNT
        // ====================================================

        if (!userAccount) {

            showStatus(
                "User account was not found.",
                "error"
            );

            return;

        }


        // ====================================================
        // VALIDATE MERCHANT ACCOUNT
        // ====================================================

        if (!merchantAccount) {

            showStatus(
                "Merchant account was not found.",
                "error"
            );

            return;

        }


        // ====================================================
        // SUCCESS
        // ====================================================

        clearStatus();


        console.log(
            "✓ User account:",
            userAccount
        );


        console.log(
            "✓ Merchant account:",
            merchantAccount
        );


    }

    catch (error) {

        console.error(
            "Account loading error:",
            error
        );


        userAccount = null;
        merchantAccount = null;


        updateAccountDisplay();


        showStatus(
            "Unable to connect to the Node.js server. Make sure it is running on port 5000.",
            "error"
        );

    }

}


// ============================================================
// 6. UPDATE ACCOUNT DISPLAY
// ============================================================

function updateAccountDisplay() {

    // --------------------------------------------------------
    // USER NAME
    // --------------------------------------------------------

    if (userName) {

        userName.textContent =
            userAccount?.name ||
            userAccount?.account_name ||
            "User Account";

    }


    // --------------------------------------------------------
    // USER BALANCE
    // --------------------------------------------------------

    if (userBalance) {

        userBalance.textContent =
            formatCurrency(
                userAccount?.balance
            );

    }


    // --------------------------------------------------------
    // MERCHANT NAME
    // --------------------------------------------------------

    if (merchantName) {

        merchantName.textContent =
            merchantAccount?.name ||
            merchantAccount?.account_name ||
            "Merchant Account";

    }


    // --------------------------------------------------------
    // MERCHANT BALANCE
    // --------------------------------------------------------

    if (merchantBalance) {

        merchantBalance.textContent =
            formatCurrency(
                merchantAccount?.balance
            );

    }

}


// ============================================================
// 7. PROCESS PAYMENT
// ============================================================

async function processPayment() {

    const amount =
        Number(amountInput?.value);


    // ========================================================
    // VALIDATE AMOUNT
    // ========================================================

    if (
        !Number.isFinite(amount) ||
        amount <= 0
    ) {

        showStatus(
            "Please enter an amount greater than ₹0.",
            "error"
        );

        return;

    }


    // ========================================================
    // CHECK USER ACCOUNT
    // ========================================================

    if (!userAccount) {

        showStatus(
            "User account is unavailable.",
            "error"
        );

        return;

    }


    // ========================================================
    // CHECK MERCHANT ACCOUNT
    // ========================================================

    if (!merchantAccount) {

        showStatus(
            "Merchant account is unavailable.",
            "error"
        );

        return;

    }


    // ========================================================
    // CHECK BALANCE
    // ========================================================

    const currentBalance =
        Number(userAccount.balance) || 0;


    if (currentBalance < amount) {

        showStatus(
            "Payment failed: Insufficient balance.",
            "error"
        );

        return;

    }


    // ========================================================
    // DISABLE PAYMENT BUTTON
    // ========================================================

    if (payButton) {

        payButton.disabled = true;

        payButton.innerHTML =
            "<span>⏳</span> Processing Payment...";

    }


    showStatus(
        "Processing secure transaction...",
        "warning"
    );


    try {

        // ====================================================
        // GET ACCOUNT IDS
        // ====================================================

        const senderId =
            userAccount.account_id ??
            userAccount.id;


        const receiverId =
            merchantAccount.account_id ??
            merchantAccount.id;


        console.log(
            "Sender ID:",
            senderId
        );


        console.log(
            "Receiver ID:",
            receiverId
        );


        console.log(
            "Payment amount:",
            amount
        );


        // ====================================================
        // SEND PAYMENT TO NODE.JS
        // ====================================================

        const response =
            await fetch(
                PAYMENT_API_URL,
                {

                    method: "POST",

                    headers: {

                        "Content-Type":
                            "application/json"

                    },

                    body: JSON.stringify({

                        senderId:
                            senderId,

                        receiverId:
                            receiverId,

                        amount:
                            amount

                    })

                }
            );


        // ====================================================
        // READ RESPONSE
        // ====================================================

        let result = {};


        try {

            result =
                await response.json();

        }

        catch {

            result = {};

        }


        console.log(
            "Payment response:",
            result
        );


        // ====================================================
        // SERVER ERROR
        // ====================================================

        if (!response.ok) {

            showStatus(
                getPaymentError(result),
                "error"
            );

            return;

        }


        // ====================================================
        // PAYMENT FAILED
        // ====================================================

        if (result.success !== true) {

            showStatus(
                result.message ||
                "Payment failed. Transaction ROLLED BACK.",
                "error"
            );

            return;

        }


        // ====================================================
        // PAYMENT SUCCESS
        // ====================================================

        showStatus(
            `Payment of ${formatCurrency(amount)} successful. Transaction COMMITTED.`,
            "success"
        );


        // ====================================================
        // CLEAR INPUT
        // ====================================================

        if (amountInput) {

            amountInput.value = "";

        }


        // ====================================================
        // REFRESH ACCOUNT BALANCES
        // ====================================================

        await loadAccounts();


        // ====================================================
        // REFRESH TRANSACTION HISTORY
        // ====================================================

        await loadTransactions();


    }

    catch (error) {

        console.error(
            "Payment error:",
            error
        );


        showStatus(
            "Unable to connect to the StudentHub server. Transaction was not completed.",
            "error"
        );

    }

    finally {

        // ====================================================
        // ENABLE BUTTON AGAIN
        // ====================================================

        if (payButton) {

            payButton.disabled = false;

            payButton.innerHTML =
                "<span>💳</span> Process Payment";

        }

    }

}


// ============================================================
// 8. PAYMENT ERROR HANDLER
// ============================================================

function getPaymentError(result) {

    const message =
        String(
            result?.message ||
            result?.error ||
            ""
        ).toLowerCase();


    // --------------------------------------------------------
    // INSUFFICIENT BALANCE
    // --------------------------------------------------------

    if (
        message.includes("insufficient") ||
        message.includes("balance")
    ) {

        return (
            "Payment failed: Insufficient balance."
        );

    }


    // --------------------------------------------------------
    // USER ACCOUNT
    // --------------------------------------------------------

    if (
        message.includes("sender") ||
        message.includes("user")
    ) {

        return (
            "Payment failed: User account not found."
        );

    }


    // --------------------------------------------------------
    // MERCHANT ACCOUNT
    // --------------------------------------------------------

    if (
        message.includes("receiver") ||
        message.includes("merchant")
    ) {

        return (
            "Payment failed: Merchant account not found."
        );

    }


    // --------------------------------------------------------
    // GENERAL SERVER MESSAGE
    // --------------------------------------------------------

    if (result?.message) {

        return result.message;

    }


    return (
        "Payment failed. Transaction ROLLED BACK."
    );

}


// ============================================================
// 9. LOAD TRANSACTION HISTORY
// ============================================================

async function loadTransactions() {

    if (!transactionTable) {

        console.error(
            "Element #transactionTable not found."
        );

        return;

    }


    try {

        console.log(
            "Loading transactions from:",
            TRANSACTIONS_API_URL
        );


        const response =
            await fetch(
                TRANSACTIONS_API_URL
            );


        if (!response.ok) {

            throw new Error(
                `HTTP ${response.status}`
            );

        }


        const result =
            await response.json();


        console.log(
            "Transactions response:",
            result
        );


        let transactions = [];


        if (Array.isArray(result)) {

            transactions = result;

        }

        else if (
            result &&
            Array.isArray(result.data)
        ) {

            transactions = result.data;

        }

        else {

            throw new Error(
                result?.message ||
                "Invalid transaction response"
            );

        }


        // ====================================================
        // NO TRANSACTIONS
        // ====================================================

        if (transactions.length === 0) {

            transactionTable.innerHTML = `

                <tr>

                    <td
                        colspan="4"
                        class="empty-state"
                    >

                        No transactions available.

                    </td>

                </tr>

            `;

            return;

        }


        // ====================================================
        // CLEAR TABLE
        // ====================================================

        transactionTable.innerHTML = "";


        // ====================================================
        // DISPLAY TRANSACTIONS
        // ====================================================

        transactions.forEach(transaction => {

            const row =
                document.createElement("tr");


            const transactionId =
                transaction.id ??
                transaction.transaction_id ??
                "-";


            const transactionAmount =
                transaction.amount ??
                transaction.payment_amount ??
                0;


            const status =
                String(
                    transaction.status ||
                    "SUCCESS"
                );


            const transactionDate =
                transaction.created_at ??
                transaction.transaction_date ??
                transaction.date;


            const statusClass =
                status.toLowerCase() === "success"
                    ? "success"
                    : "error";


            row.innerHTML = `

                <td>
                    #${escapeHTML(transactionId)}
                </td>

                <td>
                    ${formatCurrency(transactionAmount)}
                </td>

                <td>

                    <span
                        class="transaction-status ${statusClass}"
                    >

                        ${escapeHTML(status)}

                    </span>

                </td>

                <td>
                    ${formatDate(transactionDate)}
                </td>

            `;


            transactionTable.appendChild(row);

        });

    }

    catch (error) {

        console.error(
            "Transaction loading error:",
            error
        );


        transactionTable.innerHTML = `

            <tr>

                <td
                    colspan="4"
                    class="empty-state"
                >

                    Unable to load transactions.

                    <br>

                    Make sure the Node.js server is running on port 5000.

                </td>

            </tr>

        `;

    }

}


// ============================================================
// 10. STATUS MESSAGE
// ============================================================

function showStatus(message, type) {

    if (!statusMessage) {

        return;

    }


    statusMessage.textContent =
        message;


    statusMessage.className =
        `status-message ${type}`;

}


// ============================================================
// 11. CLEAR STATUS
// ============================================================

function clearStatus() {

    if (!statusMessage) {

        return;

    }


    statusMessage.textContent =
        "";


    statusMessage.className =
        "status-message";

}


// ============================================================
// 12. FORMAT CURRENCY
// ============================================================

function formatCurrency(amount) {

    const value =
        Number(amount) || 0;


    return new Intl.NumberFormat(
        "en-IN",
        {

            style: "currency",

            currency: "INR",

            minimumFractionDigits: 2,

            maximumFractionDigits: 2

        }
    ).format(value);

}


// ============================================================
// 13. FORMAT DATE
// ============================================================

function formatDate(dateValue) {

    if (!dateValue) {

        return "-";

    }


    const date =
        new Date(dateValue);


    if (
        Number.isNaN(
            date.getTime()
        )
    ) {

        return "-";

    }


    return date.toLocaleString(
        "en-IN",
        {

            dateStyle: "medium",

            timeStyle: "short"

        }
    );

}


// ============================================================
// 14. ESCAPE HTML
// ============================================================

function escapeHTML(value) {

    if (
        value === null ||
        value === undefined
    ) {

        return "";

    }


    const div =
        document.createElement("div");


    div.textContent =
        String(value);


    return div.innerHTML;

}


// ============================================================
// 15. PAYMENT BUTTON
// ============================================================

if (payButton) {

    payButton.addEventListener(
        "click",
        processPayment
    );

}


// ============================================================
// 16. ENTER KEY SUPPORT
// ============================================================

if (amountInput) {

    amountInput.addEventListener(
        "keydown",
        event => {

            if (
                event.key === "Enter" &&
                !payButton?.disabled
            ) {

                event.preventDefault();

                processPayment();

            }

        }
    );

}


// ============================================================
// END OF TASK 05
// ============================================================