-- ============================================================
-- Case Management Service  —  V1__create_case_schema.sql
-- DB:  case_service_db  |  Port: 8082
-- H2 compatible (MODE=MySQL)
-- ============================================================

-- ─── INVESTIGATION_CASE ──────────────────────────────────────
-- Core case entity. One row per investigation case.
-- Lifecycle: OPEN → UNDER_INVESTIGATION → PENDING_REVIEW
--            → SAR_FILED | NO_ACTION_TAKEN | CLOSED
CREATE TABLE IF NOT EXISTS investigation_case (
                                                  id                  BIGINT          NOT NULL AUTO_INCREMENT,
                                                  case_ref            VARCHAR(20)     NOT NULL,               -- CASE-2024-0001
    customer_id         VARCHAR(30)     NOT NULL,               -- CUST-1042  (cross-service ref, no FK)
    priority            VARCHAR(10)     NOT NULL,               -- HIGH | MEDIUM | LOW
    status              VARCHAR(25)     NOT NULL DEFAULT 'OPEN',
    assigned_analyst    VARCHAR(100)    NOT NULL,
    sar_decision        VARCHAR(15)     NULL,                   -- FILE | NO_ACTION
    sar_rationale       CLOB            NULL,                   -- TEXT equivalent in H2
    opened_at           TIMESTAMP       NOT NULL,
    resolved_at         TIMESTAMP       NULL,
    created_at          TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_case          PRIMARY KEY (id),
    CONSTRAINT uk_case_ref      UNIQUE (case_ref)
    );

CREATE INDEX idx_case_status          ON investigation_case(status);
CREATE INDEX idx_case_priority        ON investigation_case(priority);
CREATE INDEX idx_case_customer        ON investigation_case(customer_id);
CREATE INDEX idx_case_analyst         ON investigation_case(assigned_analyst);
CREATE INDEX idx_case_status_priority ON investigation_case(status, priority);

-- ─── CASE_LINKED_ALERT ───────────────────────────────────────
-- Alert IDs are stored as string references.
-- No FK to Alert table — Alert Service is a separate DB.
CREATE TABLE IF NOT EXISTS case_linked_alert (
                                                 id          BIGINT      NOT NULL AUTO_INCREMENT,
                                                 case_id     BIGINT      NOT NULL,
                                                 alert_ref   VARCHAR(20) NOT NULL,                           -- ALT-00001
    linked_at   TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_case_linked_alert  PRIMARY KEY (id),
    CONSTRAINT uk_case_alert         UNIQUE (case_id, alert_ref),
    CONSTRAINT fk_cla_case           FOREIGN KEY (case_id)
    REFERENCES investigation_case(id) ON DELETE CASCADE
    );

CREATE INDEX idx_cla_case  ON case_linked_alert(case_id);
CREATE INDEX idx_cla_alert ON case_linked_alert(alert_ref);

-- ─── CASE_NOTE ───────────────────────────────────────────────
-- Immutable once written. Application must never UPDATE or DELETE.
CREATE TABLE IF NOT EXISTS case_note (
                                         id          BIGINT          NOT NULL AUTO_INCREMENT,
                                         case_id     BIGINT          NOT NULL,
                                         author      VARCHAR(100)    NOT NULL,
    note_text   CLOB            NOT NULL,
    created_at  TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_case_note  PRIMARY KEY (id),
    CONSTRAINT fk_note_case  FOREIGN KEY (case_id)
    REFERENCES investigation_case(id)
    );

CREATE INDEX idx_note_case   ON case_note(case_id);
CREATE INDEX idx_note_author ON case_note(author);

-- ─── CASE_AUDIT_LOG ──────────────────────────────────────────
-- Append-only tamper-proof audit trail.
-- Application must never UPDATE or DELETE rows in this table.
-- Required for FCA / AML / POCA 2002 compliance evidence.
CREATE TABLE IF NOT EXISTS case_audit_log (
                                              id          BIGINT          NOT NULL AUTO_INCREMENT,
                                              case_id     BIGINT          NOT NULL,
                                              event_type  VARCHAR(30)     NOT NULL,
    -- CASE_OPENED | STATUS_CHANGED | NOTE_ADDED | SAR_FILED | SAR_DECISION
    analyst     VARCHAR(100)    NULL,                           -- NULL = system action
    detail      CLOB            NOT NULL,
    changed_at  TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_audit      PRIMARY KEY (id),
    CONSTRAINT fk_audit_case FOREIGN KEY (case_id)
    REFERENCES investigation_case(id)
    );

CREATE INDEX idx_audit_case    ON case_audit_log(case_id, changed_at);
CREATE INDEX idx_audit_event   ON case_audit_log(event_type);
CREATE INDEX idx_audit_analyst ON case_audit_log(analyst);