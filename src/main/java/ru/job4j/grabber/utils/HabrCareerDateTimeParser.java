package ru.job4j.grabber.utils;

import java.time.ZonedDateTime;

public class HabrCareerDateTimeParser implements DateTimeParser {
    @Override
    public ZonedDateTime parse(String time) {
        String[] dateTimeArr = time.split("T");
        return ZonedDateTime.parse(dateTimeArr[0]);
    }
}
