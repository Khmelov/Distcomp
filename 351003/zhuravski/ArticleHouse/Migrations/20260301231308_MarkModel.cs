using Microsoft.EntityFrameworkCore.Migrations;

#nullable disable

namespace ArticleHouse.Migrations
{
    /// <inheritdoc />
    public partial class MarkModel : Migration
    {
        /// <inheritdoc />
        protected override void Up(MigrationBuilder migrationBuilder)
        {
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

            migrationBuilder.CreateIndex(
                name: "ix_article_mark_mark_id",
                table: "article_mark",
                column: "mark_id");
        }

        /// <inheritdoc />
        protected override void Down(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropTable(
                name: "article_mark");
        }
    }
}
