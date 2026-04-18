-- =============================================================
-- Flyway Migration: V1__create_tables.sql
-- Database: H2
-- Description: Creates schema for financial crime case management
-- =============================================================

CREATE TABLE cases (
    case_id         VARCHAR(20)     NOT NULL PRIMARY KEY,
    customer_id     VARCHAR(20)     NOT NULL,
    priority        VARCHAR(10)     NOT NULL CHECK (priority IN ('LOW', 'MEDIUM', 'HIGH')),
    status          VARCHAR(30)     NOT NULL CHECK (status IN (
                        'OPEN', 'UNDER_INVESTIGATION', 'PENDING_REVIEW',
                        'SAR_FILED', 'NO_ACTION_TAKEN', 'CLOSED'
                    )),
    assigned_analyst VARCHAR(50)    NOT NULL,
    opened_at       TIMESTAMP       NOT NULL,
    sar_decision    VARCHAR(20)     CHECK (sar_decision IN ('FILE', 'NO_ACTION')),
    sar_rationale   CLOB
);

CREATE TABLE case_linked_alerts (
    id              BIGINT          GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    case_id         VARCHAR(20)     NOT NULL,
    alert_id        VARCHAR(20)     NOT NULL,
    CONSTRAINT fk_cla_case FOREIGN KEY (case_id) REFERENCES cases(case_id),
    CONSTRAINT uq_cla_case_alert UNIQUE (case_id, alert_id)
);

CREATE TABLE case_notes (
    id              BIGINT          GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    case_id         VARCHAR(20)     NOT NULL,
    author          VARCHAR(50)     NOT NULL,
    note_timestamp  TIMESTAMP       NOT NULL,
    note_text       CLOB            NOT NULL,
    CONSTRAINT fk_cn_case FOREIGN KEY (case_id) REFERENCES cases(case_id)
);

CREATE TABLE case_audit_log (
    id              BIGINT          GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    case_id         VARCHAR(20)     NOT NULL,
    event_type      VARCHAR(50)     NOT NULL,
    event_timestamp TIMESTAMP       NOT NULL,
    analyst         VARCHAR(50)     NOT NULL,
    detail          CLOB,
    CONSTRAINT fk_cal_case FOREIGN KEY (case_id) REFERENCES cases(case_id)
);

-- Indexes for common query patterns
CREATE INDEX idx_cases_customer      ON cases(customer_id);
CREATE INDEX idx_cases_status        ON cases(status);
CREATE INDEX idx_cases_analyst       ON cases(assigned_analyst);
CREATE INDEX idx_audit_case_id       ON case_audit_log(case_id);
CREATE INDEX idx_notes_case_id       ON case_notes(case_id);
CREATE INDEX idx_alerts_case_id      ON case_linked_alerts(case_id);
