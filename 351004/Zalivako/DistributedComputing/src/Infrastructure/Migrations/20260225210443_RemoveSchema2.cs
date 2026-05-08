using Microsoft.EntityFrameworkCore.Migrations;

#nullable disable

namespace Infrastructure.Migrations
{
    /// <inheritdoc />
    public partial class RemoveSchema2 : Migration
    {
        /// <inheritdoc />
        protected override void Up(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.RenameTable(
                name: "tbl_post",
                schema: "distcomp",
                newName: "tbl_post");

            migrationBuilder.RenameTable(
                name: "tbl_news",
                schema: "distcomp",
                newName: "tbl_news");

            migrationBuilder.RenameTable(
                name: "tbl_marker",
                schema: "distcomp",
                newName: "tbl_marker");

            migrationBuilder.RenameTable(
                name: "tbl_editor",
                schema: "distcomp",
                newName: "tbl_editor");

            migrationBuilder.RenameTable(
                name: "MarkerNews",
                schema: "distcomp",
                newName: "MarkerNews");
        }

        /// <inheritdoc />
        protected override void Down(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.EnsureSchema(
                name: "distcomp");

            migrationBuilder.RenameTable(
                name: "tbl_post",
                newName: "tbl_post",
                newSchema: "distcomp");

            migrationBuilder.RenameTable(
                name: "tbl_news",
                newName: "tbl_news",
                newSchema: "distcomp");

            migrationBuilder.RenameTable(
                name: "tbl_marker",
                newName: "tbl_marker",
                newSchema: "distcomp");

            migrationBuilder.RenameTable(
                name: "tbl_editor",
                newName: "tbl_editor",
                newSchema: "distcomp");

            migrationBuilder.RenameTable(
                name: "MarkerNews",
                newName: "MarkerNews",
                newSchema: "distcomp");
        }
    }
}
