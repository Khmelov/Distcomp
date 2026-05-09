using Microsoft.EntityFrameworkCore.Migrations;

#nullable disable

namespace Infrastructure.Migrations
{
    /// <inheritdoc />
    public partial class RemoveSchemae : Migration
    {
        /// <inheritdoc />
        protected override void Up(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.EnsureSchema(
                name: "public");

            migrationBuilder.RenameTable(
                name: "tbl_post",
                schema: "distcomp",
                newName: "tbl_post",
                newSchema: "public");

            migrationBuilder.RenameTable(
                name: "tbl_news",
                schema: "distcomp",
                newName: "tbl_news",
                newSchema: "public");

            migrationBuilder.RenameTable(
                name: "tbl_marker",
                schema: "distcomp",
                newName: "tbl_marker",
                newSchema: "public");

            migrationBuilder.RenameTable(
                name: "tbl_editor",
                schema: "distcomp",
                newName: "tbl_editor",
                newSchema: "public");

            migrationBuilder.RenameTable(
                name: "MarkerNews",
                schema: "distcomp",
                newName: "MarkerNews",
                newSchema: "public");
        }

        /// <inheritdoc />
        protected override void Down(MigrationBuilder migrationBuilder)
        {
            
        }
    }
}
