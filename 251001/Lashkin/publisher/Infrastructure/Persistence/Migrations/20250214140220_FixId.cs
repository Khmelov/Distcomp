using Microsoft.EntityFrameworkCore.Migrations;

#nullable disable

namespace Persistence.Migrations
{
    /// <inheritdoc />
    public partial class FixId : Migration
    {
        /// <inheritdoc />
        protected override void Up(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropForeignKey(
                name: "FK_tbl_news_tbl_user_UserId",
                table: "tbl_news");

            migrationBuilder.DropForeignKey(
                name: "FK_tbl_notice_tbl_news_NewsId",
                table: "tbl_notice");

            migrationBuilder.RenameColumn(
                name: "NewsId",
                table: "tbl_notice",
                newName: "notice_id");

            migrationBuilder.RenameIndex(
                name: "IX_tbl_notice_NewsId",
                table: "tbl_notice",
                newName: "IX_tbl_notice_notice_id");

            migrationBuilder.RenameColumn(
                name: "UserId",
                table: "tbl_news",
                newName: "user_id");

            migrationBuilder.RenameIndex(
                name: "IX_tbl_news_UserId",
                table: "tbl_news",
                newName: "IX_tbl_news_user_id");

            migrationBuilder.AddForeignKey(
                name: "FK_tbl_news_tbl_user_user_id",
                table: "tbl_news",
                column: "user_id",
                principalTable: "tbl_user",
                principalColumn: "id",
                onDelete: ReferentialAction.Cascade);

            migrationBuilder.AddForeignKey(
                name: "FK_tbl_notice_tbl_news_notice_id",
                table: "tbl_notice",
                column: "notice_id",
                principalTable: "tbl_news",
                principalColumn: "id",
                onDelete: ReferentialAction.Cascade);
        }

        /// <inheritdoc />
        protected override void Down(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropForeignKey(
                name: "FK_tbl_news_tbl_user_user_id",
                table: "tbl_news");

            migrationBuilder.DropForeignKey(
                name: "FK_tbl_notice_tbl_news_notice_id",
                table: "tbl_notice");

            migrationBuilder.RenameColumn(
                name: "notice_id",
                table: "tbl_notice",
                newName: "NewsId");

            migrationBuilder.RenameIndex(
                name: "IX_tbl_notice_notice_id",
                table: "tbl_notice",
                newName: "IX_tbl_notice_NewsId");

            migrationBuilder.RenameColumn(
                name: "user_id",
                table: "tbl_news",
                newName: "UserId");

            migrationBuilder.RenameIndex(
                name: "IX_tbl_news_user_id",
                table: "tbl_news",
                newName: "IX_tbl_news_UserId");

            migrationBuilder.AddForeignKey(
                name: "FK_tbl_news_tbl_user_UserId",
                table: "tbl_news",
                column: "UserId",
                principalTable: "tbl_user",
                principalColumn: "id",
                onDelete: ReferentialAction.Cascade);

            migrationBuilder.AddForeignKey(
                name: "FK_tbl_notice_tbl_news_NewsId",
                table: "tbl_notice",
                column: "NewsId",
                principalTable: "tbl_news",
                principalColumn: "id",
                onDelete: ReferentialAction.Cascade);
        }
    }
}
