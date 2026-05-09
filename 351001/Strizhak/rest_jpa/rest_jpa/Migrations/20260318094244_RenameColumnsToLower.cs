using Microsoft.EntityFrameworkCore.Migrations;

#nullable disable

namespace rest_jpa.Migrations
{
    /// <inheritdoc />
    public partial class RenameColumnsToLower : Migration
    {
        /// <inheritdoc />
        protected override void Up(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropForeignKey(
                name: "FK_tbl_reaction_tbl_topic_TopicId",
                table: "tbl_reaction");

            migrationBuilder.DropForeignKey(
                name: "FK_tbl_topic_tbl_user_UserId",
                table: "tbl_topic");

            migrationBuilder.DropForeignKey(
                name: "FK_tbl_topic_tag_tbl_tag_TagId",
                table: "tbl_topic_tag");

            migrationBuilder.DropPrimaryKey(
                name: "PK_tbl_tag",
                table: "tbl_tag");

            migrationBuilder.RenameTable(
                name: "tbl_tag",
                newName: "Tags");

            migrationBuilder.RenameColumn(
                name: "Password",
                table: "tbl_user",
                newName: "password");

            migrationBuilder.RenameColumn(
                name: "Login",
                table: "tbl_user",
                newName: "login");

            migrationBuilder.RenameColumn(
                name: "Lastname",
                table: "tbl_user",
                newName: "lastname");

            migrationBuilder.RenameColumn(
                name: "Firstname",
                table: "tbl_user",
                newName: "firstname");

            migrationBuilder.RenameColumn(
                name: "Id",
                table: "tbl_user",
                newName: "id");

            migrationBuilder.RenameColumn(
                name: "UserId",
                table: "tbl_topic",
                newName: "userid");

            migrationBuilder.RenameColumn(
                name: "Title",
                table: "tbl_topic",
                newName: "title");

            migrationBuilder.RenameColumn(
                name: "Modified",
                table: "tbl_topic",
                newName: "modified");

            migrationBuilder.RenameColumn(
                name: "Created",
                table: "tbl_topic",
                newName: "created");

            migrationBuilder.RenameColumn(
                name: "Content",
                table: "tbl_topic",
                newName: "content");

            migrationBuilder.RenameColumn(
                name: "Id",
                table: "tbl_topic",
                newName: "id");

            migrationBuilder.RenameIndex(
                name: "IX_tbl_topic_UserId",
                table: "tbl_topic",
                newName: "IX_tbl_topic_userid");

            migrationBuilder.RenameColumn(
                name: "TopicId",
                table: "tbl_reaction",
                newName: "topicid");

            migrationBuilder.RenameColumn(
                name: "Content",
                table: "tbl_reaction",
                newName: "content");

            migrationBuilder.RenameColumn(
                name: "Id",
                table: "tbl_reaction",
                newName: "id");

            migrationBuilder.RenameIndex(
                name: "IX_tbl_reaction_TopicId",
                table: "tbl_reaction",
                newName: "IX_tbl_reaction_topicid");

            migrationBuilder.AddPrimaryKey(
                name: "PK_Tags",
                table: "Tags",
                column: "Id");

            migrationBuilder.AddForeignKey(
                name: "FK_tbl_reaction_tbl_topic_topicid",
                table: "tbl_reaction",
                column: "topicid",
                principalTable: "tbl_topic",
                principalColumn: "id",
                onDelete: ReferentialAction.Cascade);

            migrationBuilder.AddForeignKey(
                name: "FK_tbl_topic_tbl_user_userid",
                table: "tbl_topic",
                column: "userid",
                principalTable: "tbl_user",
                principalColumn: "id",
                onDelete: ReferentialAction.Cascade);

            migrationBuilder.AddForeignKey(
                name: "FK_tbl_topic_tag_Tags_TagId",
                table: "tbl_topic_tag",
                column: "TagId",
                principalTable: "Tags",
                principalColumn: "Id",
                onDelete: ReferentialAction.Restrict);
        }

        /// <inheritdoc />
        protected override void Down(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropForeignKey(
                name: "FK_tbl_reaction_tbl_topic_topicid",
                table: "tbl_reaction");

            migrationBuilder.DropForeignKey(
                name: "FK_tbl_topic_tbl_user_userid",
                table: "tbl_topic");

            migrationBuilder.DropForeignKey(
                name: "FK_tbl_topic_tag_Tags_TagId",
                table: "tbl_topic_tag");

            migrationBuilder.DropPrimaryKey(
                name: "PK_Tags",
                table: "Tags");

            migrationBuilder.RenameTable(
                name: "Tags",
                newName: "tbl_tag");

            migrationBuilder.RenameColumn(
                name: "password",
                table: "tbl_user",
                newName: "Password");

            migrationBuilder.RenameColumn(
                name: "login",
                table: "tbl_user",
                newName: "Login");

            migrationBuilder.RenameColumn(
                name: "lastname",
                table: "tbl_user",
                newName: "Lastname");

            migrationBuilder.RenameColumn(
                name: "firstname",
                table: "tbl_user",
                newName: "Firstname");

            migrationBuilder.RenameColumn(
                name: "id",
                table: "tbl_user",
                newName: "Id");

            migrationBuilder.RenameColumn(
                name: "userid",
                table: "tbl_topic",
                newName: "UserId");

            migrationBuilder.RenameColumn(
                name: "title",
                table: "tbl_topic",
                newName: "Title");

            migrationBuilder.RenameColumn(
                name: "modified",
                table: "tbl_topic",
                newName: "Modified");

            migrationBuilder.RenameColumn(
                name: "created",
                table: "tbl_topic",
                newName: "Created");

            migrationBuilder.RenameColumn(
                name: "content",
                table: "tbl_topic",
                newName: "Content");

            migrationBuilder.RenameColumn(
                name: "id",
                table: "tbl_topic",
                newName: "Id");

            migrationBuilder.RenameIndex(
                name: "IX_tbl_topic_userid",
                table: "tbl_topic",
                newName: "IX_tbl_topic_UserId");

            migrationBuilder.RenameColumn(
                name: "topicid",
                table: "tbl_reaction",
                newName: "TopicId");

            migrationBuilder.RenameColumn(
                name: "content",
                table: "tbl_reaction",
                newName: "Content");

            migrationBuilder.RenameColumn(
                name: "id",
                table: "tbl_reaction",
                newName: "Id");

            migrationBuilder.RenameIndex(
                name: "IX_tbl_reaction_topicid",
                table: "tbl_reaction",
                newName: "IX_tbl_reaction_TopicId");

            migrationBuilder.AddPrimaryKey(
                name: "PK_tbl_tag",
                table: "tbl_tag",
                column: "Id");

            migrationBuilder.AddForeignKey(
                name: "FK_tbl_reaction_tbl_topic_TopicId",
                table: "tbl_reaction",
                column: "TopicId",
                principalTable: "tbl_topic",
                principalColumn: "Id",
                onDelete: ReferentialAction.Cascade);

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
        }
    }
}
