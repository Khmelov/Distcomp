using System;
using FluentMigrator;

namespace Infrastructure.Migrations;

[Migration(20250216101012, TransactionBehavior.None)]
public class AddFirstUser : Migration
{
    public override void Up()
    {
        const string add_user_script = 
"""
INSERT 
  INTO tbl_user 
       (
        login, password, firstname, lastname
       ) 
VALUES 
       (
        'yanucevich.d@gmail.com', 'password', 'Дмитрий', 'Януцевич'
       ) 
    ON CONFLICT (login) 
    DO NOTHING
    ;
""";
        Execute.Sql(add_user_script);
    }

    public override void Down()
    {
    }
}
