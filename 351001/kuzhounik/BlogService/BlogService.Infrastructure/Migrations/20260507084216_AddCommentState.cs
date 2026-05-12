using Microsoft.EntityFrameworkCore.Migrations;

#nullable disable

namespace BlogService.Infrastructure.Migrations
{
    /// <inheritdoc />
    public partial class AddCommentState : Migration
    {
        /// <inheritdoc />
        protected override void Up(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropForeignKey(
                name: "FK_tbl_comments_tbl_story_StoryID",
                schema: "distcomp",
                table: "tbl_comments");

            migrationBuilder.DropForeignKey(
                name: "FK_tbl_story_tbl_user_UserID",
                schema: "distcomp",
                table: "tbl_story");

            migrationBuilder.DropTable(
                name: "tbl_StorySticker",
                schema: "distcomp");

            migrationBuilder.DropPrimaryKey(
                name: "PK_tbl_stickers",
                schema: "distcomp",
                table: "tbl_stickers");

            migrationBuilder.DropPrimaryKey(
                name: "PK_tbl_comments",
                schema: "distcomp",
                table: "tbl_comments");

            migrationBuilder.RenameTable(
                name: "tbl_stickers",
                schema: "distcomp",
                newName: "tbl_sticker",
                newSchema: "distcomp");

            migrationBuilder.RenameTable(
                name: "tbl_comments",
                schema: "distcomp",
                newName: "tbl_comment",
                newSchema: "distcomp");

            migrationBuilder.RenameColumn(
                name: "Password",
                schema: "distcomp",
                table: "tbl_user",
                newName: "password");

            migrationBuilder.RenameColumn(
                name: "Login",
                schema: "distcomp",
                table: "tbl_user",
                newName: "login");

            migrationBuilder.RenameColumn(
                name: "LastName",
                schema: "distcomp",
                table: "tbl_user",
                newName: "lastname");

            migrationBuilder.RenameColumn(
                name: "FirstName",
                schema: "distcomp",
                table: "tbl_user",
                newName: "firstname");

            migrationBuilder.RenameColumn(
                name: "ID",
                schema: "distcomp",
                table: "tbl_user",
                newName: "id");

            migrationBuilder.RenameColumn(
                name: "Title",
                schema: "distcomp",
                table: "tbl_story",
                newName: "title");

            migrationBuilder.RenameColumn(
                name: "Modified",
                schema: "distcomp",
                table: "tbl_story",
                newName: "modified");

            migrationBuilder.RenameColumn(
                name: "Created",
                schema: "distcomp",
                table: "tbl_story",
                newName: "created");

            migrationBuilder.RenameColumn(
                name: "Content",
                schema: "distcomp",
                table: "tbl_story",
                newName: "content");

            migrationBuilder.RenameColumn(
                name: "ID",
                schema: "distcomp",
                table: "tbl_story",
                newName: "id");

            migrationBuilder.RenameColumn(
                name: "UserID",
                schema: "distcomp",
                table: "tbl_story",
                newName: "user_id");

            migrationBuilder.RenameIndex(
                name: "IX_tbl_story_UserID",
                schema: "distcomp",
                table: "tbl_story",
                newName: "IX_tbl_story_user_id");

            migrationBuilder.RenameColumn(
                name: "ID",
                schema: "distcomp",
                table: "tbl_sticker",
                newName: "id");

            migrationBuilder.RenameColumn(
                name: "Text",
                schema: "distcomp",
                table: "tbl_sticker",
                newName: "name");

            migrationBuilder.RenameColumn(
                name: "Content",
                schema: "distcomp",
                table: "tbl_comment",
                newName: "content");

            migrationBuilder.RenameColumn(
                name: "ID",
                schema: "distcomp",
                table: "tbl_comment",
                newName: "id");

            migrationBuilder.RenameColumn(
                name: "StoryID",
                schema: "distcomp",
                table: "tbl_comment",
                newName: "story_id");

            migrationBuilder.RenameIndex(
                name: "IX_tbl_comments_StoryID",
                schema: "distcomp",
                table: "tbl_comment",
                newName: "IX_tbl_comment_story_id");

            migrationBuilder.AddColumn<string>(
                name: "state",
                schema: "distcomp",
                table: "tbl_comment",
                type: "text",
                nullable: false,
                defaultValue: "");

            migrationBuilder.AddPrimaryKey(
                name: "PK_tbl_sticker",
                schema: "distcomp",
                table: "tbl_sticker",
                column: "id");

            migrationBuilder.AddPrimaryKey(
                name: "PK_tbl_comment",
                schema: "distcomp",
                table: "tbl_comment",
                column: "id");

            migrationBuilder.CreateTable(
                name: "tbl_story_sticker",
                schema: "distcomp",
                columns: table => new
                {
                    sticker_id = table.Column<long>(type: "bigint", nullable: false),
                    story_id = table.Column<long>(type: "bigint", nullable: false)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_tbl_story_sticker", x => new { x.sticker_id, x.story_id });
                    table.ForeignKey(
                        name: "FK_tbl_story_sticker_tbl_sticker_sticker_id",
                        column: x => x.sticker_id,
                        principalSchema: "distcomp",
                        principalTable: "tbl_sticker",
                        principalColumn: "id",
                        onDelete: ReferentialAction.Cascade);
                    table.ForeignKey(
                        name: "FK_tbl_story_sticker_tbl_story_story_id",
                        column: x => x.story_id,
                        principalSchema: "distcomp",
                        principalTable: "tbl_story",
                        principalColumn: "id",
                        onDelete: ReferentialAction.Cascade);
                });

            migrationBuilder.CreateIndex(
                name: "IX_tbl_user_login",
                schema: "distcomp",
                table: "tbl_user",
                column: "login",
                unique: true);

            migrationBuilder.CreateIndex(
                name: "IX_tbl_story_title",
                schema: "distcomp",
                table: "tbl_story",
                column: "title",
                unique: true);

            migrationBuilder.CreateIndex(
                name: "IX_tbl_story_sticker_story_id",
                schema: "distcomp",
                table: "tbl_story_sticker",
                column: "story_id");

            migrationBuilder.AddForeignKey(
                name: "FK_tbl_comment_tbl_story_story_id",
                schema: "distcomp",
                table: "tbl_comment",
                column: "story_id",
                principalSchema: "distcomp",
                principalTable: "tbl_story",
                principalColumn: "id",
                onDelete: ReferentialAction.Cascade);

            migrationBuilder.AddForeignKey(
                name: "FK_tbl_story_tbl_user_user_id",
                schema: "distcomp",
                table: "tbl_story",
                column: "user_id",
                principalSchema: "distcomp",
                principalTable: "tbl_user",
                principalColumn: "id",
                onDelete: ReferentialAction.Cascade);
        }

        /// <inheritdoc />
        protected override void Down(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropForeignKey(
                name: "FK_tbl_comment_tbl_story_story_id",
                schema: "distcomp",
                table: "tbl_comment");

            migrationBuilder.DropForeignKey(
                name: "FK_tbl_story_tbl_user_user_id",
                schema: "distcomp",
                table: "tbl_story");

            migrationBuilder.DropTable(
                name: "tbl_story_sticker",
                schema: "distcomp");

            migrationBuilder.DropIndex(
                name: "IX_tbl_user_login",
                schema: "distcomp",
                table: "tbl_user");

            migrationBuilder.DropIndex(
                name: "IX_tbl_story_title",
                schema: "distcomp",
                table: "tbl_story");

            migrationBuilder.DropPrimaryKey(
                name: "PK_tbl_sticker",
                schema: "distcomp",
                table: "tbl_sticker");

            migrationBuilder.DropPrimaryKey(
                name: "PK_tbl_comment",
                schema: "distcomp",
                table: "tbl_comment");

            migrationBuilder.DropColumn(
                name: "state",
                schema: "distcomp",
                table: "tbl_comment");

            migrationBuilder.RenameTable(
                name: "tbl_sticker",
                schema: "distcomp",
                newName: "tbl_stickers",
                newSchema: "distcomp");

            migrationBuilder.RenameTable(
                name: "tbl_comment",
                schema: "distcomp",
                newName: "tbl_comments",
                newSchema: "distcomp");

            migrationBuilder.RenameColumn(
                name: "password",
                schema: "distcomp",
                table: "tbl_user",
                newName: "Password");

            migrationBuilder.RenameColumn(
                name: "login",
                schema: "distcomp",
                table: "tbl_user",
                newName: "Login");

            migrationBuilder.RenameColumn(
                name: "lastname",
                schema: "distcomp",
                table: "tbl_user",
                newName: "LastName");

            migrationBuilder.RenameColumn(
                name: "firstname",
                schema: "distcomp",
                table: "tbl_user",
                newName: "FirstName");

            migrationBuilder.RenameColumn(
                name: "id",
                schema: "distcomp",
                table: "tbl_user",
                newName: "ID");

            migrationBuilder.RenameColumn(
                name: "title",
                schema: "distcomp",
                table: "tbl_story",
                newName: "Title");

            migrationBuilder.RenameColumn(
                name: "modified",
                schema: "distcomp",
                table: "tbl_story",
                newName: "Modified");

            migrationBuilder.RenameColumn(
                name: "created",
                schema: "distcomp",
                table: "tbl_story",
                newName: "Created");

            migrationBuilder.RenameColumn(
                name: "content",
                schema: "distcomp",
                table: "tbl_story",
                newName: "Content");

            migrationBuilder.RenameColumn(
                name: "id",
                schema: "distcomp",
                table: "tbl_story",
                newName: "ID");

            migrationBuilder.RenameColumn(
                name: "user_id",
                schema: "distcomp",
                table: "tbl_story",
                newName: "UserID");

            migrationBuilder.RenameIndex(
                name: "IX_tbl_story_user_id",
                schema: "distcomp",
                table: "tbl_story",
                newName: "IX_tbl_story_UserID");

            migrationBuilder.RenameColumn(
                name: "id",
                schema: "distcomp",
                table: "tbl_stickers",
                newName: "ID");

            migrationBuilder.RenameColumn(
                name: "name",
                schema: "distcomp",
                table: "tbl_stickers",
                newName: "Text");

            migrationBuilder.RenameColumn(
                name: "content",
                schema: "distcomp",
                table: "tbl_comments",
                newName: "Content");

            migrationBuilder.RenameColumn(
                name: "id",
                schema: "distcomp",
                table: "tbl_comments",
                newName: "ID");

            migrationBuilder.RenameColumn(
                name: "story_id",
                schema: "distcomp",
                table: "tbl_comments",
                newName: "StoryID");

            migrationBuilder.RenameIndex(
                name: "IX_tbl_comment_story_id",
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

            migrationBuilder.CreateTable(
                name: "tbl_StorySticker",
                schema: "distcomp",
                columns: table => new
                {
                    StickerID = table.Column<long>(type: "bigint", nullable: false),
                    StoryID = table.Column<long>(type: "bigint", nullable: false)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_tbl_StorySticker", x => new { x.StickerID, x.StoryID });
                    table.ForeignKey(
                        name: "FK_tbl_StorySticker_tbl_stickers_StickerID",
                        column: x => x.StickerID,
                        principalSchema: "distcomp",
                        principalTable: "tbl_stickers",
                        principalColumn: "ID",
                        onDelete: ReferentialAction.Cascade);
                    table.ForeignKey(
                        name: "FK_tbl_StorySticker_tbl_story_StoryID",
                        column: x => x.StoryID,
                        principalSchema: "distcomp",
                        principalTable: "tbl_story",
                        principalColumn: "ID",
                        onDelete: ReferentialAction.Cascade);
                });

            migrationBuilder.CreateIndex(
                name: "IX_tbl_StorySticker_StoryID",
                schema: "distcomp",
                table: "tbl_StorySticker",
                column: "StoryID");

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
                name: "FK_tbl_story_tbl_user_UserID",
                schema: "distcomp",
                table: "tbl_story",
                column: "UserID",
                principalSchema: "distcomp",
                principalTable: "tbl_user",
                principalColumn: "ID",
                onDelete: ReferentialAction.Cascade);
        }
    }
}
