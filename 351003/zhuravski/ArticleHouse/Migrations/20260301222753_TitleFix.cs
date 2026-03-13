using Microsoft.EntityFrameworkCore.Migrations;

#nullable disable

namespace ArticleHouse.Migrations
{
    /// <inheritdoc />
    public partial class TitleFix : Migration
    {
        /// <inheritdoc />
        protected override void Up(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.CreateIndex(
                name: "ix_tbl_article_title",
                table: "tbl_article",
                column: "title",
                unique: true);
        }

        /// <inheritdoc />
        protected override void Down(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropIndex(
                name: "ix_tbl_article_title",
                table: "tbl_article");
        }
    }
}
