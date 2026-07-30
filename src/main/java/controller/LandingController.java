package controller;

import model.Course;
import service.CourseService;
import view.LandingPageView;

import javax.swing.*;
import java.sql.SQLException;
import java.util.List;

public class LandingController {

    private final CourseService courseService;
    private final LandingPageView view;

    public LandingController(
            LandingPageView view
    ) {
        this.view = view;
        this.courseService = new CourseService();
    }

    public void loadFeaturedCourses() {
        SwingWorker<List<Course>, Void> worker =
                new SwingWorker<>() {

                    @Override
                    protected List<Course> doInBackground()
                            throws SQLException {

                        return courseService
                                .getFeaturedCourses();
                    }

                    @Override
                    protected void done() {
                        try {
                            List<Course> courses = get();

                            view.displayCourses(courses);

                        } catch (Exception exception) {
                            view.displayCourses(
                                    List.of()
                            );

                            view.showCourseLoadingError(
                                    "Không thể tải dữ liệu khóa học.\n"
                                            + getErrorMessage(exception)
                            );
                        }
                    }
                };

        worker.execute();
    }

    public void refreshCourses() {
        loadFeaturedCourses();
    }

    private String getErrorMessage(
            Exception exception
    ) {
        Throwable cause = exception.getCause();

        if (
                cause != null
                        && cause.getMessage() != null
        ) {
            return cause.getMessage();
        }

        if (exception.getMessage() != null) {
            return exception.getMessage();
        }

        return "Lỗi không xác định.";
    }
}