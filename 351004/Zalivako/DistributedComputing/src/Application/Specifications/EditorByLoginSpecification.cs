using Core.Entities;
using System.Linq.Expressions;

namespace Application.Specifications
{
    public class EditorByLoginSpecification : BaseSpecification<Editor>
    {
        public EditorByLoginSpecification(string login) : base(e => e.Login == login)
        {
        }
    }
}