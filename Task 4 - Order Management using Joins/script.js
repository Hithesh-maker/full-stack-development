// ============================================================
// STUDENTHUB — TASK 04
// ORDER MANAGEMENT
// Node.js + Express + MySQL
// ============================================================


// ============================================================
// 1. API CONFIGURATION
// ============================================================

const ORDERS_API_URL =
    "http://localhost:5000/api/orders";


// ============================================================
// 2. DOM ELEMENTS
// ============================================================

const loading =
    document.getElementById("loading");

const errorMessage =
    document.getElementById("errorMessage");

const orderTableBody =
    document.getElementById("orderTableBody");

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
// 3. ORDER DATA
// ============================================================

let orders = [];


// ============================================================
// 4. SHOW ERROR
// ============================================================

function showError(message) {

    if (!errorMessage) {
        return;
    }

    errorMessage.textContent = message;
    errorMessage.style.display = "block";

}


// ============================================================
// 5. CLEAR ERROR
// ============================================================

function clearError() {

    if (!errorMessage) {
        return;
    }

    errorMessage.textContent = "";
    errorMessage.style.display = "none";

}


// ============================================================
// 6. LOADING STATE
// ============================================================

function setLoading(isLoading) {

    if (!loading) {
        return;
    }

    loading.style.display =
        isLoading ? "flex" : "none";

}


// ============================================================
// 7. FORMAT CURRENCY
// ============================================================

function formatCurrency(amount) {

    const value =
        Number(amount) || 0;

    return "₹" +
        value.toLocaleString(
            "en-IN",
            {
                minimumFractionDigits: 2,
                maximumFractionDigits: 2
            }
        );

}


// ============================================================
// 8. FORMAT DATE
// ============================================================

function formatDate(dateValue) {

    if (!dateValue) {
        return "N/A";
    }

    const date =
        new Date(dateValue);

    if (
        Number.isNaN(
            date.getTime()
        )
    ) {
        return String(dateValue);
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
// 9. ESCAPE HTML
// ============================================================

function escapeHTML(value) {

    if (
        value === null ||
        value === undefined
    ) {
        return "";
    }

    return String(value)
        .replace(/&/g, "&amp;")
        .replace(/</g, "&lt;")
        .replace(/>/g, "&gt;")
        .replace(/"/g, "&quot;")
        .replace(/'/g, "&#039;");

}


// ============================================================
// 10. GET ITEM TOTAL
// ============================================================
// Uses API total_amount first.
// If total_amount is unavailable,
// quantity × unit_price is used as fallback.

function getItemTotal(order) {

    if (
        order.total_amount !== undefined &&
        order.total_amount !== null &&
        order.total_amount !== ""
    ) {

        const apiTotal =
            Number(order.total_amount);

        if (!Number.isNaN(apiTotal)) {
            return apiTotal;
        }

    }


    const quantity =
        Number(order.quantity) || 0;

    const unitPrice =
        Number(order.unit_price) || 0;

    return quantity * unitPrice;

}


// ============================================================
// 11. CALCULATE ORDER SUMMARIES
// ============================================================
// Multiple rows may belong to the same order.
//
// Example:
//
// Order #1
// Laptop       ₹55,000
// Keyboard      ₹1,500
// --------------------
// Total        ₹56,500
//
// This function groups rows by order_id.

function calculateOrderSummaries(data) {

    const orderMap = {};


    data.forEach(order => {

        const orderId =
            order.order_id;


        if (!orderMap[orderId]) {

            orderMap[orderId] = {

                order_id:
                    order.order_id,

                customer_id:
                    order.customer_id ?? null,

                customer_name:
                    order.customer_name ||
                    "Unknown",

                order_date:
                    order.order_date,

                total_amount:
                    0,

                products:
                    []

            };

        }


        const itemTotal =
            getItemTotal(order);


        orderMap[orderId].total_amount +=
            itemTotal;


        orderMap[orderId].products.push({

            product_name:
                order.product_name ||
                "Product",

            quantity:
                Number(order.quantity) || 0,

            unit_price:
                Number(order.unit_price) || 0

        });

    });


    return Object.values(orderMap);

}


// ============================================================
// 12. RENDER ORDERS
// ============================================================

function renderOrders(data) {

    if (!orderTableBody) {
        return;
    }


    orderTableBody.innerHTML = "";


    // --------------------------------------------------------
    // NO DATA
    // --------------------------------------------------------

    if (
        !data ||
        data.length === 0
    ) {

        orderTableBody.innerHTML = `

            <tr>

                <td colspan="7">

                    <div class="empty-order-state">

                        <div>
                            📦
                        </div>

                        <strong>
                            No orders found
                        </strong>

                        <span>
                            There are currently no
                            orders in the database.
                        </span>

                    </div>

                </td>

            </tr>

        `;

        return;

    }


    // --------------------------------------------------------
    // CREATE TABLE ROWS
    // --------------------------------------------------------

    data.forEach(order => {

        const row =
            document.createElement("tr");


        row.innerHTML = `

            <td>

                <strong>
                    #${escapeHTML(
                        order.order_id
                    )}
                </strong>

            </td>


            <td>

                ${escapeHTML(
                    order.customer_name ||
                    "N/A"
                )}

            </td>


            <td>

                ${escapeHTML(
                    order.product_name ||
                    "N/A"
                )}

            </td>


            <td>

                ${escapeHTML(
                    order.quantity ?? 0
                )}

            </td>


            <td>

                ${formatCurrency(
                    order.unit_price
                )}

            </td>


            <td>

                <strong>

                    ${formatCurrency(
                        getItemTotal(order)
                    )}

                </strong>

            </td>


            <td>

                ${formatDate(
                    order.order_date
                )}

            </td>

        `;


        orderTableBody.appendChild(row);

    });

}


// ============================================================
// 13. UPDATE DASHBOARD
// ============================================================

function updateDashboard(data) {

    // --------------------------------------------------------
    // EMPTY DATA
    // --------------------------------------------------------

    if (
        !data ||
        data.length === 0
    ) {

        if (highestOrderAmount) {

            highestOrderAmount.textContent =
                "₹0.00";

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


        if (totalOrders) {

            totalOrders.textContent =
                "0";

        }


        if (totalCustomers) {

            totalCustomers.textContent =
                "0";

        }

        return;

    }


    // ========================================================
    // ORDER SUMMARIES
    // ========================================================

    const orderSummaries =
        calculateOrderSummaries(data);


    // ========================================================
    // HIGHEST VALUE ORDER
    // ========================================================

    const highest =
        orderSummaries.reduce(
            (max, order) => {

                return Number(
                    order.total_amount
                ) >
                Number(
                    max.total_amount
                )
                    ? order
                    : max;

            },
            orderSummaries[0]
        );


    if (highestOrderAmount) {

        highestOrderAmount.textContent =
            formatCurrency(
                highest.total_amount
            );

    }


    if (highestOrderProduct) {

        const productNames =
            highest.products
                .map(
                    product =>
                        product.product_name
                )
                .join(" + ");


        highestOrderProduct.textContent =
            `${productNames} • Order #${highest.order_id}`;

    }


    // ========================================================
    // TOTAL ORDERS
    // ========================================================

    if (totalOrders) {

        totalOrders.textContent =
            orderSummaries.length;

    }


    // ========================================================
    // TOTAL CUSTOMERS
    // ========================================================
    // API currently provides customer_name,
    // so names are used when customer_id is unavailable.

    const customerSet =
        new Set();


    orderSummaries.forEach(order => {

        const customerKey =
            order.customer_id ??
            order.customer_name ??
            "Unknown";


        customerSet.add(
            customerKey
        );

    });


    if (totalCustomers) {

        totalCustomers.textContent =
            customerSet.size;

    }


    // ========================================================
    // MOST ACTIVE CUSTOMER
    // ========================================================

    const customerCounts = {};


    orderSummaries.forEach(order => {

        const customerKey =
            order.customer_id ??
            order.customer_name ??
            "Unknown";


        const customerName =
            order.customer_name ||
            "Unknown";


        if (!customerCounts[customerKey]) {

            customerCounts[customerKey] = {

                name:
                    customerName,

                orders:
                    0

            };

        }


        customerCounts[customerKey].orders++;

    });


    const customersList =
        Object.values(
            customerCounts
        );


    if (
        customersList.length === 0
    ) {

        if (activeCustomer) {

            activeCustomer.textContent =
                "None";

        }


        if (activeCustomerOrders) {

            activeCustomerOrders.textContent =
                "No data available";

        }

        return;

    }


    // ========================================================
    // FIND HIGHEST ORDER COUNT
    // ========================================================

    const highestCustomerCount =
        Math.max(
            ...customersList.map(
                customer =>
                    customer.orders
            )
        );


    // ========================================================
    // FIND TOP CUSTOMERS
    // ========================================================

    const topCustomers =
        customersList.filter(
            customer =>
                customer.orders ===
                highestCustomerCount
        );


    // ========================================================
    // DISPLAY CUSTOMER NAMES
    // ========================================================

    if (
        topCustomers.length > 1
    ) {

        const tiedCustomerNames =
            topCustomers
                .map(
                    customer =>
                        customer.name
                )
                .join(" & ");


        if (activeCustomer) {

            activeCustomer.textContent =
                tiedCustomerNames;

        }


        if (activeCustomerOrders) {

            activeCustomerOrders.textContent =
                `${highestCustomerCount} order` +
                `${highestCustomerCount === 1 ? "" : "s"} each`;

        }

    }


    // ========================================================
    // SINGLE MOST ACTIVE CUSTOMER
    // ========================================================

    else {

        const mostActive =
            topCustomers[0];


        if (activeCustomer) {

            activeCustomer.textContent =
                mostActive
                    ? mostActive.name
                    : "None";

        }


        if (activeCustomerOrders) {

            activeCustomerOrders.textContent =
                mostActive
                    ? `${mostActive.orders} order` +
                      `${mostActive.orders === 1 ? "" : "s"}`
                    : "No data available";

        }

    }

}


// ============================================================
// 14. SORT ORDERS
// ============================================================

function sortOrderData() {

    const sortType =
        sortOrders
            ? sortOrders.value
            : "date-desc";


    const sorted =
        [...orders];


    // ========================================================
    // NEWEST ORDER
    // ========================================================

    if (
        sortType === "date-desc"
    ) {

        sorted.sort(
            (a, b) =>
                new Date(
                    b.order_date
                ) -
                new Date(
                    a.order_date
                )
        );

    }


    // ========================================================
    // OLDEST ORDER
    // ========================================================

    else if (
        sortType === "date-asc"
    ) {

        sorted.sort(
            (a, b) =>
                new Date(
                    a.order_date
                ) -
                new Date(
                    b.order_date
                )
        );

    }


    // ========================================================
    // HIGHEST AMOUNT
    // ========================================================

    else if (
        sortType === "amount-desc"
    ) {

        sorted.sort(
            (a, b) =>
                getItemTotal(b) -
                getItemTotal(a)
        );

    }


    // ========================================================
    // LOWEST AMOUNT
    // ========================================================

    else if (
        sortType === "amount-asc"
    ) {

        sorted.sort(
            (a, b) =>
                getItemTotal(a) -
                getItemTotal(b)
        );

    }


    // ========================================================
    // CUSTOMER NAME
    // ========================================================

    else if (
        sortType === "customer"
    ) {

        sorted.sort(
            (a, b) =>
                String(
                    a.customer_name || ""
                ).localeCompare(
                    String(
                        b.customer_name || ""
                    )
                )
        );

    }


    renderOrders(sorted);

}


// ============================================================
// 15. LOAD ORDERS
// ============================================================

async function loadOrders() {

    setLoading(true);
    clearError();


    try {

        console.log(
            "======================================"
        );

        console.log(
            "StudentHub Task 04"
        );

        console.log(
            "Fetching orders from:"
        );

        console.log(
            ORDERS_API_URL
        );

        console.log(
            "======================================"
        );


        const response =
            await fetch(
                ORDERS_API_URL
            );


        // ====================================================
        // HTTP ERROR
        // ====================================================

        if (!response.ok) {

            let serverMessage =
                "";


            try {

                const errorData =
                    await response.json();


                serverMessage =
                    errorData.message ||
                    "";

            }

            catch {

                // Server did not return JSON.

            }


            throw new Error(

                serverMessage
                    ? `${serverMessage} (HTTP ${response.status})`
                    : `Server returned HTTP ${response.status}`

            );

        }


        // ====================================================
        // READ JSON
        // ====================================================

        const result =
            await response.json();


        console.log(
            "StudentHub Order Response:",
            result
        );


        // ====================================================
        // VALIDATE API RESPONSE
        // ====================================================

        if (
            Array.isArray(result)
        ) {

            orders =
                result;

        }

        else if (
            result &&
            Array.isArray(
                result.data
            )
        ) {

            orders =
                result.data;

        }

        else {

            throw new Error(
                result?.message ||
                "Invalid order data received from server."
            );

        }


        // ====================================================
        // SUCCESS
        // ====================================================

        console.log(
            `✓ Loaded ${orders.length} order record(s)`
        );


        console.log(
            "Orders:",
            orders
        );


        // ====================================================
        // UPDATE DASHBOARD
        // ====================================================

        updateDashboard(
            orders
        );


        // ====================================================
        // RENDER TABLE
        // ====================================================

        sortOrderData();


    }

    catch (error) {

        console.error(
            "StudentHub Order Error:",
            error
        );


        orders = [];


        updateDashboard([]);


        let message =
            error.message ||
            "Unknown error occurred.";


        // ====================================================
        // CONNECTION ERROR
        // ====================================================

        if (
            error.message ===
            "Failed to fetch"
        ) {

            message =
                "Unable to connect to the StudentHub server. " +
                "Make sure Node.js is running on port 5000.";

        }


        showError(
            message
        );


        if (orderTableBody) {

            orderTableBody.innerHTML = `

                <tr>

                    <td colspan="7">

                        <div class="order-error-state">

                            <div>
                                ⚠️
                            </div>

                            <strong>
                                Unable to load order data
                            </strong>

                            <span>
                                ${escapeHTML(
                                    message
                                )}
                            </span>

                            <small>
                                Make sure the Node.js server
                                is running on port 5000.
                            </small>

                        </div>

                    </td>

                </tr>

            `;

        }

    }

    finally {

        setLoading(false);

    }

}


// ============================================================
// 16. SORT EVENT
// ============================================================

if (sortOrders) {

    sortOrders.addEventListener(
        "change",
        sortOrderData
    );

}


// ============================================================
// 17. PAGE LOAD
// ============================================================

document.addEventListener(
    "DOMContentLoaded",
    function () {

        console.log(
            "StudentHub Task 04 loaded."
        );


        console.log(
            "Orders API:",
            ORDERS_API_URL
        );


        loadOrders();

    }
);