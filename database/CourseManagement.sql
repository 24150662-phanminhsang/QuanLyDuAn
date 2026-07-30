/* =========================================================
   COURSE MANAGEMENT SYSTEM
   Database: CourseManagement
   SQL Server
   ========================================================= */

USE master;
GO

IF DB_ID(N'CourseManagement') IS NULL
BEGIN
    CREATE DATABASE CourseManagement;
END;
GO

USE CourseManagement;
GO

/* =========================================================
   1. ROLES
   ========================================================= */

IF OBJECT_ID(N'dbo.Roles', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.Roles
    (
        role_id INT IDENTITY(1,1) PRIMARY KEY,
        role_name VARCHAR(30) NOT NULL UNIQUE,
        description NVARCHAR(255) NULL,
        created_at DATETIME2 NOT NULL DEFAULT SYSDATETIME()
    );
END;
GO

/* =========================================================
   2. USERS
   ========================================================= */

IF OBJECT_ID(N'dbo.Users', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.Users
    (
        user_id INT IDENTITY(1,1) PRIMARY KEY,

        username VARCHAR(50) NOT NULL,
        password_hash VARCHAR(255) NOT NULL,

        full_name NVARCHAR(100) NOT NULL,
        email VARCHAR(100) NULL,
        phone VARCHAR(20) NULL,

        role_id INT NOT NULL,

        status VARCHAR(20) NOT NULL
            DEFAULT 'ACTIVE',

        created_at DATETIME2 NOT NULL
            DEFAULT SYSDATETIME(),

        updated_at DATETIME2 NULL,

        CONSTRAINT UQ_Users_Username
            UNIQUE (username),

        CONSTRAINT UQ_Users_Email
            UNIQUE (email),

        CONSTRAINT CK_Users_Status
            CHECK (status IN ('ACTIVE', 'LOCKED', 'INACTIVE')),

        CONSTRAINT FK_Users_Roles
            FOREIGN KEY (role_id)
            REFERENCES dbo.Roles(role_id)
    );
END;
GO

/* =========================================================
   3. STUDENTS
   Thành viên 2 phụ trách code Java
   ========================================================= */

IF OBJECT_ID(N'dbo.Students', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.Students
    (
        student_id INT IDENTITY(1,1) PRIMARY KEY,

        student_code VARCHAR(20) NOT NULL UNIQUE,
        user_id INT NULL UNIQUE,

        full_name NVARCHAR(100) NOT NULL,
        date_of_birth DATE NULL,
        gender VARCHAR(10) NULL,

        email VARCHAR(100) NULL,
        phone VARCHAR(20) NULL,
        address NVARCHAR(255) NULL,

        status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
        created_at DATETIME2 NOT NULL DEFAULT SYSDATETIME(),

        CONSTRAINT CK_Students_Gender
            CHECK (
                gender IS NULL
                OR gender IN ('MALE', 'FEMALE', 'OTHER')
            ),

        CONSTRAINT CK_Students_Status
            CHECK (status IN ('ACTIVE', 'INACTIVE')),

        CONSTRAINT FK_Students_Users
            FOREIGN KEY (user_id)
            REFERENCES dbo.Users(user_id)
    );
END;
GO

/* =========================================================
   4. TEACHERS
   Thành viên 3 phụ trách code Java
   ========================================================= */

IF OBJECT_ID(N'dbo.Teachers', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.Teachers
    (
        teacher_id INT IDENTITY(1,1) PRIMARY KEY,

        teacher_code VARCHAR(20) NOT NULL UNIQUE,
        user_id INT NULL UNIQUE,

        full_name NVARCHAR(100) NOT NULL,
        email VARCHAR(100) NULL,
        phone VARCHAR(20) NULL,

        specialization NVARCHAR(100) NULL,

        status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
        created_at DATETIME2 NOT NULL DEFAULT SYSDATETIME(),

        CONSTRAINT CK_Teachers_Status
            CHECK (status IN ('ACTIVE', 'INACTIVE')),

        CONSTRAINT FK_Teachers_Users
            FOREIGN KEY (user_id)
            REFERENCES dbo.Users(user_id)
    );
END;
GO

/* =========================================================
   5. COURSES
   ========================================================= */

IF OBJECT_ID(N'dbo.Courses', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.Courses
    (
        course_id INT IDENTITY(1,1) PRIMARY KEY,

        course_code VARCHAR(20) NOT NULL UNIQUE,
        course_name NVARCHAR(150) NOT NULL,

        description NVARCHAR(500) NULL,

        credits INT NOT NULL DEFAULT 3,
        tuition_fee DECIMAL(18,2) NOT NULL DEFAULT 0,

        status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
        created_at DATETIME2 NOT NULL DEFAULT SYSDATETIME(),

        CONSTRAINT CK_Courses_Credits
            CHECK (credits > 0),

        CONSTRAINT CK_Courses_TuitionFee
            CHECK (tuition_fee >= 0),

        CONSTRAINT CK_Courses_Status
            CHECK (status IN ('ACTIVE', 'INACTIVE'))
    );
END;
GO

/* =========================================================
   6. COURSE CLASSES
   Dùng CourseClasses vì Class là tên dễ gây nhầm trong Java
   ========================================================= */

IF OBJECT_ID(N'dbo.CourseClasses', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.CourseClasses
    (
        class_id INT IDENTITY(1,1) PRIMARY KEY,

        class_code VARCHAR(30) NOT NULL UNIQUE,
        course_id INT NOT NULL,
        teacher_id INT NULL,

        semester VARCHAR(20) NOT NULL,
        school_year VARCHAR(20) NOT NULL,

        room VARCHAR(30) NULL,
        schedule_text NVARCHAR(100) NULL,

        maximum_students INT NOT NULL DEFAULT 40,

        start_date DATE NULL,
        end_date DATE NULL,

        status VARCHAR(20) NOT NULL DEFAULT 'OPEN',
        created_at DATETIME2 NOT NULL DEFAULT SYSDATETIME(),

        CONSTRAINT CK_CourseClasses_MaximumStudents
            CHECK (maximum_students > 0),

        CONSTRAINT CK_CourseClasses_Status
            CHECK (
                status IN ('OPEN', 'CLOSED', 'COMPLETED', 'CANCELLED')
            ),

        CONSTRAINT CK_CourseClasses_Dates
            CHECK (
                start_date IS NULL
                OR end_date IS NULL
                OR end_date >= start_date
            ),

        CONSTRAINT FK_CourseClasses_Courses
            FOREIGN KEY (course_id)
            REFERENCES dbo.Courses(course_id),

        CONSTRAINT FK_CourseClasses_Teachers
            FOREIGN KEY (teacher_id)
            REFERENCES dbo.Teachers(teacher_id)
    );
END;
GO

/* =========================================================
   7. ENROLLMENTS
   ========================================================= */

IF OBJECT_ID(N'dbo.Enrollments', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.Enrollments
    (
        enrollment_id INT IDENTITY(1,1) PRIMARY KEY,

        student_id INT NOT NULL,
        class_id INT NOT NULL,

        enrollment_date DATE NOT NULL DEFAULT CAST(GETDATE() AS DATE),

        status VARCHAR(20) NOT NULL DEFAULT 'ENROLLED',
        created_at DATETIME2 NOT NULL DEFAULT SYSDATETIME(),

        CONSTRAINT UQ_Enrollments_Student_Class
            UNIQUE (student_id, class_id),

        CONSTRAINT CK_Enrollments_Status
            CHECK (
                status IN ('ENROLLED', 'COMPLETED', 'CANCELLED')
            ),

        CONSTRAINT FK_Enrollments_Students
            FOREIGN KEY (student_id)
            REFERENCES dbo.Students(student_id),

        CONSTRAINT FK_Enrollments_CourseClasses
            FOREIGN KEY (class_id)
            REFERENCES dbo.CourseClasses(class_id)
    );
END;
GO

/* =========================================================
   8. GRADES
   ========================================================= */

IF OBJECT_ID(N'dbo.Grades', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.Grades
    (
        grade_id INT IDENTITY(1,1) PRIMARY KEY,

        enrollment_id INT NOT NULL UNIQUE,

        attendance_score DECIMAL(4,2) NULL,
        midterm_score DECIMAL(4,2) NULL,
        final_score DECIMAL(4,2) NULL,
        average_score DECIMAL(4,2) NULL,

        result VARCHAR(20) NULL,

        updated_at DATETIME2 NOT NULL DEFAULT SYSDATETIME(),

        CONSTRAINT CK_Grades_Attendance
            CHECK (
                attendance_score IS NULL
                OR attendance_score BETWEEN 0 AND 10
            ),

        CONSTRAINT CK_Grades_Midterm
            CHECK (
                midterm_score IS NULL
                OR midterm_score BETWEEN 0 AND 10
            ),

        CONSTRAINT CK_Grades_Final
            CHECK (
                final_score IS NULL
                OR final_score BETWEEN 0 AND 10
            ),

        CONSTRAINT CK_Grades_Average
            CHECK (
                average_score IS NULL
                OR average_score BETWEEN 0 AND 10
            ),

        CONSTRAINT CK_Grades_Result
            CHECK (
                result IS NULL
                OR result IN ('PASSED', 'FAILED')
            ),

        CONSTRAINT FK_Grades_Enrollments
            FOREIGN KEY (enrollment_id)
            REFERENCES dbo.Enrollments(enrollment_id)
    );
END;
GO

/* =========================================================
   9. PAYMENTS
   ========================================================= */

IF OBJECT_ID(N'dbo.Payments', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.Payments
    (
        payment_id INT IDENTITY(1,1) PRIMARY KEY,

        student_id INT NOT NULL,
        enrollment_id INT NULL,

        amount DECIMAL(18,2) NOT NULL,
        payment_date DATETIME2 NULL,

        payment_method VARCHAR(30) NULL,

        status VARCHAR(20) NOT NULL DEFAULT 'UNPAID',
        note NVARCHAR(255) NULL,

        created_at DATETIME2 NOT NULL DEFAULT SYSDATETIME(),

        CONSTRAINT CK_Payments_Amount
            CHECK (amount >= 0),

        CONSTRAINT CK_Payments_Status
            CHECK (
                status IN ('UNPAID', 'PAID', 'CANCELLED')
            ),

        CONSTRAINT FK_Payments_Students
            FOREIGN KEY (student_id)
            REFERENCES dbo.Students(student_id),

        CONSTRAINT FK_Payments_Enrollments
            FOREIGN KEY (enrollment_id)
            REFERENCES dbo.Enrollments(enrollment_id)
    );
END;
GO

/* =========================================================
   10. INDEX
   ========================================================= */

IF NOT EXISTS
(
    SELECT 1
    FROM sys.indexes
    WHERE name = 'IX_Users_RoleId'
      AND object_id = OBJECT_ID('dbo.Users')
)
BEGIN
    CREATE INDEX IX_Users_RoleId
        ON dbo.Users(role_id);
END;
GO

IF NOT EXISTS
(
    SELECT 1
    FROM sys.indexes
    WHERE name = 'IX_Enrollments_StudentId'
      AND object_id = OBJECT_ID('dbo.Enrollments')
)
BEGIN
    CREATE INDEX IX_Enrollments_StudentId
        ON dbo.Enrollments(student_id);
END;
GO

IF NOT EXISTS
(
    SELECT 1
    FROM sys.indexes
    WHERE name = 'IX_CourseClasses_CourseId'
      AND object_id = OBJECT_ID('dbo.CourseClasses')
)
BEGIN
    CREATE INDEX IX_CourseClasses_CourseId
        ON dbo.CourseClasses(course_id);
END;
GO

PRINT N'Đã tạo database CourseManagement thành công.';
GO








