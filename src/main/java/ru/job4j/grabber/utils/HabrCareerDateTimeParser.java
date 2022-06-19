package ru.job4j.grabber.utils;

import java.time.LocalDateTime;

public class HabrCareerDateTimeParser implements DateTimeParser {
    @Override
    public LocalDateTime parse(String time) {
        String[] dateTimeArr = time.split("T");
        return LocalDateTime.parse(dateTimeArr[0]);
    }
}
