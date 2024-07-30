CREATE TABLE transactions
(
    "id"                        SERIAL              NOT NULL,
    "created"                   TIMESTAMPTZ         NOT NULL,

    "account_from"              BIGSERIAL              NOT NULL,
    "account_to"                BIGSERIAL              NOT NULL,
    "currency_short_name"       VARCHAR(5)          NOT NULL,
    "sum"                       NUMERIC             NOT NULL,
    "expense_category"          VARCHAR(15)         NOT NULL,
    "limit_id"                  SERIAL             NOT NULL,
    "limit_exceeded"            BOOLEAN             DEFAULT FALSE,

    CONSTRAINT transactions__pkey
        PRIMARY KEY (id),

    CONSTRAINT m2m_place_fk
        FOREIGN KEY (limit_id)
            REFERENCES limits (id)
            ON DELETE RESTRICT
            ON UPDATE CASCADE
);