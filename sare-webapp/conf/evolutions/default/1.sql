# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table web_session (
  uuid                      VARBINARY(16) not null,
  owner_id                  varchar(255),
  remote_address            varchar(255),
  created                   datetime,
  updated                   datetime,
  constraint pk_web_session primary key (uuid))
;




# --- !Downs

SET FOREIGN_KEY_CHECKS=0;

drop table web_session;

SET FOREIGN_KEY_CHECKS=1;

