insert into user (email, name, password, enabled) values ('test@test.com', 'TEST_USER', '$2a$10$U.nzjUm4CZIpya53VPo0le81XuqAMJ7VJ9qjS.zYzKcELRjJSDZHS', true);

insert into authority (authority, authorities_email) values ('ROLE_USER', 'test@test.com');