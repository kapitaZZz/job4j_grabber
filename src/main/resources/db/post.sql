create table if not exists post (
    id serial primary key ,
    name varchar(255),
    text varchar(255),
    link text unique,
    created timestamp
)