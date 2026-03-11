using Microsoft.EntityFrameworkCore.Migrations;

#nullable disable

namespace ArticleHouse.Migrations
{
    /// <inheritdoc />
    public partial class FKFix : Migration
    {
        /// <inheritdoc />
        protected override void Up(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.CreateIndex(
                name: "ix_tbl_comment_article_id",
                table: "tbl_comment",
                column: "article_id");

            migrationBuilder.CreateIndex(
                name: "ix_tbl_article_creator_id",
                table: "tbl_article",
                column: "creator_id");

            migrationBuilder.AddForeignKey(
                name: "fk_tbl_article_tbl_creator_creator_id",
                table: "tbl_article",
                column: "creator_id",
                principalTable: "tbl_creator",
                principalColumn: "id",
                onDelete: ReferentialAction.Cascade);

            migrationBuilder.AddForeignKey(
                name: "fk_tbl_comment_tbl_article_article_id",
                table: "tbl_comment",
                column: "article_id",
                principalTable: "tbl_article",
                principalColumn: "id",
                onDelete: ReferentialAction.Cascade);
        }

        /// <inheritdoc />
        protected override void Down(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropForeignKey(
                name: "fk_tbl_article_tbl_creator_creator_id",
                table: "tbl_article");

            migrationBuilder.DropForeignKey(
                name: "fk_tbl_comment_tbl_article_article_id",
                table: "tbl_comment");

            migrationBuilder.DropIndex(
                name: "ix_tbl_comment_article_id",
                table: "tbl_comment");

            migrationBuilder.DropIndex(
                name: "ix_tbl_article_creator_id",
                table: "tbl_article");
        }
    }
}
