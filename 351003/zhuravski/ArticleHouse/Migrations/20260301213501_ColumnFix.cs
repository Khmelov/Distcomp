using Microsoft.EntityFrameworkCore.Migrations;

#nullable disable

namespace ArticleHouse.Migrations
{
    /// <inheritdoc />
    public partial class ColumnFix : Migration
    {
        /// <inheritdoc />
        protected override void Up(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.RenameColumn(
                name: "last_name",
                table: "tbl_creator",
                newName: "lastname");

            migrationBuilder.RenameColumn(
                name: "first_name",
                table: "tbl_creator",
                newName: "firstname");
        }

        /// <inheritdoc />
        protected override void Down(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.RenameColumn(
                name: "lastname",
                table: "tbl_creator",
                newName: "last_name");

            migrationBuilder.RenameColumn(
                name: "firstname",
                table: "tbl_creator",
                newName: "first_name");
        }
    }
}
