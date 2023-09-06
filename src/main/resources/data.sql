-- DROP TABLE IF EXISTS tech_stack;
--
-- -- 기술스택 테이블 생성
-- CREATE TABLE tech_stack(
--     id integer primary key autoincrement ,
--     name varchar(45) default NULL
-- );

-- 기술스택 데이터 추가
INSERT INTO tech_stack(name)
VALUES
    ('java'),
    ('javaScript'),
    ('typeScript'),
    ('react'),
    ('vue'),
    ('svelte'),
    ('nextjs'),
    ('nodejs'),
    ('spring'),
    ('go'),
    ('nestjs'),
    ('kotlin'),
    ('express'),
    ('mySQL'),
    ('mongoDB'),
    ('python'),
    ('django'),
    ('php'),
    ('graphQL'),
    ('firebase');

-- recruit 데이터 추가
INSERT INTO recruit(created_at, last_modified_at, recruit_member_num, team_id, team_manager_id, team_recruit_details, title)
VALUES
    (DATETIME('now'), DATETIME('now'), 5, 1, 1, 'Spring 으로 플젝 같이 하실 사람 구합니다', 'Spring 구인 공고'),
    (DATETIME('now'), DATETIME('now'), 5, 2, 2, 'React 로 플젝 같이 하실 사람 구합니다', 'React 구인 공고'),
    (DATETIME('now'), DATETIME('now'), 5, 3, 3, 'Django 로 플젝 같이 하실 사람 구합니다', 'Django 구인 공고'),
    (DATETIME('now'), DATETIME('now'), 5, 4, 4, '백엔드 플젝 같이 하실 사람 구합니다', '백엔드 구인 공고'),
    (DATETIME('now'), DATETIME('now'), 5, 5, 5, '프론트엔드 플젝 같이 하실 사람 구합니다', '프론트엔드 구인 공고');

-- recruit 데이터 삭제
DELETE FROM recruit
WHERE id BETWEEN 1 AND 5;

-- project_result 데이터 추가
INSERT INTO project_result(created_at, last_modified_at, description, github, give_up_rate, open, team_id, team_manager_id)
VALUES
    (DATETIME('now'), DATETIME('now'), '저희 팀은 Spring 을 이용한 플젝을 머시기머시기', 'https://github.com/moonwonki', 0, true, 1, 1),
    (DATETIME('now'), DATETIME('now'), '저희 팀은 React 를 이용한 플젝을 머시기머시기', 'https://github.com/wonkimoon', 0, true, 2, 2),
    (DATETIME('now'), DATETIME('now'), '저희 팀은 Django 를 이용한 플젝을 머시기머시기', 'https://github.com/wonkigod', 0, true, 3, 3),
    (DATETIME('now'), DATETIME('now'), '저희 팀은 백엔드 관련 플젝을 머시기머시기', 'https://github.com/godwonki', 0, true, 4, 4),
    (DATETIME('now'), DATETIME('now'), '저희 팀은 프론트엔드 관련 플젝을 머시기머시기', 'https://github.com/wongkee', 0, true, 5, 5);

-- project_result 데이터 삭제
DELETE FROM project_result
WHERE id BETWEEN 1 AND 5;

-- review 데이터 추가
INSERT INTO review(created_at, last_modified_at, describe, give_up, grade, project_result_id, role, user_id)
VALUES
    (DATETIME('now'), DATETIME('now'), '정말 힘들었지만 보람찼어요', 0, 1000, 1, 'User 기능 구현 담당', 1),
    (DATETIME('now'), DATETIME('now'), '두 번 다신 못 할 것같아요', 0, 1000, 2, '디자인 기능 담당', 2),
    (DATETIME('now'), DATETIME('now'), '밥먹듯이 하다보니 별거 아니네요', 0, 1000, 3, '게시판 기능 구현 담당', 3),
    (DATETIME('now'), DATETIME('now'), '머리가 빠져서 피부과에 갔습니다', 0, 1000, 4, '데이터 관리 담당', 4),
    (DATETIME('now'), DATETIME('now'), '눈알이 빠져서 안과에 갔습니다', 0, 1000, 5, 'UI/UX 기능 구현 담당', 5);

-- review 데이터 삭제
DELETE FROM review
WHERE id BETWEEN 1 AND 5;

-- user_tech_stack 데이터 추가
INSERT INTO user_tech_stack(skilled, user_id, tech_stack_id)
VALUES
    ('Spring', 1, 9),
    ('React', 2, 4),
    ('Django', 3, 17),
    ('Java', 4, 1),
    ('JavaScript', 5, 2);

-- user_tech_stack 데이터 삭제
DELETE FROM user_tech_stack
WHERE id BETWEEN 1 AND 5;

-- team_tech_stack 데이터 추가
INSERT INTO team_tech_stack(team_id, tech_stack_id)
VALUES
    (1, 9),
    (2, 4),
    (3, 17),
    (4, 1),
    (5, 2);

-- team_tech_stack 데이터 삭제
DELETE FROM team_tech_stack
WHERE id BETWEEN 1 AND 5;