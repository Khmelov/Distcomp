using Microsoft.EntityFrameworkCore.Migrations;

#nullable disable

namespace Infrastructure.Migrations
{
    /// <inheritdoc />
    public partial class RenameFieldsToDatabaseNames : Migration
    {
        /// <inheritdoc />
        protected override void Up(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropForeignKey(
                name: "FK_tbl_post_tbl_news_NewsId",
                schema: "public",
                table: "tbl_post");

            migrationBuilder.RenameColumn(
                name: "Content",
                schema: "public",
                table: "tbl_post",
                newName: "content");

            migrationBuilder.RenameColumn(
                name: "NewsId",
                schema: "public",
                table: "tbl_post",
                newName: "news_id");

            migrationBuilder.RenameIndex(
                name: "IX_tbl_post_NewsId",
                schema: "public",
                table: "tbl_post",
                newName: "IX_tbl_post_news_id");

            migrationBuilder.RenameColumn(
                name: "Title",
                schema: "public",
                table: "tbl_news",
                newName: "title");

            migrationBuilder.RenameColumn(
                name: "Modified",
                schema: "public",
                table: "tbl_news",
                newName: "modified");

            migrationBuilder.RenameColumn(
                name: "Content",
                schema: "public",
                table: "tbl_news",
                newName: "content");

            migrationBuilder.RenameColumn(
                name: "CreatedAt",
                schema: "public",
                table: "tbl_news",
                newName: "created");

            migrationBuilder.RenameColumn(
                name: "Name",
                schema: "public",
                table: "tbl_marker",
                newName: "name");

            migrationBuilder.RenameColumn(
                name: "Password",
                schema: "public",
                table: "tbl_editor",
                newName: "password");

            migrationBuilder.RenameColumn(
                name: "Login",
                schema: "public",
                table: "tbl_editor",
                newName: "login");

            migrationBuilder.RenameColumn(
                name: "Lastname",
                schema: "public",
                table: "tbl_editor",
                newName: "lastname");

            migrationBuilder.RenameColumn(
                name: "Firstname",
                schema: "public",
                table: "tbl_editor",
                newName: "firstname");

            migrationBuilder.AddForeignKey(
                name: "FK_tbl_post_tbl_news_news_id",
                schema: "public",
                table: "tbl_post",
                column: "news_id",
                principalSchema: "public",
                principalTable: "tbl_news",
                principalColumn: "id",
                onDelete: ReferentialAction.Cascade);
        }

        /// <inheritdoc />
        protected override void Down(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropForeignKey(
                name: "FK_tbl_post_tbl_news_news_id",
                schema: "public",
                table: "tbl_post");

            migrationBuilder.RenameColumn(
                name: "content",
                schema: "public",
                table: "tbl_post",
                newName: "Content");

            migrationBuilder.RenameColumn(
                name: "news_id",
                schema: "public",
                table: "tbl_post",
                newName: "NewsId");

            migrationBuilder.RenameIndex(
                name: "IX_tbl_post_news_id",
                schema: "public",
                table: "tbl_post",
                newName: "IX_tbl_post_NewsId");

            migrationBuilder.RenameColumn(
                name: "title",
                schema: "public",
                table: "tbl_news",
                newName: "Title");

            migrationBuilder.RenameColumn(
                name: "modified",
                schema: "public",
                table: "tbl_news",
                newName: "Modified");

            migrationBuilder.RenameColumn(
                name: "content",
                schema: "public",
                table: "tbl_news",
                newName: "Content");

            migrationBuilder.RenameColumn(
                name: "created",
                schema: "public",
                table: "tbl_news",
                newName: "CreatedAt");

            migrationBuilder.RenameColumn(
                name: "name",
                schema: "public",
                table: "tbl_marker",
                newName: "Name");

            migrationBuilder.RenameColumn(
                name: "password",
                schema: "public",
                table: "tbl_editor",
                newName: "Password");

            migrationBuilder.RenameColumn(
                name: "login",
                schema: "public",
                table: "tbl_editor",
                newName: "Login");

            migrationBuilder.RenameColumn(
                name: "lastname",
                schema: "public",
                table: "tbl_editor",
                newName: "Lastname");

            migrationBuilder.RenameColumn(
                name: "firstname",
                schema: "public",
                table: "tbl_editor",
                newName: "Firstname");

            migrationBuilder.AddForeignKey(
                name: "FK_tbl_post_tbl_news_NewsId",
                schema: "public",
                table: "tbl_post",
                column: "NewsId",
                principalSchema: "public",
                principalTable: "tbl_news",
                principalColumn: "id",
                onDelete: ReferentialAction.Cascade);
        }
    }
}
