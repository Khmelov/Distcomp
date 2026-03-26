using Microsoft.EntityFrameworkCore.Migrations;

#nullable disable

namespace Infrastructure.Migrations
{
    /// <inheritdoc />
    public partial class RenameChangeManyToManyRalationship : Migration
    {
        /// <inheritdoc />
        protected override void Up(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropForeignKey(
                name: "FK_MarkerNews_tbl_marker_MarkerId",
                schema: "public",
                table: "MarkerNews");

            migrationBuilder.DropForeignKey(
                name: "FK_MarkerNews_tbl_news_NewsId",
                schema: "public",
                table: "MarkerNews");

            migrationBuilder.AddForeignKey(
                name: "FK_MarkerNews_Marker",
                schema: "public",
                table: "MarkerNews",
                column: "MarkerId",
                principalSchema: "public",
                principalTable: "tbl_marker",
                principalColumn: "id",
                onDelete: ReferentialAction.Cascade);

            migrationBuilder.AddForeignKey(
                name: "FK_MarkerNews_News",
                schema: "public",
                table: "MarkerNews",
                column: "NewsId",
                principalSchema: "public",
                principalTable: "tbl_news",
                principalColumn: "id",
                onDelete: ReferentialAction.Cascade);
        }

        /// <inheritdoc />
        protected override void Down(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropForeignKey(
                name: "FK_MarkerNews_Marker",
                schema: "public",
                table: "MarkerNews");

            migrationBuilder.DropForeignKey(
                name: "FK_MarkerNews_News",
                schema: "public",
                table: "MarkerNews");

            migrationBuilder.AddForeignKey(
                name: "FK_MarkerNews_tbl_marker_MarkerId",
                schema: "public",
                table: "MarkerNews",
                column: "MarkerId",
                principalSchema: "public",
                principalTable: "tbl_marker",
                principalColumn: "id",
                onDelete: ReferentialAction.Cascade);

            migrationBuilder.AddForeignKey(
                name: "FK_MarkerNews_tbl_news_NewsId",
                schema: "public",
                table: "MarkerNews",
                column: "NewsId",
                principalSchema: "public",
                principalTable: "tbl_news",
                principalColumn: "id",
                onDelete: ReferentialAction.Cascade);
        }
    }
}
