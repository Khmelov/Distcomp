using Domain.Models;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Application.Repository
{
    public interface IAuthorRepository : IRepository<Author>
    {
        Task<Author?> GetByLoginAsync(string login);
    }
}
