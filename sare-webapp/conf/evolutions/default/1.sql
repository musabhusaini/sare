# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table prog_obs_tokens (
  uuid                      VARBINARY(16) not null,
  session_uuid              VARBINARY(16),
  progress                  double,
  created                   datetime,
  updated                   datetime not null,
  constraint pk_prog_obs_tokens primary key (uuid))
;

create table web_sessions (
  uuid                      VARBINARY(16) not null,
  owner_uuid                VARBINARY(16),
  status                    varchar(1),
  remote_address            varchar(255),
  created                   datetime,
  refresh_token             varchar(255),
  updated                   datetime not null,
  constraint ck_web_sessions_status check (status in ('a','t','k')),
  constraint pk_web_sessions primary key (uuid))
;

create table web_users (
  uuid                      VARBINARY(16) not null,
  provider_id               VARCHAR(2048),
  profile                   TEXT,
  created                   datetime,
  updated                   datetime not null,
  constraint pk_web_users primary key (uuid))
;

alter table prog_obs_tokens add constraint fk_prog_obs_tokens_session_1 foreign key (session_uuid) references web_sessions (uuid) on delete restrict on update restrict;
create index ix_prog_obs_tokens_session_1 on prog_obs_tokens (session_uuid);
alter table web_sessions add constraint fk_web_sessions_owner_2 foreign key (owner_uuid) references web_users (uuid) on delete restrict on update restrict;
create index ix_web_sessions_owner_2 on web_sessions (owner_uuid);



# --- !Downs

SET FOREIGN_KEY_CHECKS=0;

drop table prog_obs_tokens;

drop table web_sessions;

drop table web_users;

SET FOREIGN_KEY_CHECKS=1;

