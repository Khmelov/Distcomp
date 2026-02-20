/*
  Warnings:

  - You are about to drop the `Article` table. If the table is not empty, all the data it contains will be lost.
  - You are about to drop the `ArticleSticker` table. If the table is not empty, all the data it contains will be lost.
  - You are about to drop the `Notice` table. If the table is not empty, all the data it contains will be lost.
  - You are about to drop the `Sticker` table. If the table is not empty, all the data it contains will be lost.
  - You are about to drop the `User` table. If the table is not empty, all the data it contains will be lost.

*/
-- DropForeignKey
ALTER TABLE "Article" DROP CONSTRAINT "Article_userId_fkey";

-- DropForeignKey
ALTER TABLE "ArticleSticker" DROP CONSTRAINT "ArticleSticker_articleId_fkey";

-- DropForeignKey
ALTER TABLE "ArticleSticker" DROP CONSTRAINT "ArticleSticker_stickerId_fkey";

-- DropForeignKey
ALTER TABLE "Notice" DROP CONSTRAINT "Notice_articleId_fkey";

-- DropTable
DROP TABLE "Article";

-- DropTable
DROP TABLE "ArticleSticker";

-- DropTable
DROP TABLE "Notice";

-- DropTable
DROP TABLE "Sticker";

-- DropTable
DROP TABLE "User";

-- CreateTable
CREATE TABLE "tbl_user" (
    "id" BIGSERIAL NOT NULL,
    "login" TEXT NOT NULL,
    "password" TEXT NOT NULL,
    "firstname" TEXT NOT NULL,
    "lastname" TEXT NOT NULL,

    CONSTRAINT "tbl_user_pkey" PRIMARY KEY ("id")
);

-- CreateTable
CREATE TABLE "tbl_article" (
    "id" BIGSERIAL NOT NULL,
    "title" TEXT NOT NULL,
    "content" TEXT NOT NULL,
    "created" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "modified" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "userId" BIGINT NOT NULL,

    CONSTRAINT "tbl_article_pkey" PRIMARY KEY ("id")
);

-- CreateTable
CREATE TABLE "tbl_notice" (
    "id" BIGSERIAL NOT NULL,
    "content" TEXT NOT NULL,
    "articleId" BIGINT NOT NULL,

    CONSTRAINT "tbl_notice_pkey" PRIMARY KEY ("id")
);

-- CreateTable
CREATE TABLE "tbl_article-sticker" (
    "articleId" BIGINT NOT NULL,
    "stickerId" BIGINT NOT NULL
);

-- CreateTable
CREATE TABLE "tbl_sticker" (
    "id" BIGSERIAL NOT NULL,
    "name" TEXT NOT NULL,

    CONSTRAINT "tbl_sticker_pkey" PRIMARY KEY ("id")
);

-- CreateIndex
CREATE UNIQUE INDEX "tbl_user_login_key" ON "tbl_user"("login");

-- CreateIndex
CREATE UNIQUE INDEX "tbl_article_title_key" ON "tbl_article"("title");

-- CreateIndex
CREATE INDEX "tbl_article-sticker_stickerId_articleId_idx" ON "tbl_article-sticker"("stickerId", "articleId");

-- CreateIndex
CREATE UNIQUE INDEX "tbl_article-sticker_stickerId_articleId_key" ON "tbl_article-sticker"("stickerId", "articleId");

-- CreateIndex
CREATE UNIQUE INDEX "tbl_sticker_id_name_key" ON "tbl_sticker"("id", "name");

-- AddForeignKey
ALTER TABLE "tbl_article" ADD CONSTRAINT "tbl_article_userId_fkey" FOREIGN KEY ("userId") REFERENCES "tbl_user"("id") ON DELETE RESTRICT ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "tbl_notice" ADD CONSTRAINT "tbl_notice_articleId_fkey" FOREIGN KEY ("articleId") REFERENCES "tbl_article"("id") ON DELETE RESTRICT ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "tbl_article-sticker" ADD CONSTRAINT "tbl_article-sticker_articleId_fkey" FOREIGN KEY ("articleId") REFERENCES "tbl_article"("id") ON DELETE RESTRICT ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "tbl_article-sticker" ADD CONSTRAINT "tbl_article-sticker_stickerId_fkey" FOREIGN KEY ("stickerId") REFERENCES "tbl_sticker"("id") ON DELETE RESTRICT ON UPDATE CASCADE;
