-- ============================================================
-- STUDENTHUB — TASK 06
-- AUTOMATED LOGGING USING TRIGGERS & VIEWS
-- MySQL Version
-- ============================================================

-- ============================================================
-- 1. SELECT DATABASE
-- ============================================================

USE studenthub;


-- ============================================================
-- 2. CHECK CUSTOMERS TABLE
-- ============================================================

SELECT *
FROM customers
LIMIT 5;


-- ============================================================
-- 3. CREATE AUDIT LOG TABLE
-- ============================================================

DROP TABLE IF EXISTS audit_logs;

CREATE TABLE audit_logs (
    log_id INT AUTO_INCREMENT PRIMARY KEY,
    table_name VARCHAR(100) NOT NULL,
    operation_type VARCHAR(20) NOT NULL,
    record_id INT,
    old_data TEXT,
    new_data TEXT,
    changed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);


-- ============================================================
-- 4. CREATE INSERT TRIGGER
-- ============================================================

DROP TRIGGER IF EXISTS after_customer_insert;

DELIMITER $$

CREATE TRIGGER after_customer_insert
AFTER INSERT ON customers
FOR EACH ROW
BEGIN
    INSERT INTO audit_logs (
        table_name,
        operation_type,
        record_id,
        old_data,
        new_data
    )
    VALUES (
        'customers',
        'INSERT',
        NEW.customer_id,
        NULL,
        CONCAT(
            'customer_id=', NEW.customer_id,
            ', name=', NEW.name,
            ', email=', NEW.email
        )
    );
END$$

DELIMITER ;


-- ============================================================
-- 5. CREATE UPDATE TRIGGER
-- ============================================================

DROP TRIGGER IF EXISTS after_customer_update;

DELIMITER $$

CREATE TRIGGER after_customer_update
AFTER UPDATE ON customers
FOR EACH ROW
BEGIN
    INSERT INTO audit_logs (
        table_name,
        operation_type,
        record_id,
        old_data,
        new_data
    )
    VALUES (
        'customers',
        'UPDATE',
        NEW.customer_id,
        CONCAT(
            'customer_id=', OLD.customer_id,
            ', name=', OLD.name,
            ', email=', OLD.email
        ),
        CONCAT(
            'customer_id=', NEW.customer_id,
            ', name=', NEW.name,
            ', email=', NEW.email
        )
    );
END$$

DELIMITER ;


-- ============================================================
-- 6. CREATE DAILY ACTIVITY REPORT VIEW
-- ============================================================

DROP VIEW IF EXISTS daily_activity_report;

CREATE VIEW daily_activity_report AS
SELECT
    DATE(changed_at) AS activity_date,
    operation_type,
    COUNT(*) AS total_operations
FROM audit_logs
GROUP BY
    DATE(changed_at),
    operation_type
ORDER BY
    activity_date DESC;


-- ============================================================
-- 7. TEST INSERT TRIGGER
-- ============================================================

INSERT INTO customers (
    name,
    email
)
VALUES (
    'Test Student',
    'teststudent@example.com'
);


-- ============================================================
-- 8. TEST UPDATE TRIGGER
-- ============================================================

UPDATE customers
SET name = 'Updated Student'
WHERE email = 'teststudent@example.com';


-- ============================================================
-- 9. DISPLAY AUDIT LOGS
-- ============================================================

SELECT *
FROM audit_logs
ORDER BY changed_at DESC;


-- ============================================================
-- 10. DISPLAY DAILY ACTIVITY REPORT
-- ============================================================

SELECT *
FROM daily_activity_report;