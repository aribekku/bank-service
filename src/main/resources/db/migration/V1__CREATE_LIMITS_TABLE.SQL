CREATE TABLE limits
(
    "id"                        SERIAL              NOT NULL,
    "created"                   TIMESTAMPTZ         NOT NULL,

    "limit_setting_date"        TIMESTAMPTZ         DEFAULT NULL,
    "limit_amount"              NUMERIC             NOT NULL,
    "limit_balance"             NUMERIC             NOT NULL,
    "currency_short_name"       VARCHAR(5)          NOT NULL,
    "expense_category"          VARCHAR(15)         NOT NULL,

    CONSTRAINT limits__pkey
        PRIMARY KEY (id)
);