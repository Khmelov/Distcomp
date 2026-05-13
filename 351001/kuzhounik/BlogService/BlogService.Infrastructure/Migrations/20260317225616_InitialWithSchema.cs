using Microsoft.EntityFrameworkCore.Migrations;

#nullable disable

namespace BlogService.Infrastructure.Migrations
{
    /// <inheritdoc />
    public partial class InitialWithSchema : Migration
    {
        /// <inheritdoc />
        protected override void Up(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropForeignKey(
                name: "FK_Comments_tbl_story_StoryID",
                table: "Comments");

            migrationBuilder.DropForeignKey(
                name: "FK_tbl_StorySticker_Stickers_StickerID",
                table: "tbl_StorySticker");

            migrationBuilder.DropPrimaryKey(
                name: "PK_Stickers",
                table: "Stickers");

            migrationBuilder.DropPrimaryKey(
                name: "PK_Comments",
                table: "Comments");

            migrationBuilder.EnsureSchema(
                name: "distcomp");

            migrationBuilder.RenameTable(
                name: "tbl_user",
                newName: "tbl_user",
                newSchema: "distcomp");

            migrationBuilder.RenameTable(
                name: "tbl_StorySticker",
                newName: "tbl_StorySticker",
                newSchema: "distcomp");

            migrationBuilder.RenameTable(
                name: "tbl_story",
                newName: "tbl_story",
                newSchema: "distcomp");

            migrationBuilder.RenameTable(
                name: "Stickers",
                newName: "tbl_stickers",
                newSchema: "distcomp");

            migrationBuilder.RenameTable(
                name: "Comments",
                newName: "tbl_comments",
                newSchema: "distcomp");

            migrationBuilder.RenameIndex(
                name: "IX_Comments_StoryID",
                schema: "distcomp",
                table: "tbl_comments",
                newName: "IX_tbl_comments_StoryID");

            migrationBuilder.AddPrimaryKey(
                name: "PK_tbl_stickers",
                schema: "distcomp",
                table: "tbl_stickers",
                column: "ID");

            migrationBuilder.AddPrimaryKey(
                name: "PK_tbl_comments",
                schema: "distcomp",
                table: "tbl_comments",
                column: "ID");

            migrationBuilder.AddForeignKey(
                name: "FK_tbl_comments_tbl_story_StoryID",
                schema: "distcomp",
                table: "tbl_comments",
                column: "StoryID",
                principalSchema: "distcomp",
                principalTable: "tbl_story",
                principalColumn: "ID",
                onDelete: ReferentialAction.Cascade);

            migrationBuilder.AddForeignKey(
                name: "FK_tbl_StorySticker_tbl_stickers_StickerID",
                schema: "distcomp",
                table: "tbl_StorySticker",
                column: "StickerID",
                principalSchema: "distcomp",
                principalTable: "tbl_stickers",
                principalColumn: "ID",
                onDelete: ReferentialAction.Cascade);
        }

        /// <inheritdoc />
        protected override void Down(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropForeignKey(
                name: "FK_tbl_comments_tbl_story_StoryID",
                schema: "distcomp",
                table: "tbl_comments");

            migrationBuilder.DropForeignKey(
                name: "FK_tbl_StorySticker_tbl_stickers_StickerID",
                schema: "distcomp",
                table: "tbl_StorySticker");

            migrationBuilder.DropPrimaryKey(
                name: "PK_tbl_stickers",
                schema: "distcomp",
                table: "tbl_stickers");

            migrationBuilder.DropPrimaryKey(
                name: "PK_tbl_comments",
                schema: "distcomp",
                table: "tbl_comments");

            migrationBuilder.RenameTable(
                name: "tbl_user",
                schema: "distcomp",
                newName: "tbl_user");

            migrationBuilder.RenameTable(
                name: "tbl_StorySticker",
                schema: "distcomp",
                newName: "tbl_StorySticker");

            migrationBuilder.RenameTable(
                name: "tbl_story",
                schema: "distcomp",
                newName: "tbl_story");

            migrationBuilder.RenameTable(
                name: "tbl_stickers",
                schema: "distcomp",
                newName: "Stickers");

            migrationBuilder.RenameTable(
                name: "tbl_comments",
                schema: "distcomp",
                newName: "Comments");

            migrationBuilder.RenameIndex(
                name: "IX_tbl_comments_StoryID",
                table: "Comments",
                newName: "IX_Comments_StoryID");

            migrationBuilder.AddPrimaryKey(
                name: "PK_Stickers",
                table: "Stickers",
                column: "ID");

            migrationBuilder.AddPrimaryKey(
                name: "PK_Comments",
                table: "Comments",
                column: "ID");

            migrationBuilder.AddForeignKey(
                name: "FK_Comments_tbl_story_StoryID",
                table: "Comments",
                column: "StoryID",
                principalTable: "tbl_story",
                principalColumn: "ID",
                onDelete: ReferentialAction.Cascade);

            migrationBuilder.AddForeignKey(
                name: "FK_tbl_StorySticker_Stickers_StickerID",
                table: "tbl_StorySticker",
                column: "StickerID",
                principalTable: "Stickers",
                principalColumn: "ID",
                onDelete: ReferentialAction.Cascade);
        }
    }
}
