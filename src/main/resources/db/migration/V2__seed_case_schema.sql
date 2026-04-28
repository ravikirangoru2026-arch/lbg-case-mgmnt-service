-- ============================================================
-- Case Management Service  —  V2__seed_case_data.sql
-- Seed: 8 cases from cases.json with full audit logs & notes
-- H2 compatible — uses TIMESTAMP literals, CLOB-safe strings
-- Single-quotes escaped as '' throughout
-- ============================================================

-- ============================================================
--  SECTION 1 — INVESTIGATION CASES  (8 rows)
-- ============================================================

INSERT INTO investigation_case
(case_ref, customer_id, priority, status, assigned_analyst,
 sar_decision, sar_rationale, opened_at, resolved_at)
VALUES

-- ── CASE-2024-0001  OPEN, no SAR ────────────────────────────
('CASE-2024-0001', 'CUST-1042', 'HIGH', 'OPEN',
 'j.rahman',
 NULL, NULL,
 TIMESTAMP '2024-03-08 08:30:00', NULL),

-- ── CASE-2024-0002  UNDER_INVESTIGATION, no SAR ──────────────
('CASE-2024-0002', 'CUST-2187', 'HIGH', 'UNDER_INVESTIGATION',
 's.okafor',
 NULL, NULL,
 TIMESTAMP '2024-01-19 09:00:00', NULL),

-- ── CASE-2024-0003  PENDING_REVIEW, no SAR yet ───────────────
('CASE-2024-0003', 'CUST-4455', 'HIGH', 'PENDING_REVIEW',
 't.bergmann',
 NULL, NULL,
 TIMESTAMP '2024-01-26 10:00:00', NULL),

-- ── CASE-2024-0004  SAR_FILED — FILE decision ────────────────
('CASE-2024-0004', 'CUST-6612', 'HIGH', 'SAR_FILED',
 'j.rahman',
 'FILE',
 'The customer''s account was accessed from an unrecognised device in a foreign jurisdiction. A transfer of £15,200 was initiated within 4 minutes of login — inconsistent with the customer''s normal behaviour. The customer confirmed via the bank''s fraud line that they did not authorise the transfer. The destination account is linked to three other fraud reports in the past 30 days. Grounds for suspicion are sufficient to file a SAR under POCA 2002 Section 330.',
 TIMESTAMP '2024-02-02 08:00:00', TIMESTAMP '2024-02-04 09:15:00'),

-- ── CASE-2024-0005  NO_ACTION_TAKEN ─────────────────────────
('CASE-2024-0005', 'CUST-7734', 'LOW', 'NO_ACTION_TAKEN',
 'p.nwosu',
 'NO_ACTION',
 'The unusual activity alert was triggered by a single transaction of £1,100 that fell outside the customer''s normal spending pattern. On review, the customer provided a satisfactory explanation — this was a payment for annual car insurance renewal. The transaction was to a regulated insurer and the amount is consistent with the stated purpose. No grounds for suspicion found. No further action required.',
 TIMESTAMP '2024-02-04 11:00:00', TIMESTAMP '2024-02-05 09:45:00'),

-- ── CASE-2024-0006  CLOSED — NO_ACTION ──────────────────────
('CASE-2024-0006', 'CUST-8821', 'MEDIUM', 'CLOSED',
 'j.rahman',
 'NO_ACTION',
 'Structuring alert triggered by two cash deposits totalling £7,400. Customer is a self-employed market trader. Bank statements provided show regular cash income consistent with the customer''s stated business activity. Both deposits are within the customer''s established 6-month pattern. No grounds for suspicion. Case closed following MLRO sign-off.',
 TIMESTAMP '2024-02-09 09:30:00', TIMESTAMP '2024-02-13 09:01:00'),

-- ── CASE-2024-0007  UNDER_INVESTIGATION (mule account) ───────
('CASE-2024-0007', 'CUST-2187', 'HIGH', 'UNDER_INVESTIGATION',
 's.okafor',
 NULL, NULL,
 TIMESTAMP '2024-02-06 09:00:00', NULL),

-- ── CASE-2024-0008  OPEN, sanctions hit ─────────────────────
('CASE-2024-0008', 'CUST-9043', 'HIGH', 'OPEN',
 't.bergmann',
 NULL, NULL,
 TIMESTAMP '2024-03-08 11:00:00', NULL);


-- ============================================================
--  SECTION 2 — LINKED ALERTS
--  Resolved via subquery on case_ref — no hardcoded IDs
-- ============================================================

-- CASE-2024-0001  →  ALT-00001, ALT-00020
INSERT INTO case_linked_alert (case_id, alert_ref)
SELECT id, 'ALT-00001' FROM investigation_case WHERE case_ref = 'CASE-2024-0001';
INSERT INTO case_linked_alert (case_id, alert_ref)
SELECT id, 'ALT-00020' FROM investigation_case WHERE case_ref = 'CASE-2024-0001';

-- CASE-2024-0002  →  ALT-00002
INSERT INTO case_linked_alert (case_id, alert_ref)
SELECT id, 'ALT-00002' FROM investigation_case WHERE case_ref = 'CASE-2024-0002';

-- CASE-2024-0003  →  ALT-00004, ALT-00013
INSERT INTO case_linked_alert (case_id, alert_ref)
SELECT id, 'ALT-00004' FROM investigation_case WHERE case_ref = 'CASE-2024-0003';
INSERT INTO case_linked_alert (case_id, alert_ref)
SELECT id, 'ALT-00013' FROM investigation_case WHERE case_ref = 'CASE-2024-0003';

-- CASE-2024-0004  →  ALT-00006
INSERT INTO case_linked_alert (case_id, alert_ref)
SELECT id, 'ALT-00006' FROM investigation_case WHERE case_ref = 'CASE-2024-0004';

-- CASE-2024-0005  →  ALT-00007
INSERT INTO case_linked_alert (case_id, alert_ref)
SELECT id, 'ALT-00007' FROM investigation_case WHERE case_ref = 'CASE-2024-0005';

-- CASE-2024-0006  →  ALT-00009
INSERT INTO case_linked_alert (case_id, alert_ref)
SELECT id, 'ALT-00009' FROM investigation_case WHERE case_ref = 'CASE-2024-0006';

-- CASE-2024-0007  →  ALT-00008, ALT-00017
INSERT INTO case_linked_alert (case_id, alert_ref)
SELECT id, 'ALT-00008' FROM investigation_case WHERE case_ref = 'CASE-2024-0007';
INSERT INTO case_linked_alert (case_id, alert_ref)
SELECT id, 'ALT-00017' FROM investigation_case WHERE case_ref = 'CASE-2024-0007';

-- CASE-2024-0008  →  ALT-00010
INSERT INTO case_linked_alert (case_id, alert_ref)
SELECT id, 'ALT-00010' FROM investigation_case WHERE case_ref = 'CASE-2024-0008';


-- ============================================================
--  SECTION 3 — CASE NOTES
--  Only CASE-2024-0003, 0004, 0007 have notes.
--  Immutable — no update/delete after insert.
-- ============================================================

-- ── CASE-2024-0003  (2 notes — sanctions investigation) ──────
INSERT INTO case_note (case_id, author, note_text, created_at)
SELECT id,
       't.bergmann',
       'Sanctions screening confirmed a name match against the OFAC SDN list. Match confidence is 94%. Customer has been temporarily restricted pending MLRO review. Documentation requested from customer on source of funds.',
       TIMESTAMP '2024-01-27 11:30:00'
FROM investigation_case WHERE case_ref = 'CASE-2024-0003';

INSERT INTO case_note (case_id, author, note_text, created_at)
SELECT id,
       't.bergmann',
       'Customer has failed to provide requested documentation within the 5-day window. Secondary review by financial crime team confirms the match is likely a true positive. Escalating to senior investigator for PENDING_REVIEW sign-off.',
       TIMESTAMP '2024-02-02 09:45:00'
FROM investigation_case WHERE case_ref = 'CASE-2024-0003';

-- ── CASE-2024-0004  (2 notes — account takeover / SAR filed) ─
INSERT INTO case_note (case_id, author, note_text, created_at)
SELECT id,
       'j.rahman',
       'Customer contacted via fraud line and confirmed account compromise. Transfer of £15,200 to SORT: 20-91-44 ACC: 87654321 not authorised by customer. Receiving account has been flagged in the CIFAS database.',
       TIMESTAMP '2024-02-02 10:30:00'
FROM investigation_case WHERE case_ref = 'CASE-2024-0004';

INSERT INTO case_note (case_id, author, note_text, created_at)
SELECT id,
       'j.rahman',
       'IP address geo-located to a VPN exit node associated with known fraud activity. Device fingerprint is new and does not match any of the customer''s registered devices. Proceeds likely unrecoverable — receiving bank notified.',
       TIMESTAMP '2024-02-03 09:00:00'
FROM investigation_case WHERE case_ref = 'CASE-2024-0004';

-- ── CASE-2024-0007  (2 notes — mule account investigation) ───
INSERT INTO case_note (case_id, author, note_text, created_at)
SELECT id,
       's.okafor',
       'Customer''s account has now been linked to two separate APP scam victims across alerts ALT-00002 and ALT-00008. A third victim report came in via the PSR hotline today, relating to ALT-00017. This account is operating as a mule. Total victim losses across all three cases: £103,000. Account suspended pending investigation.',
       TIMESTAMP '2024-02-07 10:15:00'
FROM investigation_case WHERE case_ref = 'CASE-2024-0007';

INSERT INTO case_note (case_id, author, note_text, created_at)
SELECT id,
       's.okafor',
       'Customer account holder is uncontactable. Last known address confirmed via DVLA as correct but neighbours state the occupant moved out 3 weeks ago. Identity documents used at account opening may be fraudulent — being referred to the ID fraud team for analysis. Progressing to SAR filing.',
       TIMESTAMP '2024-02-10 14:30:00'
FROM investigation_case WHERE case_ref = 'CASE-2024-0007';


-- ============================================================
--  SECTION 4 — AUDIT LOG  (append-only, 25 entries total)
--  Every status change, note, and SAR event from cases.json
-- ============================================================

-- ── CASE-2024-0001  (1 audit entry) ─────────────────────────
INSERT INTO case_audit_log (case_id, event_type, analyst, detail, changed_at)
SELECT id, 'CASE_OPENED', 'j.rahman',
       'Case opened and linked to alerts ALT-00001 and ALT-00020',
       TIMESTAMP '2024-03-08 08:30:00'
FROM investigation_case WHERE case_ref = 'CASE-2024-0001';

-- ── CASE-2024-0002  (2 audit entries) ───────────────────────
INSERT INTO case_audit_log (case_id, event_type, analyst, detail, changed_at)
SELECT id, 'CASE_OPENED', 's.okafor',
       'Case opened and linked to alert ALT-00002',
       TIMESTAMP '2024-01-19 09:00:00'
FROM investigation_case WHERE case_ref = 'CASE-2024-0002';

INSERT INTO case_audit_log (case_id, event_type, analyst, detail, changed_at)
SELECT id, 'STATUS_CHANGED', 's.okafor',
       'Status changed from OPEN to UNDER_INVESTIGATION',
       TIMESTAMP '2024-01-19 14:15:00'
FROM investigation_case WHERE case_ref = 'CASE-2024-0002';

-- ── CASE-2024-0003  (5 audit entries) ───────────────────────
INSERT INTO case_audit_log (case_id, event_type, analyst, detail, changed_at)
SELECT id, 'CASE_OPENED', 't.bergmann',
       'Case opened and linked to alerts ALT-00004 and ALT-00013',
       TIMESTAMP '2024-01-26 10:00:00'
FROM investigation_case WHERE case_ref = 'CASE-2024-0003';

INSERT INTO case_audit_log (case_id, event_type, analyst, detail, changed_at)
SELECT id, 'STATUS_CHANGED', 't.bergmann',
       'Status changed from OPEN to UNDER_INVESTIGATION',
       TIMESTAMP '2024-01-26 15:00:00'
FROM investigation_case WHERE case_ref = 'CASE-2024-0003';

INSERT INTO case_audit_log (case_id, event_type, analyst, detail, changed_at)
SELECT id, 'NOTE_ADDED', 't.bergmann',
       'Investigation note added',
       TIMESTAMP '2024-01-27 11:30:00'
FROM investigation_case WHERE case_ref = 'CASE-2024-0003';

INSERT INTO case_audit_log (case_id, event_type, analyst, detail, changed_at)
SELECT id, 'NOTE_ADDED', 't.bergmann',
       'Investigation note added',
       TIMESTAMP '2024-02-02 09:45:00'
FROM investigation_case WHERE case_ref = 'CASE-2024-0003';

INSERT INTO case_audit_log (case_id, event_type, analyst, detail, changed_at)
SELECT id, 'STATUS_CHANGED', 't.bergmann',
       'Status changed from UNDER_INVESTIGATION to PENDING_REVIEW',
       TIMESTAMP '2024-02-03 10:00:00'
FROM investigation_case WHERE case_ref = 'CASE-2024-0003';

-- ── CASE-2024-0004  (6 audit entries — SAR filed) ────────────
INSERT INTO case_audit_log (case_id, event_type, analyst, detail, changed_at)
SELECT id, 'CASE_OPENED', 'j.rahman',
       'Case opened and linked to alert ALT-00006',
       TIMESTAMP '2024-02-02 08:00:00'
FROM investigation_case WHERE case_ref = 'CASE-2024-0004';

INSERT INTO case_audit_log (case_id, event_type, analyst, detail, changed_at)
SELECT id, 'STATUS_CHANGED', 'j.rahman',
       'Status changed from OPEN to UNDER_INVESTIGATION',
       TIMESTAMP '2024-02-02 08:30:00'
FROM investigation_case WHERE case_ref = 'CASE-2024-0004';

INSERT INTO case_audit_log (case_id, event_type, analyst, detail, changed_at)
SELECT id, 'NOTE_ADDED', 'j.rahman',
       'Investigation note added',
       TIMESTAMP '2024-02-02 10:30:00'
FROM investigation_case WHERE case_ref = 'CASE-2024-0004';

INSERT INTO case_audit_log (case_id, event_type, analyst, detail, changed_at)
SELECT id, 'NOTE_ADDED', 'j.rahman',
       'Investigation note added',
       TIMESTAMP '2024-02-03 09:00:00'
FROM investigation_case WHERE case_ref = 'CASE-2024-0004';

INSERT INTO case_audit_log (case_id, event_type, analyst, detail, changed_at)
SELECT id, 'STATUS_CHANGED', 'j.rahman',
       'Status changed from UNDER_INVESTIGATION to PENDING_REVIEW',
       TIMESTAMP '2024-02-03 11:00:00'
FROM investigation_case WHERE case_ref = 'CASE-2024-0004';

INSERT INTO case_audit_log (case_id, event_type, analyst, detail, changed_at)
SELECT id, 'SAR_FILED', 'j.rahman',
       'SAR decision: FILE. Case moved to SAR_FILED.',
       TIMESTAMP '2024-02-04 09:15:00'
FROM investigation_case WHERE case_ref = 'CASE-2024-0004';

-- ── CASE-2024-0005  (4 audit entries — NO_ACTION) ────────────
INSERT INTO case_audit_log (case_id, event_type, analyst, detail, changed_at)
SELECT id, 'CASE_OPENED', 'p.nwosu',
       'Case opened and linked to alert ALT-00007',
       TIMESTAMP '2024-02-04 11:00:00'
FROM investigation_case WHERE case_ref = 'CASE-2024-0005';

INSERT INTO case_audit_log (case_id, event_type, analyst, detail, changed_at)
SELECT id, 'STATUS_CHANGED', 'p.nwosu',
       'Status changed from OPEN to UNDER_INVESTIGATION',
       TIMESTAMP '2024-02-04 11:30:00'
FROM investigation_case WHERE case_ref = 'CASE-2024-0005';

INSERT INTO case_audit_log (case_id, event_type, analyst, detail, changed_at)
SELECT id, 'STATUS_CHANGED', 'p.nwosu',
       'Status changed from UNDER_INVESTIGATION to PENDING_REVIEW',
       TIMESTAMP '2024-02-05 09:00:00'
FROM investigation_case WHERE case_ref = 'CASE-2024-0005';

INSERT INTO case_audit_log (case_id, event_type, analyst, detail, changed_at)
SELECT id, 'SAR_DECISION', 'p.nwosu',
       'SAR decision: NO_ACTION. Case moved to NO_ACTION_TAKEN.',
       TIMESTAMP '2024-02-05 09:45:00'
FROM investigation_case WHERE case_ref = 'CASE-2024-0005';

-- ── CASE-2024-0006  (5 audit entries — CLOSED) ───────────────
INSERT INTO case_audit_log (case_id, event_type, analyst, detail, changed_at)
SELECT id, 'CASE_OPENED', 'j.rahman',
       'Case opened and linked to alert ALT-00009',
       TIMESTAMP '2024-02-09 09:30:00'
FROM investigation_case WHERE case_ref = 'CASE-2024-0006';

INSERT INTO case_audit_log (case_id, event_type, analyst, detail, changed_at)
SELECT id, 'STATUS_CHANGED', 'j.rahman',
       'Status changed from OPEN to UNDER_INVESTIGATION',
       TIMESTAMP '2024-02-09 10:00:00'
FROM investigation_case WHERE case_ref = 'CASE-2024-0006';

INSERT INTO case_audit_log (case_id, event_type, analyst, detail, changed_at)
SELECT id, 'STATUS_CHANGED', 'j.rahman',
       'Status changed from UNDER_INVESTIGATION to PENDING_REVIEW',
       TIMESTAMP '2024-02-12 14:00:00'
FROM investigation_case WHERE case_ref = 'CASE-2024-0006';

INSERT INTO case_audit_log (case_id, event_type, analyst, detail, changed_at)
SELECT id, 'SAR_DECISION', 'j.rahman',
       'SAR decision: NO_ACTION. Case closed.',
       TIMESTAMP '2024-02-13 09:00:00'
FROM investigation_case WHERE case_ref = 'CASE-2024-0006';

INSERT INTO case_audit_log (case_id, event_type, analyst, detail, changed_at)
SELECT id, 'STATUS_CHANGED', 'j.rahman',
       'Status changed to CLOSED',
       TIMESTAMP '2024-02-13 09:01:00'
FROM investigation_case WHERE case_ref = 'CASE-2024-0006';

-- ── CASE-2024-0007  (4 audit entries — mule investigation) ───
INSERT INTO case_audit_log (case_id, event_type, analyst, detail, changed_at)
SELECT id, 'CASE_OPENED', 's.okafor',
       'Case opened and linked to alerts ALT-00008 and ALT-00017',
       TIMESTAMP '2024-02-06 09:00:00'
FROM investigation_case WHERE case_ref = 'CASE-2024-0007';

INSERT INTO case_audit_log (case_id, event_type, analyst, detail, changed_at)
SELECT id, 'STATUS_CHANGED', 's.okafor',
       'Status changed from OPEN to UNDER_INVESTIGATION',
       TIMESTAMP '2024-02-06 09:30:00'
FROM investigation_case WHERE case_ref = 'CASE-2024-0007';

INSERT INTO case_audit_log (case_id, event_type, analyst, detail, changed_at)
SELECT id, 'NOTE_ADDED', 's.okafor',
       'Investigation note added',
       TIMESTAMP '2024-02-07 10:15:00'
FROM investigation_case WHERE case_ref = 'CASE-2024-0007';

INSERT INTO case_audit_log (case_id, event_type, analyst, detail, changed_at)
SELECT id, 'NOTE_ADDED', 's.okafor',
       'Investigation note added',
       TIMESTAMP '2024-02-10 14:30:00'
FROM investigation_case WHERE case_ref = 'CASE-2024-0007';

-- ── CASE-2024-0008  (1 audit entry) ─────────────────────────
INSERT INTO case_audit_log (case_id, event_type, analyst, detail, changed_at)
SELECT id, 'CASE_OPENED', 't.bergmann',
       'Case opened and linked to alert ALT-00010',
       TIMESTAMP '2024-03-08 11:00:00'
FROM investigation_case WHERE case_ref = 'CASE-2024-0008';