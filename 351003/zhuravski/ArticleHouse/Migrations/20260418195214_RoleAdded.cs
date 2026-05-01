using Microsoft.EntityFrameworkCore.Migrations;

#nullable disable

namespace ArticleHouse.Migrations
{
    /// <inheritdoc />
    public partial class RoleAdded : Migration
    {
        /// <inheritdoc />
        protected override void Up(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.AddColumn<string>(
                name: "role",
                table: "tbl_creator",
                type: "text",
                nullable: false,
                defaultValue: "");
        }

        /// <inheritdoc />
        protected override void Down(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropColumn(
                name: "role",
                table: "tbl_creator");
        }
    }
}
