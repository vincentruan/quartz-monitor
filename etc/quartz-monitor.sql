--drop table quartz_config;

create table if not exists quartz_config (
id int primary key auto_increment,
uuid char(36) not null,
server_container varchar(64) not null,
instance_name varchar(64),
host varchar(32) not null,
port int,
jmx_username varchar(32),
jmx_password varchar(2048)
);

show tables;
desc quartz_config;
select * from quartz_config;
