-- Sakura Japanese Language School sample data
--
-- Demo credentials for every seeded account:
-- username: see the generated seed_* usernames below
-- password: SakuraDemo123!
--
-- This script is intentionally isolated and repeatable:
-- * every generated username/email/course/batch uses a seed_ or Seed prefix;
-- * existing rows are never updated or deleted;
-- * rerunning the script does not create duplicate seeded rows.

SET NAMES utf8mb4;
START TRANSACTION;

-- Reuse the application's existing role model.
INSERT INTO roles (role)
SELECT 'ADMIN'
WHERE NOT EXISTS (
    SELECT 1 FROM roles WHERE role = 'ADMIN'
);

INSERT INTO roles (role)
SELECT 'TEACHER'
WHERE NOT EXISTS (
    SELECT 1 FROM roles WHERE role = 'TEACHER'
);

INSERT INTO roles (role)
SELECT 'STUDENT'
WHERE NOT EXISTS (
    SELECT 1 FROM roles WHERE role = 'STUDENT'
);

-- BCrypt("SakuraDemo123!").
-- The same hash is used only to keep the local sample dataset easy to use.
INSERT INTO users (username, email, password, enabled)
SELECT 'seed_admin', 'seed.admin@sakura.example',
       '$2a$10$bejrUozj4x7eOYr2EZ2f5.Aq5UISXeUpi4UNEy882akiOapaxEsjy', 1
WHERE NOT EXISTS (
    SELECT 1 FROM users
    WHERE username = 'seed_admin'
       OR email = 'seed.admin@sakura.example'
);

INSERT INTO users (username, email, password, enabled)
WITH RECURSIVE teacher_numbers AS (
    SELECT 1 AS number
    UNION ALL
    SELECT number + 1 FROM teacher_numbers WHERE number < 8
)
SELECT CONCAT('seed_teacher', LPAD(number, 2, '0')),
       CONCAT('seed.teacher', LPAD(number, 2, '0'), '@sakura.example'),
       '$2a$10$bejrUozj4x7eOYr2EZ2f5.Aq5UISXeUpi4UNEy882akiOapaxEsjy',
       1
FROM teacher_numbers
WHERE NOT EXISTS (
    SELECT 1 FROM users existing
    WHERE existing.username = CONCAT('seed_teacher', LPAD(teacher_numbers.number, 2, '0'))
       OR existing.email = CONCAT('seed.teacher', LPAD(teacher_numbers.number, 2, '0'), '@sakura.example')
);

INSERT INTO users (username, email, password, enabled)
WITH RECURSIVE student_numbers AS (
    SELECT 1 AS number
    UNION ALL
    SELECT number + 1 FROM student_numbers WHERE number < 40
)
SELECT CONCAT('seed_student', LPAD(number, 2, '0')),
       CONCAT('seed.student', LPAD(number, 2, '0'), '@sakura.example'),
       '$2a$10$bejrUozj4x7eOYr2EZ2f5.Aq5UISXeUpi4UNEy882akiOapaxEsjy',
       1
FROM student_numbers
WHERE NOT EXISTS (
    SELECT 1 FROM users existing
    WHERE existing.username = CONCAT('seed_student', LPAD(student_numbers.number, 2, '0'))
       OR existing.email = CONCAT('seed.student', LPAD(student_numbers.number, 2, '0'), '@sakura.example')
);

-- Assign exactly one sample role to each generated account.
INSERT INTO user_role (user_id, role_id)
SELECT u.id,
       r.id
FROM users u
JOIN roles r
  ON r.role = CASE
        WHEN u.username = 'seed_admin' THEN 'ADMIN'
        WHEN u.username LIKE 'seed_teacher%' THEN 'TEACHER'
        WHEN u.username LIKE 'seed_student%' THEN 'STUDENT'
     END
WHERE (u.username = 'seed_admin'
   OR u.username LIKE 'seed_teacher%'
   OR u.username LIKE 'seed_student%'
  )
  AND NOT EXISTS (
      SELECT 1
      FROM user_role existing
      WHERE existing.user_id = u.id
        AND existing.role_id = r.id
  );

-- Teacher profiles for the eight generated teacher accounts.
INSERT INTO teacher_profile
    (full_name, phone, specialization, qualification, active, user_id)
SELECT CONCAT('Seed Teacher ', LPAD(CAST(SUBSTRING(u.username, 13) AS UNSIGNED), 2, '0')),
       CONCAT('09', LPAD(CAST(SUBSTRING(u.username, 13) AS UNSIGNED), 9, '0')),
       CASE MOD(CAST(SUBSTRING(u.username, 13) AS UNSIGNED), 4)
           WHEN 0 THEN 'JLPT Preparation'
           WHEN 1 THEN 'Japanese Language'
           WHEN 2 THEN 'Conversation Practice'
           ELSE 'Business Japanese'
       END,
       CASE MOD(CAST(SUBSTRING(u.username, 13) AS UNSIGNED), 3)
           WHEN 0 THEN 'JLPT N1'
           WHEN 1 THEN 'JLPT N2'
           ELSE 'JLPT N3'
       END,
       1,
       u.id
FROM users u
WHERE u.username LIKE 'seed_teacher%'
  AND NOT EXISTS (
      SELECT 1 FROM teacher_profile existing WHERE existing.user_id = u.id
  );

-- Student profiles for the forty generated student accounts.
INSERT INTO student_profile (full_name, phone, address, user_id)
SELECT CONCAT('Seed Student ', LPAD(CAST(SUBSTRING(u.username, 13) AS UNSIGNED), 2, '0')),
       CONCAT('09', LPAD(CAST(SUBSTRING(u.username, 13) AS UNSIGNED), 9, '0')),
       CASE MOD(CAST(SUBSTRING(u.username, 13) AS UNSIGNED), 5)
           WHEN 0 THEN 'Yangon, Myanmar'
           WHEN 1 THEN 'Mandalay, Myanmar'
           WHEN 2 THEN 'Naypyidaw, Myanmar'
           WHEN 3 THEN 'Bago, Myanmar'
           ELSE 'Mawlamyine, Myanmar'
       END,
       u.id
FROM users u
WHERE u.username LIKE 'seed_student%'
  AND NOT EXISTS (
      SELECT 1 FROM student_profile existing WHERE existing.user_id = u.id
  );

-- Twelve courses spanning beginner, intermediate, exam, and practical study.
INSERT INTO course (name, level, description, duration_months, fee, active)
SELECT c.name, c.level, c.description, c.duration_months, c.fee, 1
FROM (
    SELECT 'Seed Japanese N5' AS name, 'N5' AS level,
           'Beginner Japanese and JLPT N5 basics.' AS description,
           6 AS duration_months, 180000.00 AS fee
    UNION ALL SELECT 'Seed Japanese N4', 'N4',
           'Elementary Japanese and JLPT N4 practice.', 6, 220000.00
    UNION ALL SELECT 'Seed Japanese N3', 'N3',
           'Intermediate grammar, reading, and listening.', 7, 260000.00
    UNION ALL SELECT 'Seed Japanese N2', 'N2',
           'Upper-intermediate JLPT N2 preparation.', 8, 320000.00
    UNION ALL SELECT 'Seed Japanese N1', 'N1',
           'Advanced Japanese and JLPT N1 preparation.', 9, 380000.00
    UNION ALL SELECT 'Seed Conversation', 'CONVERSATION',
           'Practical speaking and listening practice.', 3, 150000.00
    UNION ALL SELECT 'Seed Business Japanese', 'BUSINESS',
           'Japanese for meetings, email, and workplace.', 4, 240000.00
    UNION ALL SELECT 'Seed Kanji Workshop', 'KANJI',
           'Kanji reading, writing, and memory practice.', 3, 140000.00
    UNION ALL SELECT 'Seed JLPT Prep', 'JLPT',
           'Exam-focused JLPT drills and mock tests.', 4, 210000.00
    UNION ALL SELECT 'Seed Kids Japanese', 'KIDS',
           'Japanese lessons for young learners.', 4, 160000.00
    UNION ALL SELECT 'Seed Travel Japanese', 'TRAVEL',
           'Useful Japanese for travelers.', 2, 95000.00
    UNION ALL SELECT 'Seed Writing Workshop', 'WRITING',
           'Japanese composition and structured writing.', 3, 175000.00
) c
WHERE NOT EXISTS (
    SELECT 1 FROM course existing WHERE existing.name = c.name
);

-- Twenty-four batches: two batches for each generated course.
INSERT INTO class_batch
    (batch_name, class_type, start_date, end_date, schedule, capacity, status, course_id, teacher_id)
SELECT b.batch_name,
       b.class_type,
       b.start_date,
       b.end_date,
       b.schedule,
       b.capacity,
       b.status,
       c.id,
       tp.id
FROM (
    SELECT 'Seed Batch 01' AS batch_name, 'IN_PERSON' AS class_type,
           '2026-12-05' AS start_date, '2027-03-05' AS end_date,
           'Saturday and Sunday, 9:00 AM - 11:00 AM' AS schedule,
           20 AS capacity, 'OPEN' AS status, 'Seed Japanese N5' AS course_name, 1 AS teacher_number
    UNION ALL SELECT 'Seed Batch 02', 'ONLINE', '2026-11-01', '2027-02-01',
           'Monday and Wednesday, 6:30 PM - 8:00 PM', 25, 'ONGOING', 'Seed Japanese N5', 2
    UNION ALL SELECT 'Seed Batch 01', 'IN_PERSON', '2026-12-06', '2027-03-06',
           'Sunday, 1:00 PM - 3:00 PM', 20, 'OPEN', 'Seed Japanese N4', 3
    UNION ALL SELECT 'Seed Batch 02', 'ONLINE', '2026-10-15', '2027-01-15',
           'Tuesday and Thursday, 6:30 PM - 8:00 PM', 25, 'ONGOING', 'Seed Japanese N4', 4
    UNION ALL SELECT 'Seed Batch 01', 'IN_PERSON', '2027-01-09', '2027-05-09',
           'Saturday, 9:00 AM - 12:00 PM', 20, 'OPEN', 'Seed Japanese N3', 5
    UNION ALL SELECT 'Seed Batch 02', 'ONLINE', '2026-09-01', '2026-12-20',
           'Monday and Friday, 7:00 PM - 8:30 PM', 25, 'ONGOING', 'Seed Japanese N3', 6
    UNION ALL SELECT 'Seed Batch 01', 'IN_PERSON', '2027-02-06', '2027-07-06',
           'Saturday, 1:00 PM - 4:00 PM', 18, 'OPEN', 'Seed Japanese N2', 7
    UNION ALL SELECT 'Seed Batch 02', 'ONLINE', '2026-07-01', '2026-11-01',
           'Wednesday and Friday, 6:00 PM - 7:30 PM', 22, 'COMPLETED', 'Seed Japanese N2', 8
    UNION ALL SELECT 'Seed Batch 01', 'ONLINE', '2027-03-01', '2027-09-01',
           'Tuesday and Thursday, 7:00 PM - 8:30 PM', 18, 'OPEN', 'Seed Japanese N1', 1
    UNION ALL SELECT 'Seed Batch 02', 'IN_PERSON', '2026-08-01', '2026-12-01',
           'Sunday, 9:00 AM - 12:00 PM', 18, 'ONGOING', 'Seed Japanese N1', 2
    UNION ALL SELECT 'Seed Batch 01', 'IN_PERSON', '2026-12-12', '2027-02-12',
           'Saturday, 10:00 AM - 12:00 PM', 24, 'OPEN', 'Seed Conversation', 3
    UNION ALL SELECT 'Seed Batch 02', 'ONLINE', '2026-09-15', '2026-12-15',
           'Wednesday, 7:00 PM - 9:00 PM', 24, 'ONGOING', 'Seed Conversation', 4
    UNION ALL SELECT 'Seed Batch 01', 'ONLINE', '2027-01-10', '2027-05-10',
           'Sunday, 2:00 PM - 4:00 PM', 20, 'OPEN', 'Seed Business Japanese', 5
    UNION ALL SELECT 'Seed Batch 02', 'IN_PERSON', '2026-06-01', '2026-10-01',
           'Monday, 6:00 PM - 8:00 PM', 20, 'COMPLETED', 'Seed Business Japanese', 6
    UNION ALL SELECT 'Seed Batch 01', 'IN_PERSON', '2026-12-19', '2027-02-19',
           'Saturday, 2:00 PM - 4:00 PM', 24, 'OPEN', 'Seed Kanji Workshop', 7
    UNION ALL SELECT 'Seed Batch 02', 'ONLINE', '2026-09-10', '2026-11-10',
           'Thursday, 7:00 PM - 9:00 PM', 24, 'CLOSED', 'Seed Kanji Workshop', 8
    UNION ALL SELECT 'Seed Batch 01', 'ONLINE', '2027-01-16', '2027-04-16',
           'Saturday, 6:00 PM - 8:00 PM', 30, 'OPEN', 'Seed JLPT Prep', 1
    UNION ALL SELECT 'Seed Batch 02', 'IN_PERSON', '2026-08-15', '2026-11-15',
           'Sunday, 10:00 AM - 12:00 PM', 30, 'COMPLETED', 'Seed JLPT Prep', 2
    UNION ALL SELECT 'Seed Batch 01', 'IN_PERSON', '2027-02-07', '2027-06-07',
           'Sunday, 9:00 AM - 11:00 AM', 16, 'OPEN', 'Seed Kids Japanese', 3
    UNION ALL SELECT 'Seed Batch 02', 'ONLINE', '2026-10-05', '2027-01-05',
           'Monday and Wednesday, 4:00 PM - 5:30 PM', 16, 'ONGOING', 'Seed Kids Japanese', 4
    UNION ALL SELECT 'Seed Batch 01', 'IN_PERSON', '2026-12-20', '2027-01-20',
           'Sunday, 1:00 PM - 3:00 PM', 30, 'OPEN', 'Seed Travel Japanese', 5
    UNION ALL SELECT 'Seed Batch 02', 'ONLINE', '2026-07-10', '2026-08-10',
           'Friday, 7:00 PM - 9:00 PM', 30, 'COMPLETED', 'Seed Travel Japanese', 6
    UNION ALL SELECT 'Seed Batch 01', 'ONLINE', '2027-01-17', '2027-04-17',
           'Sunday, 3:00 PM - 5:00 PM', 20, 'OPEN', 'Seed Writing Workshop', 7
    UNION ALL SELECT 'Seed Batch 02', 'IN_PERSON', '2026-09-20', '2026-12-20',
           'Sunday, 3:00 PM - 5:00 PM', 20, 'ONGOING', 'Seed Writing Workshop', 8
) b
JOIN course c ON c.name = b.course_name
JOIN users teacher_user
  ON teacher_user.username = CONCAT('seed_teacher', LPAD(b.teacher_number, 2, '0'))
JOIN teacher_profile tp ON tp.user_id = teacher_user.id
WHERE NOT EXISTS (
    SELECT 1
    FROM class_batch existing
    WHERE existing.course_id = c.id
      AND existing.batch_name = b.batch_name
);

-- Generate a varied enrollment set for the generated students and batches.
-- The live schema currently has unique constraints on both student_id and
-- batch_id, so one deterministic enrollment is created for each seeded batch
-- and the first 24 generated students.
INSERT INTO enrollment
    (applied_at, review_at, approved_at, status, reviewed_by, admin_note, student_id, batch_id)
SELECT applied.applied_at,
       CASE WHEN applied.status = 'PENDING' THEN NULL
            ELSE TIMESTAMPADD(DAY, 2, applied.applied_at)
       END,
       CASE WHEN applied.status = 'APPROVED'
            THEN TIMESTAMPADD(DAY, 2, applied.applied_at)
            ELSE NULL
       END,
       applied.status,
       CASE WHEN applied.status = 'PENDING' THEN NULL ELSE admin_user.id END,
       CASE WHEN applied.status = 'PENDING' THEN NULL
            WHEN applied.status = 'REJECTED' THEN 'Sample application not approved.'
            ELSE 'Sample application approved.'
       END,
       applied.student_id,
       applied.batch_id
FROM (
    SELECT TIMESTAMPADD(
               HOUR,
               b.batch_index,
               TIMESTAMPADD(
                   DAY,
                   CAST(SUBSTRING(u.username, 13) AS UNSIGNED),
                   TIMESTAMP('2026-08-01 09:00:00')
               )
           ) AS applied_at,
           CASE MOD(
               CAST(SUBSTRING(u.username, 13) AS UNSIGNED)
                   + b.batch_index,
               5
           )
               WHEN 0 THEN 'REJECTED'
               WHEN 1 THEN 'PENDING'
               ELSE 'APPROVED'
           END AS status,
           sp.id AS student_id,
           b.id AS batch_id
    FROM users u
    JOIN student_profile sp ON sp.user_id = u.id
    JOIN (
        SELECT id,
               ROW_NUMBER() OVER (ORDER BY id) - 1 AS batch_index
        FROM class_batch
        WHERE batch_name LIKE 'Seed Batch %'
    ) b ON b.batch_index = CAST(SUBSTRING(u.username, 13) AS UNSIGNED) - 1
    WHERE u.username LIKE 'seed_student%'
      AND CAST(SUBSTRING(u.username, 13) AS UNSIGNED) BETWEEN 1 AND 24
) applied
CROSS JOIN users admin_user
WHERE admin_user.username = 'seed_admin'
  AND NOT EXISTS (
      SELECT 1
      FROM enrollment existing
      WHERE existing.student_id = applied.student_id
        AND existing.batch_id = applied.batch_id
  );

COMMIT;
