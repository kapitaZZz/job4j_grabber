package ru.job4j.grabber;

import ru.job4j.grabber.dto.Post;
import ru.job4j.grabber.utils.DateTimeParser;
import ru.job4j.grabber.utils.HabrCareerDateTimeParser;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class PsqlStore implements Store, AutoCloseable {

    private Connection connection;

    public PsqlStore(Properties properties) {
        try {
            Class.forName(properties.getProperty("driver-class-name"));
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        try {
            connection = DriverManager.getConnection(
                    properties.getProperty("url"),
                    properties.getProperty("username"),
                    properties.getProperty("password")
            );
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void close() throws Exception {
        if (connection != null) {
            connection.close();
        }
    }

    @Override
    public void save(Post post) {
        try (PreparedStatement statement = connection.prepareStatement(
                "insert into post(title, link, description, created) values (?, ?, ?, ?) " +
                        "on conflict (link) do nothing")
        ) {
            statement.setString(1, post.getTitle());
            statement.setString(2, post.getLink());
            statement.setString(3, post.getDescription());
            statement.setTimestamp(4, Timestamp.valueOf(post.getCreated()));
            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Post> getAll() {
        List<Post> posts = new ArrayList<>();
        try (PreparedStatement statement = connection.prepareStatement("select * from post")) {
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                posts.add(getPost(resultSet));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return posts;
    }

    public Post getPost(ResultSet resultSet) throws SQLException {
        int id = resultSet.getInt(1);
        String title = resultSet.getString(2);
        String link = resultSet.getString(3);
        String description = resultSet.getString(4);
        LocalDateTime created = resultSet.getTimestamp(5).toLocalDateTime();
        return new Post(id, title, link, description, created);
    }

    @Override
    public Post findById(int id) {
        Post post = null;
        try (PreparedStatement statement = connection.prepareStatement("select * from post where id=?")) {
            statement.setInt(1, id);
            ResultSet resultSet = statement.getResultSet();
            if (resultSet.next()) {
                post = getPost(resultSet);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return post;
    }

    private static Properties getConfig() {
        Properties properties = new Properties();
        try (InputStream inputStream = PsqlStore.class.getClassLoader().getResourceAsStream("db.properties")) {
            properties.load(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return properties;
    }

    public void createTable(Connection connection) {
        String sql = String.format("create table if not exists post (%s, %s, %s, %s, %s);",
                "id serial primary key",
                "title varchar(255)",
                "link varchar(255) unique",
                "description text",
                "created timestamp");
        try (Statement statement = connection.createStatement()) {
            statement.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        try (PsqlStore psqlStore = new PsqlStore(getConfig())) {
            psqlStore.createTable(psqlStore.connection);
            DateTimeParser dateTimeParser = new HabrCareerDateTimeParser();
            HabrCareerParse habrCareerParse = new HabrCareerParse(dateTimeParser);
            String link = String.format("%s/vacancies/java_developer?page=", "https://career.habr.com");
            for (Post vacancy : habrCareerParse.list(link)) {
                psqlStore.save(vacancy);
                System.out.println(vacancy);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}