using Microsoft.EntityFrameworkCore.Migrations;

#nullable disable

namespace Infrastructure.Migrations
{
    /// <inheritdoc />
    public partial class RenameIdColumns : Migration
    {
        /// <inheritdoc />
        protected override void Up(MigrationBuilder migrationBuilder)
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

            migrationBuilder.RenameColumn(
                name: "Id",
                schema: "distcomp",
                table: "tbl_post",
                newName: "id");

            migrationBuilder.RenameColumn(
                name: "Id",
                schema: "distcomp",
                table: "tbl_news",
                newName: "id");

            migrationBuilder.RenameColumn(
                name: "Id",
                schema: "distcomp",
                table: "tbl_marker",
                newName: "id");

            migrationBuilder.RenameColumn(
                name: "Id",
                schema: "distcomp",
                table: "tbl_editor",
                newName: "id");
        }

        /// <inheritdoc />
        protected override void Down(MigrationBuilder migrationBuilder)
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

            migrationBuilder.RenameColumn(
                name: "id",
                table: "tbl_post",
                newName: "Id");

            migrationBuilder.RenameColumn(
                name: "id",
                table: "tbl_news",
                newName: "Id");

            migrationBuilder.RenameColumn(
                name: "id",
                table: "tbl_marker",
                newName: "Id");

            migrationBuilder.RenameColumn(
                name: "id",
                table: "tbl_editor",
                newName: "Id");
        }
    }
}
