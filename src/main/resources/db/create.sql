CREATE TABLE IF NOT EXISTS rabbit;

ALTER TABLE IF EXISTS quartz
    OWNER to postgres;

ALTER TABLE IF EXISTS quartz
    ADD COLUMN created_date date;