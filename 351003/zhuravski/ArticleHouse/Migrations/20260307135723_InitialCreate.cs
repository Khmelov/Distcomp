using Microsoft.EntityFrameworkCore.Migrations;
using Npgsql.EntityFrameworkCore.PostgreSQL.Metadata;

#nullable disable

namespace ArticleHouse.Migrations
{
    /// <inheritdoc />
    public partial class InitialCreate : Migration
    {
        /// <inheritdoc />
        protected override void Up(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.CreateTable(
                name: "tbl_creator",
                columns: table => new
                {
                    id = table.Column<long>(type: "bigint", nullable: false)
                        .Annotation("Npgsql:ValueGenerationStrategy", NpgsqlValueGenerationStrategy.IdentityByDefaultColumn),
                    password = table.Column<string>(type: "text", nullable: false),
                    login = table.Column<string>(type: "text", nullable: false),
                    firstname = table.Column<string>(type: "text", nullable: false),
                    lastname = table.Column<string>(type: "text", nullable: false)
                },
                constraints: table =>
                {
                    table.PrimaryKey("pk_tbl_creator", x => x.id);
                });

            migrationBuilder.CreateTable(
                name: "tbl_mark",
                columns: table => new
                {
                    id = table.Column<long>(type: "bigint", nullable: false)
                        .Annotation("Npgsql:ValueGenerationStrategy", NpgsqlValueGenerationStrategy.IdentityByDefaultColumn),
                    name = table.Column<string>(type: "text", nullable: false)
                },
                constraints: table =>
                {
                    table.PrimaryKey("pk_tbl_mark", x => x.id);
                });

            migrationBuilder.CreateTable(
                name: "tbl_article",
                columns: table => new
                {
                    id = table.Column<long>(type: "bigint", nullable: false)
                        .Annotation("Npgsql:ValueGenerationStrategy", NpgsqlValueGenerationStrategy.IdentityByDefaultColumn),
                    creator_id = table.Column<long>(type: "bigint", nullable: false),
                    title = table.Column<string>(type: "text", nullable: false),
                    content = table.Column<string>(type: "text", nullable: false)
                },
                constraints: table =>
                {
                    table.PrimaryKey("pk_tbl_article", x => x.id);
                    table.ForeignKey(
                        name: "fk_tbl_article_tbl_creator_creator_id",
                        column: x => x.creator_id,
                        principalTable: "tbl_creator",
                        principalColumn: "id",
                        onDelete: ReferentialAction.Cascade);
                });

            migrationBuilder.CreateTable(
                name: "article_mark",
                columns: table => new
                {
                    article_id = table.Column<long>(type: "bigint", nullable: false),
                    mark_id = table.Column<long>(type: "bigint", nullable: false)
                },
                constraints: table =>
                {
                    table.PrimaryKey("pk_article_mark", x => new { x.article_id, x.mark_id });
                    table.ForeignKey(
                        name: "fk_article_mark_tbl_article_article_id",
                        column: x => x.article_id,
                        principalTable: "tbl_article",
                        principalColumn: "id",
                        onDelete: ReferentialAction.Cascade);
                    table.ForeignKey(
                        name: "fk_article_mark_tbl_mark_mark_id",
                        column: x => x.mark_id,
                        principalTable: "tbl_mark",
                        principalColumn: "id",
                        onDelete: ReferentialAction.Cascade);
                });

            migrationBuilder.CreateTable(
                name: "tbl_comment",
                columns: table => new
                {
                    id = table.Column<long>(type: "bigint", nullable: false)
                        .Annotation("Npgsql:ValueGenerationStrategy", NpgsqlValueGenerationStrategy.IdentityByDefaultColumn),
                    article_id = table.Column<long>(type: "bigint", nullable: false),
                    content = table.Column<string>(type: "text", nullable: false)
                },
                constraints: table =>
                {
                    table.PrimaryKey("pk_tbl_comment", x => x.id);
                    table.ForeignKey(
                        name: "fk_tbl_comment_tbl_article_article_id",
                        column: x => x.article_id,
                        principalTable: "tbl_article",
                        principalColumn: "id",
                        onDelete: ReferentialAction.Cascade);
                });

            migrationBuilder.CreateIndex(
                name: "ix_article_mark_mark_id",
                table: "article_mark",
                column: "mark_id");

            migrationBuilder.CreateIndex(
                name: "ix_tbl_article_creator_id",
                table: "tbl_article",
                column: "creator_id");

            migrationBuilder.CreateIndex(
                name: "ix_tbl_article_title",
                table: "tbl_article",
                column: "title",
                unique: true);

            migrationBuilder.CreateIndex(
                name: "ix_tbl_comment_article_id",
                table: "tbl_comment",
                column: "article_id");

            migrationBuilder.CreateIndex(
                name: "ix_tbl_creator_login",
                table: "tbl_creator",
                column: "login",
                unique: true);

            migrationBuilder.CreateIndex(
                name: "ix_tbl_mark_name",
                table: "tbl_mark",
                column: "name",
                unique: true);
        }

        /// <inheritdoc />
        protected override void Down(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropTable(
                name: "article_mark");

            migrationBuilder.DropTable(
                name: "tbl_comment");

            migrationBuilder.DropTable(
                name: "tbl_mark");

            migrationBuilder.DropTable(
                name: "tbl_article");

            migrationBuilder.DropTable(
                name: "tbl_creator");
        }
    }
}
