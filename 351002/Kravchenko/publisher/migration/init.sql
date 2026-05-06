
CREATE TABLE tbl_editor (
                            id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                            login VARCHAR(64) UNIQUE NOT NULL,
                            password VARCHAR(255) NOT NULL,
                            firstname VARCHAR(64) NOT NULL,
                            lastname VARCHAR(64) NOT NULL,
                            role VARCHAR(20) NOT NULL DEFAULT 'CUSTOMER',

                            CONSTRAINT editor_login_len CHECK (char_length(login) >= 2),
                            CONSTRAINT editor_firstname_len CHECK (char_length(firstname) >= 2),
                            CONSTRAINT editor_lastname_len CHECK (char_length(lastname) >= 2),
                            CONSTRAINT editor_role_check CHECK (role IN ('ADMIN', 'CUSTOMER'))
);

CREATE TABLE tbl_issue (
                           id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                           editor_id BIGINT NOT NULL REFERENCES tbl_editor(id) ON DELETE CASCADE,
                           title VARCHAR(64) UNIQUE NOT NULL,
                           content VARCHAR(2048) NOT NULL,
                           created TIMESTAMPTZ NOT NULL DEFAULT NOW(),
                           modified TIMESTAMPTZ NOT NULL DEFAULT NOW(),

                           CONSTRAINT issue_title_len CHECK (char_length(title) >= 2),
                           CONSTRAINT issue_content_len CHECK (char_length(content) >= 4)
);

CREATE TABLE tbl_note (
                          id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                          issue_id BIGINT NOT NULL REFERENCES tbl_issue(id) ON DELETE CASCADE,
                          content VARCHAR(2048) NOT NULL,

                          CONSTRAINT note_content_len CHECK (char_length(content) >= 2)
);

CREATE TABLE tbl_sticker (
                             id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                             name VARCHAR(32) UNIQUE NOT NULL,

                             CONSTRAINT sticker_name_len CHECK (char_length(name) >= 2)
);

CREATE TABLE tbl_issue_sticker (
                                   id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                                   issue_id BIGINT NOT NULL REFERENCES tbl_issue(id) ON DELETE CASCADE,
                                   sticker_id BIGINT NOT NULL REFERENCES tbl_sticker(id) ON DELETE CASCADE,

                                   UNIQUE(issue_id, sticker_id)
);


INSERT INTO tbl_editor (login, password, firstname, lastname, role)
VALUES (
           'admin@distcomp.com',
           '$2a$10$8K9Vf1nN2M/rO9T.MhC9GeX0J3vGqL5S5Z.C.R1QZ9S1fK2QW3eWq',
           'Admin',
           'System',
           'ADMIN'
       );

INSERT INTO tbl_editor (login, password, firstname, lastname, role)
VALUES (
           'skvidich0106@gmail.com',
           '$2a$10$8K9Vf1nN2M/rO9T.MhC9GeX0J3vGqL5S5Z.C.R1QZ9S1fK2QW3eWq',
           'Владислав',
           'Кравченко',
           'CUSTOMER'
       );