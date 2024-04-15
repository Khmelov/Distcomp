using Microsoft.AspNetCore.JsonPatch;

namespace Publisher.Service.Interface.Common
{
    public interface ICrudService<Entity, RequestTO, ResponseTO> where Entity : class
    {
        Task<ResponseTO> Patch(int id, JsonPatchDocument<Entity> patch);
        Task<IList<ResponseTO>> GetAll();
        Task<ResponseTO> Add(RequestTO requestTo);
        Task<bool> Remove(int id);
        Task<ResponseTO> Update(RequestTO requestTo);
        Task<ResponseTO> GetByID(int id);
    }
}
