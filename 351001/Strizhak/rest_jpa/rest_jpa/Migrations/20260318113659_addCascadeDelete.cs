using Microsoft.EntityFrameworkCore.Migrations;

#nullable disable

namespace rest_jpa.Migrations
{
    /// <inheritdoc />
    public partial class addCascadeDelete : Migration
    {
        /// <inheritdoc />
        protected override void Up(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropForeignKey(
                name: "FK_tbl_topic_tag_tbl_tag_TagId",
                table: "tbl_topic_tag");

            migrationBuilder.DropForeignKey(
                name: "FK_tbl_topic_tag_tbl_topic_TopicId",
                table: "tbl_topic_tag");

            migrationBuilder.AddForeignKey(
                name: "FK_tbl_topic_tag_tbl_tag_TagId",
                table: "tbl_topic_tag",
                column: "TagId",
                principalTable: "tbl_tag",
                principalColumn: "id",
                onDelete: ReferentialAction.Cascade);

            migrationBuilder.AddForeignKey(
                name: "FK_tbl_topic_tag_tbl_topic_TopicId",
                table: "tbl_topic_tag",
                column: "TopicId",
                principalTable: "tbl_topic",
                principalColumn: "id",
                onDelete: ReferentialAction.Cascade);
        }

        /// <inheritdoc />
        protected override void Down(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropForeignKey(
                name: "FK_tbl_topic_tag_tbl_tag_TagId",
                table: "tbl_topic_tag");

            migrationBuilder.DropForeignKey(
                name: "FK_tbl_topic_tag_tbl_topic_TopicId",
                table: "tbl_topic_tag");

            migrationBuilder.AddForeignKey(
                name: "FK_tbl_topic_tag_tbl_tag_TagId",
                table: "tbl_topic_tag",
                column: "TagId",
                principalTable: "tbl_tag",
                principalColumn: "id",
                onDelete: ReferentialAction.Restrict);

            migrationBuilder.AddForeignKey(
                name: "FK_tbl_topic_tag_tbl_topic_TopicId",
                table: "tbl_topic_tag",
                column: "TopicId",
                principalTable: "tbl_topic",
                principalColumn: "id",
                onDelete: ReferentialAction.Restrict);
        }
    }
}
