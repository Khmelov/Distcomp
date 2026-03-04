using Microsoft.EntityFrameworkCore.Migrations;

#nullable disable

namespace Infrastructure.Migrations
{
    /// <inheritdoc />
    public partial class End : Migration
    {
        /// <inheritdoc />
        protected override void Up(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.RenameColumn(
                name: "LastName",
                table: "users",
                newName: "Lastname");

            migrationBuilder.RenameColumn(
                name: "FirstName",
                table: "users",
                newName: "Firstname");
        }

        /// <inheritdoc />
        protected override void Down(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.RenameColumn(
                name: "Lastname",
                table: "users",
                newName: "LastName");

            migrationBuilder.RenameColumn(
                name: "Firstname",
                table: "users",
                newName: "FirstName");
        }
    }
}
