using System;
using Microsoft.EntityFrameworkCore.Migrations;
using Npgsql.EntityFrameworkCore.PostgreSQL.Metadata;

#nullable disable

namespace Publisher.Infrastructure.Migrations
{
    /// <inheritdoc />
    public partial class AddUserRoleToAuthor : Migration
    {
        /// <inheritdoc />
        protected override void Up(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.CreateTable(
                name: "tbl_author",
                columns: table => new
                {
                    id = table.Column<long>(type: "bigint", nullable: false)
                        .Annotation("Npgsql:ValueGenerationStrategy", NpgsqlValueGenerationStrategy.IdentityByDefaultColumn),
                    login = table.Column<string>(type: "text", nullable: false),
                    password = table.Column<string>(type: "text", nullable: false),
                    firstname = table.Column<string>(type: "text", nullable: false),
                    lastname = table.Column<string>(type: "text", nullable: false),
                    role = table.Column<string>(type: "text", nullable: false)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_tbl_author", x => x.id);
                });

            migrationBuilder.CreateTable(
                name: "tbl_label",
                columns: table => new
                {
                    id = table.Column<long>(type: "bigint", nullable: false)
                        .Annotation("Npgsql:ValueGenerationStrategy", NpgsqlValueGenerationStrategy.IdentityByDefaultColumn),
                    name = table.Column<string>(type: "text", nullable: false)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_tbl_label", x => x.id);
                });

            migrationBuilder.CreateTable(
                name: "tbl_issue",
                columns: table => new
                {
                    id = table.Column<long>(type: "bigint", nullable: false)
                        .Annotation("Npgsql:ValueGenerationStrategy", NpgsqlValueGenerationStrategy.IdentityByDefaultColumn),
                    author_id = table.Column<long>(type: "bigint", nullable: false),
                    title = table.Column<string>(type: "text", nullable: false),
                    content = table.Column<string>(type: "text", nullable: false),
                    created = table.Column<DateTime>(type: "timestamp with time zone", nullable: false),
                    modified = table.Column<DateTime>(type: "timestamp with time zone", nullable: false)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_tbl_issue", x => x.id);
                    table.ForeignKey(
                        name: "FK_tbl_issue_tbl_author_author_id",
                        column: x => x.author_id,
                        principalTable: "tbl_author",
                        principalColumn: "id",
                        onDelete: ReferentialAction.Cascade);
                });

            migrationBuilder.CreateTable(
                name: "tbl_issue_label",
                columns: table => new
                {
                    issue_id = table.Column<long>(type: "bigint", nullable: false),
                    label_id = table.Column<long>(type: "bigint", nullable: false)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_tbl_issue_label", x => new { x.issue_id, x.label_id });
                    table.ForeignKey(
                        name: "FK_tbl_issue_label_tbl_issue_issue_id",
                        column: x => x.issue_id,
                        principalTable: "tbl_issue",
                        principalColumn: "id",
                        onDelete: ReferentialAction.Cascade);
                    table.ForeignKey(
                        name: "FK_tbl_issue_label_tbl_label_label_id",
                        column: x => x.label_id,
                        principalTable: "tbl_label",
                        principalColumn: "id",
                        onDelete: ReferentialAction.Cascade);
                });

            migrationBuilder.InsertData(
                table: "tbl_author",
                columns: new[] { "id", "firstname", "lastname", "login", "password", "role" },
                values: new object[] { 1L, "Александр", "Шевченко", "alexander.shevchenko.bsuir@gmail.com", "$2a$11$nKwuwXw/UB2jCh8OpRy.Je2MNKUI1qEbY4/fq4sGhdsKiZuoLtTEa", "ADMIN" });

            migrationBuilder.CreateIndex(
                name: "IX_tbl_author_login",
                table: "tbl_author",
                column: "login",
                unique: true);

            migrationBuilder.CreateIndex(
                name: "IX_tbl_issue_author_id",
                table: "tbl_issue",
                column: "author_id");

            migrationBuilder.CreateIndex(
                name: "IX_tbl_issue_label_label_id",
                table: "tbl_issue_label",
                column: "label_id");

            migrationBuilder.CreateIndex(
                name: "IX_tbl_label_name",
                table: "tbl_label",
                column: "name",
                unique: true);
        }

        /// <inheritdoc />
        protected override void Down(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropTable(
                name: "tbl_issue_label");

            migrationBuilder.DropTable(
                name: "tbl_issue");

            migrationBuilder.DropTable(
                name: "tbl_label");

            migrationBuilder.DropTable(
                name: "tbl_author");
        }
    }
}
