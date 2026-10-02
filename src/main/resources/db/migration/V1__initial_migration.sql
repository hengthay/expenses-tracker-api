create table users
(
    id  bigint auto_increment
        primary key,
    email      varchar(255) UNIQUE                    not null,
    username   varchar(255)                           not null,
    password   varchar(255)                           not null,
    created_at timestamp  default current_timestamp   not null,
    updated_at timestamp  default current_timestamp   not null
);

create table accounts
(
    id         binary(16)    default (uuid_to_bin(uuid())) not null
        primary key,
    user_id    bigint                                not null,
    name       varchar(100)                          not null,
    type       varchar(50)                           not null,
    balance    decimal(10, 2)                        not null,
    created_at timestamp default current_timestamp   not null,
    updated_at timestamp default current_timestamp   not null,
    constraint accounts_users_id_fk
        foreign key (user_id) references users (id)
);

create table categories
(
    id         bigint auto_increment
        primary key,
    name       varchar(100)  UNIQUE                not null,
    created_at timestamp default current_timestamp not null
);

create table transactions
(
    id               binary(16) default (uuid_to_bin(uuid())) not null
        primary key,
    user_id          bigint                                 not null,
    account_id       binary(16)                             not null,
    category_id      bigint                                 not null,
    amount           decimal(10, 2)                         not null,
    type             varchar(50)                            not null,
    transaction_date DATE                                   not null,
    notes            text                                   null,
    created_at       timestamp  default current_timestamp   not null,
    updated_at       timestamp  default current_timestamp   not null,
    constraint transactions_accounts_id_fk
        foreign key (account_id) references accounts (id),
    constraint transactions_categories_id_fk
        foreign key (category_id) references categories (id),
    constraint transactions_users_id_fk
        foreign key (user_id) references users (id)
);



