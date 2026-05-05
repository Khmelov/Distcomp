using Microsoft.EntityFrameworkCore.Migrations;

#nullable disable

namespace rest_jpa.Migrations
{
    /// <inheritdoc />
    public partial class AddTablePrefix : Migration
    {
        /// <inheritdoc />
        protected override void Up(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropForeignKey(
                name: "FK_Reactions_Topics_TopicId",
                table: "Reactions");

            migrationBuilder.DropForeignKey(
                name: "FK_Reactions_Users_UserId",
                table: "Reactions");

            migrationBuilder.DropForeignKey(
                name: "FK_Topics_Users_UserId",
                table: "Topics");

            migrationBuilder.DropForeignKey(
                name: "FK_TopicTag_Tags_TagId",
                table: "TopicTag");

            migrationBuilder.DropForeignKey(
                name: "FK_TopicTag_Topics_TopicId",
                table: "TopicTag");

            migrationBuilder.DropPrimaryKey(
                name: "PK_Users",
                table: "Users");

            migrationBuilder.DropPrimaryKey(
                name: "PK_TopicTag",
                table: "TopicTag");

            migrationBuilder.DropPrimaryKey(
                name: "PK_Topics",
                table: "Topics");

            migrationBuilder.DropPrimaryKey(
                name: "PK_Tags",
                table: "Tags");

            migrationBuilder.DropPrimaryKey(
                name: "PK_Reactions",
                table: "Reactions");

            migrationBuilder.RenameTable(
                name: "Users",
                newName: "tbl_user");

            migrationBuilder.RenameTable(
                name: "TopicTag",
                newName: "tbl_topic_tag");

            migrationBuilder.RenameTable(
                name: "Topics",
                newName: "tbl_topic");

            migrationBuilder.RenameTable(
                name: "Tags",
                newName: "tbl_tag");

            migrationBuilder.RenameTable(
                name: "Reactions",
                newName: "tbl_reaction");

            migrationBuilder.RenameIndex(
                name: "IX_TopicTag_TagId",
                table: "tbl_topic_tag",
                newName: "IX_tbl_topic_tag_TagId");

            migrationBuilder.RenameIndex(
                name: "IX_Topics_UserId",
                table: "tbl_topic",
                newName: "IX_tbl_topic_UserId");

            migrationBuilder.RenameIndex(
                name: "IX_Reactions_UserId",
                table: "tbl_reaction",
                newName: "IX_tbl_reaction_UserId");

            migrationBuilder.RenameIndex(
                name: "IX_Reactions_TopicId",
                table: "tbl_reaction",
                newName: "IX_tbl_reaction_TopicId");

            migrationBuilder.AddPrimaryKey(
                name: "PK_tbl_user",
                table: "tbl_user",
                column: "Id");

            migrationBuilder.AddPrimaryKey(
                name: "PK_tbl_topic_tag",
                table: "tbl_topic_tag",
                columns: new[] { "TopicId", "TagId" });

            migrationBuilder.AddPrimaryKey(
                name: "PK_tbl_topic",
                table: "tbl_topic",
                column: "Id");

            migrationBuilder.AddPrimaryKey(
                name: "PK_tbl_tag",
                table: "tbl_tag",
                column: "Id");

            migrationBuilder.AddPrimaryKey(
                name: "PK_tbl_reaction",
                table: "tbl_reaction",
                column: "Id");

            migrationBuilder.AddForeignKey(
                name: "FK_tbl_reaction_tbl_topic_TopicId",
                table: "tbl_reaction",
                column: "TopicId",
                principalTable: "tbl_topic",
                principalColumn: "Id",
                onDelete: ReferentialAction.Cascade);

            migrationBuilder.AddForeignKey(
                name: "FK_tbl_reaction_tbl_user_UserId",
                table: "tbl_reaction",
                column: "UserId",
                principalTable: "tbl_user",
                principalColumn: "Id");

            migrationBuilder.AddForeignKey(
                name: "FK_tbl_topic_tbl_user_UserId",
                table: "tbl_topic",
                column: "UserId",
                principalTable: "tbl_user",
                principalColumn: "Id",
                onDelete: ReferentialAction.Cascade);

            migrationBuilder.AddForeignKey(
                name: "FK_tbl_topic_tag_tbl_tag_TagId",
                table: "tbl_topic_tag",
                column: "TagId",
                principalTable: "tbl_tag",
                principalColumn: "Id",
                onDelete: ReferentialAction.Restrict);

            migrationBuilder.AddForeignKey(
                name: "FK_tbl_topic_tag_tbl_topic_TopicId",
                table: "tbl_topic_tag",
                column: "TopicId",
                principalTable: "tbl_topic",
                principalColumn: "Id",
                onDelete: ReferentialAction.Restrict);
        }

        /// <inheritdoc />
        protected override void Down(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropForeignKey(
                name: "FK_tbl_reaction_tbl_topic_TopicId",
                table: "tbl_reaction");

            migrationBuilder.DropForeignKey(
                name: "FK_tbl_reaction_tbl_user_UserId",
                table: "tbl_reaction");

            migrationBuilder.DropForeignKey(
                name: "FK_tbl_topic_tbl_user_UserId",
                table: "tbl_topic");

            migrationBuilder.DropForeignKey(
                name: "FK_tbl_topic_tag_tbl_tag_TagId",
                table: "tbl_topic_tag");

            migrationBuilder.DropForeignKey(
                name: "FK_tbl_topic_tag_tbl_topic_TopicId",
                table: "tbl_topic_tag");

            migrationBuilder.DropPrimaryKey(
                name: "PK_tbl_user",
                table: "tbl_user");

            migrationBuilder.DropPrimaryKey(
                name: "PK_tbl_topic_tag",
                table: "tbl_topic_tag");

            migrationBuilder.DropPrimaryKey(
                name: "PK_tbl_topic",
                table: "tbl_topic");

            migrationBuilder.DropPrimaryKey(
                name: "PK_tbl_tag",
                table: "tbl_tag");

            migrationBuilder.DropPrimaryKey(
                name: "PK_tbl_reaction",
                table: "tbl_reaction");

            migrationBuilder.RenameTable(
                name: "tbl_user",
                newName: "Users");

            migrationBuilder.RenameTable(
                name: "tbl_topic_tag",
                newName: "TopicTag");

            migrationBuilder.RenameTable(
                name: "tbl_topic",
                newName: "Topics");

            migrationBuilder.RenameTable(
                name: "tbl_tag",
                newName: "Tags");

            migrationBuilder.RenameTable(
                name: "tbl_reaction",
                newName: "Reactions");

            migrationBuilder.RenameIndex(
                name: "IX_tbl_topic_tag_TagId",
                table: "TopicTag",
                newName: "IX_TopicTag_TagId");

            migrationBuilder.RenameIndex(
                name: "IX_tbl_topic_UserId",
                table: "Topics",
                newName: "IX_Topics_UserId");

            migrationBuilder.RenameIndex(
                name: "IX_tbl_reaction_UserId",
                table: "Reactions",
                newName: "IX_Reactions_UserId");

            migrationBuilder.RenameIndex(
                name: "IX_tbl_reaction_TopicId",
                table: "Reactions",
                newName: "IX_Reactions_TopicId");

            migrationBuilder.AddPrimaryKey(
                name: "PK_Users",
                table: "Users",
                column: "Id");

            migrationBuilder.AddPrimaryKey(
                name: "PK_TopicTag",
                table: "TopicTag",
                columns: new[] { "TopicId", "TagId" });

            migrationBuilder.AddPrimaryKey(
                name: "PK_Topics",
                table: "Topics",
                column: "Id");

            migrationBuilder.AddPrimaryKey(
                name: "PK_Tags",
                table: "Tags",
                column: "Id");

            migrationBuilder.AddPrimaryKey(
                name: "PK_Reactions",
                table: "Reactions",
                column: "Id");

            migrationBuilder.AddForeignKey(
                name: "FK_Reactions_Topics_TopicId",
                table: "Reactions",
                column: "TopicId",
                principalTable: "Topics",
                principalColumn: "Id",
                onDelete: ReferentialAction.Cascade);

            migrationBuilder.AddForeignKey(
                name: "FK_Reactions_Users_UserId",
                table: "Reactions",
                column: "UserId",
                principalTable: "Users",
                principalColumn: "Id");

            migrationBuilder.AddForeignKey(
                name: "FK_Topics_Users_UserId",
                table: "Topics",
                column: "UserId",
                principalTable: "Users",
                principalColumn: "Id",
                onDelete: ReferentialAction.Cascade);

            migrationBuilder.AddForeignKey(
                name: "FK_TopicTag_Tags_TagId",
                table: "TopicTag",
                column: "TagId",
                principalTable: "Tags",
                principalColumn: "Id",
                onDelete: ReferentialAction.Restrict);

            migrationBuilder.AddForeignKey(
                name: "FK_TopicTag_Topics_TopicId",
                table: "TopicTag",
                column: "TopicId",
                principalTable: "Topics",
                principalColumn: "Id",
                onDelete: ReferentialAction.Restrict);
        }
    }
}
