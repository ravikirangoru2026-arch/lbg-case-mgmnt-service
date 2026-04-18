-- =============================================================
-- Flyway Migration: V2__insert_cases.sql
-- Database: H2
-- Description: Seed data from cases.json (8 cases)
-- =============================================================

-- ---------------------------------------------------------------
-- CASES
-- ---------------------------------------------------------------
INSERT INTO cases (case_id, customer_id, priority, status, assigned_analyst, opened_at, sar_decision, sar_rationale)
VALUES
    ('CASE-2024-0001', 'CUST-1042', 'HIGH', 'OPEN', 'j.rahman',
     TIMESTAMP '2024-03-08 08:30:00', NULL, NULL),

    ('CASE-2024-0002', 'CUST-2187', 'HIGH', 'UNDER_INVESTIGATION', 's.okafor',
     TIMESTAMP '2024-01-19 09:00:00', NULL, NULL),

    ('CASE-2024-0003', 'CUST-4455', 'HIGH', 'PENDING_REVIEW', 't.bergmann',
     TIMESTAMP '2024-01-26 10:00:00', NULL, NULL),

    ('CASE-2024-0004', 'CUST-6612', 'HIGH', 'SAR_FILED', 'j.rahman',
     TIMESTAMP '2024-02-02 08:00:00', 'FILE',
     'The customer''s account was accessed from an unrecognised device in a foreign jurisdiction. A transfer of £15,200 was initiated within 4 minutes of login — inconsistent with the customer''s normal behaviour. The customer confirmed via the bank''s fraud line that they did not authorise the transfer. The destination account is linked to three other fraud reports in the past 30 days. Grounds for suspicion are sufficient to file a SAR under POCA 2002 Section 330.'),

    ('CASE-2024-0005', 'CUST-7734', 'LOW', 'NO_ACTION_TAKEN', 'p.nwosu',
     TIMESTAMP '2024-02-04 11:00:00', 'NO_ACTION',
     'The unusual activity alert was triggered by a single transaction of £1,100 that fell outside the customer''s normal spending pattern. On review, the customer provided a satisfactory explanation — this was a payment for annual car insurance renewal. The transaction was to a regulated insurer and the amount is consistent with the stated purpose. No grounds for suspicion found. No further action required.'),

    ('CASE-2024-0006', 'CUST-8821', 'MEDIUM', 'CLOSED', 'j.rahman',
     TIMESTAMP '2024-02-09 09:30:00', 'NO_ACTION',
     'Structuring alert triggered by two cash deposits totalling £7,400. Customer is a self-employed market trader. Bank statements provided show regular cash income consistent with the customer''s stated business activity. Both deposits are within the customer''s established 6-month pattern. No grounds for suspicion. Case closed following MLRO sign-off.'),

    ('CASE-2024-0007', 'CUST-2187', 'HIGH', 'UNDER_INVESTIGATION', 's.okafor',
     TIMESTAMP '2024-02-06 09:00:00', NULL, NULL),

    ('CASE-2024-0008', 'CUST-9043', 'HIGH', 'OPEN', 't.bergmann',
     TIMESTAMP '2024-03-08 11:00:00', NULL, NULL);


-- ---------------------------------------------------------------
-- LINKED ALERTS
-- ---------------------------------------------------------------
INSERT INTO case_linked_alerts (case_id, alert_id) VALUES
    ('CASE-2024-0001', 'ALT-00001'),
    ('CASE-2024-0001', 'ALT-00020'),
    ('CASE-2024-0002', 'ALT-00002'),
    ('CASE-2024-0003', 'ALT-00004'),
    ('CASE-2024-0003', 'ALT-00013'),
    ('CASE-2024-0004', 'ALT-00006'),
    ('CASE-2024-0005', 'ALT-00007'),
    ('CASE-2024-0006', 'ALT-00009'),
    ('CASE-2024-0007', 'ALT-00008'),
    ('CASE-2024-0007', 'ALT-00017'),
    ('CASE-2024-0008', 'ALT-00010');


-- ---------------------------------------------------------------
-- CASE NOTES
-- ---------------------------------------------------------------
INSERT INTO case_notes (case_id, author, note_timestamp, note_text) VALUES
    ('CASE-2024-0003', 't.bergmann', TIMESTAMP '2024-01-27 11:30:00',
     'Sanctions screening confirmed a name match against the OFAC SDN list. Match confidence is 94%. Customer has been temporarily restricted pending MLRO review. Documentation requested from customer on source of funds.'),

    ('CASE-2024-0003', 't.bergmann', TIMESTAMP '2024-02-02 09:45:00',
     'Customer has failed to provide requested documentation within the 5-day window. Secondary review by financial crime team confirms the match is likely a true positive. Escalating to senior investigator for PENDING_REVIEW sign-off.'),

    ('CASE-2024-0004', 'j.rahman', TIMESTAMP '2024-02-02 10:30:00',
     'Customer contacted via fraud line and confirmed account compromise. Transfer of £15,200 to SORT: 20-91-44 ACC: 87654321 not authorised by customer. Receiving account has been flagged in the CIFAS database.'),

    ('CASE-2024-0004', 'j.rahman', TIMESTAMP '2024-02-03 09:00:00',
     'IP address geo-located to a VPN exit node associated with known fraud activity. Device fingerprint is new and does not match any of the customer''s registered devices. Proceeds likely unrecoverable — receiving bank notified.'),

    ('CASE-2024-0007', 's.okafor', TIMESTAMP '2024-02-07 10:15:00',
     'Customer''s account has now been linked to two separate APP scam victims across alerts ALT-00002 and ALT-00008. A third victim report came in via the PSR hotline today, relating to ALT-00017. This account is operating as a mule. Total victim losses across all three cases: £103,000. Account suspended pending investigation.'),

    ('CASE-2024-0007', 's.okafor', TIMESTAMP '2024-02-10 14:30:00',
     'Customer account holder is uncontactable. Last known address confirmed via DVLA as correct but neighbours state the occupant moved out 3 weeks ago. Identity documents used at account opening may be fraudulent — being referred to the ID fraud team for analysis. Progressing to SAR filing.');


-- ---------------------------------------------------------------
-- AUDIT LOG
-- ---------------------------------------------------------------
INSERT INTO case_audit_log (case_id, event_type, event_timestamp, analyst, detail) VALUES
    -- CASE-2024-0001
    ('CASE-2024-0001', 'CASE_OPENED', TIMESTAMP '2024-03-08 08:30:00', 'j.rahman',
     'Case opened and linked to alerts ALT-00001 and ALT-00020'),

    -- CASE-2024-0002
    ('CASE-2024-0002', 'CASE_OPENED',    TIMESTAMP '2024-01-19 09:00:00', 's.okafor',
     'Case opened and linked to alert ALT-00002'),
    ('CASE-2024-0002', 'STATUS_CHANGED', TIMESTAMP '2024-01-19 14:15:00', 's.okafor',
     'Status changed from OPEN to UNDER_INVESTIGATION'),

    -- CASE-2024-0003
    ('CASE-2024-0003', 'CASE_OPENED',    TIMESTAMP '2024-01-26 10:00:00', 't.bergmann',
     'Case opened and linked to alerts ALT-00004 and ALT-00013'),
    ('CASE-2024-0003', 'STATUS_CHANGED', TIMESTAMP '2024-01-26 15:00:00', 't.bergmann',
     'Status changed from OPEN to UNDER_INVESTIGATION'),
    ('CASE-2024-0003', 'NOTE_ADDED',     TIMESTAMP '2024-01-27 11:30:00', 't.bergmann',
     'Investigation note added'),
    ('CASE-2024-0003', 'NOTE_ADDED',     TIMESTAMP '2024-02-02 09:45:00', 't.bergmann',
     'Investigation note added'),
    ('CASE-2024-0003', 'STATUS_CHANGED', TIMESTAMP '2024-02-03 10:00:00', 't.bergmann',
     'Status changed from UNDER_INVESTIGATION to PENDING_REVIEW'),

    -- CASE-2024-0004
    ('CASE-2024-0004', 'CASE_OPENED',    TIMESTAMP '2024-02-02 08:00:00', 'j.rahman',
     'Case opened and linked to alert ALT-00006'),
    ('CASE-2024-0004', 'STATUS_CHANGED', TIMESTAMP '2024-02-02 08:30:00', 'j.rahman',
     'Status changed from OPEN to UNDER_INVESTIGATION'),
    ('CASE-2024-0004', 'NOTE_ADDED',     TIMESTAMP '2024-02-02 10:30:00', 'j.rahman',
     'Investigation note added'),
    ('CASE-2024-0004', 'NOTE_ADDED',     TIMESTAMP '2024-02-03 09:00:00', 'j.rahman',
     'Investigation note added'),
    ('CASE-2024-0004', 'STATUS_CHANGED', TIMESTAMP '2024-02-03 11:00:00', 'j.rahman',
     'Status changed from UNDER_INVESTIGATION to PENDING_REVIEW'),
    ('CASE-2024-0004', 'SAR_FILED',      TIMESTAMP '2024-02-04 09:15:00', 'j.rahman',
     'SAR decision: FILE. Case moved to SAR_FILED.'),

    -- CASE-2024-0005
    ('CASE-2024-0005', 'CASE_OPENED',    TIMESTAMP '2024-02-04 11:00:00', 'p.nwosu',
     'Case opened and linked to alert ALT-00007'),
    ('CASE-2024-0005', 'STATUS_CHANGED', TIMESTAMP '2024-02-04 11:30:00', 'p.nwosu',
     'Status changed from OPEN to UNDER_INVESTIGATION'),
    ('CASE-2024-0005', 'STATUS_CHANGED', TIMESTAMP '2024-02-05 09:00:00', 'p.nwosu',
     'Status changed from UNDER_INVESTIGATION to PENDING_REVIEW'),
    ('CASE-2024-0005', 'SAR_DECISION',   TIMESTAMP '2024-02-05 09:45:00', 'p.nwosu',
     'SAR decision: NO_ACTION. Case moved to NO_ACTION_TAKEN.'),

    -- CASE-2024-0006
    ('CASE-2024-0006', 'CASE_OPENED',    TIMESTAMP '2024-02-09 09:30:00', 'j.rahman',
     'Case opened and linked to alert ALT-00009'),
    ('CASE-2024-0006', 'STATUS_CHANGED', TIMESTAMP '2024-02-09 10:00:00', 'j.rahman',
     'Status changed from OPEN to UNDER_INVESTIGATION'),
    ('CASE-2024-0006', 'STATUS_CHANGED', TIMESTAMP '2024-02-12 14:00:00', 'j.rahman',
     'Status changed from UNDER_INVESTIGATION to PENDING_REVIEW'),
    ('CASE-2024-0006', 'SAR_DECISION',   TIMESTAMP '2024-02-13 09:00:00', 'j.rahman',
     'SAR decision: NO_ACTION. Case closed.'),
    ('CASE-2024-0006', 'STATUS_CHANGED', TIMESTAMP '2024-02-13 09:01:00', 'j.rahman',
     'Status changed to CLOSED'),

    -- CASE-2024-0007
    ('CASE-2024-0007', 'CASE_OPENED',    TIMESTAMP '2024-02-06 09:00:00', 's.okafor',
     'Case opened and linked to alerts ALT-00008 and ALT-00017'),
    ('CASE-2024-0007', 'STATUS_CHANGED', TIMESTAMP '2024-02-06 09:30:00', 's.okafor',
     'Status changed from OPEN to UNDER_INVESTIGATION'),
    ('CASE-2024-0007', 'NOTE_ADDED',     TIMESTAMP '2024-02-07 10:15:00', 's.okafor',
     'Investigation note added'),
    ('CASE-2024-0007', 'NOTE_ADDED',     TIMESTAMP '2024-02-10 14:30:00', 's.okafor',
     'Investigation note added'),

    -- CASE-2024-0008
    ('CASE-2024-0008', 'CASE_OPENED',    TIMESTAMP '2024-03-08 11:00:00', 't.bergmann',
     'Case opened and linked to alert ALT-00010');
