using System;
using Microsoft.EntityFrameworkCore.Migrations;
using Npgsql.EntityFrameworkCore.PostgreSQL.Metadata;

#nullable disable

namespace rest1.migrations
{
    /// <inheritdoc />
    public partial class CreateInitial : Migration
    {
        /// <inheritdoc />
        protected override void Up(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.EnsureSchema(
                name: "public");

            migrationBuilder.CreateTable(
                name: "tbl_creator",
                schema: "public",
                columns: table => new
                {
                    id = table.Column<long>(type: "bigint", nullable: false)
                        .Annotation("Npgsql:ValueGenerationStrategy", NpgsqlValueGenerationStrategy.IdentityByDefaultColumn),
                    login = table.Column<string>(type: "text", nullable: false),
                    password = table.Column<string>(type: "text", nullable: false),
                    firstname = table.Column<string>(type: "text", nullable: false),
                    lastname = table.Column<string>(type: "text", nullable: false)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_tbl_creator", x => x.id);
                });

            migrationBuilder.CreateTable(
                name: "tbl_mark",
                schema: "public",
                columns: table => new
                {
                    id = table.Column<long>(type: "bigint", nullable: false)
                        .Annotation("Npgsql:ValueGenerationStrategy", NpgsqlValueGenerationStrategy.IdentityByDefaultColumn),
                    name = table.Column<string>(type: "text", nullable: false)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_tbl_mark", x => x.id);
                });

            migrationBuilder.CreateTable(
                name: "tbl_news",
                schema: "public",
                columns: table => new
                {
                    id = table.Column<long>(type: "bigint", nullable: false)
                        .Annotation("Npgsql:ValueGenerationStrategy", NpgsqlValueGenerationStrategy.IdentityByDefaultColumn),
                    creator_id = table.Column<long>(type: "bigint", nullable: false),
                    title = table.Column<string>(type: "text", nullable: false),
                    content = table.Column<string>(type: "text", nullable: false),
                    created = table.Column<DateTime>(type: "timestamp with time zone", nullable: false),
                    modified = table.Column<DateTime>(type: "timestamp with time zone", nullable: false)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_tbl_news", x => x.id);
                    table.ForeignKey(
                        name: "FK_tbl_news_tbl_creator_creator_id",
                        column: x => x.creator_id,
                        principalSchema: "public",
                        principalTable: "tbl_creator",
                        principalColumn: "id",
                        onDelete: ReferentialAction.Cascade);
                });

            migrationBuilder.CreateTable(
                name: "tbl_newsMark",
                schema: "public",
                columns: table => new
                {
                    MarkId = table.Column<long>(type: "bigint", nullable: false),
                    NewsId = table.Column<long>(type: "bigint", nullable: false)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_tbl_newsMark", x => new { x.MarkId, x.NewsId });
                    table.ForeignKey(
                        name: "FK_MarkNews_Mark",
                        column: x => x.MarkId,
                        principalSchema: "public",
                        principalTable: "tbl_mark",
                        principalColumn: "id",
                        onDelete: ReferentialAction.Cascade);
                    table.ForeignKey(
                        name: "FK_MarkNews_News",
                        column: x => x.NewsId,
                        principalSchema: "public",
                        principalTable: "tbl_news",
                        principalColumn: "id",
                        onDelete: ReferentialAction.Cascade);
                });

            migrationBuilder.CreateTable(
                name: "tbl_note",
                schema: "public",
                columns: table => new
                {
                    id = table.Column<long>(type: "bigint", nullable: false)
                        .Annotation("Npgsql:ValueGenerationStrategy", NpgsqlValueGenerationStrategy.IdentityByDefaultColumn),
                    news_id = table.Column<long>(type: "bigint", nullable: false),
                    content = table.Column<string>(type: "text", nullable: false)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_tbl_note", x => x.id);
                    table.ForeignKey(
                        name: "FK_tbl_note_tbl_news_news_id",
                        column: x => x.news_id,
                        principalSchema: "public",
                        principalTable: "tbl_news",
                        principalColumn: "id",
                        onDelete: ReferentialAction.Cascade);
                });

            migrationBuilder.CreateIndex(
                name: "IX_tbl_news_creator_id",
                schema: "public",
                table: "tbl_news",
                column: "creator_id");

            migrationBuilder.CreateIndex(
                name: "IX_tbl_newsMark_NewsId",
                schema: "public",
                table: "tbl_newsMark",
                column: "NewsId");

            migrationBuilder.CreateIndex(
                name: "IX_tbl_note_news_id",
                schema: "public",
                table: "tbl_note",
                column: "news_id");
        }

        /// <inheritdoc />
        protected override void Down(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropTable(
                name: "tbl_newsMark",
                schema: "public");

            migrationBuilder.DropTable(
                name: "tbl_note",
                schema: "public");

            migrationBuilder.DropTable(
                name: "tbl_mark",
                schema: "public");

            migrationBuilder.DropTable(
                name: "tbl_news",
                schema: "public");

            migrationBuilder.DropTable(
                name: "tbl_creator",
                schema: "public");
        }
    }
}
