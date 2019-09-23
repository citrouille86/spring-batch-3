DROP TABLE people IF EXISTS;
DROP TABLE marksheet IF EXISTS;
DROP TABLE student IF EXISTS;

CREATE TABLE people  (
    person_id BIGINT IDENTITY NOT NULL PRIMARY KEY,
    first_name VARCHAR(20),
    last_name VARCHAR(20)
);

CREATE TABLE marksheet  (
    stdId VARCHAR(20),
    totalSubMark INTEGER
);

CREATE TABLE student  (
    stdId VARCHAR(20),
    subMarkOne INTEGER,
    subMarkTwo INTEGER
);

INSERT INTO student values('student-1',10,15);
INSERT INTO student values('student-2',12,17);
INSERT INTO student values('student-3',15,13);
INSERT INTO student values('student-4',11,13);
INSERT INTO student values('student-5',3,15);
INSERT INTO student values('student-6',15,12);
INSERT INTO student values('student-7',17,14);
INSERT INTO student values('student-8',10,15);
INSERT INTO student values('student-9',12,10);
INSERT INTO student values('student-10',13,16);
INSERT INTO student values('student-11',16,12);
INSERT INTO student values('student-12',9,11);
INSERT INTO student values('student-13',8,13);
INSERT INTO student values('student-14',14,10);
INSERT INTO student values('student-15',18,8);
INSERT INTO student values('student-16',6,12);
INSERT INTO student values('student-17',10,7);