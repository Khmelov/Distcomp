using Microsoft.EntityFrameworkCore.Migrations;

#nullable disable

namespace Publisher.Migrations
{
    /// <inheritdoc />
    public partial class CheckMigration : Migration
    {
        /// <inheritdoc />
        protected override void Up(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropForeignKey(
                name: "FK_tbl_story_tbl_user_user_id",
                table: "tbl_story");

            migrationBuilder.DropTable(
                name: "StoryTag");

            migrationBuilder.DropPrimaryKey(
                name: "PK_tbl_user",
                table: "tbl_user");

            migrationBuilder.DropPrimaryKey(
                name: "PK_tbl_tag",
                table: "tbl_tag");

            migrationBuilder.DropPrimaryKey(
                name: "PK_tbl_story",
                table: "tbl_story");

            migrationBuilder.RenameTable(
                name: "tbl_user",
                newName: "tbl_author");

            migrationBuilder.RenameTable(
                name: "tbl_tag",
                newName: "tbl_sticker");

            migrationBuilder.RenameTable(
                name: "tbl_story",
                newName: "tbl_topic");

            migrationBuilder.RenameIndex(
                name: "IX_tbl_user_Login",
                table: "tbl_author",
                newName: "IX_tbl_author_Login");

            migrationBuilder.RenameIndex(
                name: "IX_tbl_tag_Name",
                table: "tbl_sticker",
                newName: "IX_tbl_sticker_Name");

            migrationBuilder.RenameColumn(
                name: "user_id",
                table: "tbl_topic",
                newName: "author_id");

            migrationBuilder.RenameIndex(
                name: "IX_tbl_story_user_id",
                table: "tbl_topic",
                newName: "IX_tbl_topic_author_id");

            migrationBuilder.RenameIndex(
                name: "IX_tbl_story_Title",
                table: "tbl_topic",
                newName: "IX_tbl_topic_Title");

            migrationBuilder.AddPrimaryKey(
                name: "PK_tbl_author",
                table: "tbl_author",
                column: "id");

            migrationBuilder.AddPrimaryKey(
                name: "PK_tbl_sticker",
                table: "tbl_sticker",
                column: "id");

            migrationBuilder.AddPrimaryKey(
                name: "PK_tbl_topic",
                table: "tbl_topic",
                column: "id");

            migrationBuilder.CreateTable(
                name: "StickerTopic",
                columns: table => new
                {
                    StickersId = table.Column<long>(type: "bigint", nullable: false),
                    StoriesId = table.Column<long>(type: "bigint", nullable: false)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_StickerTopic", x => new { x.StickersId, x.StoriesId });
                    table.ForeignKey(
                        name: "FK_StickerTopic_tbl_sticker_StickersId",
                        column: x => x.StickersId,
                        principalTable: "tbl_sticker",
                        principalColumn: "id",
                        onDelete: ReferentialAction.Cascade);
                    table.ForeignKey(
                        name: "FK_StickerTopic_tbl_topic_StoriesId",
                        column: x => x.StoriesId,
                        principalTable: "tbl_topic",
                        principalColumn: "id",
                        onDelete: ReferentialAction.Cascade);
                });

            migrationBuilder.CreateIndex(
                name: "IX_StickerTopic_StoriesId",
                table: "StickerTopic",
                column: "StoriesId");

            migrationBuilder.AddForeignKey(
                name: "FK_tbl_topic_tbl_author_author_id",
                table: "tbl_topic",
                column: "author_id",
                principalTable: "tbl_author",
                principalColumn: "id",
                onDelete: ReferentialAction.Cascade);
        }

        /// <inheritdoc />
        protected override void Down(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropForeignKey(
                name: "FK_tbl_topic_tbl_author_author_id",
                table: "tbl_topic");

            migrationBuilder.DropTable(
                name: "StickerTopic");

            migrationBuilder.DropPrimaryKey(
                name: "PK_tbl_topic",
                table: "tbl_topic");

            migrationBuilder.DropPrimaryKey(
                name: "PK_tbl_sticker",
                table: "tbl_sticker");

            migrationBuilder.DropPrimaryKey(
                name: "PK_tbl_author",
                table: "tbl_author");

            migrationBuilder.RenameTable(
                name: "tbl_topic",
                newName: "tbl_story");

            migrationBuilder.RenameTable(
                name: "tbl_sticker",
                newName: "tbl_tag");

            migrationBuilder.RenameTable(
                name: "tbl_author",
                newName: "tbl_user");

            migrationBuilder.RenameColumn(
                name: "author_id",
                table: "tbl_story",
                newName: "user_id");

            migrationBuilder.RenameIndex(
                name: "IX_tbl_topic_Title",
                table: "tbl_story",
                newName: "IX_tbl_story_Title");

            migrationBuilder.RenameIndex(
                name: "IX_tbl_topic_author_id",
                table: "tbl_story",
                newName: "IX_tbl_story_user_id");

            migrationBuilder.RenameIndex(
                name: "IX_tbl_sticker_Name",
                table: "tbl_tag",
                newName: "IX_tbl_tag_Name");

            migrationBuilder.RenameIndex(
                name: "IX_tbl_author_Login",
                table: "tbl_user",
                newName: "IX_tbl_user_Login");

            migrationBuilder.AddPrimaryKey(
                name: "PK_tbl_story",
                table: "tbl_story",
                column: "id");

            migrationBuilder.AddPrimaryKey(
                name: "PK_tbl_tag",
                table: "tbl_tag",
                column: "id");

            migrationBuilder.AddPrimaryKey(
                name: "PK_tbl_user",
                table: "tbl_user",
                column: "id");

            migrationBuilder.CreateTable(
                name: "StoryTag",
                columns: table => new
                {
                    StoriesId = table.Column<long>(type: "bigint", nullable: false),
                    TagsId = table.Column<long>(type: "bigint", nullable: false)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_StoryTag", x => new { x.StoriesId, x.TagsId });
                    table.ForeignKey(
                        name: "FK_StoryTag_tbl_story_StoriesId",
                        column: x => x.StoriesId,
                        principalTable: "tbl_story",
                        principalColumn: "id",
                        onDelete: ReferentialAction.Cascade);
                    table.ForeignKey(
                        name: "FK_StoryTag_tbl_tag_TagsId",
                        column: x => x.TagsId,
                        principalTable: "tbl_tag",
                        principalColumn: "id",
                        onDelete: ReferentialAction.Cascade);
                });

            migrationBuilder.CreateIndex(
                name: "IX_StoryTag_TagsId",
                table: "StoryTag",
                column: "TagsId");

            migrationBuilder.AddForeignKey(
                name: "FK_tbl_story_tbl_user_user_id",
                table: "tbl_story",
                column: "user_id",
                principalTable: "tbl_user",
                principalColumn: "id",
                onDelete: ReferentialAction.Cascade);
        }
    }
}
