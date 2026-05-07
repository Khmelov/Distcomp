using System;
using Microsoft.EntityFrameworkCore.Migrations;
using Npgsql.EntityFrameworkCore.PostgreSQL.Metadata;

#nullable disable

namespace Infrastructure.Migrations
{
    /// <inheritdoc />
    public partial class initial : Migration
    {
        /// <inheritdoc />
        protected override void Up(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.CreateTable(
                name: "tbl_label",
                columns: table => new
                {
                    id = table.Column<long>(type: "bigint", nullable: false)
                        .Annotation("Npgsql:ValueGenerationStrategy", NpgsqlValueGenerationStrategy.IdentityByDefaultColumn),
                    name = table.Column<string>(type: "character varying(32)", maxLength: 32, nullable: false)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_tbl_label", x => x.id);
                });

            migrationBuilder.CreateTable(
                name: "tbl_user",
                columns: table => new
                {
                    id = table.Column<long>(type: "bigint", nullable: false)
                        .Annotation("Npgsql:ValueGenerationStrategy", NpgsqlValueGenerationStrategy.IdentityByDefaultColumn),
                    login = table.Column<string>(type: "character varying(50)", maxLength: 50, nullable: false),
                    password = table.Column<string>(type: "character varying(128)", maxLength: 128, nullable: true),
                    firstname = table.Column<string>(type: "character varying(64)", maxLength: 64, nullable: false),
                    lastname = table.Column<string>(type: "character varying(64)", maxLength: 64, nullable: false)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_tbl_user", x => x.id);
                });

            migrationBuilder.CreateTable(
                name: "tbl_topic",
                columns: table => new
                {
                    id = table.Column<long>(type: "bigint", nullable: false)
                        .Annotation("Npgsql:ValueGenerationStrategy", NpgsqlValueGenerationStrategy.IdentityByDefaultColumn),
                    user_id = table.Column<long>(type: "bigint", nullable: false),
                    title = table.Column<string>(type: "character varying(64)", maxLength: 64, nullable: false),
                    content = table.Column<string>(type: "character varying(2048)", maxLength: 2048, nullable: false),
                    created_at = table.Column<DateTime>(type: "timestamp with time zone", nullable: false, defaultValueSql: "CURRENT_TIMESTAMP"),
                    modified_at = table.Column<DateTime>(type: "timestamp with time zone", nullable: false, defaultValueSql: "CURRENT_TIMESTAMP")
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_tbl_topic", x => x.id);
                    table.ForeignKey(
                        name: "FK_tbl_topic_tbl_user_user_id",
                        column: x => x.user_id,
                        principalTable: "tbl_user",
                        principalColumn: "id",
                        onDelete: ReferentialAction.Cascade);
                });

            migrationBuilder.CreateTable(
                name: "LabelTopic",
                columns: table => new
                {
                    LabelsId = table.Column<long>(type: "bigint", nullable: false),
                    TopicsId = table.Column<long>(type: "bigint", nullable: false)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_LabelTopic", x => new { x.LabelsId, x.TopicsId });
                    table.ForeignKey(
                        name: "FK_LabelTopic_tbl_label_LabelsId",
                        column: x => x.LabelsId,
                        principalTable: "tbl_label",
                        principalColumn: "id",
                        onDelete: ReferentialAction.Cascade);
                    table.ForeignKey(
                        name: "FK_LabelTopic_tbl_topic_TopicsId",
                        column: x => x.TopicsId,
                        principalTable: "tbl_topic",
                        principalColumn: "id",
                        onDelete: ReferentialAction.Cascade);
                });

            migrationBuilder.CreateTable(
                name: "tbl_reaction",
                columns: table => new
                {
                    id = table.Column<long>(type: "bigint", nullable: false)
                        .Annotation("Npgsql:ValueGenerationStrategy", NpgsqlValueGenerationStrategy.IdentityByDefaultColumn),
                    topic_id = table.Column<long>(type: "bigint", nullable: false),
                    content = table.Column<string>(type: "character varying(2048)", maxLength: 2048, nullable: false)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_tbl_reaction", x => x.id);
                    table.ForeignKey(
                        name: "FK_tbl_reaction_tbl_topic_topic_id",
                        column: x => x.topic_id,
                        principalTable: "tbl_topic",
                        principalColumn: "id",
                        onDelete: ReferentialAction.Cascade);
                });

            migrationBuilder.CreateIndex(
                name: "IX_LabelTopic_TopicsId",
                table: "LabelTopic",
                column: "TopicsId");

            migrationBuilder.CreateIndex(
                name: "IX_tbl_label_name",
                table: "tbl_label",
                column: "name",
                unique: true);

            migrationBuilder.CreateIndex(
                name: "IX_tbl_reaction_topic_id",
                table: "tbl_reaction",
                column: "topic_id");

            migrationBuilder.CreateIndex(
                name: "IX_tbl_topic_title",
                table: "tbl_topic",
                column: "title",
                unique: true);

            migrationBuilder.CreateIndex(
                name: "IX_tbl_topic_user_id",
                table: "tbl_topic",
                column: "user_id");

            migrationBuilder.CreateIndex(
                name: "IX_tbl_user_login",
                table: "tbl_user",
                column: "login",
                unique: true);
        }

        /// <inheritdoc />
        protected override void Down(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropTable(
                name: "LabelTopic");

            migrationBuilder.DropTable(
                name: "tbl_reaction");

            migrationBuilder.DropTable(
                name: "tbl_label");

            migrationBuilder.DropTable(
                name: "tbl_topic");

            migrationBuilder.DropTable(
                name: "tbl_user");
        }
    }
}
