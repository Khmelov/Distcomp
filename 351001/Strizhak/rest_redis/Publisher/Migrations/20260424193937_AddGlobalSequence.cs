using Microsoft.EntityFrameworkCore.Migrations;

#nullable disable

namespace Publisher.Migrations
{
    /// <inheritdoc />
    public partial class AddGlobalSequence : Migration
    {
        /// <inheritdoc />
        protected override void Up(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropSequence(
                name: "tbl_notice_id_seq",
                schema: "distcomp");

            migrationBuilder.CreateSequence(
                name: "tbl_reaction_id_seq",
                schema: "distcomp");
        }

        /// <inheritdoc />
        protected override void Down(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropSequence(
                name: "tbl_reaction_id_seq",
                schema: "distcomp");

            migrationBuilder.CreateSequence(
                name: "tbl_notice_id_seq",
                schema: "distcomp");
        }
    }
}
