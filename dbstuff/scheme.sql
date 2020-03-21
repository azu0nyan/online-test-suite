

create table if not exists User(
  id int not null auto_increment,
  login varchar(64) not null,
  password varchar(64) not null,
  lastLogin timestamp null,
  registeredAt timestamp not null default 'CURRENT_TIMESTAMP',
  firstName varchar(64) null,
  lastName varchar(64) null,
  email varchar(255) null,
  primary key (id),
  constraint unique_login
    unique (login)
);
create table if not exists ProblemSetTemplateAlias(
  id int not null auto_increment,
  alias varchar(255) not null,
  primary key (id),
  constraint unique_alias
    unique (alias)
);
create table if not exists ProblemSetTemplateForUser(
  id int not null auto_increment,
  templateId int not null,
  userid int not null,
  primary key (id)
);
create table if not exists ProblemSetTemplateForAllUsers(
  id int not null auto_increment,
  templateid int not null,
  maxAttempts int null,
  primary key (id),
  constraint unique_templateid
    unique (templateid)
);
create table if not exists ProblemSetInstance(
  id int not null auto_increment,
  templateId int not null,
  userId int not null,
  createdAt timestamp not null default 'CURRENT_TIMESTAMP',
  expiresAt timestamp null,
  status int not null,
  score int not null default '0',
  primary key (id)
);
create table if not exists ProblemInstance(
  id int not null auto_increment,
  templateId int not null,
  problemSetId int not null,
  seed int not null,
  allowedAnswers int not null default '1',
  primary key (id)
);
create table if not exists ProblemTemplateAlias(
  id int not null auto_increment,
  alias varchar(255) not null,
  primary key (id)
);
create table if not exists ProblemInstanceAnswer(
  id int not null auto_increment,
  problemInstanceId int not null,
  answeredAt timestamp not null,
  answer clob not null,
  score int null,
  review varchar(8192) null,
  primary key (id)
);
alter table ProblemSetTemplateForUser
  add constraint ProblemSetTemplateForUser_fk_0_templateId
    foreign key (templateId)
    references ProblemSetTemplateAlias (id);
alter table ProblemSetTemplateForUser
  add constraint ProblemSetTemplateForUser_fk_0_userid
    foreign key (userid)
    references User (id);
alter table ProblemSetTemplateForAllUsers
  add constraint ProblemSetTemplateForAllUsers_fk_0_templateid
    foreign key (templateid)
    references ProblemSetTemplateAlias (id);
alter table ProblemSetInstance
  add constraint ProblemSetInstance_fk_0_templateId
    foreign key (templateId)
    references ProblemSetTemplateAlias (id);
alter table ProblemSetInstance
  add constraint ProblemSetInstance_fk_0_userId
    foreign key (userId)
    references User (id);
alter table ProblemInstance
  add constraint ProblemInstance_fk_0_templateId
    foreign key (templateId)
    references ProblemTemplateAlias (id);
alter table ProblemInstance
  add constraint ProblemInstance_fk_0_problemSetId
    foreign key (problemSetId)
    references ProblemSetInstance (id);
alter table ProblemInstanceAnswer
  add constraint ProblemInstanceAnswer_fk_0_problemInstanceId
    foreign key (problemInstanceId)
    references ProblemInstance (id);