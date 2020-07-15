create table user
(
	id int auto_increment,
	unique_id varchar(36) COLLATE latin1_bin not null,
	name varchar(64) COLLATE utf8_general_ci not null,
	surname varchar(64) COLLATE utf8_general_ci not null,
	created timestamp not null,
    PRIMARY KEY (`id`),
    UNIQUE KEY `user_unique_id_uindex` (`unique_id`),
    UNIQUE KEY `users_name_surname_uindex` (`name`,`surname`)
);

create table loan
(
    id int auto_increment,
    user_id int not null,
    amount float not null,
    term_days int not null,
    created timestamp default current_timestamp not null,
    constraint loan_pk
        primary key (id),
    constraint loan_user_id_fk
        foreign key (user_id) references user (id)
);