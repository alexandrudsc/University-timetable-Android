package com.developer.alexandru.orarusv.data;

import java.io.BufferedReader;

/**
 * Created by alexandru on 10/16/16.
 * Parser for alternative courses;
 */
public class AlternativeCoursesParser extends CSVParser {

    private AlternativeCoursesListAdapter adapter;
    private Course courseToReplace;

    public AlternativeCoursesParser(AlternativeCoursesListAdapter adapter, BufferedReader br, Course courseToReplace) {
        super(br);
        this.adapter = adapter;
        this.courseToReplace = courseToReplace;
    }

    @Override
    public boolean handleData(String[] data) {
        if (this.courseToReplace == null)
            return false;
        if (!CourseBuilder.isDataValid(data))
            return false;

        if (courseToReplace.getFullName() != null && courseToReplace.getFullName().equals(data[CsvAPI.COURSE_FULL_NAME]) &&
                courseToReplace.getType().equals(data[CsvAPI.TYPE])) {
            Course c = CourseBuilder.build(data);
            adapter.add(c);
            return true;
        }
        return false;
    }
}
