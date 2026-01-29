PRAGMA foreign_keys = ON;

CREATE TABLE IF NOT EXISTS customers (
                                         id TEXT PRIMARY KEY,
                                         company_name TEXT NOT NULL UNIQUE,
                                         org_no TEXT NOT NULL UNIQUE,
                                         contact_name TEXT,
                                         contact_email TEXT,
                                         contact_phone TEXT,
                                         address TEXT,
                                         city TEXT,
                                         zip_code TEXT,
                                         country TEXT,
                                         industry TEXT,
                                         customer_type TEXT,
                                         created_at TEXT,
                                         notes TEXT
);

CREATE TABLE IF NOT EXISTS resellers (
                                         id TEXT PRIMARY KEY,
                                         name TEXT NOT NULL UNIQUE,
                                         org_no TEXT NOT NULL UNIQUE,
                                         active INTEGER NOT NULL,
                                         address TEXT,
                                         contact_email TEXT,
                                         contact_telephone TEXT,
                                         invoice_reference TEXT,
                                         created_at TEXT
);

CREATE TABLE IF NOT EXISTS subscriptions (
                                             id TEXT PRIMARY KEY,
                                             name TEXT NOT NULL UNIQUE,
                                             category TEXT,
                                             description TEXT,
                                             service_level TEXT,
                                             price_per_month REAL NOT NULL,
                                             contract_length INTEGER NOT NULL,
                                             renewal_period INTEGER,
                                             active INTEGER,
                                             support_contact TEXT,
                                             created_at TEXT,
                                             notes TEXT
);

CREATE TABLE IF NOT EXISTS contracts (
                                         id TEXT PRIMARY KEY,
                                         customer_id TEXT NOT NULL,
                                         status INTEGER NOT NULL,
                                         active INTEGER NOT NULL,
                                         contract_date TEXT NOT NULL,
                                         contract_length_months INTEGER NOT NULL,
                                         total_price_per_month REAL NOT NULL,
                                         due_date TEXT NOT NULL,
                                         comment TEXT,
                                         FOREIGN KEY (customer_id) REFERENCES customers(id)
    );

CREATE TABLE IF NOT EXISTS contract_resellers (
                                                  contract_id TEXT,
                                                  reseller_id TEXT,
                                                  PRIMARY KEY (contract_id, reseller_id)
    );

CREATE TABLE IF NOT EXISTS contract_subscriptions (
                                                      contract_id TEXT,
                                                      subscription_id TEXT,
                                                      PRIMARY KEY (contract_id, subscription_id)
    );

CREATE TABLE IF NOT EXISTS contract_renewal_dates (
                                                      contract_id TEXT,
                                                      renewal_dates TEXT
);

CREATE TABLE IF NOT EXISTS lookup_values (
                                             id TEXT PRIMARY KEY,
                                             type TEXT NOT NULL,
                                             value TEXT NOT NULL,
                                             label TEXT NOT NULL,
                                             sort_order INTEGER,
                                             active INTEGER NOT NULL
);

-- =========================
-- EVENT TABLES
-- =========================

CREATE TABLE IF NOT EXISTS contract_event (
                                              id CHAR(36) PRIMARY KEY,
                                              contract_id CHAR(36) NOT NULL,
                                              customer_org_no VARCHAR(64),
                                              event_type VARCHAR(64),
                                              event_ts TIMESTAMP NOT NULL,
                                              detail TEXT,
                                              actor VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS customer_event (
                                              id CHAR(36) PRIMARY KEY,
                                              customer_id CHAR(36) NOT NULL,
                                              event_type VARCHAR(64) NOT NULL,
                                              event_ts TIMESTAMP NOT NULL,
                                              detail TEXT,
                                              actor VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS reseller_event (
                                              id CHAR(36) PRIMARY KEY,
                                              reseller_id CHAR(36) NOT NULL,
                                              event_type VARCHAR(64) NOT NULL,
                                              event_ts TIMESTAMP NOT NULL,
                                              detail TEXT,
                                              actor VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS subscription_event (
                                                  id CHAR(36) PRIMARY KEY,
                                                  subscription_id CHAR(36) NOT NULL,
                                                  event_type VARCHAR(64) NOT NULL,
                                                  event_ts TIMESTAMP NOT NULL,
                                                  detail TEXT,
                                                  actor VARCHAR(255)
);
