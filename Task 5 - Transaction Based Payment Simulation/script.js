// ============================================================
// STUDENTHUB — TRANSACTION-BASED PAYMENT SIMULATION
// TASK 05
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
// ACCOUNT VARIABLES
// ============================================================

let userAccount = null;
let merchantAccount = null;


// ============================================================
// PAGE LOAD
// ============================================================

document.addEventListener(
    "DOMContentLoaded",
    async function () {

        await loadAccounts();

        await loadTransactions();

    }
);


// ============================================================
// LOAD ACCOUNTS
// ============================================================

async function loadAccounts() {

    try {

        const { data, error } =
            await supabaseClient
                .from("accounts")
                .select("*")
                .order("id", {
                    ascending: true
                });


        if (error) {

            console.error(
                "Account Error:",
                error
            );

            showStatus(
                "Unable to load account information.",
                "error"
            );

            return;
        }


        if (!data || data.length < 2) {

            showStatus(
                "Please create a user and merchant account first.",
                "warning"
            );

            return;
        }


        // ----------------------------------------------------
        // FIND USER ACCOUNT
        // ----------------------------------------------------

        userAccount =
            data.find(
                account =>
                    account.account_type
                        ?.toLowerCase() === "user"
            );


        // ----------------------------------------------------
        // FIND MERCHANT ACCOUNT
        // ----------------------------------------------------

        merchantAccount =
            data.find(
                account =>
                    account.account_type
                        ?.toLowerCase() === "merchant"
            );


        // ----------------------------------------------------
        // UPDATE UI
        // ----------------------------------------------------

        updateAccountDisplay();

    }
    catch (error) {

        console.error(error);

        showStatus(
            "Unexpected error while loading accounts.",
            "error"
        );

    }

}


// ============================================================
// UPDATE ACCOUNT DISPLAY
// ============================================================

function updateAccountDisplay() {

    if (userAccount) {

        document.getElementById(
            "userName"
        ).textContent =
            userAccount.name;


        document.getElementById(
            "userBalance"
        ).textContent =
            formatCurrency(
                userAccount.balance
            );

    }


    if (merchantAccount) {

        document.getElementById(
            "merchantName"
        ).textContent =
            merchantAccount.name;


        document.getElementById(
            "merchantBalance"
        ).textContent =
            formatCurrency(
                merchantAccount.balance
            );

    }

}


// ============================================================
// PROCESS PAYMENT
// ============================================================

async function processPayment() {

    const amountInput =
        document.getElementById("amount");

    const payButton =
        document.getElementById("payButton");


    const amount =
        parseFloat(
            amountInput.value
        );


    // ========================================================
    // VALIDATE AMOUNT
    // ========================================================

    if (
        isNaN(amount) ||
        amount <= 0
    ) {

        showStatus(
            "Please enter a valid payment amount.",
            "error"
        );

        return;
    }


    // ========================================================
    // CHECK ACCOUNTS
    // ========================================================

    if (
        !userAccount ||
        !merchantAccount
    ) {

        showStatus(
            "User or merchant account is unavailable.",
            "error"
        );

        return;
    }


    // ========================================================
    // CHECK BALANCE BEFORE REQUEST
    // ========================================================

    if (
        Number(userAccount.balance) <
        amount
    ) {

        showStatus(
            "Payment failed: Insufficient balance.",
            "error"
        );

        return;
    }


    // ========================================================
    // DISABLE BUTTON
    // ========================================================

    payButton.disabled = true;

    payButton.innerHTML =
        "Processing Payment...";


    showStatus(
        "Processing secure transaction...",
        "warning"
    );


    try {

        // ====================================================
        // CALL POSTGRESQL TRANSACTION FUNCTION
        // ====================================================

        const { data, error } =
            await supabaseClient.rpc(
                "process_payment",
                {
                    p_sender_id:
                        userAccount.id,

                    p_receiver_id:
                        merchantAccount.id,

                    p_amount:
                        amount
                }
            );


        // ====================================================
        // HANDLE DATABASE ERROR
        // ====================================================

        if (error) {

            console.error(
                "Payment RPC Error:",
                error
            );

            showStatus(
                getPaymentError(error),
                "error"
            );

            return;
        }


        // ====================================================
        // SUCCESS
        // ====================================================

        if (
            data &&
            data.success
        ) {

            showStatus(
                `Payment of ${formatCurrency(amount)} successful. Transaction COMMITTED.`,
                "success"
            );


            // Clear input

            amountInput.value = "";


            // Reload latest balances

            await loadAccounts();


            // Reload transactions

            await loadTransactions();

        }

    }
    catch (error) {

        console.error(
            "Unexpected payment error:",
            error
        );


        showStatus(
            "Payment failed. Transaction ROLLED BACK.",
            "error"
        );

    }
    finally {

        // ====================================================
        // ENABLE BUTTON AGAIN
        // ====================================================

        payButton.disabled = false;

        payButton.innerHTML =
            "<span>💳</span> Process Payment";

    }

}


// ============================================================
// PAYMENT ERROR MESSAGE
// ============================================================

function getPaymentError(error) {

    const message =
        error?.message ||
        "";


    if (
        message
            .toLowerCase()
            .includes("insufficient balance")
    ) {

        return "Payment failed: Insufficient balance.";

    }


    if (
        message
            .toLowerCase()
            .includes("sender account")
    ) {

        return "Payment failed: User account not found.";

    }


    if (
        message
            .toLowerCase()
            .includes("merchant account")
    ) {

        return "Payment failed: Merchant account not found.";

    }


    return (
        "Payment failed. Transaction ROLLED BACK."
    );

}


// ============================================================
// LOAD TRANSACTION HISTORY
// ============================================================

async function loadTransactions() {

    const table =
        document.getElementById(
            "transactionTable"
        );


    try {

        const { data, error } =
            await supabaseClient
                .from("transactions")
                .select("*")
                .order(
                    "created_at",
                    {
                        ascending: false
                    }
                )
                .limit(10);


        if (error) {

            console.error(
                "Transaction Error:",
                error
            );

            table.innerHTML = `
                <tr>
                    <td
                        colspan="4"
                        class="empty-state"
                    >
                        Unable to load transactions
                    </td>
                </tr>
            `;

            return;
        }


        // ====================================================
        // NO TRANSACTIONS
        // ====================================================

        if (
            !data ||
            data.length === 0
        ) {

            table.innerHTML = `
                <tr>
                    <td
                        colspan="4"
                        class="empty-state"
                    >
                        No transactions available
                    </td>
                </tr>
            `;

            return;
        }


        // ====================================================
        // DISPLAY TRANSACTIONS
        // ====================================================

        table.innerHTML = "";


        data.forEach(
            transaction => {

                const row =
                    document.createElement(
                        "tr"
                    );


                const statusClass =
                    transaction.status
                        ?.toLowerCase() ===
                    "success"
                        ? "success"
                        : "error";


                row.innerHTML = `

                    <td>
                        #${transaction.id}
                    </td>

                    <td>
                        ${formatCurrency(
                            transaction.amount
                        )}
                    </td>

                    <td>
                        <strong>
                            ${transaction.status}
                        </strong>
                    </td>

                    <td>
                        ${formatDate(
                            transaction.created_at
                        )}
                    </td>

                `;


                table.appendChild(row);

            }
        );

    }
    catch (error) {

        console.error(error);

    }

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


    status.textContent =
        message;


    status.className =
        `status-message ${type}`;

}


// ============================================================
// FORMAT CURRENCY
// ============================================================

function formatCurrency(
    amount
) {

    return new Intl.NumberFormat(
        "en-IN",
        {
            style: "currency",
            currency: "INR",
            minimumFractionDigits: 2
        }
    ).format(
        Number(amount) || 0
    );

}


// ============================================================
// FORMAT DATE
// ============================================================

function formatDate(
    date
) {

    if (!date) {

        return "-";

    }


    return new Date(
        date
    ).toLocaleString(
        "en-IN",
        {
            dateStyle: "medium",
            timeStyle: "short"
        }
    );

}