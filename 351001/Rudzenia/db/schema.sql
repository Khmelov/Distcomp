CREATE TABLE "author" (
    "Id" bigint NOT NULL GENERATED ALWAYS AS IDENTITY,
    "Login" varchar(64) NOT NULL,
    "Password" varchar(128) NOT NULL,
    "FirstName" varchar(64) NOT NULL,
    "LastName" varchar(64) NOT NULL,
    CONSTRAINT "PK_author" PRIMARY KEY ("Id")
);

CREATE TABLE "article" (
    "Id" bigint NOT NULL GENERATED ALWAYS AS IDENTITY,
    "AuthorId" bigint NOT NULL REFERENCES "author"("Id") ON DELETE CASCADE,
    "Title" varchar(64) NOT NULL,
    "Content" varchar(2048) NOT NULL,
    "Created" timestamptz NOT NULL DEFAULT now(),
    "Modified" timestamptz NOT NULL DEFAULT now(),
    CONSTRAINT "PK_article" PRIMARY KEY ("Id")
);

CREATE TABLE "note" (
    "Id" bigint NOT NULL GENERATED ALWAYS AS IDENTITY,
    "ArticleId" bigint NOT NULL REFERENCES "article"("Id") ON DELETE CASCADE,
    "Content" varchar(2048) NOT NULL,
    "FirstName" varchar(64) NOT NULL,
    "LastName" varchar(64) NOT NULL,
    CONSTRAINT "PK_note" PRIMARY KEY ("Id")
);

CREATE TABLE "tag" (
    "Id" bigint NOT NULL GENERATED ALWAYS AS IDENTITY,
    "Name" varchar(32) NOT NULL,
    CONSTRAINT "PK_tag" PRIMARY KEY ("Id")
);
