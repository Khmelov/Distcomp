using System;
using Microsoft.EntityFrameworkCore.Migrations;
using Npgsql.EntityFrameworkCore.PostgreSQL.Metadata;

#nullable disable

namespace RV.Migrations
{
    /// <inheritdoc />
    public partial class Initial : Migration
    {
        /// <inheritdoc />
        protected override void Up(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.CreateTable(
                name: "tbl_Sticker",
                columns: table => new
                {
                    id = table.Column<int>(type: "integer", nullable: false)
                        .Annotation("Npgsql:ValueGenerationStrategy", NpgsqlValueGenerationStrategy.IdentityByDefaultColumn),
                    name = table.Column<string>(type: "character varying(32)", maxLength: 32, nullable: false)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_tbl_Sticker", x => x.id);
                    table.CheckConstraint("len(name) < 2", "LENGTH(\"name\") > 2");
                });

            migrationBuilder.CreateTable(
                name: "tbl_User",
                columns: table => new
                {
                    id = table.Column<int>(type: "integer", nullable: false)
                        .Annotation("Npgsql:ValueGenerationStrategy", NpgsqlValueGenerationStrategy.IdentityByDefaultColumn),
                    login = table.Column<string>(type: "character varying(64)", maxLength: 64, nullable: false),
                    password = table.Column<string>(type: "character varying(128)", maxLength: 128, nullable: false),
                    firstname = table.Column<string>(type: "character varying(64)", maxLength: 64, nullable: false),
                    lastname = table.Column<string>(type: "character varying(64)", maxLength: 64, nullable: false)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_tbl_User", x => x.id);
                    table.CheckConstraint("len(firstname) < 2", "LENGTH(\"firstname\") > 2");
                    table.CheckConstraint("len(lastname) < 2", "LENGTH(\"lastname\") > 2");
                    table.CheckConstraint("len(login) < 2", "LENGTH(\"login\") > 2");
                    table.CheckConstraint("len(password) < 8", "LENGTH(\"password\") > 8");
                });

            migrationBuilder.CreateTable(
                name: "tbl_Tweet",
                columns: table => new
                {
                    id = table.Column<int>(type: "integer", nullable: false)
                        .Annotation("Npgsql:ValueGenerationStrategy", NpgsqlValueGenerationStrategy.IdentityByDefaultColumn),
                    userId = table.Column<int>(type: "integer", nullable: false),
                    title = table.Column<string>(type: "character varying(64)", maxLength: 64, nullable: false),
                    content = table.Column<string>(type: "character varying(2048)", maxLength: 2048, nullable: false),
                    created = table.Column<DateTime>(type: "timestamp with time zone", nullable: false),
                    modified = table.Column<DateTime>(type: "timestamp with time zone", nullable: false)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_tbl_Tweet", x => x.id);
                    table.CheckConstraint("len(content) < 4", "LENGTH(\"content\") > 4");
                    table.CheckConstraint("len(title) < 2", "LENGTH(\"title\") > 2");
                    table.ForeignKey(
                        name: "FK_tbl_Tweet_tbl_User_userId",
                        column: x => x.userId,
                        principalTable: "tbl_User",
                        principalColumn: "id",
                        onDelete: ReferentialAction.Cascade);
                });

            migrationBuilder.CreateTable(
                name: "TweetsSticker",
                columns: table => new
                {
                    id = table.Column<int>(type: "integer", nullable: false)
                        .Annotation("Npgsql:ValueGenerationStrategy", NpgsqlValueGenerationStrategy.IdentityByDefaultColumn),
                    tweetId = table.Column<int>(type: "integer", nullable: false),
                    stickerId = table.Column<int>(type: "integer", nullable: false)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_TweetsSticker", x => x.id);
                    table.ForeignKey(
                        name: "FK_TweetsSticker_tbl_Sticker_stickerId",
                        column: x => x.stickerId,
                        principalTable: "tbl_Sticker",
                        principalColumn: "id",
                        onDelete: ReferentialAction.Cascade);
                    table.ForeignKey(
                        name: "FK_TweetsSticker_tbl_Tweet_tweetId",
                        column: x => x.tweetId,
                        principalTable: "tbl_Tweet",
                        principalColumn: "id",
                        onDelete: ReferentialAction.Cascade);
                });

            migrationBuilder.CreateTable(
                name: "tbl_Note",
                columns: table => new
                {
                    id = table.Column<int>(type: "integer", nullable: false)
                        .Annotation("Npgsql:ValueGenerationStrategy", NpgsqlValueGenerationStrategy.IdentityByDefaultColumn),
                    tweetId = table.Column<int>(type: "integer", nullable: false),
                    content = table.Column<string>(type: "character varying(2048)", maxLength: 2048, nullable: false)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_tbl_Note", x => x.id);
                    table.CheckConstraint("len(content) < 2", "LENGTH(\"content\") > 2");
                    table.ForeignKey(
                        name: "FK_tbl_Note_tbl_Tweet_tweetId",
                        column: x => x.tweetId,
                        principalTable: "tbl_Tweet",
                        principalColumn: "id",
                        onDelete: ReferentialAction.Cascade);
                });

            migrationBuilder.CreateIndex(
                name: "IX_TweetsSticker_stickerId",
                table: "TweetsSticker",
                column: "stickerId");

            migrationBuilder.CreateIndex(
                name: "IX_TweetsSticker_tweetId",
                table: "TweetsSticker",
                column: "tweetId");

            migrationBuilder.CreateIndex(
                name: "IX_tbl_Note_tweetId",
                table: "tbl_Note",
                column: "tweetId");

            migrationBuilder.CreateIndex(
                name: "IX_tbl_Sticker_name",
                table: "tbl_Sticker",
                column: "name",
                unique: true);

            migrationBuilder.CreateIndex(
                name: "IX_tbl_Tweet_title",
                table: "tbl_Tweet",
                column: "title",
                unique: true);

            migrationBuilder.CreateIndex(
                name: "IX_tbl_Tweet_userId",
                table: "tbl_Tweet",
                column: "userId");

            migrationBuilder.CreateIndex(
                name: "IX_tbl_User_login",
                table: "tbl_User",
                column: "login",
                unique: true);
        }

        /// <inheritdoc />
        protected override void Down(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropTable(
                name: "TweetsSticker");

            migrationBuilder.DropTable(
                name: "tbl_Note");

            migrationBuilder.DropTable(
                name: "tbl_Sticker");

            migrationBuilder.DropTable(
                name: "tbl_Tweet");

            migrationBuilder.DropTable(
                name: "tbl_User");
        }
    }
}
