using System;
using FluentMigrator;

namespace Infrastructure.Migrations;

[Migration(20250216101010, TransactionBehavior.None)]
public class Empty : Migration
{
    public override void Up()
    {
    }

    public override void Down()
    {
    }
}
