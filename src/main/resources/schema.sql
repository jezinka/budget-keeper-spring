create table IF NOT EXISTS bank
(
    id   int auto_increment
        primary key,
    name varchar(20) null
);

create table IF NOT EXISTS category
(
    id   int auto_increment
        primary key,
    name varchar(50) null
);

create table IF NOT EXISTS liability
(
    id             int auto_increment
        primary key,
    name           varchar(50)          null,
    start_date     date                 null,
    end_date       date                 null,
    bank_id        int                  null,
    is_crisis_fund tinyint(1) default 0 null,
    constraint liability_bank_id_fk
        foreign key (bank_id) references bank (id)
);

create table IF NOT EXISTS liability_lookout
(
    id           int auto_increment
        primary key,
    date         date  not null,
    outcome      float not null,
    liability_id int   null,
    constraint liability_lookout_passive_item_id_fk
        foreign key (liability_id) references liability (id)
);

create table IF NOT EXISTS log
(
    id         int auto_increment
        primary key,
    date       datetime             null,
    type       varchar(10)          null,
    message    longtext             null,
    is_deleted tinyint(1) default 0 null
);

create table IF NOT EXISTS money_amount
(
    id     int auto_increment
        primary key,
    month  date  not null,
    amount float not null
);

create table IF NOT EXISTS transaction
(
    id               int auto_increment
        primary key,
    transaction_date date                 null,
    title            varchar(200)         null,
    payee            varchar(200)         null,
    amount           float                null,
    category_id      int                  null,
    is_deleted       tinyint(1) default 0 null,
    constraint transaction_category_id_fk
        foreign key (category_id) references category (id)
);

create table IF NOT EXISTS users
(
    user_id  int auto_increment
        primary key,
    username varchar(45) not null,
    password varchar(64) not null,
    role     varchar(45) not null,
    enabled  tinyint     null
);

