package dao;

import model.dto.AvailableClassDTO;
import model.dto.RegistrationResult;
import util.DBConnection;

import java.math.BigDecimal;
import java.sql.*;
import java.text.Normalizer;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StudentRegistrationDAO {

    private static final Pattern DAY_PATTERN =
            Pattern.compile("(thu)\\s*(2|3|4|5|6|7)|(chu\\s*nhat)");

    private static final Pattern PERIOD_PATTERN =
            Pattern.compile("(tiet)\\s*(\\d+)\\s*(den|-|–)\\s*(\\d+)");

    private static final String AVAILABLE_SQL =
            """
            SELECT
                cc.class_id,
                cc.class_code,
                cc.course_id,
                cc.teacher_id,

                c.course_code,
                c.course_name,
                c.credits,
                c.tuition_fee,

                t.full_name teacher_name,

                cc.semester,
                cc.school_year,
                cc.room,
                cc.schedule_text,
                cc.maximum_students,
                cc.start_date,
                cc.end_date,
                cc.status class_status,

                (
                    SELECT COUNT(*)
                    FROM Enrollments e
                    WHERE e.class_id=cc.class_id
                    AND UPPER(e.status) IN('ENROLLED','COMPLETED')
                ) enrolled_students

            FROM CourseClasses cc

            INNER JOIN Courses c
                ON c.course_id=cc.course_id

            LEFT JOIN Teachers t
                ON t.teacher_id=cc.teacher_id

            WHERE
                UPPER(cc.status) IN('OPEN','ACTIVE')
            AND
                UPPER(c.status)='ACTIVE'
            """;

    public List<AvailableClassDTO> getAvailableClasses(
            int studentId
    ){

        String sql=
                AVAILABLE_SQL+
                        """
                        AND NOT EXISTS
                        (
                            SELECT 1
                            FROM Enrollments e
                            WHERE
                                e.student_id=?
                            AND
                                e.class_id=cc.class_id
                            AND
                                UPPER(e.status)
                                IN('ENROLLED','COMPLETED')
                        )
        
                        AND NOT EXISTS
                        (
                            SELECT 1
                            FROM Enrollments e
                            INNER JOIN CourseClasses x
                                ON x.class_id=e.class_id
                            WHERE
                                e.student_id=?
                            AND
                                x.course_id=cc.course_id
                            AND
                                UPPER(e.status)
                                IN('ENROLLED','COMPLETED')
                        )
        
                        ORDER BY
                            cc.start_date,
                            c.course_name
                        """;

        return loadClasses(
                sql,
                studentId,
                null
        );
    }

    public List<AvailableClassDTO> searchAvailableClasses(
            int studentId,
            String keyword
    ){

        if(keyword==null||keyword.isBlank()){
            return getAvailableClasses(studentId);
        }

        String sql=
                AVAILABLE_SQL+
                        """
                        AND NOT EXISTS
                        (
                            SELECT 1
                            FROM Enrollments e
                            WHERE
                                e.student_id=?
                            AND
                                e.class_id=cc.class_id
                            AND
                                UPPER(e.status)
                                IN('ENROLLED','COMPLETED')
                        )
        
                        AND NOT EXISTS
                        (
                            SELECT 1
                            FROM Enrollments e
                            INNER JOIN CourseClasses x
                                ON x.class_id=e.class_id
                            WHERE
                                e.student_id=?
                            AND
                                x.course_id=cc.course_id
                            AND
                                UPPER(e.status)
                                IN('ENROLLED','COMPLETED')
                        )
        
                        AND
                        (
                            c.course_name LIKE ?
                            OR c.course_code LIKE ?
                            OR cc.class_code LIKE ?
                            OR ISNULL(t.full_name,'') LIKE ?
                            OR ISNULL(cc.room,'') LIKE ?
                            OR ISNULL(cc.schedule_text,'') LIKE ?
                        )
        
                        ORDER BY
                            cc.start_date,
                            c.course_name
                        """;

        return loadClasses(
                sql,
                studentId,
                "%" + keyword.trim() + "%"
        );
    }

    private List<AvailableClassDTO> loadClasses(
            String sql,
            int studentId,
            String keyword
    ){

        List<AvailableClassDTO> list=
                new ArrayList<>();

        try(
                Connection con=
                        DBConnection.getConnection();

                PreparedStatement ps=
                        con.prepareStatement(sql)
        ){

            ps.setInt(1,studentId);
            ps.setInt(2,studentId);

            if(keyword!=null){

                ps.setString(3,keyword);
                ps.setString(4,keyword);
                ps.setString(5,keyword);
                ps.setString(6,keyword);
                ps.setString(7,keyword);
                ps.setString(8,keyword);

            }

            try(ResultSet rs=ps.executeQuery()){

                while(rs.next()){

                    list.add(
                            mapClass(rs)
                    );

                }

            }

        }catch(Exception ex){

            throw new RuntimeException(ex);

        }

        return list;
    }

    private AvailableClassDTO mapClass(
            ResultSet rs
    ) throws SQLException {

        AvailableClassDTO dto=
                new AvailableClassDTO();

        dto.setClassId(
                rs.getInt("class_id")
        );

        dto.setCourseId(
                rs.getInt("course_id")
        );

        dto.setClassCode(
                rs.getString("class_code")
        );

        dto.setCourseCode(
                rs.getString("course_code")
        );

        dto.setCourseName(
                rs.getString("course_name")
        );

        dto.setTeacherName(
                rs.getString("teacher_name")
        );

        dto.setSemester(
                rs.getString("semester")
        );

        dto.setSchoolYear(
                rs.getString("school_year")
        );

        dto.setRoom(
                rs.getString("room")
        );

        dto.setScheduleText(
                rs.getString("schedule_text")
        );

        dto.setCredits(
                rs.getInt("credits")
        );

        dto.setTuitionFee(
                rs.getBigDecimal("tuition_fee")
        );

        dto.setMaximumStudents(
                rs.getInt("maximum_students")
        );

        dto.setEnrolledStudents(
                rs.getInt("enrolled_students")
        );

        dto.setRemainingSlots(
                dto.getMaximumStudents()
                        -
                        dto.getEnrolledStudents()
        );

        dto.setStartDate(
                rs.getDate("start_date")
        );

        dto.setEndDate(
                rs.getDate("end_date")
        );

        dto.setClassStatus(
                rs.getString("class_status")
        );

        return dto;
    }
    public RegistrationResult registerClass(
            int studentId,
            int classId
    ) {

        if (studentId <= 0) {
            return RegistrationResult.failure(
                    "ID sinh viên không hợp lệ."
            );
        }

        if (classId <= 0) {
            return RegistrationResult.failure(
                    "ID lớp học không hợp lệ."
            );
        }

        try (
                Connection connection =
                        DBConnection.getConnection()
        ) {

            boolean originalAutoCommit =
                    connection.getAutoCommit();

            int originalIsolation =
                    connection.getTransactionIsolation();

            try {

                connection.setAutoCommit(false);

                connection.setTransactionIsolation(
                        Connection.TRANSACTION_SERIALIZABLE
                );

                AvailableClassDTO classInfo =
                        getClassForRegistration(
                                connection,
                                classId
                        );

                if (classInfo == null) {

                    connection.rollback();

                    return RegistrationResult.failure(
                            "Lớp học không tồn tại "
                                    + "hoặc không còn mở đăng ký."
                    );
                }

                if (isAlreadyRegisteredClass(
                        connection,
                        studentId,
                        classId
                )) {

                    connection.rollback();

                    return RegistrationResult.failure(
                            "Bạn đã đăng ký lớp học này."
                    );
                }

                if (isAlreadyRegisteredCourse(
                        connection,
                        studentId,
                        classInfo.getCourseId()
                )) {

                    connection.rollback();

                    return RegistrationResult.failure(
                            "Bạn đã đăng ký một lớp khác "
                                    + "của khóa học này."
                    );
                }

                if (!hasAvailableSlot(
                        connection,
                        classId,
                        classInfo.getMaximumStudents()
                )) {

                    connection.rollback();

                    return RegistrationResult.failure(
                            "Lớp học đã đủ sĩ số."
                    );
                }

                String conflictClass =
                        findScheduleConflict(
                                connection,
                                studentId,
                                classInfo.getScheduleText()
                        );

                if (conflictClass != null) {

                    connection.rollback();

                    return RegistrationResult.failure(
                            "Lịch học bị trùng với lớp "
                                    + conflictClass
                                    + "."
                    );
                }

                int enrollmentId =
                        insertEnrollment(
                                connection,
                                studentId,
                                classId
                        );

                int paymentId =
                        insertPayment(
                                connection,
                                studentId,
                                enrollmentId,
                                classInfo.getTuitionFee()
                        );

                connection.commit();

                return RegistrationResult.success(
                        "Đăng ký khóa học thành công. "
                                + "Khoản học phí đã được tạo.",
                        enrollmentId,
                        paymentId
                );

            } catch (
                    SQLException
                    | RuntimeException exception
            ) {

                try {
                    connection.rollback();
                } catch (SQLException rollbackException) {
                    exception.addSuppressed(
                            rollbackException
                    );
                }

                throw exception;

            } finally {

                try {
                    connection.setTransactionIsolation(
                            originalIsolation
                    );
                } catch (SQLException ignored) {
                }

                try {
                    connection.setAutoCommit(
                            originalAutoCommit
                    );
                } catch (SQLException ignored) {
                }
            }

        } catch (SQLException exception) {

            throw new RuntimeException(
                    "Không thể đăng ký khóa học: "
                            + exception.getMessage(),
                    exception
            );
        }
    }

    private AvailableClassDTO getClassForRegistration(
            Connection connection,
            int classId
    ) throws SQLException {

        String sql =
                """
                SELECT
                    cc.class_id,
                    cc.class_code,
                    cc.course_id,
                    cc.teacher_id,

                    c.course_code,
                    c.course_name,
                    c.credits,
                    c.tuition_fee,

                    t.full_name AS teacher_name,

                    cc.semester,
                    cc.school_year,
                    cc.room,
                    cc.schedule_text,
                    cc.maximum_students,
                    cc.start_date,
                    cc.end_date,
                    cc.status AS class_status,

                    (
                        SELECT COUNT(*)
                        FROM dbo.Enrollments e
                        WHERE e.class_id = cc.class_id
                          AND UPPER(e.status)
                              IN ('ENROLLED', 'COMPLETED')
                    ) AS enrolled_students

                FROM dbo.CourseClasses cc

                INNER JOIN dbo.Courses c
                    ON c.course_id = cc.course_id

                LEFT JOIN dbo.Teachers t
                    ON t.teacher_id = cc.teacher_id

                WHERE cc.class_id = ?
                  AND UPPER(cc.status)
                      IN ('OPEN', 'ACTIVE')
                  AND UPPER(c.status) = 'ACTIVE'
                """;

        try (
                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setInt(
                    1,
                    classId
            );

            try (
                    ResultSet resultSet =
                            statement.executeQuery()
            ) {

                if (resultSet.next()) {
                    return mapClass(
                            resultSet
                    );
                }
            }
        }

        return null;
    }

    private boolean isAlreadyRegisteredClass(
            Connection connection,
            int studentId,
            int classId
    ) throws SQLException {

        String sql =
                """
                SELECT COUNT(*) AS total

                FROM dbo.Enrollments

                WHERE student_id = ?
                  AND class_id = ?
                  AND UPPER(status)
                      IN ('ENROLLED', 'COMPLETED')
                """;

        try (
                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setInt(
                    1,
                    studentId
            );

            statement.setInt(
                    2,
                    classId
            );

            try (
                    ResultSet resultSet =
                            statement.executeQuery()
            ) {

                return resultSet.next()
                        && resultSet.getInt(
                        "total"
                ) > 0;
            }
        }
    }

    private boolean isAlreadyRegisteredCourse(
            Connection connection,
            int studentId,
            int courseId
    ) throws SQLException {

        String sql =
                """
                SELECT COUNT(*) AS total

                FROM dbo.Enrollments e

                INNER JOIN dbo.CourseClasses cc
                    ON cc.class_id = e.class_id

                WHERE e.student_id = ?
                  AND cc.course_id = ?
                  AND UPPER(e.status)
                      IN ('ENROLLED', 'COMPLETED')
                """;

        try (
                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setInt(
                    1,
                    studentId
            );

            statement.setInt(
                    2,
                    courseId
            );

            try (
                    ResultSet resultSet =
                            statement.executeQuery()
            ) {

                return resultSet.next()
                        && resultSet.getInt(
                        "total"
                ) > 0;
            }
        }
    }

    private boolean hasAvailableSlot(
            Connection connection,
            int classId,
            int maximumStudents
    ) throws SQLException {

        String sql =
                """
                SELECT COUNT(*) AS total

                FROM dbo.Enrollments

                WHERE class_id = ?
                  AND UPPER(status)
                      IN ('ENROLLED', 'COMPLETED')
                """;

        try (
                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setInt(
                    1,
                    classId
            );

            try (
                    ResultSet resultSet =
                            statement.executeQuery()
            ) {

                if (!resultSet.next()) {
                    return false;
                }

                int enrolledStudents =
                        resultSet.getInt(
                                "total"
                        );

                return enrolledStudents
                        < maximumStudents;
            }
        }
    }

    private String findScheduleConflict(
            Connection connection,
            int studentId,
            String candidateSchedule
    ) throws SQLException {

        ScheduleRange candidate =
                parseSchedule(
                        candidateSchedule
                );

        if (candidate == null) {
            return null;
        }

        String sql =
                """
                SELECT
                    cc.class_code,
                    cc.schedule_text

                FROM dbo.Enrollments e

                INNER JOIN dbo.CourseClasses cc
                    ON cc.class_id = e.class_id

                WHERE e.student_id = ?
                  AND UPPER(e.status)
                      IN ('ENROLLED', 'COMPLETED')
                """;

        try (
                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setInt(
                    1,
                    studentId
            );

            try (
                    ResultSet resultSet =
                            statement.executeQuery()
            ) {

                while (resultSet.next()) {

                    ScheduleRange existing =
                            parseSchedule(
                                    resultSet.getString(
                                            "schedule_text"
                                    )
                            );

                    if (existing != null
                            && candidate.conflictsWith(
                            existing
                    )) {

                        return resultSet.getString(
                                "class_code"
                        );
                    }
                }
            }
        }

        return null;
    }
    private int insertEnrollment(
            Connection connection,
            int studentId,
            int classId
    ) throws SQLException {

        String sql =
                """
                INSERT INTO dbo.Enrollments
                (
                    student_id,
                    class_id,
                    enrollment_date,
                    status,
                    created_at,
                    progress_percent
                )
                VALUES
                (
                    ?,
                    ?,
                    ?,
                    'ENROLLED',
                    SYSDATETIME(),
                    0
                )
                """;

        try (
                PreparedStatement statement =
                        connection.prepareStatement(
                                sql,
                                Statement.RETURN_GENERATED_KEYS
                        )
        ) {

            statement.setInt(
                    1,
                    studentId
            );

            statement.setInt(
                    2,
                    classId
            );

            statement.setDate(
                    3,
                    Date.valueOf(
                            LocalDate.now()
                    )
            );

            int affectedRows =
                    statement.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException(
                        "Không thể tạo đăng ký học."
                );
            }

            try (
                    ResultSet generatedKeys =
                            statement.getGeneratedKeys()
            ) {

                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                }
            }
        }

        throw new SQLException(
                "Không lấy được enrollment_id."
        );
    }

    private int insertPayment(
            Connection connection,
            int studentId,
            int enrollmentId,
            BigDecimal tuitionFee
    ) throws SQLException {

        BigDecimal safeTuitionFee =
                tuitionFee == null
                        ? BigDecimal.ZERO
                        : tuitionFee.max(
                        BigDecimal.ZERO
                );

        String sql =
                """
                INSERT INTO dbo.Payments
                (
                    student_id,
                    enrollment_id,
                    amount,
                    payment_date,
                    payment_method,
                    status,
                    note,
                    created_at
                )
                VALUES
                (
                    ?,
                    ?,
                    ?,
                    NULL,
                    NULL,
                    'UNPAID',
                    N'Học phí khóa học mới đăng ký',
                    SYSDATETIME()
                )
                """;

        try (
                PreparedStatement statement =
                        connection.prepareStatement(
                                sql,
                                Statement.RETURN_GENERATED_KEYS
                        )
        ) {

            statement.setInt(
                    1,
                    studentId
            );

            statement.setInt(
                    2,
                    enrollmentId
            );

            statement.setBigDecimal(
                    3,
                    safeTuitionFee
            );

            int affectedRows =
                    statement.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException(
                        "Không thể tạo khoản học phí."
                );
            }

            try (
                    ResultSet generatedKeys =
                            statement.getGeneratedKeys()
            ) {

                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                }
            }
        }

        throw new SQLException(
                "Không lấy được payment_id."
        );
    }

    private ScheduleRange parseSchedule(
            String scheduleText
    ) {

        if (scheduleText == null
                || scheduleText.isBlank()) {

            return null;
        }

        String normalizedText =
                removeVietnameseAccents(
                        scheduleText
                                .trim()
                                .toLowerCase(
                                        Locale.ROOT
                                )
                );

        int dayOfWeek =
                extractDayOfWeek(
                        normalizedText
                );

        Matcher periodMatcher =
                PERIOD_PATTERN.matcher(
                        normalizedText
                );

        if (dayOfWeek <= 0
                || !periodMatcher.find()) {

            return null;
        }

        int startPeriod =
                Integer.parseInt(
                        periodMatcher.group(2)
                );

        int endPeriod =
                Integer.parseInt(
                        periodMatcher.group(4)
                );

        return new ScheduleRange(
                dayOfWeek,
                Math.min(
                        startPeriod,
                        endPeriod
                ),
                Math.max(
                        startPeriod,
                        endPeriod
                )
        );
    }

    private int extractDayOfWeek(
            String normalizedText
    ) {

        if (normalizedText.contains(
                "chu nhat"
        )) {
            return 7;
        }

        Matcher matcher =
                DAY_PATTERN.matcher(
                        normalizedText
                );

        if (!matcher.find()) {
            return 0;
        }

        String dayNumber =
                matcher.group(2);

        if (dayNumber == null) {
            return 0;
        }

        return switch (
                Integer.parseInt(
                        dayNumber
                )
                ) {
            case 2 -> 1;
            case 3 -> 2;
            case 4 -> 3;
            case 5 -> 4;
            case 6 -> 5;
            case 7 -> 6;
            default -> 0;
        };
    }

    private String removeVietnameseAccents(
            String text
    ) {

        if (text == null) {
            return "";
        }

        String normalized =
                Normalizer.normalize(
                        text,
                        Normalizer.Form.NFD
                );

        return normalized
                .replaceAll(
                        "\\p{M}",
                        ""
                )
                .replace(
                        'đ',
                        'd'
                )
                .replace(
                        'Đ',
                        'D'
                );
    }

    private void validatePositiveId(
            int id,
            String fieldName
    ) {

        if (id <= 0) {

            throw new IllegalArgumentException(
                    fieldName
                            + " phải lớn hơn 0."
            );
        }
    }

    private record ScheduleRange(
            int dayOfWeek,
            int startPeriod,
            int endPeriod
    ) {

        private boolean conflictsWith(
                ScheduleRange other
        ) {

            if (other == null) {
                return false;
            }

            if (dayOfWeek
                    != other.dayOfWeek) {

                return false;
            }

            return startPeriod
                    <= other.endPeriod
                    && other.startPeriod
                    <= endPeriod;
        }
    }
}