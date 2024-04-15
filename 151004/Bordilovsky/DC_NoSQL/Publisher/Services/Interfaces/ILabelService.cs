using DC_REST.DTOs.Request;
using DC_REST.DTOs.Response;

namespace DC_REST.Services.Interfaces
{
    public interface ILabelService
	{
		LabelResponseTo CreateLabel(LabelRequestTo labelRequestDto);
		LabelResponseTo GetLabelById(int id);
		List<LabelResponseTo> GetAllLabels();
		LabelResponseTo UpdateLabel(int id, LabelRequestTo labelRequestDto);
		bool DeleteLabel(int id);
	}
}
