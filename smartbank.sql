CREATE DATABASE IF NOT EXISTS smartbank;
USE smartbank;

CREATE TABLE IF NOT EXISTS customers (
 id BIGINT PRIMARY KEY AUTO_INCREMENT,
 full_name VARCHAR(100) NOT NULL,
 email VARCHAR(150) NOT NULL UNIQUE,
 phone VARCHAR(20),
 password_hash VARCHAR(300) NOT NULL,
 created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
CREATE TABLE IF NOT EXISTS bank_accounts (
 id BIGINT PRIMARY KEY AUTO_INCREMENT,
 account_number VARCHAR(20) NOT NULL UNIQUE,
 customer_id BIGINT NOT NULL,
 account_type VARCHAR(20) NOT NULL,
 balance DECIMAL(15,2) NOT NULL DEFAULT 0.00,
 status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
 created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
 CONSTRAINT fk_account_customer FOREIGN KEY(customer_id) REFERENCES customers(id)
);
CREATE TABLE IF NOT EXISTS transactions (
 id BIGINT PRIMARY KEY AUTO_INCREMENT,
 reference_no VARCHAR(40) NOT NULL UNIQUE,
 account_id BIGINT NOT NULL,
 transaction_type VARCHAR(20) NOT NULL,
 amount DECIMAL(15,2) NOT NULL,
 description VARCHAR(255),
 created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
 CONSTRAINT fk_transaction_account FOREIGN KEY(account_id) REFERENCES bank_accounts(id)
);
CREATE INDEX idx_accounts_customer ON bank_accounts(customer_id);
CREATE INDEX idx_transactions_account ON transactions(account_id, created_at);
