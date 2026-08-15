// ============================================================
// STUDENTHUB — TASK 4
// ORDER MANAGEMENT USING JOINS
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

const orderTableBody =
    document.getElementById("orderTableBody");

const loading =
    document.getElementById("loading");

const errorMessage =
    document.getElementById("errorMessage");

const sortOrders =
    document.getElementById("sortOrders");

const highestOrderAmount =
    document.getElementById("highestOrderAmount");

const highestOrderProduct =
    document.getElementById("highestOrderProduct");

const activeCustomer =
    document.getElementById("activeCustomer");

const activeCustomerOrders =
    document.getElementById("activeCustomerOrders");

const totalOrders =
    document.getElementById("totalOrders");

const totalCustomers =
    document.getElementById("totalCustomers");


// ============================================================
// DATA STORAGE
// ============================================================

let orderData = [];


// ============================================================
// FORMAT CURRENCY
// ============================================================

function formatCurrency(amount) {

    return new Intl.NumberFormat(
        "en-IN",
        {
            style: "currency",
            currency: "INR",
            maximumFractionDigits: 2
        }
    ).format(Number(amount) || 0);
}


// ============================================================
// FORMAT DATE
// ============================================================

function formatDate(date) {

    if (!date) {
        return "-";
    }

    const parsedDate =
        new Date(date);

    if (Number.isNaN(parsedDate.getTime())) {
        return "-";
    }

    return parsedDate.toLocaleDateString(
        "en-IN",
        {
            day: "2-digit",
            month: "short",
            year: "numeric"
        }
    );
}


// ============================================================
// SHOW ERROR
// ============================================================

function showError(message) {

    if (!errorMessage) {
        return;
    }

    errorMessage.textContent =
        message;

    errorMessage.style.display =
        "block";
}


// ============================================================
// HIDE ERROR
// ============================================================

function hideError() {

    if (!errorMessage) {
        return;
    }

    errorMessage.textContent =
        "";

    errorMessage.style.display =
        "none";
}


// ============================================================
// SET LOADING STATE
// ============================================================

function setLoading(isLoading) {

    if (!loading) {
        return;
    }

    loading.style.display =
        isLoading ? "flex" : "none";
}


// ============================================================
// LOAD ORDER DATA
// ============================================================
//
// Supabase RPC functions:
//
// 1. get_customer_order_history()
//    JOIN + ORDER BY
//
// 2. get_highest_value_order()
//    JOIN + SUBQUERY
//
// 3. get_most_active_customer()
//    JOIN + SUBQUERY + GROUP BY
//
// ============================================================

async function loadOrderData() {

    setLoading(true);

    hideError();


    try {

        // ====================================================
        // 1. CUSTOMER ORDER HISTORY
        // JOIN + ORDER BY
        // ====================================================

        const orderResult =
            await supabaseClient.rpc(
                "get_customer_order_history"
            );


        if (orderResult.error) {

            console.error(
                "Customer order history error:",
                orderResult.error
            );

            throw new Error(
                "Unable to load customer order history. " +
                orderResult.error.message
            );
        }


        // Always convert null into an empty array.
        orderData =
            Array.isArray(orderResult.data)
                ? orderResult.data
                : [];


        // ====================================================
        // 2. HIGHEST VALUE ORDER
        // SUBQUERY
        // ====================================================

        const highestResult =
            await supabaseClient.rpc(
                "get_highest_value_order"
            );


        if (highestResult.error) {

            console.error(
                "Highest-value order error:",
                highestResult.error
            );

            throw new Error(
                "Unable to find the highest-value order. " +
                highestResult.error.message
            );
        }


        const highestOrders =
            Array.isArray(highestResult.data)
                ? highestResult.data
                : [];


        // ====================================================
        // 3. MOST ACTIVE CUSTOMER
        // SUBQUERY + GROUP BY
        // ====================================================

        const activeResult =
            await supabaseClient.rpc(
                "get_most_active_customer"
            );


        if (activeResult.error) {

            console.error(
                "Most-active customer error:",
                activeResult.error
            );

            throw new Error(
                "Unable to find the most-active customer. " +
                activeResult.error.message
            );
        }


        const activeCustomers =
            Array.isArray(activeResult.data)
                ? activeResult.data
                : [];


        // ====================================================
        // UPDATE TOTAL ORDERS
        // ====================================================

        if (totalOrders) {

            totalOrders.textContent =
                orderData.length;
        }


        // ====================================================
        // UPDATE TOTAL CUSTOMERS
        // ====================================================
        //
        // Count unique customer IDs/names from the JOIN result.
        //
        // ====================================================

        const uniqueCustomers =
            new Set(
                orderData.map(
                    order => {

                        return (
                            order.customer_id ??
                            order.customer_name
                        );

                    }
                )
            );


        if (totalCustomers) {

            totalCustomers.textContent =
                uniqueCustomers.size;
        }


        // ====================================================
        // UPDATE HIGHEST VALUE ORDER
        // ====================================================

        if (
            highestOrders.length > 0
        ) {

            const highest =
                highestOrders[0];


            if (highestOrderAmount) {

                highestOrderAmount.textContent =
                    formatCurrency(
                        highest.total_amount
                    );
            }


            if (highestOrderProduct) {

                highestOrderProduct.textContent =
                    `${highest.customer_name || "Unknown Customer"} • ` +
                    `${highest.product_name || "Unknown Product"}`;
            }

        } else {

            if (highestOrderAmount) {

                highestOrderAmount.textContent =
                    "₹0";
            }


            if (highestOrderProduct) {

                highestOrderProduct.textContent =
                    "No orders available";
            }
        }


        // ====================================================
        // UPDATE MOST ACTIVE CUSTOMER
        // ====================================================

        if (
            activeCustomers.length > 0
        ) {

            const active =
                activeCustomers[0];


            if (activeCustomer) {

                activeCustomer.textContent =
                    active.customer_name ||
                    "Unknown Customer";
            }


            if (activeCustomerOrders) {

                const count =
                    Number(
                        active.order_count
                    ) || 0;


                activeCustomerOrders.textContent =
                    `${count} order${
                        count === 1
                            ? ""
                            : "s"
                    }`;
            }

        } else {

            if (activeCustomer) {

                activeCustomer.textContent =
                    "None";
            }


            if (activeCustomerOrders) {

                activeCustomerOrders.textContent =
                    "No orders available";
            }
        }


        // ====================================================
        // DISPLAY ORDER TABLE
        // ====================================================

        renderOrders(
            orderData
        );

    }

    catch (error) {

        console.error(
            "Task 4 dashboard error:",
            error
        );


        // Clear stale data if the request failed.
        orderData = [];


        if (totalOrders) {
            totalOrders.textContent = "0";
        }


        if (totalCustomers) {
            totalCustomers.textContent = "0";
        }


        if (highestOrderAmount) {
            highestOrderAmount.textContent = "₹0";
        }


        if (highestOrderProduct) {
            highestOrderProduct.textContent =
                "No data available";
        }


        if (activeCustomer) {
            activeCustomer.textContent =
                "None";
        }


        if (activeCustomerOrders) {
            activeCustomerOrders.textContent =
                "No data available";
        }


        if (orderTableBody) {

            orderTableBody.innerHTML = `
                <tr>
                    <td
                        colspan="7"
                        class="empty-row"
                    >
                        Unable to load order data.
                    </td>
                </tr>
            `;
        }


        showError(
            error.message ||
            "Unable to load the order management dashboard."
        );

    }

    finally {

        setLoading(false);
    }
}


// ============================================================
// RENDER ORDERS
// ============================================================
//
// RPC returns:
//
// order_id
// customer_name
// product_name
// category
// quantity
// total_amount
// order_date
//
// ============================================================

function renderOrders(orders) {

    if (!orderTableBody) {

        console.error(
            "HTML element #orderTableBody was not found."
        );

        return;
    }


    orderTableBody.innerHTML =
        "";


    // ========================================================
    // EMPTY DATA
    // ========================================================

    if (
        !Array.isArray(orders) ||
        orders.length === 0
    ) {

        orderTableBody.innerHTML = `
            <tr>
                <td
                    colspan="7"
                    class="empty-row"
                >
                    No orders found.
                </td>
            </tr>
        `;

        return;
    }


    // ========================================================
    // CREATE TABLE ROWS
    // ========================================================

    orders.forEach(order => {

        const row =
            document.createElement("tr");


        const orderId =
            order.order_id ??
            "-";


        const customerName =
            order.customer_name ||
            "Unknown";


        const productName =
            order.product_name ||
            "Unknown";


        const category =
            order.category ||
            "—";


        const quantity =
            order.quantity ??
            0;


        const amount =
            formatCurrency(
                order.total_amount
            );


        const date =
            formatDate(
                order.order_date
            );


        row.innerHTML = `

            <td>
                #${escapeHTML(orderId)}
            </td>

            <td class="customer-name">
                ${escapeHTML(customerName)}
            </td>

            <td class="product-name">
                ${escapeHTML(productName)}
            </td>

            <td>

                <span class="category">
                    ${escapeHTML(category)}
                </span>

            </td>

            <td>
                ${escapeHTML(quantity)}
            </td>

            <td class="amount">
                ${amount}
            </td>

            <td>
                ${escapeHTML(date)}
            </td>

        `;


        orderTableBody.appendChild(
            row
        );

    });
}


// ============================================================
// SORT ORDERS
// ============================================================

function sortOrderData(type) {

    const sorted =
        [...orderData];


    switch (type) {


        // ----------------------------------------------------
        // NEWEST FIRST
        // ----------------------------------------------------

        case "date-desc":

            sorted.sort(
                (a, b) =>
                    new Date(b.order_date) -
                    new Date(a.order_date)
            );

            break;


        // ----------------------------------------------------
        // OLDEST FIRST
        // ----------------------------------------------------

        case "date-asc":

            sorted.sort(
                (a, b) =>
                    new Date(a.order_date) -
                    new Date(b.order_date)
            );

            break;


        // ----------------------------------------------------
        // HIGHEST AMOUNT
        // ----------------------------------------------------

        case "amount-desc":

            sorted.sort(
                (a, b) =>
                    Number(b.total_amount) -
                    Number(a.total_amount)
            );

            break;


        // ----------------------------------------------------
        // LOWEST AMOUNT
        // ----------------------------------------------------

        case "amount-asc":

            sorted.sort(
                (a, b) =>
                    Number(a.total_amount) -
                    Number(b.total_amount)
            );

            break;


        // ----------------------------------------------------
        // CUSTOMER NAME
        // ----------------------------------------------------

        case "customer":

            sorted.sort(
                (a, b) => {

                    const customerA =
                        String(
                            a.customer_name || ""
                        );

                    const customerB =
                        String(
                            b.customer_name || ""
                        );


                    return customerA.localeCompare(
                        customerB
                    );
                }
            );

            break;


        // ----------------------------------------------------
        // DEFAULT
        // ----------------------------------------------------

        default:

            break;
    }


    renderOrders(
        sorted
    );
}


// ============================================================
// ESCAPE HTML
// ============================================================

function escapeHTML(value) {

    const div =
        document.createElement("div");


    div.textContent =
        String(
            value ?? ""
        );


    return div.innerHTML;
}


// ============================================================
// SORT EVENT
// ============================================================

if (sortOrders) {

    sortOrders.addEventListener(
        "change",
        () => {

            sortOrderData(
                sortOrders.value
            );

        }
    );
}


// ============================================================
// START APPLICATION
// ============================================================

document.addEventListener(
    "DOMContentLoaded",
    () => {

        loadOrderData();

    }
);