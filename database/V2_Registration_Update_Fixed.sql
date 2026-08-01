/* =========================================================
   COURSE MANAGEMENT SYSTEM
   MIGRATION V2 - FIXED VERSION

   Chức năng:
   - Đăng ký tài khoản Student/Teacher
   - Xác minh email bằng OTP
   - Admin duyệt tài khoản Teacher
   - Theo dõi đăng nhập
   - Nhật ký hoạt động

   Database: CourseManagement
   SQL Server
   ========================================================= */

USE CourseManagement;
GO

/* =========================================================
   1. KHÔI PHỤC CONSTRAINT LOGIN_ATTEMPTS BỊ XÓA NHẦM
   ========================================================= */

IF COL_LENGTH(
        N'dbo.Users',
        N'login_attempts'
   ) IS NOT NULL
AND NOT EXISTS
(
    SELECT 1
    FROM sys.check_constraints
    WHERE parent_object_id = OBJECT_ID(N'dbo.Users')
      AND
      (
          name = N'CK_Users_LoginAttempts'
          OR CHARINDEX(
                N'login_attempts',
                LOWER(definition)
             ) > 0
      )
)
BEGIN
    ALTER TABLE dbo.Users
    ADD CONSTRAINT CK_Users_LoginAttempts
        CHECK (login_attempts >= 0);

    PRINT N'Đã khôi phục CK_Users_LoginAttempts.';
END
ELSE
BEGIN
    PRINT N'CK_Users_LoginAttempts đã tồn tại hoặc cột chưa có.';
END;
GO

/* =========================================================
   2. TÌM ĐÚNG CONSTRAINT CỦA USERS.STATUS
   ========================================================= */

DECLARE @statusConstraintName SYSNAME;
DECLARE @dropSql NVARCHAR(MAX);

SELECT TOP (1)
    @statusConstraintName = cc.name
FROM sys.check_constraints AS cc
WHERE cc.parent_object_id = OBJECT_ID(N'dbo.Users')
  AND
  (
      cc.name = N'CK_Users_Status'
      OR
      (
          CHARINDEX(
              N'status',
              LOWER(cc.definition)
          ) > 0
          AND CHARINDEX(
              N'login_attempts',
              LOWER(cc.definition)
          ) = 0
          AND CHARINDEX(
              N'registration_source',
              LOWER(cc.definition)
          ) = 0
      )
  )
ORDER BY
    CASE
        WHEN cc.name = N'CK_Users_Status' THEN 0
        ELSE 1
    END;

IF @statusConstraintName IS NOT NULL
BEGIN
    SET @dropSql =
        N'ALTER TABLE dbo.Users DROP CONSTRAINT '
        + QUOTENAME(@statusConstraintName)
        + N';';

    EXEC sys.sp_executesql @dropSql;

    PRINT N'Đã xóa constraint trạng thái cũ: '
        + @statusConstraintName;
END
ELSE
BEGIN
    PRINT N'Không tìm thấy constraint trạng thái Users cũ.';
END;
GO

/* =========================================================
   3. TẠO CONSTRAINT STATUS MỚI
   ========================================================= */

IF NOT EXISTS
(
    SELECT 1
    FROM sys.check_constraints
    WHERE name = N'CK_Users_Status'
      AND parent_object_id = OBJECT_ID(N'dbo.Users')
)
BEGIN
    ALTER TABLE dbo.Users
    ADD CONSTRAINT CK_Users_Status
        CHECK
        (
            status IN
            (
                'ACTIVE',
                'PENDING_EMAIL',
                'PENDING_APPROVAL',
                'LOCKED',
                'INACTIVE'
            )
        );

    PRINT N'Đã tạo CK_Users_Status mới.';
END
ELSE
BEGIN
    PRINT N'CK_Users_Status mới đã tồn tại.';
END;
GO

/* =========================================================
   4. KIỂM TRA KẾT QUẢ
   ========================================================= */

SELECT
    cc.name AS constraint_name,
    cc.definition
FROM sys.check_constraints AS cc
WHERE cc.parent_object_id = OBJECT_ID(N'dbo.Users')
ORDER BY cc.name;
GO

/* =========================================================
   1. ĐẢM BẢO CÁC ROLE CƠ BẢN
   ========================================================= */

IF NOT EXISTS
(
    SELECT 1
    FROM dbo.Roles
    WHERE role_name = 'ADMIN'
)
BEGIN
INSERT INTO dbo.Roles
(
    role_name,
    description
)
VALUES
    (
        'ADMIN',
        N'Quản trị viên hệ thống'
    );
END;
GO

IF NOT EXISTS
(
    SELECT 1
    FROM dbo.Roles
    WHERE role_name = 'TEACHER'
)
BEGIN
INSERT INTO dbo.Roles
(
    role_name,
    description
)
VALUES
    (
        'TEACHER',
        N'Giảng viên'
    );
END;
GO

IF NOT EXISTS
(
    SELECT 1
    FROM dbo.Roles
    WHERE role_name = 'STUDENT'
)
BEGIN
INSERT INTO dbo.Roles
(
    role_name,
    description
)
VALUES
    (
        'STUDENT',
        N'Sinh viên'
    );
END;
GO

/* =========================================================
   2. NÂNG CẤP USERS.STATUS
   ========================================================= */

/*
   Trạng thái mới:

   ACTIVE
       Tài khoản hoạt động.

   PENDING_EMAIL
       Đã đăng ký nhưng chưa xác minh email.

   PENDING_APPROVAL
       Teacher đã xác minh email nhưng chờ Admin duyệt.

   LOCKED
       Tài khoản bị khóa.

   INACTIVE
       Tài khoản ngừng hoạt động.
*/

DECLARE @UsersStatusConstraint SYSNAME;

SELECT TOP 1
    @UsersStatusConstraint = cc.name
FROM sys.check_constraints cc
         INNER JOIN sys.columns c
                    ON cc.parent_object_id = c.object_id
WHERE cc.parent_object_id = OBJECT_ID(N'dbo.Users')
  AND cc.definition LIKE '%status%'
  AND cc.definition LIKE '%ACTIVE%';

IF @UsersStatusConstraint IS NOT NULL
BEGIN
EXEC
    (
        N'ALTER TABLE dbo.Users DROP CONSTRAINT '
        + QUOTENAME(@UsersStatusConstraint)
    );
END;
GO

IF NOT EXISTS
(
    SELECT 1
    FROM sys.check_constraints
    WHERE name = N'CK_Users_Status'
      AND parent_object_id = OBJECT_ID(N'dbo.Users')
)
BEGIN
ALTER TABLE dbo.Users
    ADD CONSTRAINT CK_Users_Status
        CHECK
            (
            status IN
            (
             'ACTIVE',
             'PENDING_EMAIL',
             'PENDING_APPROVAL',
             'LOCKED',
             'INACTIVE'
                )
            );
END;
GO

/* =========================================================
   3. THÊM EMAIL_VERIFIED VÀ EMAIL_VERIFIED_AT
   ========================================================= */

IF COL_LENGTH(
        N'dbo.Users',
        N'email_verified'
   ) IS NULL
BEGIN
ALTER TABLE dbo.Users
    ADD email_verified BIT NULL;
END;
GO

/*
   Các tài khoản cũ được xem là đã xác minh
   để không làm gián đoạn đăng nhập hiện tại.
*/

UPDATE dbo.Users
SET email_verified = 1
WHERE email_verified IS NULL;
GO

IF EXISTS
(
    SELECT 1
    FROM sys.columns
    WHERE object_id = OBJECT_ID(N'dbo.Users')
      AND name = N'email_verified'
      AND is_nullable = 1
)
BEGIN
ALTER TABLE dbo.Users
ALTER COLUMN email_verified BIT NOT NULL;
END;
GO

IF NOT EXISTS
(
    SELECT 1
    FROM sys.default_constraints dc
    INNER JOIN sys.columns c
        ON dc.parent_object_id = c.object_id
       AND dc.parent_column_id = c.column_id
    WHERE dc.parent_object_id = OBJECT_ID(N'dbo.Users')
      AND c.name = N'email_verified'
)
BEGIN
ALTER TABLE dbo.Users
    ADD CONSTRAINT DF_Users_EmailVerified
        DEFAULT 0 FOR email_verified;
END;
GO

IF COL_LENGTH(
        N'dbo.Users',
        N'email_verified_at'
   ) IS NULL
BEGIN
ALTER TABLE dbo.Users
    ADD email_verified_at DATETIME2 NULL;
END;
GO

UPDATE dbo.Users
SET email_verified_at =
        COALESCE(
                email_verified_at,
                created_at,
                SYSDATETIME()
        )
WHERE email_verified = 1
  AND email_verified_at IS NULL;
GO

/* =========================================================
   4. NGUỒN TẠO TÀI KHOẢN
   ========================================================= */

/*
   ADMIN:
       Tài khoản được Admin tạo.

   SELF_REGISTER:
       Student hoặc Teacher tự đăng ký.
*/

IF COL_LENGTH(
        N'dbo.Users',
        N'registration_source'
   ) IS NULL
BEGIN
ALTER TABLE dbo.Users
    ADD registration_source VARCHAR(20) NULL;
END;
GO

UPDATE dbo.Users
SET registration_source = 'ADMIN'
WHERE registration_source IS NULL
   OR LTRIM(RTRIM(registration_source)) = '';
GO

IF EXISTS
(
    SELECT 1
    FROM sys.columns
    WHERE object_id = OBJECT_ID(N'dbo.Users')
      AND name = N'registration_source'
      AND is_nullable = 1
)
BEGIN
ALTER TABLE dbo.Users
ALTER COLUMN registration_source VARCHAR(20) NOT NULL;
END;
GO

IF NOT EXISTS
(
    SELECT 1
    FROM sys.default_constraints dc
    INNER JOIN sys.columns c
        ON dc.parent_object_id = c.object_id
       AND dc.parent_column_id = c.column_id
    WHERE dc.parent_object_id = OBJECT_ID(N'dbo.Users')
      AND c.name = N'registration_source'
)
BEGIN
ALTER TABLE dbo.Users
    ADD CONSTRAINT DF_Users_RegistrationSource
        DEFAULT 'ADMIN' FOR registration_source;
END;
GO

IF NOT EXISTS
(
    SELECT 1
    FROM sys.check_constraints
    WHERE name = N'CK_Users_RegistrationSource'
      AND parent_object_id = OBJECT_ID(N'dbo.Users')
)
BEGIN
ALTER TABLE dbo.Users
    ADD CONSTRAINT CK_Users_RegistrationSource
        CHECK
            (
            registration_source IN
            (
             'ADMIN',
             'SELF_REGISTER'
                )
            );
END;
GO

/* =========================================================
   5. BẢO MẬT ĐĂNG NHẬP
   ========================================================= */

IF COL_LENGTH(
        N'dbo.Users',
        N'last_login'
   ) IS NULL
BEGIN
ALTER TABLE dbo.Users
    ADD last_login DATETIME2 NULL;
END;
GO

IF COL_LENGTH(
        N'dbo.Users',
        N'login_attempts'
   ) IS NULL
BEGIN
ALTER TABLE dbo.Users
    ADD login_attempts INT NULL;
END;
GO

UPDATE dbo.Users
SET login_attempts = 0
WHERE login_attempts IS NULL;
GO

IF EXISTS
(
    SELECT 1
    FROM sys.columns
    WHERE object_id = OBJECT_ID(N'dbo.Users')
      AND name = N'login_attempts'
      AND is_nullable = 1
)
BEGIN
ALTER TABLE dbo.Users
ALTER COLUMN login_attempts INT NOT NULL;
END;
GO

IF NOT EXISTS
(
    SELECT 1
    FROM sys.default_constraints dc
    INNER JOIN sys.columns c
        ON dc.parent_object_id = c.object_id
       AND dc.parent_column_id = c.column_id
    WHERE dc.parent_object_id = OBJECT_ID(N'dbo.Users')
      AND c.name = N'login_attempts'
)
BEGIN
ALTER TABLE dbo.Users
    ADD CONSTRAINT DF_Users_LoginAttempts
        DEFAULT 0 FOR login_attempts;
END;
GO

IF NOT EXISTS
(
    SELECT 1
    FROM sys.check_constraints
    WHERE name = N'CK_Users_LoginAttempts'
      AND parent_object_id = OBJECT_ID(N'dbo.Users')
)
BEGIN
ALTER TABLE dbo.Users
    ADD CONSTRAINT CK_Users_LoginAttempts
        CHECK (login_attempts >= 0);
END;
GO

IF COL_LENGTH(
        N'dbo.Users',
        N'locked_until'
   ) IS NULL
BEGIN
ALTER TABLE dbo.Users
    ADD locked_until DATETIME2 NULL;
END;
GO

/* =========================================================
   6. THÔNG TIN DUYỆT TÀI KHOẢN TEACHER
   ========================================================= */

IF COL_LENGTH(
        N'dbo.Users',
        N'approved_by'
   ) IS NULL
BEGIN
ALTER TABLE dbo.Users
    ADD approved_by INT NULL;
END;
GO

IF COL_LENGTH(
        N'dbo.Users',
        N'approved_at'
   ) IS NULL
BEGIN
ALTER TABLE dbo.Users
    ADD approved_at DATETIME2 NULL;
END;
GO

IF COL_LENGTH(
        N'dbo.Users',
        N'rejected_by'
   ) IS NULL
BEGIN
ALTER TABLE dbo.Users
    ADD rejected_by INT NULL;
END;
GO

IF COL_LENGTH(
        N'dbo.Users',
        N'rejected_at'
   ) IS NULL
BEGIN
ALTER TABLE dbo.Users
    ADD rejected_at DATETIME2 NULL;
END;
GO

IF COL_LENGTH(
        N'dbo.Users',
        N'rejection_reason'
   ) IS NULL
BEGIN
ALTER TABLE dbo.Users
    ADD rejection_reason NVARCHAR(500) NULL;
END;
GO

IF NOT EXISTS
(
    SELECT 1
    FROM sys.foreign_keys
    WHERE name = N'FK_Users_ApprovedBy'
      AND parent_object_id = OBJECT_ID(N'dbo.Users')
)
BEGIN
ALTER TABLE dbo.Users
    ADD CONSTRAINT FK_Users_ApprovedBy
        FOREIGN KEY (approved_by)
            REFERENCES dbo.Users(user_id);
END;
GO

IF NOT EXISTS
(
    SELECT 1
    FROM sys.foreign_keys
    WHERE name = N'FK_Users_RejectedBy'
      AND parent_object_id = OBJECT_ID(N'dbo.Users')
)
BEGIN
ALTER TABLE dbo.Users
    ADD CONSTRAINT FK_Users_RejectedBy
        FOREIGN KEY (rejected_by)
            REFERENCES dbo.Users(user_id);
END;
GO

/* =========================================================
   7. NÂNG CẤP STUDENTS
   ========================================================= */

/*
   Chưa đổi user_id thành NOT NULL trong migration này.

   Lý do:
   - Database hiện có UNIQUE và INDEX trên user_id.
   - Có thể tồn tại dữ liệu cũ chưa liên kết.
   - Service mới sẽ luôn bắt buộc user_id khi thêm Student.
*/

IF COL_LENGTH(
        N'dbo.Students',
        N'updated_at'
   ) IS NULL
BEGIN
ALTER TABLE dbo.Students
    ADD updated_at DATETIME2 NULL;
END;
GO

UPDATE dbo.Students
SET updated_at = created_at
WHERE updated_at IS NULL;
GO

/* =========================================================
   8. NÂNG CẤP TEACHERS
   ========================================================= */

IF COL_LENGTH(
        N'dbo.Teachers',
        N'date_of_birth'
   ) IS NULL
BEGIN
ALTER TABLE dbo.Teachers
    ADD date_of_birth DATE NULL;
END;
GO

IF COL_LENGTH(
        N'dbo.Teachers',
        N'gender'
   ) IS NULL
BEGIN
ALTER TABLE dbo.Teachers
    ADD gender VARCHAR(10) NULL;
END;
GO

IF COL_LENGTH(
        N'dbo.Teachers',
        N'address'
   ) IS NULL
BEGIN
ALTER TABLE dbo.Teachers
    ADD address NVARCHAR(255) NULL;
END;
GO

IF COL_LENGTH(
        N'dbo.Teachers',
        N'updated_at'
   ) IS NULL
BEGIN
ALTER TABLE dbo.Teachers
    ADD updated_at DATETIME2 NULL;
END;
GO

UPDATE dbo.Teachers
SET updated_at = created_at
WHERE updated_at IS NULL;
GO

IF NOT EXISTS
(
    SELECT 1
    FROM sys.check_constraints
    WHERE name = N'CK_Teachers_Gender'
      AND parent_object_id = OBJECT_ID(N'dbo.Teachers')
)
BEGIN
ALTER TABLE dbo.Teachers
    ADD CONSTRAINT CK_Teachers_Gender
        CHECK
            (
            gender IS NULL
                OR gender IN
                   (
                    'MALE',
                    'FEMALE',
                    'OTHER'
                       )
            );
END;
GO

/* =========================================================
   9. EMAIL VERIFICATIONS
   ========================================================= */

IF OBJECT_ID(
        N'dbo.EmailVerifications',
        N'U'
   ) IS NULL
BEGIN
CREATE TABLE dbo.EmailVerifications
(
    verification_id BIGINT IDENTITY(1,1)
            NOT NULL,

    user_id INT NOT NULL,

    otp_hash VARCHAR(255) NOT NULL,

    purpose VARCHAR(30) NOT NULL
        CONSTRAINT DF_EmailVerifications_Purpose
        DEFAULT 'EMAIL_VERIFICATION',

    expires_at DATETIME2 NOT NULL,

    verified_at DATETIME2 NULL,
    invalidated_at DATETIME2 NULL,

    attempt_count INT NOT NULL
        CONSTRAINT DF_EmailVerifications_AttemptCount
        DEFAULT 0,

    resend_count INT NOT NULL
        CONSTRAINT DF_EmailVerifications_ResendCount
        DEFAULT 0,

    created_at DATETIME2 NOT NULL
        CONSTRAINT DF_EmailVerifications_CreatedAt
        DEFAULT SYSDATETIME(),

    CONSTRAINT PK_EmailVerifications
        PRIMARY KEY (verification_id),

    CONSTRAINT CK_EmailVerifications_Purpose
        CHECK
            (
            purpose IN
            (
             'EMAIL_VERIFICATION',
             'PASSWORD_RESET'
                )
            ),

    CONSTRAINT CK_EmailVerifications_AttemptCount
        CHECK (attempt_count >= 0),

    CONSTRAINT CK_EmailVerifications_ResendCount
        CHECK (resend_count >= 0),

    CONSTRAINT FK_EmailVerifications_Users
        FOREIGN KEY (user_id)
            REFERENCES dbo.Users(user_id)
            ON DELETE CASCADE
);
END;
GO

/* =========================================================
   10. LOGIN HISTORY
   ========================================================= */

IF OBJECT_ID(
        N'dbo.LoginHistory',
        N'U'
   ) IS NULL
BEGIN
CREATE TABLE dbo.LoginHistory
(
    login_history_id BIGINT IDENTITY(1,1)
            NOT NULL,

    user_id INT NULL,

    username_attempted VARCHAR(50) NULL,

    login_time DATETIME2 NOT NULL
        CONSTRAINT DF_LoginHistory_LoginTime
        DEFAULT SYSDATETIME(),

    success BIT NOT NULL,

    failure_reason NVARCHAR(255) NULL,

    ip_address VARCHAR(45) NULL,
    device_info NVARCHAR(255) NULL,

    CONSTRAINT PK_LoginHistory
        PRIMARY KEY (login_history_id),

    CONSTRAINT FK_LoginHistory_Users
        FOREIGN KEY (user_id)
            REFERENCES dbo.Users(user_id)
);
END;
GO

/* =========================================================
   11. AUDIT LOGS
   ========================================================= */

IF OBJECT_ID(
        N'dbo.AuditLogs',
        N'U'
   ) IS NULL
BEGIN
CREATE TABLE dbo.AuditLogs
(
    log_id BIGINT IDENTITY(1,1)
            NOT NULL,

    user_id INT NULL,

    action VARCHAR(50) NOT NULL,

    entity_name VARCHAR(50) NULL,
    entity_id VARCHAR(50) NULL,

    description NVARCHAR(1000) NULL,

    old_values NVARCHAR(MAX) NULL,
    new_values NVARCHAR(MAX) NULL,

    created_at DATETIME2 NOT NULL
        CONSTRAINT DF_AuditLogs_CreatedAt
        DEFAULT SYSDATETIME(),

    CONSTRAINT PK_AuditLogs
        PRIMARY KEY (log_id),

    CONSTRAINT FK_AuditLogs_Users
        FOREIGN KEY (user_id)
            REFERENCES dbo.Users(user_id)
);
END;
GO

/* =========================================================
   12. INDEX USERS
   ========================================================= */

IF NOT EXISTS
(
    SELECT 1
    FROM sys.indexes
    WHERE name = N'IX_Users_Status'
      AND object_id = OBJECT_ID(N'dbo.Users')
)
BEGIN
CREATE INDEX IX_Users_Status
    ON dbo.Users(status);
END;
GO

IF NOT EXISTS
(
    SELECT 1
    FROM sys.indexes
    WHERE name = N'IX_Users_EmailVerified'
      AND object_id = OBJECT_ID(N'dbo.Users')
)
BEGIN
CREATE INDEX IX_Users_EmailVerified
    ON dbo.Users(email_verified);
END;
GO

IF NOT EXISTS
(
    SELECT 1
    FROM sys.indexes
    WHERE name = N'IX_Users_RegistrationSource'
      AND object_id = OBJECT_ID(N'dbo.Users')
)
BEGIN
CREATE INDEX IX_Users_RegistrationSource
    ON dbo.Users(registration_source);
END;
GO

IF NOT EXISTS
(
    SELECT 1
    FROM sys.indexes
    WHERE name = N'IX_Users_PendingApproval'
      AND object_id = OBJECT_ID(N'dbo.Users')
)
BEGIN
CREATE INDEX IX_Users_PendingApproval
    ON dbo.Users
        (
         status,
         role_id
            )
    INCLUDE
        (
            username,
            full_name,
            email,
            registration_source,
            created_at
        );
END;
GO

/* =========================================================
   13. INDEX EMAIL VERIFICATIONS
   ========================================================= */

IF NOT EXISTS
(
    SELECT 1
    FROM sys.indexes
    WHERE name = N'IX_EmailVerifications_UserPurpose'
      AND object_id =
          OBJECT_ID(N'dbo.EmailVerifications')
)
BEGIN
CREATE INDEX IX_EmailVerifications_UserPurpose
    ON dbo.EmailVerifications
        (
         user_id,
         purpose,
         created_at DESC
            );
END;
GO

IF NOT EXISTS
(
    SELECT 1
    FROM sys.indexes
    WHERE name = N'IX_EmailVerifications_Active'
      AND object_id =
          OBJECT_ID(N'dbo.EmailVerifications')
)
BEGIN
CREATE INDEX IX_EmailVerifications_Active
    ON dbo.EmailVerifications
        (
         user_id,
         purpose,
         expires_at
            )
    INCLUDE
        (
            verified_at,
            invalidated_at,
            attempt_count,
            resend_count
        );
END;
GO

/* =========================================================
   14. INDEX LOGIN HISTORY
   ========================================================= */

IF NOT EXISTS
(
    SELECT 1
    FROM sys.indexes
    WHERE name = N'IX_LoginHistory_UserTime'
      AND object_id =
          OBJECT_ID(N'dbo.LoginHistory')
)
BEGIN
CREATE INDEX IX_LoginHistory_UserTime
    ON dbo.LoginHistory
        (
         user_id,
         login_time DESC
            );
END;
GO

IF NOT EXISTS
(
    SELECT 1
    FROM sys.indexes
    WHERE name = N'IX_LoginHistory_UsernameTime'
      AND object_id =
          OBJECT_ID(N'dbo.LoginHistory')
)
BEGIN
CREATE INDEX IX_LoginHistory_UsernameTime
    ON dbo.LoginHistory
        (
         username_attempted,
         login_time DESC
            );
END;
GO

/* =========================================================
   15. INDEX AUDIT LOGS
   ========================================================= */

IF NOT EXISTS
(
    SELECT 1
    FROM sys.indexes
    WHERE name = N'IX_AuditLogs_UserCreatedAt'
      AND object_id =
          OBJECT_ID(N'dbo.AuditLogs')
)
BEGIN
CREATE INDEX IX_AuditLogs_UserCreatedAt
    ON dbo.AuditLogs
        (
         user_id,
         created_at DESC
            );
END;
GO

IF NOT EXISTS
(
    SELECT 1
    FROM sys.indexes
    WHERE name = N'IX_AuditLogs_Entity'
      AND object_id =
          OBJECT_ID(N'dbo.AuditLogs')
)
BEGIN
CREATE INDEX IX_AuditLogs_Entity
    ON dbo.AuditLogs
        (
         entity_name,
         entity_id,
         created_at DESC
            );
END;
GO

/* =========================================================
   16. CẬP NHẬT DỮ LIỆU CŨ
   ========================================================= */

/*
   Tài khoản cũ:
   - ACTIVE
   - được xem là đã xác minh
   - nguồn ADMIN
*/

UPDATE dbo.Users
SET
    email_verified = 1,
    email_verified_at =
        COALESCE(
                email_verified_at,
                created_at,
                SYSDATETIME()
        ),
    registration_source =
        COALESCE(
                NULLIF(
                        LTRIM(RTRIM(registration_source)),
                        ''
                ),
                'ADMIN'
        ),
    login_attempts =
        COALESCE(login_attempts, 0)
WHERE status IN
      (
       'ACTIVE',
       'LOCKED',
       'INACTIVE'
          );
GO

/* =========================================================
   17. KIỂM TRA HỒ SƠ CHƯA LIÊN KẾT TÀI KHOẢN
   ========================================================= */

PRINT N'---------------- STUDENT CHƯA CÓ USER ----------------';
GO

SELECT
    student_id,
    student_code,
    full_name,
    email,
    phone
FROM dbo.Students
WHERE user_id IS NULL
ORDER BY student_id;
GO

PRINT N'---------------- TEACHER CHƯA CÓ USER ----------------';
GO

SELECT
    teacher_id,
    teacher_code,
    full_name,
    email,
    phone
FROM dbo.Teachers
WHERE user_id IS NULL
ORDER BY teacher_id;
GO

/* =========================================================
   18. KIỂM TRA CẤU TRÚC USERS
   ========================================================= */

PRINT N'---------------- CẤU TRÚC USERS ----------------';
GO

SELECT
    COLUMN_NAME,
    DATA_TYPE,
    CHARACTER_MAXIMUM_LENGTH,
    IS_NULLABLE,
    COLUMN_DEFAULT
FROM INFORMATION_SCHEMA.COLUMNS
WHERE TABLE_SCHEMA = 'dbo'
  AND TABLE_NAME = 'Users'
ORDER BY ORDINAL_POSITION;
GO

/* =========================================================
   19. KIỂM TRA CÁC BẢNG
   ========================================================= */

PRINT N'---------------- DANH SÁCH BẢNG ----------------';
GO

SELECT
    TABLE_NAME
FROM INFORMATION_SCHEMA.TABLES
WHERE TABLE_SCHEMA = 'dbo'
  AND TABLE_TYPE = 'BASE TABLE'
ORDER BY TABLE_NAME;
GO

/* =========================================================
   20. KIỂM TRA DỮ LIỆU USERS
   ========================================================= */

PRINT N'---------------- DỮ LIỆU USERS ----------------';
GO

SELECT
    u.user_id,
    u.username,
    u.full_name,
    u.email,
    r.role_name,
    u.status,
    u.email_verified,
    u.email_verified_at,
    u.registration_source,
    u.approved_by,
    u.approved_at,
    u.rejected_by,
    u.rejected_at,
    u.last_login,
    u.login_attempts,
    u.locked_until,
    u.created_at,
    u.updated_at
FROM dbo.Users u
         INNER JOIN dbo.Roles r
                    ON u.role_id = r.role_id
ORDER BY u.user_id;
GO

/* =========================================================
   21. KẾT THÚC
   ========================================================= */

PRINT N'================================================';
PRINT N'NÂNG CẤP DATABASE V2 HOÀN TẤT';
PRINT N'================================================';
GO




SELECT
    COLUMN_NAME,
    DATA_TYPE,
    IS_NULLABLE
FROM INFORMATION_SCHEMA.COLUMNS
WHERE TABLE_SCHEMA = 'dbo'
  AND TABLE_NAME = 'Users'
  AND COLUMN_NAME IN
  (
      'email_verified',
      'email_verified_at',
      'registration_source',
      'last_login',
      'login_attempts',
      'locked_until',
      'approved_by',
      'approved_at',
      'rejected_by',
      'rejected_at',
      'rejection_reason'
  )
ORDER BY COLUMN_NAME;
GO



SELECT
    TABLE_NAME
FROM INFORMATION_SCHEMA.TABLES
WHERE TABLE_SCHEMA = 'dbo'
  AND TABLE_NAME IN
  (
      'EmailVerifications',
      'LoginHistory',
      'AuditLogs'
  )
ORDER BY TABLE_NAME;
GO