CREATE TABLE transactions
(
    "id"                        SERIAL              NOT NULL,
    "created"                   TIMESTAMPTZ         NOT NULL,

    "account_from"              BIGINT              NOT NULL,
    "account_to"                BIGINT              NOT NULL,
    "currency_short_name"       VARCHAR(5)          NOT NULL,
    "sum"                       DOUBLE PRECISION    NOT NULL,

    CONSTRAINT transactions__pkey
        PRIMARY KEY (id)
);