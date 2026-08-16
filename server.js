// ============================================================
// STUDENTHUB — SHARED MYSQL SERVER
// Tasks 01, 02, 03, 04, 05, 06 & 07
// Node.js + Express + MySQL
// ============================================================

const express = require("express");
const mysql = require("mysql2/promise");
const cors = require("cors");
require("dotenv").config();

// ============================================================
// 1. CREATE EXPRESS APP
// ============================================================

const app = express();

// ============================================================
// 2. MIDDLEWARE
// ============================================================

app.use(cors());

app.use(express.json());

app.use(
    express.urlencoded({
        extended: true
    })
);

// ============================================================
// 3. MYSQL DATABASE CONNECTION
// ============================================================

const db = mysql.createPool({
    host: process.env.DB_HOST,
    user: process.env.DB_USER,
    password: process.env.DB_PASSWORD,
    database: process.env.DB_NAME,
    port: Number(process.env.DB_PORT) || 3306,

    waitForConnections: true,
    connectionLimit: 10,
    queueLimit: 0
});

// ============================================================
// 4. TEST DATABASE CONNECTION
// ============================================================

async function testDatabaseConnection() {
    let connection;

    try {
        connection = await db.getConnection();

        await connection.query("SELECT 1");

        console.log("✓ MySQL database connected");
    }

    catch (error) {
        console.error(
            "✗ MySQL database connection failed:",
            error.message
        );
    }

    finally {
        if (connection) {
            connection.release();
        }
    }
}

// ============================================================
// 5. ROOT API
// ============================================================

app.get("/", (req, res) => {

    res.json({
        success: true,

        message: "StudentHub API is running",

        database: "MySQL",

        tasks:
            "Task 01, Task 02, Task 03, Task 04, Task 05, Task 06 & Task 07",

        endpoints: {
            health: "/api/health",
            students: "/api/students",
            updateStudent: "/api/students/:id",
            login: "/api/login",
            customers: "/api/customers",
            updateCustomer: "/api/customers/:id",
            products: "/api/products",
            orders: "/api/orders",
            orderSummary: "/api/orders/summary",
            singleOrder: "/api/orders/:id",
            accounts: "/api/accounts",
            paymentAccounts: "/api/payment/accounts",
            transactions: "/api/transactions",
            paymentTransactions: "/api/payment/transactions",
            payment: "/api/payment",
            auditLogs: "/api/audit-logs",
            dailyActivity: "/api/daily-activity",
            feedback: "/api/feedback",
            feedbacks: "/api/feedbacks",
            feedbackSubmit: "/api/feedback/submit",
            feedbackTest: "/api/feedback/test"
        }
    });

});

// ============================================================
// 6. HEALTH CHECK
// ============================================================

app.get("/api/health", async (req, res) => {

    try {

        await db.query("SELECT 1");

        res.json({
            success: true,
            server: "running",
            database: "connected"
        });

    }

    catch (error) {

        console.error(
            "Health check database error:",
            error.message
        );

        res.status(500).json({
            success: false,
            server: "running",
            database: "disconnected"
        });

    }

});

// ============================================================
// 7. GET ALL STUDENTS
// TASK 02
// ============================================================

app.get("/api/students", async (req, res) => {

    try {

        const [rows] = await db.query(`

            SELECT
                id,
                name,
                email,
                dob,
                department,
                phone,
                year,
                created_at

            FROM students

            ORDER BY id DESC

        `);

        res.json({
            success: true,
            count: rows.length,
            data: rows
        });

    }

    catch (error) {

        console.error(
            "Student fetch error:",
            error.message
        );

        res.status(500).json({
            success: false,
            message: "Failed to fetch students"
        });

    }

});

// ============================================================
// 8. ADD STUDENT
// TASK 01
// FIXED: PASSWORD COLUMN
// ============================================================

app.post("/api/students", async (req, res) => {

    try {

        const {
            name,
            email,
            dob,
            department,
            phone,
            year,
            password
        } = req.body;

        // ------------------------------------------------------
        // REQUIRED FIELD VALIDATION
        // ------------------------------------------------------

        if (
            !name ||
            !email ||
            !dob ||
            !department ||
            !phone
        ) {

            return res.status(400).json({
                success: false,
                message:
                    "Name, email, date of birth, department and phone are required"
            });

        }

        // ------------------------------------------------------
        // CLEAN INPUT
        // ------------------------------------------------------

        const cleanName =
            String(name).trim();

        const cleanEmail =
            String(email)
                .trim()
                .toLowerCase();

        const cleanDepartment =
            String(department).trim();

        const cleanPhone =
            String(phone).trim();

        // ------------------------------------------------------
        // PASSWORD FIX
        //
        // Your students table contains a required password
        // column. If frontend doesn't send one, use empty string.
        // ------------------------------------------------------

        const studentPassword =
            password !== undefined &&
            password !== null
                ? String(password)
                : "";

        const studentYear =
            year === undefined ||
            year === null ||
            year === ""
                ? 1
                : Number(year);

        // ------------------------------------------------------
        // EMPTY VALIDATION
        // ------------------------------------------------------

        if (
            !cleanName ||
            !cleanEmail ||
            !cleanDepartment ||
            !cleanPhone
        ) {

            return res.status(400).json({
                success: false,
                message:
                    "All required student fields must contain valid values"
            });

        }

        // ------------------------------------------------------
        // YEAR VALIDATION
        // ------------------------------------------------------

        if (
            !Number.isInteger(studentYear) ||
            studentYear < 1
        ) {

            return res.status(400).json({
                success: false,
                message:
                    "Year must be a valid positive number"
            });

        }

        // ------------------------------------------------------
        // EMAIL VALIDATION
        // ------------------------------------------------------

        const emailPattern =
            /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

        if (!emailPattern.test(cleanEmail)) {

            return res.status(400).json({
                success: false,
                message:
                    "Please enter a valid email address"
            });

        }

        // ------------------------------------------------------
        // INSERT STUDENT
        //
        // IMPORTANT:
        // password is now included because your database
        // requires this column.
        // ------------------------------------------------------

        const [result] = await db.query(`

            INSERT INTO students
            (
                name,
                email,
                dob,
                department,
                phone,
                year,
                password
            )

            VALUES (?, ?, ?, ?, ?, ?, ?)

        `, [
            cleanName,
            cleanEmail,
            dob,
            cleanDepartment,
            cleanPhone,
            studentYear,
            studentPassword
        ]);

        console.log(
            "✓ Student inserted:",
            result.insertId
        );

        res.status(201).json({

            success: true,

            message:
                "Student added successfully",

            studentId:
                result.insertId

        });

    }

    catch (error) {

        console.error(
            "Student insert error:",
            error.message
        );

        if (error.code === "ER_DUP_ENTRY") {

            return res.status(409).json({

                success: false,

                message:
                    "This email is already registered"

            });

        }

        res.status(500).json({

            success: false,

            message:
                "Failed to add student"

        });

    }

});

// ============================================================
// 9. UPDATE STUDENT
// TASK 06
// ============================================================

app.put("/api/students/:id", async (req, res) => {

    try {

        const studentId =
            Number(req.params.id);

        const {
            name,
            email,
            dob,
            department,
            phone,
            year,
            password
        } = req.body;

        // ------------------------------------------------------
        // ID VALIDATION
        // ------------------------------------------------------

        if (
            !Number.isInteger(studentId) ||
            studentId <= 0
        ) {

            return res.status(400).json({
                success: false,
                message: "Invalid student ID"
            });

        }

        // ------------------------------------------------------
        // REQUIRED FIELD VALIDATION
        // ------------------------------------------------------

        if (
            !name ||
            !email ||
            !dob ||
            !department ||
            !phone
        ) {

            return res.status(400).json({
                success: false,
                message:
                    "Name, email, date of birth, department and phone are required"
            });

        }

        // ------------------------------------------------------
        // CLEAN INPUT
        // ------------------------------------------------------

        const cleanName =
            String(name).trim();

        const cleanEmail =
            String(email)
                .trim()
                .toLowerCase();

        const cleanDepartment =
            String(department).trim();

        const cleanPhone =
            String(phone).trim();

        // ------------------------------------------------------
        // YEAR
        // ------------------------------------------------------

        let studentYear;

        if (
            year === undefined ||
            year === null ||
            year === ""
        ) {

            studentYear = null;

        }

        else {

            studentYear = Number(year);

            if (
                !Number.isInteger(studentYear) ||
                studentYear < 1
            ) {

                return res.status(400).json({
                    success: false,
                    message:
                        "Year must be a valid positive number"
                });

            }

        }

        // ------------------------------------------------------
        // EMAIL VALIDATION
        // ------------------------------------------------------

        const emailPattern =
            /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

        if (!emailPattern.test(cleanEmail)) {

            return res.status(400).json({
                success: false,
                message:
                    "Please enter a valid email address"
            });

        }

        // ------------------------------------------------------
        // UPDATE QUERY
        // ------------------------------------------------------

        let query;
        let values;

        if (
            password !== undefined &&
            password !== null
        ) {

            if (studentYear !== null) {

                query = `

                    UPDATE students

                    SET
                        name = ?,
                        email = ?,
                        dob = ?,
                        department = ?,
                        phone = ?,
                        year = ?,
                        password = ?

                    WHERE id = ?

                `;

                values = [
                    cleanName,
                    cleanEmail,
                    dob,
                    cleanDepartment,
                    cleanPhone,
                    studentYear,
                    String(password),
                    studentId
                ];

            }

            else {

                query = `

                    UPDATE students

                    SET
                        name = ?,
                        email = ?,
                        dob = ?,
                        department = ?,
                        phone = ?,
                        password = ?

                    WHERE id = ?

                `;

                values = [
                    cleanName,
                    cleanEmail,
                    dob,
                    cleanDepartment,
                    cleanPhone,
                    String(password),
                    studentId
                ];

            }

        }

        else {

            if (studentYear !== null) {

                query = `

                    UPDATE students

                    SET
                        name = ?,
                        email = ?,
                        dob = ?,
                        department = ?,
                        phone = ?,
                        year = ?

                    WHERE id = ?

                `;

                values = [
                    cleanName,
                    cleanEmail,
                    dob,
                    cleanDepartment,
                    cleanPhone,
                    studentYear,
                    studentId
                ];

            }

            else {

                query = `

                    UPDATE students

                    SET
                        name = ?,
                        email = ?,
                        dob = ?,
                        department = ?,
                        phone = ?

                    WHERE id = ?

                `;

                values = [
                    cleanName,
                    cleanEmail,
                    dob,
                    cleanDepartment,
                    cleanPhone,
                    studentId
                ];

            }

        }

        const [result] =
            await db.query(query, values);

        // ------------------------------------------------------
        // CHECK IF STUDENT EXISTS
        // ------------------------------------------------------

        if (result.affectedRows === 0) {

            const [existing] = await db.query(
                "SELECT id FROM students WHERE id = ? LIMIT 1",
                [studentId]
            );

            if (existing.length === 0) {

                return res.status(404).json({
                    success: false,
                    message: "Student not found"
                });

            }

        }

        console.log(
            "✓ Student updated:",
            studentId
        );

        res.json({

            success: true,

            message:
                "Student updated successfully"

        });

    }

    catch (error) {

        console.error(
            "Student update error:",
            error.message
        );

        if (error.code === "ER_DUP_ENTRY") {

            return res.status(409).json({
                success: false,
                message:
                    "This email is already registered"
            });

        }

        res.status(500).json({
            success: false,
            message:
                "Failed to update student"
        });

    }

});

// ============================================================
// 10. LOGIN
// TASK 03
// ============================================================

app.post("/api/login", async (req, res) => {

    try {

        const {
            email,
            username,
            password
        } = req.body;

        const loginEmail =
            email || username;

        if (
            !loginEmail ||
            !password
        ) {

            return res.status(400).json({
                success: false,
                message:
                    "Email and password are required"
            });

        }

        const cleanEmail =
            String(loginEmail)
                .trim()
                .toLowerCase();

        const [rows] = await db.query(`

            SELECT
                id,
                username

            FROM users

            WHERE LOWER(username) = ?
            AND password = ?

            LIMIT 1

        `, [
            cleanEmail,
            password
        ]);

        if (rows.length === 0) {

            return res.status(401).json({
                success: false,
                message:
                    "Invalid email or password"
            });

        }

        console.log(
            "✓ Login successful:",
            cleanEmail
        );

        res.json({

            success: true,

            message:
                "Login successful",

            user: {

                id:
                    rows[0].id,

                email:
                    rows[0].username

            }

        });

    }

    catch (error) {

        console.error(
            "Login error:",
            error.message
        );

        res.status(500).json({
            success: false,
            message:
                "Login failed"
        });

    }

});

// ============================================================
// 11. GET AUDIT LOGS
// TASK 06
// ============================================================

app.get("/api/audit-logs", async (req, res) => {

    try {

        const [rows] = await db.query(`

            SELECT
                log_id,
                table_name,
                action_type,
                record_id,
                description,
                action_time

            FROM audit_logs

            ORDER BY
                action_time DESC,
                log_id DESC

        `);

        res.json({

            success: true,
            count: rows.length,
            data: rows

        });

    }

    catch (error) {

        console.error(
            "Audit log error:",
            error.message
        );

        res.status(500).json({

            success: false,
            message:
                "Failed to fetch audit logs"

        });

    }

});

// ============================================================
// 12. DAILY ACTIVITY REPORT
// TASK 06
// ============================================================

app.get("/api/daily-activity", async (req, res) => {

    try {

        const [rows] = await db.query(`

            SELECT
                activity_date,
                action_type,
                total_actions

            FROM daily_activity_report

            ORDER BY
                activity_date DESC,
                action_type ASC

        `);

        res.json({

            success: true,
            count: rows.length,
            data: rows

        });

    }

    catch (error) {

        console.error(
            "Daily activity error:",
            error.message
        );

        res.status(500).json({

            success: false,
            message:
                "Failed to fetch daily activity report"

        });

    }

});

// ============================================================
// 13. GET CUSTOMERS
// TASK 04
// ============================================================

app.get("/api/customers", async (req, res) => {

    try {

        const [rows] = await db.query(`

            SELECT
                customer_id,
                name,
                email,
                phone,
                created_at

            FROM customers

            ORDER BY customer_id DESC

        `);

        res.json({

            success: true,
            count: rows.length,
            data: rows

        });

    }

    catch (error) {

        console.error(
            "Customer fetch error:",
            error.message
        );

        res.status(500).json({

            success: false,
            message:
                "Failed to fetch customers"

        });

    }

});

// ============================================================
// 14. ADD CUSTOMER
// TASK 04
// ============================================================

app.post("/api/customers", async (req, res) => {

    try {

        const {
            name,
            email,
            phone
        } = req.body;

        if (
            !name ||
            !email
        ) {

            return res.status(400).json({

                success: false,
                message:
                    "Name and email are required"

            });

        }

        const cleanName =
            String(name).trim();

        const cleanEmail =
            String(email)
                .trim()
                .toLowerCase();

        const cleanPhone =
            phone
                ? String(phone).trim()
                : null;

        if (!cleanName) {

            return res.status(400).json({

                success: false,
                message:
                    "Name cannot be empty"

            });

        }

        const emailPattern =
            /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

        if (!emailPattern.test(cleanEmail)) {

            return res.status(400).json({

                success: false,
                message:
                    "Please enter a valid email address"

            });

        }

        const [result] = await db.query(`

            INSERT INTO customers
            (
                name,
                email,
                phone
            )

            VALUES (?, ?, ?)

        `, [
            cleanName,
            cleanEmail,
            cleanPhone
        ]);

        res.status(201).json({

            success: true,

            message:
                "Customer added successfully",

            customerId:
                result.insertId

        });

    }

    catch (error) {

        console.error(
            "Customer insert error:",
            error.message
        );

        if (error.code === "ER_DUP_ENTRY") {

            return res.status(409).json({

                success: false,

                message:
                    "Customer email already exists"

            });

        }

        res.status(500).json({

            success: false,

            message:
                "Failed to add customer"

        });

    }

});

// ============================================================
// 15. UPDATE CUSTOMER
// TASK 04
// ============================================================

app.put("/api/customers/:id", async (req, res) => {

    try {

        const customerId =
            Number(req.params.id);

        const {
            name,
            email,
            phone
        } = req.body;

        if (
            !Number.isInteger(customerId) ||
            customerId <= 0
        ) {

            return res.status(400).json({

                success: false,

                message:
                    "Invalid customer ID"

            });

        }

        if (
            !name ||
            !email
        ) {

            return res.status(400).json({

                success: false,

                message:
                    "Name and email are required"

            });

        }

        const cleanName =
            String(name).trim();

        const cleanEmail =
            String(email)
                .trim()
                .toLowerCase();

        const cleanPhone =
            phone
                ? String(phone).trim()
                : null;

        const emailPattern =
            /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

        if (!emailPattern.test(cleanEmail)) {

            return res.status(400).json({

                success: false,

                message:
                    "Please enter a valid email address"

            });

        }

        const [result] = await db.query(`

            UPDATE customers

            SET
                name = ?,
                email = ?,
                phone = ?

            WHERE customer_id = ?

        `, [
            cleanName,
            cleanEmail,
            cleanPhone,
            customerId
        ]);

        if (result.affectedRows === 0) {

            const [existing] = await db.query(
                "SELECT customer_id FROM customers WHERE customer_id = ? LIMIT 1",
                [customerId]
            );

            if (existing.length === 0) {

                return res.status(404).json({

                    success: false,

                    message:
                        "Customer not found"

                });

            }

        }

        res.json({

            success: true,

            message:
                "Customer updated successfully"

        });

    }

    catch (error) {

        console.error(
            "Customer update error:",
            error.message
        );

        if (error.code === "ER_DUP_ENTRY") {

            return res.status(409).json({

                success: false,

                message:
                    "Customer email already exists"

            });

        }

        res.status(500).json({

            success: false,

            message:
                "Failed to update customer"

        });

    }

});

// ============================================================
// 16. GET ALL ORDERS
// TASK 04
// ============================================================

app.get("/api/orders", async (req, res) => {

    try {

        const sort =
            String(req.query.sort || "newest");

        const sortOptions = {

            newest: `
                o.order_date DESC,
                o.order_id DESC
            `,

            oldest: `
                o.order_date ASC,
                o.order_id ASC
            `,

            amount: `
                total_amount DESC,
                o.order_id DESC
            `,

            customer: `
                c.name ASC,
                o.order_date DESC
            `

        };

        const orderBy =
            sortOptions[sort] ||
            sortOptions.newest;

        const [rows] = await db.query(`

            SELECT

                o.order_id AS order_id,

                c.name AS customer_name,

                p.product_name AS product_name,

                oi.quantity AS quantity,

                oi.price AS unit_price,

                (
                    oi.quantity * oi.price
                ) AS total_amount,

                o.order_date AS order_date

            FROM orders o

            INNER JOIN customers c
                ON o.customer_id = c.customer_id

            INNER JOIN order_items oi
                ON o.order_id = oi.order_id

            INNER JOIN products p
                ON oi.product_id = p.product_id

            ORDER BY ${orderBy}

        `);

        res.json({

            success: true,
            count: rows.length,
            data: rows

        });

    }

    catch (error) {

        console.error(
            "Order fetch error:",
            error.message
        );

        res.status(500).json({

            success: false,

            message:
                "Failed to fetch order history"

        });

    }

});

// ============================================================
// 17. ORDER SUMMARY
// TASK 04
// ============================================================

app.get("/api/orders/summary", async (req, res) => {

    try {

        const [highestOrder] = await db.query(`

            SELECT

                o.order_id,

                c.name AS customer_name,

                SUM(
                    oi.quantity * oi.price
                ) AS total_amount

            FROM orders o

            INNER JOIN customers c
                ON o.customer_id = c.customer_id

            INNER JOIN order_items oi
                ON o.order_id = oi.order_id

            GROUP BY
                o.order_id,
                c.name

            ORDER BY
                total_amount DESC

            LIMIT 1

        `);

        const [activeCustomer] = await db.query(`

            SELECT

                c.customer_id,

                c.name AS customer_name,

                COUNT(
                    DISTINCT o.order_id
                ) AS order_count

            FROM customers c

            INNER JOIN orders o
                ON c.customer_id = o.customer_id

            GROUP BY
                c.customer_id,
                c.name

            ORDER BY
                order_count DESC,
                c.name ASC

            LIMIT 1

        `);

        const [totalOrders] = await db.query(`

            SELECT
                COUNT(*) AS total_orders

            FROM orders

        `);

        const [totalCustomers] = await db.query(`

            SELECT
                COUNT(
                    DISTINCT customer_id
                ) AS total_customers

            FROM orders

        `);

        res.json({

            success: true,

            data: {

                highestOrder:
                    highestOrder[0] || null,

                activeCustomer:
                    activeCustomer[0] || null,

                totalOrders:
                    Number(
                        totalOrders[0].total_orders
                    ),

                totalCustomers:
                    Number(
                        totalCustomers[0].total_customers
                    )

            }

        });

    }

    catch (error) {

        console.error(
            "Order summary error:",
            error.message
        );

        res.status(500).json({

            success: false,

            message:
                "Failed to fetch order summary"

        });

    }

});

// ============================================================
// 18. GET SINGLE ORDER
// TASK 04
// ============================================================

app.get("/api/orders/:id", async (req, res) => {

    try {

        const orderId =
            Number(req.params.id);

        if (
            !Number.isInteger(orderId) ||
            orderId <= 0
        ) {

            return res.status(400).json({

                success: false,

                message:
                    "Invalid order ID"

            });

        }

        const [rows] = await db.query(`

            SELECT

                o.order_id AS order_id,

                c.name AS customer_name,

                c.email AS customer_email,

                c.phone AS customer_phone,

                p.product_name AS product_name,

                oi.quantity AS quantity,

                oi.price AS unit_price,

                (
                    oi.quantity * oi.price
                ) AS total_amount,

                o.order_date AS order_date

            FROM orders o

            INNER JOIN customers c
                ON o.customer_id = c.customer_id

            INNER JOIN order_items oi
                ON o.order_id = oi.order_id

            INNER JOIN products p
                ON oi.product_id = p.product_id

            WHERE o.order_id = ?

            ORDER BY
                p.product_name ASC

        `, [orderId]);

        if (rows.length === 0) {

            return res.status(404).json({

                success: false,

                message:
                    "Order not found"

            });

        }

        res.json({

            success: true,

            data: rows

        });

    }

    catch (error) {

        console.error(
            "Single order fetch error:",
            error.message
        );

        res.status(500).json({

            success: false,

            message:
                "Failed to fetch order"

        });

    }

});

// ============================================================
// 19. GET PRODUCTS
// TASK 04
// ============================================================

app.get("/api/products", async (req, res) => {

    try {

        const [rows] = await db.query(`

            SELECT

                product_id,

                product_name,

                price,

                stock,

                created_at

            FROM products

            ORDER BY product_id DESC

        `);

        res.json({

            success: true,
            count: rows.length,
            data: rows

        });

    }

    catch (error) {

        console.error(
            "Product fetch error:",
            error.message
        );

        res.status(500).json({

            success: false,

            message:
                "Failed to fetch products"

        });

    }

});

// ============================================================
// 20. GET PAYMENT ACCOUNTS
// TASK 05
// ============================================================

app.get("/api/accounts", async (req, res) => {

    try {

        const [rows] = await db.query(`

            SELECT

                account_id,

                account_name,

                balance

            FROM accounts

            ORDER BY account_id ASC

        `);

        const data =
            rows.map(account => ({

                id:
                    account.account_id,

                account_id:
                    account.account_id,

                name:
                    account.account_name,

                account_name:
                    account.account_name,

                balance:
                    Number(account.balance)

            }));

        res.json({

            success: true,
            count: data.length,
            data

        });

    }

    catch (error) {

        console.error(
            "Account fetch error:",
            error.message
        );

        res.status(500).json({

            success: false,

            message:
                "Failed to load account information"

        });

    }

});

// ============================================================
// 21. PAYMENT ACCOUNT ALIAS
// TASK 05
// ============================================================

app.get("/api/payment/accounts", async (req, res) => {

    try {

        const [rows] = await db.query(`

            SELECT

                account_id,

                account_name,

                balance

            FROM accounts

            ORDER BY account_id ASC

        `);

        if (rows.length === 0) {

            return res.status(404).json({

                success: false,

                message:
                    "No payment accounts found."

            });

        }

        const normalized =
            rows.map(account => ({

                ...account,

                normalizedName:
                    String(account.account_name)
                        .trim()
                        .toLowerCase()

            }));

        const user =
            normalized.find(account =>
                account.normalizedName.includes("hithesh")
            ) ||

            normalized.find(account =>
                account.normalizedName.includes("user")
            ) ||

            normalized.find(account =>
                !account.normalizedName.includes("merchant")
            );

        const merchant =
            normalized.find(account =>
                account.normalizedName.includes("merchant")
            );

        if (!user) {

            return res.status(404).json({

                success: false,

                message:
                    "User account was not found."

            });

        }

        if (!merchant) {

            return res.status(404).json({

                success: false,

                message:
                    "Merchant account was not found."

            });

        }

        if (
            user.account_id ===
            merchant.account_id
        ) {

            return res.status(400).json({

                success: false,

                message:
                    "User and merchant accounts must be different."

            });

        }

        res.json({

            success: true,

            data: {

                user: {

                    account_id:
                        user.account_id,

                    name:
                        user.account_name,

                    balance:
                        Number(user.balance)

                },

                merchant: {

                    account_id:
                        merchant.account_id,

                    name:
                        merchant.account_name,

                    balance:
                        Number(merchant.balance)

                }

            }

        });

    }

    catch (error) {

        console.error(
            "Payment account error:",
            error.message
        );

        res.status(500).json({

            success: false,

            message:
                "Failed to load payment accounts"

        });

    }

});

// ============================================================
// 22. GET TRANSACTION HISTORY
// TASK 05
// ============================================================

app.get("/api/transactions", async (req, res) => {

    try {

        const [rows] = await db.query(`

            SELECT

                transaction_id,

                amount,

                status,

                transaction_date

            FROM payment_transactions

            ORDER BY
                transaction_date DESC,
                transaction_id DESC

            LIMIT 20

        `);

        const data =
            rows.map(transaction => ({

                id:
                    transaction.transaction_id,

                transaction_id:
                    transaction.transaction_id,

                amount:
                    Number(transaction.amount),

                status:
                    transaction.status,

                created_at:
                    transaction.transaction_date,

                transaction_date:
                    transaction.transaction_date

            }));

        res.json({

            success: true,
            count: data.length,
            data

        });

    }

    catch (error) {

        console.error(
            "Transaction history error:",
            error.message
        );

        res.status(500).json({

            success: false,

            message:
                "Failed to load transactions"

        });

    }

});

// ============================================================
// 23. PAYMENT TRANSACTION ALIAS
// TASK 05
// ============================================================

app.get(
    "/api/payment/transactions",
    async (req, res) => {

        try {

            const [rows] = await db.query(`

                SELECT

                    transaction_id,

                    amount,

                    status,

                    transaction_date

                FROM payment_transactions

                ORDER BY
                    transaction_date DESC,
                    transaction_id DESC

                LIMIT 20

            `);

            const data =
                rows.map(transaction => ({

                    id:
                        transaction.transaction_id,

                    transaction_id:
                        transaction.transaction_id,

                    amount:
                        Number(transaction.amount),

                    status:
                        transaction.status,

                    created_at:
                        transaction.transaction_date,

                    transaction_date:
                        transaction.transaction_date

                }));

            res.json({

                success: true,
                count: data.length,
                data

            });

        }

        catch (error) {

            console.error(
                "Payment history error:",
                error.message
            );

            res.status(500).json({

                success: false,

                message:
                    "Failed to load transactions"

            });

        }

    }
);

// ============================================================
// 24. PROCESS PAYMENT
// TASK 05
// ============================================================

app.post("/api/payment", async (req, res) => {

    let connection = null;

    let transactionStarted = false;

    try {

        let senderId =
            req.body.senderId ??
            req.body.sender_id;

        let receiverId =
            req.body.receiverId ??
            req.body.receiver_id;

        const amount =
            Number(req.body.amount);

        if (
            !Number.isFinite(amount) ||
            amount <= 0
        ) {

            return res.status(400).json({

                success: false,

                message:
                    "Enter an amount greater than ₹0."

            });

        }

        const paymentAmount =
            Number(
                amount.toFixed(2)
            );

        connection =
            await db.getConnection();

        await connection.beginTransaction();

        transactionStarted = true;

        const [accounts] =
            await connection.query(`

                SELECT

                    account_id,

                    account_name,

                    balance

                FROM accounts

                FOR UPDATE

            `);

        if (accounts.length === 0) {

            throw new Error(
                "No payment accounts found"
            );

        }

        const normalized =
            accounts.map(account => ({

                ...account,

                normalizedName:
                    String(account.account_name)
                        .trim()
                        .toLowerCase()

            }));

        const automaticUser =
            normalized.find(account =>
                account.normalizedName.includes("hithesh")
            ) ||

            normalized.find(account =>
                account.normalizedName.includes("user")
            ) ||

            normalized.find(account =>
                !account.normalizedName.includes("merchant")
            );

        const automaticMerchant =
            normalized.find(account =>
                account.normalizedName.includes("merchant")
            );

        if (
            senderId === undefined ||
            senderId === null ||
            senderId === ""
        ) {

            if (!automaticUser) {

                throw new Error(
                    "User account not found"
                );

            }

            senderId =
                automaticUser.account_id;

        }

        if (
            receiverId === undefined ||
            receiverId === null ||
            receiverId === ""
        ) {

            if (!automaticMerchant) {

                throw new Error(
                    "Merchant account not found"
                );

            }

            receiverId =
                automaticMerchant.account_id;

        }

        senderId =
            Number(senderId);

        receiverId =
            Number(receiverId);

        if (
            !Number.isInteger(senderId) ||
            !Number.isInteger(receiverId)
        ) {

            throw new Error(
                "Invalid sender or receiver account"
            );

        }

        if (
            senderId === receiverId
        ) {

            throw new Error(
                "Sender and receiver accounts must be different"
            );

        }

        const sender =
            accounts.find(account =>
                Number(account.account_id) ===
                senderId
            );

        const receiver =
            accounts.find(account =>
                Number(account.account_id) ===
                receiverId
            );

        if (!sender) {

            throw new Error(
                "User account not found"
            );

        }

        if (!receiver) {

            throw new Error(
                "Merchant account not found"
            );

        }

        const senderBalance =
            Number(sender.balance);

        const receiverBalance =
            Number(receiver.balance);

        if (
            senderBalance < paymentAmount
        ) {

            throw new Error(
                "Insufficient balance"
            );

        }

        const [deductResult] =
            await connection.query(`

                UPDATE accounts

                SET
                    balance = balance - ?

                WHERE account_id = ?

            `, [
                paymentAmount,
                senderId
            ]);

        if (
            deductResult.affectedRows !== 1
        ) {

            throw new Error(
                "Failed to deduct user balance"
            );

        }

        const [addResult] =
            await connection.query(`

                UPDATE accounts

                SET
                    balance = balance + ?

                WHERE account_id = ?

            `, [
                paymentAmount,
                receiverId
            ]);

        if (
            addResult.affectedRows !== 1
        ) {

            throw new Error(
                "Failed to update merchant balance"
            );

        }

        const [transactionResult] =
            await connection.query(`

                INSERT INTO payment_transactions
                (
                    amount,
                    status,
                    transaction_date
                )

                VALUES
                (
                    ?,
                    'SUCCESS',
                    NOW()
                )

            `, [
                paymentAmount
            ]);

        await connection.commit();

        transactionStarted = false;

        console.log(
            "✓ PAYMENT COMMITTED"
        );

        res.json({

            success: true,

            message:
                "Payment processed successfully",

            transaction: {

                id:
                    transactionResult.insertId,

                amount:
                    paymentAmount,

                status:
                    "SUCCESS"

            },

            balances: {

                user:
                    Number(
                        (
                            senderBalance -
                            paymentAmount
                        ).toFixed(2)
                    ),

                merchant:
                    Number(
                        (
                            receiverBalance +
                            paymentAmount
                        ).toFixed(2)
                    )

            }

        });

    }

    catch (error) {

        if (
            connection &&
            transactionStarted
        ) {

            try {

                await connection.rollback();

                console.log(
                    "↩ TRANSACTION ROLLED BACK"
                );

            }

            catch (rollbackError) {

                console.error(
                    "Rollback error:",
                    rollbackError.message
                );

            }

        }

        console.error(
            "Payment transaction error:",
            error.message
        );

        if (
            error.message ===
            "Insufficient balance"
        ) {

            return res.status(400).json({

                success: false,

                message:
                    "Insufficient balance. Payment was rolled back."

            });

        }

        if (
            error.message ===
            "User account not found"
        ) {

            return res.status(404).json({

                success: false,

                message:
                    "User account was not found. Transaction was rolled back."

            });

        }

        if (
            error.message ===
            "Merchant account not found"
        ) {

            return res.status(404).json({

                success: false,

                message:
                    "Merchant account was not found. Transaction was rolled back."

            });

        }

        if (
            error.message ===
            "No payment accounts found"
        ) {

            return res.status(404).json({

                success: false,

                message:
                    "No payment accounts found."

            });

        }

        if (
            error.message ===
            "Invalid sender or receiver account"
        ) {

            return res.status(400).json({

                success: false,

                message:
                    "Invalid sender or receiver account."

            });

        }

        if (
            error.message ===
            "Sender and receiver accounts must be different"
        ) {

            return res.status(400).json({

                success: false,

                message:
                    "Sender and receiver accounts must be different."

            });

        }

        res.status(500).json({

            success: false,

            message:
                "Payment failed. Transaction was rolled back."

        });

    }

    finally {

        if (connection) {

            connection.release();

        }

    }

});

// ============================================================
// 25. TASK 07 — GET FEEDBACK HANDLER
// ============================================================

async function getFeedback(req, res) {

    try {

        const [rows] = await db.query(`

            SELECT

                feedback_id,

                name,

                email,

                category,

                rating,

                message,

                submitted_at

            FROM feedback

            ORDER BY
                submitted_at DESC,
                feedback_id DESC

        `);

        res.json({

            success: true,

            count: rows.length,

            data: rows

        });

    }

    catch (error) {

        console.error(
            "Feedback fetch error:",
            error.message
        );

        res.status(500).json({

            success: false,

            message:
                "Failed to fetch feedback"

        });

    }

}

// ============================================================
// 26. TASK 07 — SUBMIT FEEDBACK HANDLER
// ============================================================

async function submitFeedback(req, res) {

    try {

        const {
            name,
            email,
            category,
            rating,
            message
        } = req.body;

        if (
            !name ||
            !email ||
            !category ||
            rating === undefined ||
            rating === null ||
            !message
        ) {

            return res.status(400).json({

                success: false,

                message:
                    "Name, email, category, rating and message are required"

            });

        }

        const cleanName =
            String(name).trim();

        const cleanEmail =
            String(email)
                .trim()
                .toLowerCase();

        const cleanCategory =
            String(category).trim();

        const cleanMessage =
            String(message).trim();

        const cleanRating =
            Number(rating);

        if (
            cleanName.length === 0 ||
            cleanEmail.length === 0 ||
            cleanCategory.length === 0 ||
            cleanMessage.length === 0
        ) {

            return res.status(400).json({

                success: false,

                message:
                    "All feedback fields are required"

            });

        }

        if (
            !Number.isInteger(cleanRating) ||
            cleanRating < 1 ||
            cleanRating > 5
        ) {

            return res.status(400).json({

                success: false,

                message:
                    "Rating must be between 1 and 5"

            });

        }

        const emailPattern =
            /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

        if (!emailPattern.test(cleanEmail)) {

            return res.status(400).json({

                success: false,

                message:
                    "Please enter a valid email address"

            });

        }

        const [result] = await db.query(`

            INSERT INTO feedback
            (
                name,
                email,
                category,
                rating,
                message
            )

            VALUES (?, ?, ?, ?, ?)

        `, [
            cleanName,
            cleanEmail,
            cleanCategory,
            cleanRating,
            cleanMessage
        ]);

        console.log(
            "✓ Feedback submitted:",
            result.insertId
        );

        res.status(201).json({

            success: true,

            message:
                "Feedback submitted successfully",

            feedbackId:
                result.insertId

        });

    }

    catch (error) {

        console.error(
            "Feedback insert error:",
            error.message
        );

        res.status(500).json({

            success: false,

            message:
                "Failed to submit feedback"

        });

    }

}

// ============================================================
// 27. TASK 07 — PRIMARY FEEDBACK API
// ============================================================

app.get(
    "/api/feedback",
    getFeedback
);

app.post(
    "/api/feedback",
    submitFeedback
);

// ============================================================
// 28. TASK 07 — COMPATIBILITY ALIASES
// ============================================================

app.get(
    "/api/feedbacks",
    getFeedback
);

app.post(
    "/api/feedbacks",
    submitFeedback
);

app.post(
    "/api/feedback/submit",
    submitFeedback
);

app.get(
    "/feedback",
    getFeedback
);

app.post(
    "/feedback",
    submitFeedback
);

// ============================================================
// 29. TASK 07 — API TEST ENDPOINT
// ============================================================

app.get("/api/feedback/test", (req, res) => {

    res.json({

        success: true,

        message:
            "Task 07 Feedback API is working",

        endpoints: {

            get:
                "/api/feedback",

            post:
                "/api/feedback",

            aliases: [

                "/api/feedbacks",

                "/api/feedback/submit",

                "/feedback"

            ]

        },

        methodForSubmission:
            "POST"

    });

});

// ============================================================
// 30. 404 HANDLER
// ============================================================

app.use((req, res) => {

    res.status(404).json({

        success: false,

        message:
            "API endpoint not found",

        path:
            req.originalUrl,

        method:
            req.method,

        hint:
            "Check the frontend API URL and HTTP method."

    });

});

// ============================================================
// 31. GLOBAL ERROR HANDLER
// ============================================================

app.use((error, req, res, next) => {

    console.error(
        "Server error:",
        error
    );

    if (res.headersSent) {

        return next(error);

    }

    res.status(500).json({

        success: false,

        message:
            "Internal server error"

    });

});

// ============================================================
// 32. START SERVER
// ============================================================

const PORT =
    Number(process.env.PORT) || 5000;

const server =
    app.listen(
        PORT,
        async () => {

            console.log("");

            console.log(
                "=============================================="
            );

            console.log(
                "          STUDENTHUB MYSQL SERVER"
            );

            console.log(
                "=============================================="
            );

            console.log(
                `✓ API running at http://localhost:${PORT}`
            );

            console.log("");

            console.log(
                `✓ Health:
http://localhost:${PORT}/api/health`
            );

            console.log(
                `✓ Students:
http://localhost:${PORT}/api/students`
            );

            console.log(
                `✓ Login:
http://localhost:${PORT}/api/login`
            );

            console.log(
                `✓ Customers:
http://localhost:${PORT}/api/customers`
            );

            console.log(
                `✓ Products:
http://localhost:${PORT}/api/products`
            );

            console.log(
                `✓ Orders:
http://localhost:${PORT}/api/orders`
            );

            console.log(
                `✓ Order Summary:
http://localhost:${PORT}/api/orders/summary`
            );

            console.log(
                `✓ Accounts:
http://localhost:${PORT}/api/accounts`
            );

            console.log(
                `✓ Transactions:
http://localhost:${PORT}/api/transactions`
            );

            console.log(
                `✓ Payment:
POST http://localhost:${PORT}/api/payment`
            );

            console.log(
                `✓ Audit Logs:
http://localhost:${PORT}/api/audit-logs`
            );

            console.log(
                `✓ Daily Report:
http://localhost:${PORT}/api/daily-activity`
            );

            console.log(
                `✓ Feedback GET:
http://localhost:${PORT}/api/feedback`
            );

            console.log(
                `✓ Feedback POST:
http://localhost:${PORT}/api/feedback`
            );

            console.log(
                `✓ Feedback Test:
http://localhost:${PORT}/api/feedback/test`
            );

            console.log("");

            console.log(
                "=============================================="
            );

            console.log("");

            await testDatabaseConnection();

        }
    );

// ============================================================
// 33. GRACEFUL SHUTDOWN
// ============================================================

async function shutdown(signal) {

    console.log(
        `\n${signal} received. Shutting down server...`
    );

    server.close(async () => {

        try {

            await db.end();

            console.log(
                "✓ MySQL connection pool closed"
            );

            console.log(
                "✓ Server stopped"
            );

            process.exit(0);

        }

        catch (error) {

            console.error(
                "Shutdown error:",
                error.message
            );

            process.exit(1);

        }

    });

}

process.on(
    "SIGINT",
    () => shutdown("SIGINT")
);

process.on(
    "SIGTERM",
    () => shutdown("SIGTERM")
);