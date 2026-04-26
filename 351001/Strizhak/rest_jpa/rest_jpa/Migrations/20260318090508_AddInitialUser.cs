using Microsoft.EntityFrameworkCore.Migrations;

#nullable disable

namespace rest_jpa.Migrations
{
    /// <inheritdoc />
    public partial class AddInitialUser : Migration
    {
        /// <inheritdoc />
        protected override void Up(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.InsertData(
                table: "tbl_user",
                columns: new[] { "Id", "Firstname", "Lastname", "Login", "Password" },
                values: new object[] { 1L, "Veranika", "Stryzhak", "veranikastryzhak@gmail.com", "$2b$10$l5.uRtHAlx1gvNkml0exgu32vCSsy4L3n2.mAfjhu6QXvmXYWhpGm" });
        }

        /// <inheritdoc />
        protected override void Down(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DeleteData(
                table: "tbl_user",
                keyColumn: "Id",
                keyValue: 1L);
        }
    }
}
