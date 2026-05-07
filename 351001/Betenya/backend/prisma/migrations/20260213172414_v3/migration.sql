/*
  Warnings:

  - The primary key for the `tbl_user` table will be changed. If it partially fails, the table could be left without primary key constraint.
  - You are about to drop the column `user_id` on the `tbl_user` table. All the data in the column will be lost.

*/
-- DropForeignKey
ALTER TABLE "tbl_article" DROP CONSTRAINT "tbl_article_userId_fkey";

-- AlterTable
ALTER TABLE "tbl_user" DROP CONSTRAINT "tbl_user_pkey",
DROP COLUMN "user_id",
ADD COLUMN     "id" BIGSERIAL NOT NULL,
ADD CONSTRAINT "tbl_user_pkey" PRIMARY KEY ("id");

-- AddForeignKey
ALTER TABLE "tbl_article" ADD CONSTRAINT "tbl_article_userId_fkey" FOREIGN KEY ("userId") REFERENCES "tbl_user"("id") ON DELETE RESTRICT ON UPDATE CASCADE;
