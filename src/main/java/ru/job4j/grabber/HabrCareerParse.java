package ru.job4j.grabber;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import ru.job4j.grabber.dto.Post;
import ru.job4j.grabber.utils.DateTimeParser;
import ru.job4j.grabber.utils.HabrCareerDateTimeParser;

import java.io.IOException;

public class HabrCareerParse {

    private Post post;
    private static final String SOURCE_LINK = "https://career.habr.com";
    private static final String PAGE_LINK = String.format("%s/vacancies/java_developer", SOURCE_LINK);

    private final DateTimeParser dateTimeParser;

    public HabrCareerParse(DateTimeParser dateTimeParser) {
        this.dateTimeParser = dateTimeParser;
    }

    private String retrieveDescription(String link) throws Exception {
        Document document = Jsoup.connect(link).get();
        Elements element = document.getElementsByAttributeValue("class", "style-ugc");
        post.setDescription(element.text());
        return post.getDescription();
    }

    public static void main(String[] args) throws IOException {
        for (int i = 1; i <= 5; i++) {
            Document document = Jsoup.connect("https://career.habr.com/vacancies/java_developer?page=" + i).get();
            Elements rows = document.select(".vacancy-card__inner");
            for (Element element : rows) {
                HabrCareerDateTimeParser parser = new HabrCareerDateTimeParser();
                System.out.println("https://career.habr.com" + element.children().get(1).attr("href"));
                System.out.println(element.children().get(2).text());
                System.out.println(parser
                        .parse(element.children().get(0).children().get(0).attr("datetime")));
            }
        }
    }
}


