delete from user_role;
delete from usr;

insert into usr(id, active, password, username) values
(1, true , '$2a$08$6fOXWv2p7udA6iJKFEWAIeouhX/MRvkX3ULWntOycAaJsWEEnDOGK', 'www'),
(2, true, '$2a$08$6fOXWv2p7udA6iJKFEWAIeouhX/MRvkX3ULWntOycAaJsWEEnDOGK', 'qqq');

insert into user_role(user_id, roles) values
(1, 'USER'), (1, 'ADMIN'),
(2, 'USER');