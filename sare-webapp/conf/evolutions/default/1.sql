# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table progress_observer_token (
  uuid                      VARBINARY(16) not null,
  session_uuid              VARBINARY(16),
  progress                  double,
  created                   datetime,
  updated                   datetime not null,
  constraint pk_progress_observer_token primary key (uuid))
;

create table web_session (
  uuid                      VARBINARY(16) not null,
  owner_id                  varchar(255),
  remote_address            varchar(255),
  created                   datetime,
  refresh_token             varchar(255),
  updated                   datetime not null,
  constraint pk_web_session primary key (uuid))
;

alter table progress_observer_token add constraint fk_progress_observer_token_session_1 foreign key (session_uuid) references web_session (uuid) on delete restrict on update restrict;
create index ix_progress_observer_token_session_1 on progress_observer_token (session_uuid);



# --- !Downs

SET FOREIGN_KEY_CHECKS=0;

drop table progress_observer_token;

drop table web_session;

SET FOREIGN_KEY_CHECKS=1;

