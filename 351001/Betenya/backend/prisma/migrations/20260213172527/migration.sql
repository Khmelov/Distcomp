/*
  Warnings:

  - You are about to drop the column `userId` on the `tbl_article` table. All the data in the column will be lost.
  - Added the required column `user_id` to the `tbl_article` table without a default value. This is not possible if the table is not empty.

*/
-- DropForeignKey
ALTER TABLE "tbl_article" DROP CONSTRAINT "tbl_article_userId_fkey";

-- AlterTable
ALTER TABLE "tbl_article" DROP COLUMN "userId",
ADD COLUMN     "user_id" BIGINT NOT NULL;

-- AddForeignKey
ALTER TABLE "tbl_article" ADD CONSTRAINT "tbl_article_user_id_fkey" FOREIGN KEY ("user_id") REFERENCES "tbl_user"("id") ON DELETE RESTRICT ON UPDATE CASCADE;
