using Microsoft.EntityFrameworkCore.Migrations;

#nullable disable

namespace rest_jpa.Migrations
{
    /// <inheritdoc />
    public partial class addTag : Migration
    {
        /// <inheritdoc />
        protected override void Up(MigrationBuilder migrationBuilder)
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
                name: "userid",
                table: "tbl_topic",
                newName: "user_id");

            migrationBuilder.RenameIndex(
                name: "IX_tbl_topic_userid",
                table: "tbl_topic",
                newName: "IX_tbl_topic_user_id");

            migrationBuilder.RenameColumn(
                name: "topicid",
                table: "tbl_reaction",
                newName: "topic_id");

            migrationBuilder.RenameIndex(
                name: "IX_tbl_reaction_topicid",
                table: "tbl_reaction",
                newName: "IX_tbl_reaction_topic_id");

            migrationBuilder.RenameColumn(
                name: "Name",
                table: "tbl_tag",
                newName: "name");

            migrationBuilder.RenameColumn(
                name: "Id",
                table: "tbl_tag",
                newName: "id");

            migrationBuilder.AddPrimaryKey(
                name: "PK_tbl_tag",
                table: "tbl_tag",
                column: "id");

            migrationBuilder.AddForeignKey(
                name: "FK_tbl_reaction_tbl_topic_topic_id",
                table: "tbl_reaction",
                column: "topic_id",
                principalTable: "tbl_topic",
                principalColumn: "id",
                onDelete: ReferentialAction.Cascade);

            migrationBuilder.AddForeignKey(
                name: "FK_tbl_topic_tbl_user_user_id",
                table: "tbl_topic",
                column: "user_id",
                principalTable: "tbl_user",
                principalColumn: "id",
                onDelete: ReferentialAction.Cascade);

            migrationBuilder.AddForeignKey(
                name: "FK_tbl_topic_tag_tbl_tag_TagId",
                table: "tbl_topic_tag",
                column: "TagId",
                principalTable: "tbl_tag",
                principalColumn: "id",
                onDelete: ReferentialAction.Restrict);
        }

        /// <inheritdoc />
        protected override void Down(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropForeignKey(
                name: "FK_tbl_reaction_tbl_topic_topic_id",
                table: "tbl_reaction");

            migrationBuilder.DropForeignKey(
                name: "FK_tbl_topic_tbl_user_user_id",
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
                name: "user_id",
                table: "tbl_topic",
                newName: "userid");

            migrationBuilder.RenameIndex(
                name: "IX_tbl_topic_user_id",
                table: "tbl_topic",
                newName: "IX_tbl_topic_userid");

            migrationBuilder.RenameColumn(
                name: "topic_id",
                table: "tbl_reaction",
                newName: "topicid");

            migrationBuilder.RenameIndex(
                name: "IX_tbl_reaction_topic_id",
                table: "tbl_reaction",
                newName: "IX_tbl_reaction_topicid");

            migrationBuilder.RenameColumn(
                name: "name",
                table: "Tags",
                newName: "Name");

            migrationBuilder.RenameColumn(
                name: "id",
                table: "Tags",
                newName: "Id");

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
    }
}
