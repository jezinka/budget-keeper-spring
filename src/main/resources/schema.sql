create or replace table bank
(
    id   int         not null primary key,
    name varchar(20) null
);

create or replace table category
(
    id                   int         not null primary key,
    name                 varchar(50) null,
    use_in_yearly_charts bit         null
);

create or replace table expense
(
    id               int          not null primary key,
    transaction_date date         null,
    title            varchar(200) null,
    payee            varchar(200) null,
    amount           double       null,
    category_id      int          null,
    deleted          tinyint      null,
    liability_id     int          null
);

create or replace table liability
(
    id         int         not null primary key,
    name       varchar(50) null,
    start_date date        null,
    end_date   date        null,
    bank_id    int         null
);

create or replace table liability_lookout
(
    id           int          not null primary key,
    date         date         null,
    outcome      float(23, 0) null,
    liability_id int          null
);

create or replace table log
(
    id      int         not null primary key,
    date    datetime    null,
    type    varchar(10) null,
    message longtext    null,
    deleted tinyint     null
);

create or replace table money_amount
(
    date   date         not null        primary key,
    amount float(23, 0) null
);

create or replace table users
(
    user_id  int         not null primary key,
    username varchar(45) null,
    password varchar(64) null,
    role     varchar(45) null,
    enabled  tinyint     null
);

