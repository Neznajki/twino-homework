create table blacklist
(
	id int auto_increment,
	user_id int null,
	constraint blacklist_pk
		primary key (id),
	constraint blacklist_user_id_fk
		foreign key (user_id) references user (id)
);