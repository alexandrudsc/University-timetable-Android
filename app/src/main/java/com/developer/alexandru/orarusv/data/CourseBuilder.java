package com.developer.alexandru.orarusv.data;

/** Created by alexandru on 10/20/16. Helper class for creating a course from an array of strings */
public class CourseBuilder {

  public static Course build(String[] data) {
    if (!isDataValid(data)) return null;
    try {
      final int startTime = Integer.valueOf(data[CsvAPI.START]) / 60;
      final int endTime = Integer.valueOf(data[CsvAPI.STOP]) / 60 + startTime;

      Course c = new Course();
      c.setName(data[CsvAPI.COURSE_NAME]);
      c.setFullName(data[CsvAPI.COURSE_FULL_NAME]);
      c.setType(data[CsvAPI.TYPE]);

      c.setLocation(data[CsvAPI.ROOM_SHORT_NAME]);
      c.setFullLocation(data[CsvAPI.BUILDING] + " " + data[CsvAPI.ROOM]);

      c.setStartTime(startTime);
      c.setEndTime(endTime);
      c.setTime(startTime + ":00 - " + endTime + ":00");

      c.setDay(Integer.parseInt(data[CsvAPI.DAY]));
      // internally sunday is kept as day 0
      if (c.getDay() == 7)
      {
        c.setDay(0);
      }

      c.setProf(
          data[CsvAPI.RANK]
              + " "
              + data[CsvAPI.HAS_PHD]
              + " "
              + data[CsvAPI.PROF_FIRST_NAME]
              + " "
              + data[CsvAPI.PROF_LAST_NAME]);
      c.setProfID(data[CsvAPI.PROF_ID]);

      c.setParity(data[CsvAPI.PARITY]);
      c.setInfo(data[CsvAPI.INFO]);
      return c;
    } catch (Exception ex) {
      return null;
    }
  }

  public static boolean isDataValid(String[] data) {
    return data != null && data.length >= CsvAPI.CSV_COUNT;
  }
}
