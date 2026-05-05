-- Под пользователем с правом CREATEDB. После выполнения перезапустите приложение (или выполните Liquibase).

CREATE DATABASE distcomp;

-- Чтобы проверки вида SELECT * FROM tbl_writer без префикса схемы находили таблицы:
ALTER DATABASE distcomp SET search_path TO distcomp, public;
