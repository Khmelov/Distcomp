using Microsoft.EntityFrameworkCore.Migrations;

#nullable disable

namespace Infrastructure.Migrations
{
    /// <inheritdoc />
    public partial class lowchar : Migration
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

            migrationBuilder.RenameColumn(
                name: "UserId",
                table: "tbl_topic",
                newName: "user_id");

            migrationBuilder.RenameIndex(
                name: "IX_tbl_topic_UserId",
                table: "tbl_topic",
                newName: "IX_tbl_topic_user_id");

            migrationBuilder.RenameColumn(
                name: "TopicId",
                table: "tbl_reaction",
                newName: "topic_id");

            migrationBuilder.RenameIndex(
                name: "IX_tbl_reaction_TopicId",
                table: "tbl_reaction",
                newName: "IX_tbl_reaction_topic_id");

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

            migrationBuilder.RenameColumn(
                name: "user_id",
                table: "tbl_topic",
                newName: "UserId");

            migrationBuilder.RenameIndex(
                name: "IX_tbl_topic_user_id",
                table: "tbl_topic",
                newName: "IX_tbl_topic_UserId");

            migrationBuilder.RenameColumn(
                name: "topic_id",
                table: "tbl_reaction",
                newName: "TopicId");

            migrationBuilder.RenameIndex(
                name: "IX_tbl_reaction_topic_id",
                table: "tbl_reaction",
                newName: "IX_tbl_reaction_TopicId");

            migrationBuilder.AddForeignKey(
                name: "FK_tbl_reaction_tbl_topic_TopicId",
                table: "tbl_reaction",
                column: "TopicId",
                principalTable: "tbl_topic",
                principalColumn: "id",
                onDelete: ReferentialAction.Cascade);

            migrationBuilder.AddForeignKey(
                name: "FK_tbl_topic_tbl_user_UserId",
                table: "tbl_topic",
                column: "UserId",
                principalTable: "tbl_user",
                principalColumn: "id",
                onDelete: ReferentialAction.Cascade);
        }
    }
}
