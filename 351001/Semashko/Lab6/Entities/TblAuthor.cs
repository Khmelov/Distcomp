using System;
using System.Collections.Generic;

namespace lab_1.Entities;

public partial class TblAuthor:TblBase
{
    public TblAuthor()
    {
    }

    public TblAuthor(long id, string login, string password, string firstname, string lastname, string role)
    {
        Id = id;
        Login = login;
        Password = password;
        Firstname = firstname;
        Lastname = lastname;
        Role = role;
    }



    public string Login { get; set; } = null!;

    public string Password { get; set; } = null!;

    public string Lastname { get; set; } = null!;

    public string Firstname { get; set; } = null!;

    public string Role { get; set; } = "CUSTOMER";

}
