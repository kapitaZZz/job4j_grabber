package ru.job4j.grabber;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import ru.job4j.grabber.dto.Post;
import ru.job4j.grabber.utils.DateTimeParser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class HabrCareerParse implements Parse {

    public static final int PAGES = 5;
    private static final String SOURCE_LINK = "https://career.habr.com";
    private static final String PAGE_LINK = String.format("%s/vacancies/java_developer?page=", SOURCE_LINK);

    private final DateTimeParser dateTimeParser;

    public HabrCareerParse(DateTimeParser dateTimeParser) {
        this.dateTimeParser = dateTimeParser;
    }

    private String retrieveDescription(String link) throws Exception {
        Document document = Jsoup.connect(link).get();
        Element element = document.select(".collapsible-description__content").first();
        link = element.text();
        return link;
    }

    @Override
    public List<Post> list(String link) {
        List<Post> posts = new ArrayList<>();
        for (int i = 1; i <= PAGES; i++) {
            Document document = null;
            try {
                document = Jsoup.connect(link + i).get();
                Elements rows = document.select(".vacancy-card__inner");
                rows.forEach(row ->
                        posts.add(postParser(row)));
            } catch (IOException e) {
                throw new IllegalArgumentException();
            }
        }
        return posts;
    }

    private Post postParser(Element el) {
        Element titleElement = el.select(".vacancy-card__title").first();
        Element linkElement = titleElement.child(0);
        Element titleDate = el.select(".vacancy-card__date").first();
        Element linkDate = titleDate.child(0);
        String vacancyName = linkElement.text();
        String date = linkDate.attr("datetime");
        String link = String.format("%s%s", SOURCE_LINK,
                linkElement.attr("href"),
                linkDate.attr("time"));
        Post post = null;
        try {
            post = new Post(
                    vacancyName,
                    link,
                    retrieveDescription(link),
                    dateTimeParser.parse(date));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return post;
    }
}


