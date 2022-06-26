package ru.job4j.grabber;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import ru.job4j.grabber.utils.HabrCareerDateTimeParser;

import java.io.IOException;

public class HabrCareerParse {

    private static final String SOURCE_LINK = "https://career.habr.com";

    private static final String PAGE_LINK = String.format("%s/vacancies/java_developer", SOURCE_LINK);

    public static void main(String[] args) throws IOException {
        for (int i = 0; i <= 5; i++) {
            Document document = Jsoup.connect("https://career.habr.com/vacancies/java_developer?page=" + i).get();
            Elements rows = document.select(".vacancy-card__inner");
            for (Element element : rows) {
                HabrCareerDateTimeParser parser = new HabrCareerDateTimeParser();
                element.children().get(2).text();
                parser.parse(element.children().get(0).children().get(0).attr("datetime"));

            }
        }
    }
}


