insert into user (`id`, `name`, `email`, `password`, `birthday`, `created_at`, `updated_at`) values (1, 'TEST', 'test@naver.com', '1234', null, now(), now());

insert into board (`id`, `user_id`,`title`, `content`, `created_at`, `updated_at`) values (1, 1, '제목1', '내용1', now(), now());

insert into board (`id`, `user_id`,`title`, `content`, `created_at`, `updated_at`) values (2, 1, '제목2', '내용2', now(), now());

insert into board (`id`, `user_id`,`title`, `content`, `created_at`, `updated_at`) values (3, 1, '제목3', '내용2-1', now(), now());

insert into board (`id`, `user_id`,`title`, `content`, `created_at`, `updated_at`) values (4, 1, '제목4', '내용3', now(), now());

-- call next value for hibernate_sequence; 테이블 상관없이 hibernate가 순서대로 id를 증가시킴
insert into board (`id`, `user_id`, `title`, `content`, `created_at`, `updated_at`) values (5, 1, '제목5', '내용5', now(), now());

