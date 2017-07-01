package com.developer.alexandru.orarusv.data

import java.io.BufferedReader

/**
 * Created by alexandru on 10/16/16.
 * Parser for alternative courses;
 */
class AlternativeCoursesParser(private val adapter: AlternativeCoursesListAdapter, br: BufferedReader, private val courseToReplace: Course?) : CSVParser(br) {

    override fun handleData(data: Array<String>): Boolean {
        if (this.courseToReplace == null)
            return false
        if (!CourseBuilder.isDataValid(data))
            return false

        if (courseToReplace.fullName != null && courseToReplace.fullName == data[CsvAPI.COURSE_FULL_NAME] &&
                courseToReplace.type == data[CsvAPI.TYPE]) {
            val c = CourseBuilder.build(data)
            adapter.add(c)
            return true
        }
        return false
    }
}
