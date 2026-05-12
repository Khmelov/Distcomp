using System.ComponentModel.DataAnnotations;

namespace lab_1.Domain
{
    public class Author:BaseEntity
    {   
        private string _login;
      
        private string _password;
        
        private string _firstname;
        
        private string _lastname;

        public Author(long? id, string login, string password, string firstname, string lastname) : base(id)
        {  
                _login = login;
                _password = password;
                _firstname = firstname;
                _lastname = lastname;
        }
        [StringLength(64,MinimumLength = 2)]
        public string Login { get => _login; }
        [StringLength(128,MinimumLength = 8)]
        
        public string Password { get => _password;}
        [StringLength(64,MinimumLength = 2)]
        public string Firstname { get => _firstname;}
        [StringLength(64,MinimumLength = 2)]
        public string Lastname { get => _lastname;}

    }
}
