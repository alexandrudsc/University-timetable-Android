package com.developer.alexandru.orarusv.data;

/**
 * Created by alexandru on 10/20/16.
 * Helper class for creating a course from an array of strings
 */
public class CourseBuilder {

    public static Course build(String[] data) {
        if (!isDataValid(data))
            return  null;
        try {
            final int startTime = Integer.valueOf(data[CsvAPI.START]) / 60;
            final int endTime = Integer.valueOf(data[CsvAPI.STOP]) / 60 + startTime;

            Course c = new Course();
            c.name = data[CsvAPI.COURSE_NAME];
            c.fullName = data[CsvAPI.COURSE_FULL_NAME];
            c.type = data[CsvAPI.TYPE];

            c.location = data[CsvAPI.ROOM_SHORT_NAME];
            c.fullLocation = data[CsvAPI.BUILDING] + " " +data[CsvAPI.ROOM];

            c.startTime = startTime;
            c.endTime = endTime;
            c.time = startTime + ":00 - " + endTime + ":00";

            c.prof = data[CsvAPI.RANK] + " " +
                    data[CsvAPI.HAS_PHD] + " " +
                    data[CsvAPI.PROF_FIRST_NAME] + " " +
                    data[CsvAPI.PROF_LAST_NAME];
            c.profID = data[CsvAPI.PROF_ID];

            c.parity = data[CsvAPI.PARITY];
            c.info = data[CsvAPI.INFO];
            return c;
        }
        catch (Exception ex) {
            return null;
        }
    }

    public static boolean isDataValid(String[] data) {
        return data != null && data.length >= CsvAPI.CSV_COUNT;
    }
}
