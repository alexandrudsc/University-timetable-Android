package com.test.developer.alexandru.orarusv


import com.developer.alexandru.orarusv.data.CourseBuilder
import com.developer.alexandru.orarusv.data.CsvAPI
import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Test

class CourseBuilderTest {

    @Test
    fun simpleCourseBuild() {
        val data = Array<String>(CsvAPI.CSV_COUNT + 1, { "" })
        data[CsvAPI.PROF_ID] = "1"
        data[CsvAPI.PROF_FIRST_NAME] = "Ovidiu"
        data[CsvAPI.PROF_LAST_NAME] = "Schipor"
        data[CsvAPI.COURSE_NAME] = "PCLP"
        data[CsvAPI.COURSE_FULL_NAME] = "Programare"
        data[CsvAPI.DAY] = "0"
        data[CsvAPI.START] = "0"
        data[CsvAPI.STOP] = "120"

        assertThat(CourseBuilder.isDataValid(data), `is`(true))
        val course = CourseBuilder.build(data)
        assertThat(course, `is`(notNullValue()))

        assertThat(course.profID, equalTo("1"))
        assertThat(course.prof.trim(), equalTo("Ovidiu Schipor"))
        assertThat(course.name, equalTo("PCLP"))
        assertThat(course.fullName, equalTo("Programare"))

    }

    @Test
    fun startAndEndTime() {
        val data = Array<String>(CsvAPI.CSV_COUNT + 1, { "" })
        data[CsvAPI.PROF_ID] = "1"
        data[CsvAPI.PROF_FIRST_NAME] = "Ovidiu"
        data[CsvAPI.PROF_LAST_NAME] = "Schipor"
        data[CsvAPI.COURSE_NAME] = "PCLP"
        data[CsvAPI.COURSE_FULL_NAME] = "Programare"
        data[CsvAPI.DAY] = "0"
        data[CsvAPI.START] = "480"
        data[CsvAPI.STOP] = "120"

        assertThat(CourseBuilder.isDataValid(data), `is`(true))
        val course = CourseBuilder.build(data)
        assertThat(course.time, `is`("8:00 - 10:00"))
    }


}