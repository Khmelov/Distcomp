using Microsoft.EntityFrameworkCore.Migrations;

#nullable disable

namespace Infrastructure.Migrations
{
    /// <inheritdoc />
    public partial class RenameEditorIdInNews : Migration
    {
        /// <inheritdoc />
        protected override void Up(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropForeignKey(
                name: "FK_tbl_news_tbl_editor_EditorId",
                schema: "public",
                table: "tbl_news");

            migrationBuilder.RenameColumn(
                name: "EditorId",
                schema: "public",
                table: "tbl_news",
                newName: "editor_id");

            migrationBuilder.RenameIndex(
                name: "IX_tbl_news_EditorId",
                schema: "public",
                table: "tbl_news",
                newName: "IX_tbl_news_editor_id");

            migrationBuilder.AddForeignKey(
                name: "FK_tbl_news_tbl_editor_editor_id",
                schema: "public",
                table: "tbl_news",
                column: "editor_id",
                principalSchema: "public",
                principalTable: "tbl_editor",
                principalColumn: "id",
                onDelete: ReferentialAction.Cascade);
        }

        /// <inheritdoc />
        protected override void Down(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropForeignKey(
                name: "FK_tbl_news_tbl_editor_editor_id",
                schema: "public",
                table: "tbl_news");

            migrationBuilder.RenameColumn(
                name: "editor_id",
                schema: "public",
                table: "tbl_news",
                newName: "EditorId");

            migrationBuilder.RenameIndex(
                name: "IX_tbl_news_editor_id",
                schema: "public",
                table: "tbl_news",
                newName: "IX_tbl_news_EditorId");

            migrationBuilder.AddForeignKey(
                name: "FK_tbl_news_tbl_editor_EditorId",
                schema: "public",
                table: "tbl_news",
                column: "EditorId",
                principalSchema: "public",
                principalTable: "tbl_editor",
                principalColumn: "id",
                onDelete: ReferentialAction.Cascade);
        }
    }
}
