CREATE TABLE limits
(
    "id"                        SERIAL              NOT NULL,

    "created"                   TIMESTAMPTZ         NOT NULL,
    "is_deleted"                BOOLEAN             DEFAULT FALSE,

    "limit_setting_date"        TIMESTAMPTZ         DEFAULT NULL,
    "limit_amount"              DOUBLE PRECISION    NOT NULL,
    "limit_balance"             DOUBLE PRECISION    NOT NULL,
    "currency_short_name"       VARCHAR(5)          NOT NULL,
    "expense_category"          VARCHAR(15)         NOT NULL,
    "transaction_id"            INTEGER             DEFAULT NULL,
    "limit_exceeded"            BOOLEAN             DEFAULT FALSE,

    CONSTRAINT limits__pkey
        PRIMARY KEY (id),

    CONSTRAINT m2m_place__fk
        FOREIGN KEY (transaction_id)
            REFERENCES transactions (id)
            ON DELETE RESTRICT
            ON UPDATE CASCADE
);