# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table web_session (
  uuid                      VARBINARY(16) not null,
  owner_id                  varchar(255),
  remote_address            varchar(255),
  created                   timestamp,
  updated                   timestamp,
  constraint pk_web_session primary key (uuid))
;

create sequence web_session_seq;




# --- !Downs

SET REFERENTIAL_INTEGRITY FALSE;

drop table if exists web_session;

SET REFERENTIAL_INTEGRITY TRUE;

drop sequence if exists web_session_seq;

