using Domain.Models;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Application.Repository
{
    public interface IIssueRepository : IRepository<Issue>
    {
        Task<Issue?> GetByTitleAsync(string title);
    }
}
