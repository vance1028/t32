-- 公安证据与枪械管理平台 - 表结构（MySQL）

CREATE TABLE IF NOT EXISTS officers (
    id          BIGINT       NOT NULL AUTO_INCREMENT,
    police_no   VARCHAR(32)  NOT NULL,
    name        VARCHAR(64)  NOT NULL,
    department  VARCHAR(128) NOT NULL DEFAULT '',
    rank_title  VARCHAR(64)  NOT NULL DEFAULT '',
    status      VARCHAR(16)  NOT NULL DEFAULT 'ACTIVE',
    created_at  DATETIME(3)  NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    PRIMARY KEY (id),
    UNIQUE KEY uk_officers_police_no (police_no)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS evidence (
    id           BIGINT       NOT NULL AUTO_INCREMENT,
    evidence_no  VARCHAR(48)  NOT NULL,
    case_no      VARCHAR(48)  NOT NULL,
    name         VARCHAR(128) NOT NULL,
    category     VARCHAR(32)  NOT NULL DEFAULT 'OTHER',
    description  VARCHAR(1000) NOT NULL DEFAULT '',
    status       VARCHAR(16)  NOT NULL DEFAULT 'REGISTERED',
    location     VARCHAR(128) NOT NULL DEFAULT '',
    registered_by BIGINT      NULL,
    created_at   DATETIME(3)  NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    updated_at   DATETIME(3)  NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    PRIMARY KEY (id),
    UNIQUE KEY uk_evidence_no (evidence_no),
    KEY idx_evidence_case (case_no),
    KEY idx_evidence_status (status),
    CONSTRAINT fk_evidence_officer FOREIGN KEY (registered_by) REFERENCES officers (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS custody_records (
    id           BIGINT       NOT NULL AUTO_INCREMENT,
    evidence_id  BIGINT       NOT NULL,
    action       VARCHAR(16)  NOT NULL,
    from_officer BIGINT       NULL,
    to_officer   BIGINT       NULL,
    remark       VARCHAR(500) NOT NULL DEFAULT '',
    occurred_at  DATETIME(3)  NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    PRIMARY KEY (id),
    KEY idx_custody_evidence (evidence_id),
    CONSTRAINT fk_custody_evidence FOREIGN KEY (evidence_id) REFERENCES evidence (id),
    CONSTRAINT fk_custody_from FOREIGN KEY (from_officer) REFERENCES officers (id),
    CONSTRAINT fk_custody_to FOREIGN KEY (to_officer) REFERENCES officers (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS firearms (
    id          BIGINT       NOT NULL AUTO_INCREMENT,
    serial_no   VARCHAR(48)  NOT NULL,
    model       VARCHAR(64)  NOT NULL,
    type        VARCHAR(32)  NOT NULL DEFAULT 'PISTOL',
    caliber     VARCHAR(32)  NOT NULL DEFAULT '',
    status      VARCHAR(16)  NOT NULL DEFAULT 'IN_STORE',
    created_at  DATETIME(3)  NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    updated_at  DATETIME(3)  NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    PRIMARY KEY (id),
    UNIQUE KEY uk_firearm_serial (serial_no),
    KEY idx_firearm_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS firearm_issuances (
    id                      BIGINT      NOT NULL AUTO_INCREMENT,
    firearm_id              BIGINT      NOT NULL,
    officer_id              BIGINT      NOT NULL,
    purpose                 VARCHAR(255) NOT NULL DEFAULT '',
    ammo_batch_id           BIGINT      NULL,
    ammo_issued             INT         NOT NULL DEFAULT 0,
    ammo_returned           INT         NULL,
    ammo_consumed           INT         NOT NULL DEFAULT 0,
    reconciliation_status   VARCHAR(16) NOT NULL DEFAULT 'PENDING',
    issued_at               DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    due_at                  DATETIME(3) NOT NULL,
    returned_at             DATETIME(3) NULL,
    status                  VARCHAR(16) NOT NULL DEFAULT 'ISSUED',
    PRIMARY KEY (id),
    KEY idx_issuance_firearm (firearm_id),
    KEY idx_issuance_officer (officer_id),
    KEY idx_issuance_status (status),
    KEY idx_issuance_batch (ammo_batch_id),
    CONSTRAINT fk_issuance_firearm FOREIGN KEY (firearm_id) REFERENCES firearms (id),
    CONSTRAINT fk_issuance_officer FOREIGN KEY (officer_id) REFERENCES officers (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================
-- 弹药批次表（库存台账）：按弹种、口径、型号、批次管理库存
-- ============================================================
CREATE TABLE IF NOT EXISTS ammo_batches (
    id                  BIGINT       NOT NULL AUTO_INCREMENT,
    batch_no            VARCHAR(64)  NOT NULL,
    caliber             VARCHAR(32)  NOT NULL,
    model               VARCHAR(64)  NOT NULL,
    manufacturer        VARCHAR(128) NOT NULL DEFAULT '',
    production_date     DATE         NULL,
    expiry_date         DATE         NULL,
    initial_quantity    INT          NOT NULL DEFAULT 0,
    current_quantity    INT          NOT NULL DEFAULT 0,
    unit_price          DECIMAL(10,2) NULL,
    status              VARCHAR(16)  NOT NULL DEFAULT 'ACTIVE',
    remark              VARCHAR(500) NOT NULL DEFAULT '',
    version             INT          NOT NULL DEFAULT 0,
    created_at          DATETIME(3)  NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    updated_at          DATETIME(3)  NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    PRIMARY KEY (id),
    UNIQUE KEY uk_ammo_batch_no (batch_no),
    KEY idx_ammo_caliber (caliber),
    KEY idx_ammo_status (status),
    KEY idx_ammo_expiry (expiry_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================
-- 弹药出入库流水表：每一笔库存变动都有记录
-- ============================================================
CREATE TABLE IF NOT EXISTS ammo_transactions (
    id                  BIGINT       NOT NULL AUTO_INCREMENT,
    batch_id            BIGINT       NOT NULL,
    transaction_type    VARCHAR(16)  NOT NULL,
    quantity            INT          NOT NULL,
    balance_after       INT          NOT NULL,
    reference_type      VARCHAR(32)  NULL,
    reference_id        BIGINT       NULL,
    operator_id         BIGINT       NULL,
    remark              VARCHAR(500) NOT NULL DEFAULT '',
    occurred_at         DATETIME(3)  NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    PRIMARY KEY (id),
    KEY idx_trans_batch (batch_id),
    KEY idx_trans_type (transaction_type),
    KEY idx_trans_ref (reference_type, reference_id),
    KEY idx_trans_time (occurred_at),
    CONSTRAINT fk_trans_batch FOREIGN KEY (batch_id) REFERENCES ammo_batches (id),
    CONSTRAINT fk_trans_operator FOREIGN KEY (operator_id) REFERENCES officers (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================
-- 弹药消耗核销表：记录实际消耗的弹药去向
-- ============================================================
CREATE TABLE IF NOT EXISTS ammo_consumptions (
    id                  BIGINT       NOT NULL AUTO_INCREMENT,
    issuance_id         BIGINT       NOT NULL,
    batch_id            BIGINT       NOT NULL,
    consumed_quantity   INT          NOT NULL,
    consumption_type    VARCHAR(16)  NOT NULL,
    reason              VARCHAR(500) NOT NULL DEFAULT '',
    recorded_by         BIGINT       NULL,
    created_at          DATETIME(3)  NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    PRIMARY KEY (id),
    KEY idx_consumption_issuance (issuance_id),
    KEY idx_consumption_batch (batch_id),
    KEY idx_consumption_type (consumption_type),
    CONSTRAINT fk_consumption_issuance FOREIGN KEY (issuance_id) REFERENCES firearm_issuances (id),
    CONSTRAINT fk_consumption_batch FOREIGN KEY (batch_id) REFERENCES ammo_batches (id),
    CONSTRAINT fk_consumption_recorded FOREIGN KEY (recorded_by) REFERENCES officers (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================
-- 弹药盘点表：记录盘点记录
-- ============================================================
CREATE TABLE IF NOT EXISTS ammo_inventory_checks (
    id                  BIGINT       NOT NULL AUTO_INCREMENT,
    batch_id            BIGINT       NOT NULL,
    check_date            DATE         NOT NULL,
    book_quantity       INT          NOT NULL,
    actual_quantity     INT          NOT NULL,
    difference          INT          NOT NULL DEFAULT 0,
    check_status        VARCHAR(16)  NOT NULL DEFAULT 'PENDING',
    reason              VARCHAR(500) NOT NULL DEFAULT '',
    checked_by          BIGINT       NULL,
    adjusted_by         BIGINT       NULL,
    adjusted_at         DATETIME(3)  NULL,
    created_at          DATETIME(3)  NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    PRIMARY KEY (id),
    KEY idx_check_batch (batch_id),
    KEY idx_check_status (check_status),
    KEY idx_check_date (check_date),
    CONSTRAINT fk_check_batch FOREIGN KEY (batch_id) REFERENCES ammo_batches (id),
    CONSTRAINT fk_check_checked_by FOREIGN KEY (checked_by) REFERENCES officers (id),
    CONSTRAINT fk_check_adjusted_by FOREIGN KEY (adjusted_by) REFERENCES officers (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================
-- 射击记录表：留存实际射击记录
-- ============================================================
CREATE TABLE IF NOT EXISTS shooting_records (
    id                  BIGINT       NOT NULL AUTO_INCREMENT,
    issuance_id         BIGINT       NULL,
    officer_id          BIGINT       NOT NULL,
    firearm_id          BIGINT       NOT NULL,
    batch_id            BIGINT       NOT NULL,
    shots_fired         INT          NOT NULL,
    shooting_purpose    VARCHAR(32)  NOT NULL,
    location            VARCHAR(128) NOT NULL DEFAULT '',
    occurred_at         DATETIME(3)  NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    recorded_by         BIGINT       NULL,
    remark              VARCHAR(500) NOT NULL DEFAULT '',
    created_at          DATETIME(3)  NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    PRIMARY KEY (id),
    KEY idx_shooting_issuance (issuance_id),
    KEY idx_shooting_officer (officer_id),
    KEY idx_shooting_firearm (firearm_id),
    KEY idx_shooting_batch (batch_id),
    KEY idx_shooting_time (occurred_at),
    CONSTRAINT fk_shooting_issuance FOREIGN KEY (issuance_id) REFERENCES firearm_issuances (id),
    CONSTRAINT fk_shooting_officer FOREIGN KEY (officer_id) REFERENCES officers (id),
    CONSTRAINT fk_shooting_firearm FOREIGN KEY (firearm_id) REFERENCES firearms (id),
    CONSTRAINT fk_shooting_batch FOREIGN KEY (batch_id) REFERENCES ammo_batches (id),
    CONSTRAINT fk_shooting_recorded FOREIGN KEY (recorded_by) REFERENCES officers (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
