USE CourseManagement;
GO

/* =========================================================
   1. ROLE
   ========================================================= */

IF NOT EXISTS (
    SELECT 1 FROM dbo.Roles WHERE role_name = 'ADMIN'
)
BEGIN
    INSERT INTO dbo.Roles(role_name, description)
    VALUES ('ADMIN', N'Quản trị viên hệ thống');
END;

IF NOT EXISTS (
    SELECT 1 FROM dbo.Roles WHERE role_name = 'TEACHER'
)
BEGIN
    INSERT INTO dbo.Roles(role_name, description)
    VALUES ('TEACHER', N'Giảng viên');
END;

IF NOT EXISTS (
    SELECT 1 FROM dbo.Roles WHERE role_name = 'STUDENT'
)
BEGIN
    INSERT INTO dbo.Roles(role_name, description)
    VALUES ('STUDENT', N'Sinh viên');
END;
GO

/* =========================================================
   2. USER
   Mật khẩu thử nghiệm:
   admin / Admin@123
   teacher01 / Teacher@123
   student01 / Student@123

   Hiện dùng SHA-256 để tạo dữ liệu demo.
   LoginService sau này phải băm mật khẩu trước khi so sánh.
   ========================================================= */

IF NOT EXISTS (
    SELECT 1 FROM dbo.Users WHERE username = 'admin'
)
BEGIN
    INSERT INTO dbo.Users
    (
        username,
        password_hash,
        full_name,
        email,
        phone,
        role_id,
        status
    )
    SELECT
        'admin',
        CONVERT(
            VARCHAR(64),
            HASHBYTES('SHA2_256', 'Admin@123'),
            2
        ),
        N'Quản trị viên',
        'admin@course.local',
        '0900000001',
        role_id,
        'ACTIVE'
    FROM dbo.Roles
    WHERE role_name = 'ADMIN';
END;
GO

IF NOT EXISTS (
    SELECT 1 FROM dbo.Users WHERE username = 'teacher01'
)
BEGIN
    INSERT INTO dbo.Users
    (
        username,
        password_hash,
        full_name,
        email,
        phone,
        role_id,
        status
    )
    SELECT
        'teacher01',
        CONVERT(
            VARCHAR(64),
            HASHBYTES('SHA2_256', 'Teacher@123'),
            2
        ),
        N'Nguyễn Văn Giảng',
        'teacher01@course.local',
        '0900000002',
        role_id,
        'ACTIVE'
    FROM dbo.Roles
    WHERE role_name = 'TEACHER';
END;
GO

IF NOT EXISTS (
    SELECT 1 FROM dbo.Users WHERE username = 'student01'
)
BEGIN
    INSERT INTO dbo.Users
    (
        username,
        password_hash,
        full_name,
        email,
        phone,
        role_id,
        status
    )
    SELECT
        'student01',
        CONVERT(
            VARCHAR(64),
            HASHBYTES('SHA2_256', 'Student@123'),
            2
        ),
        N'Nguyễn Văn Sinh',
        'student01@course.local',
        '0900000003',
        role_id,
        'ACTIVE'
    FROM dbo.Roles
    WHERE role_name = 'STUDENT';
END;
GO

/* =========================================================
   3. TEACHER
   ========================================================= */

IF NOT EXISTS (
    SELECT 1
    FROM dbo.Teachers
    WHERE teacher_code = 'GV001'
)
BEGIN
    INSERT INTO dbo.Teachers
    (
        teacher_code,
        user_id,
        full_name,
        email,
        phone,
        specialization
    )
    SELECT
        'GV001',
        user_id,
        full_name,
        email,
        phone,
        N'Công nghệ phần mềm'
    FROM dbo.Users
    WHERE username = 'teacher01';
END;
GO

/* =========================================================
   4. STUDENT
   ========================================================= */

IF NOT EXISTS (
    SELECT 1
    FROM dbo.Students
    WHERE student_code = 'SV001'
)
BEGIN
    INSERT INTO dbo.Students
    (
        student_code,
        user_id,
        full_name,
        date_of_birth,
        gender,
        email,
        phone,
        address
    )
    SELECT
        'SV001',
        user_id,
        full_name,
        '2005-05-20',
        'MALE',
        email,
        phone,
        N'Thành phố Hồ Chí Minh'
    FROM dbo.Users
    WHERE username = 'student01';
END;
GO

/* =========================================================
   5. COURSE
   ========================================================= */

IF NOT EXISTS (
    SELECT 1 FROM dbo.Courses WHERE course_code = 'JAVA01'
)
BEGIN
    INSERT INTO dbo.Courses
    (
        course_code,
        course_name,
        description,
        credits,
        tuition_fee
    )
    VALUES
    (
        'JAVA01',
        N'Lập trình ứng dụng Java',
        N'Java Core, Swing, JDBC và mô hình MVC',
        3,
        3000000
    );
END;

IF NOT EXISTS (
    SELECT 1 FROM dbo.Courses WHERE course_code = 'SQL01'
)
BEGIN
    INSERT INTO dbo.Courses
    (
        course_code,
        course_name,
        description,
        credits,
        tuition_fee
    )
    VALUES
    (
        'SQL01',
        N'Cơ sở dữ liệu SQL Server',
        N'Thiết kế và quản trị cơ sở dữ liệu SQL Server',
        3,
        2800000
    );
END;
GO

/* =========================================================
   6. CLASS
   ========================================================= */

IF NOT EXISTS (
    SELECT 1
    FROM dbo.CourseClasses
    WHERE class_code = 'JAVA01-01'
)
BEGIN
    INSERT INTO dbo.CourseClasses
    (
        class_code,
        course_id,
        teacher_id,
        semester,
        school_year,
        room,
        schedule_text,
        maximum_students,
        start_date,
        end_date
    )
    SELECT
        'JAVA01-01',
        c.course_id,
        t.teacher_id,
        'HK1',
        '2026-2027',
        'P.A101',
        N'Thứ 2 - Tiết 1 đến 3',
        40,
        '2026-08-10',
        '2026-12-10'
    FROM dbo.Courses c
    CROSS JOIN dbo.Teachers t
    WHERE c.course_code = 'JAVA01'
      AND t.teacher_code = 'GV001';
END;
GO

/* =========================================================
   7. ENROLLMENT
   ========================================================= */

IF NOT EXISTS
(
    SELECT 1
    FROM dbo.Enrollments e
    JOIN dbo.Students s
        ON e.student_id = s.student_id
    JOIN dbo.CourseClasses cc
        ON e.class_id = cc.class_id
    WHERE s.student_code = 'SV001'
      AND cc.class_code = 'JAVA01-01'
)
BEGIN
    INSERT INTO dbo.Enrollments
    (
        student_id,
        class_id,
        status
    )
    SELECT
        s.student_id,
        cc.class_id,
        'ENROLLED'
    FROM dbo.Students s
    CROSS JOIN dbo.CourseClasses cc
    WHERE s.student_code = 'SV001'
      AND cc.class_code = 'JAVA01-01';
END;
GO

/* =========================================================
   8. PAYMENT
   ========================================================= */

IF NOT EXISTS (
    SELECT 1
    FROM dbo.Payments p
    JOIN dbo.Students s
        ON p.student_id = s.student_id
    WHERE s.student_code = 'SV001'
)
BEGIN
    INSERT INTO dbo.Payments
    (
        student_id,
        enrollment_id,
        amount,
        status,
        note
    )
    SELECT
        e.student_id,
        e.enrollment_id,
        c.tuition_fee,
        'UNPAID',
        N'Học phí lớp Java'
    FROM dbo.Enrollments e
    JOIN dbo.CourseClasses cc
        ON e.class_id = cc.class_id
    JOIN dbo.Courses c
        ON cc.course_id = c.course_id
    JOIN dbo.Students s
        ON e.student_id = s.student_id
    WHERE s.student_code = 'SV001'
      AND cc.class_code = 'JAVA01-01';
END;
GO

/* =========================================================
   KIỂM TRA DỮ LIỆU
   ========================================================= */

SELECT * FROM dbo.Roles;
SELECT * FROM dbo.Users;
SELECT * FROM dbo.Students;
SELECT * FROM dbo.Teachers;
SELECT * FROM dbo.Courses;
SELECT * FROM dbo.CourseClasses;
SELECT * FROM dbo.Enrollments;
SELECT * FROM dbo.Payments;
GO