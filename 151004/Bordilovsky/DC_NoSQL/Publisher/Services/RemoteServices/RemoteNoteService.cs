using DC_REST.DTOs.Request;
using DC_REST.DTOs.Response;
using DC_REST.Services.Interfaces;
using RestSharp;
using Newtonsoft.Json;

namespace Publisher.Services.RemoteServices
{
	public class RemoteNoteService : INoteService
	{
		public NoteResponseTo CreateNote(NoteRequestTo noteRequestDto)
		{
			var client = new RestClient("http://localhost:24130/api/v1.0/");
			var request = new RestRequest("notes", Method.Post);
			request.AddBody(noteRequestDto, ContentType.Json);
			var response = client.Execute(request);
			var notePespondeDto = JsonConvert.DeserializeObject<NoteResponseTo>(response.Content);
			return notePespondeDto;
		}

		public bool DeleteNote(int id)
		{
			var client = new RestClient("http://localhost:24130/api/v1.0/");
			var request = new RestRequest($"notes/{id}", Method.Delete);
			var response = client.Execute(request);
			if (response.StatusCode == System.Net.HttpStatusCode.NoContent)
				return true;
			else
				return false;
		}

		public List<NoteResponseTo> GetAllNotes()
		{
			var client = new RestClient("http://localhost:24130/api/v1.0/");
            var request = new RestRequest("notes", Method.Get);
            var response = client.Execute(request);
            var notePespondeDto = JsonConvert.DeserializeObject<List<NoteResponseTo>>(response.Content);
            return notePespondeDto;
		}

		public NoteResponseTo GetNoteById(int id)
		{
			var client = new RestClient("http://localhost:24130/api/v1.0/");
			var request = new RestRequest($"notes/{id}", Method.Get);
			var response = client.Execute(request);
			var noteResponseDto = JsonConvert.DeserializeObject<NoteResponseTo>(response.Content);
			return noteResponseDto;
		}

		public NoteResponseTo UpdateNote(int id, NoteRequestTo noteRequestDto)
		{
			var client = new RestClient("http://localhost:24130/api/v1.0/");
			var request = new RestRequest("notes", Method.Put);
			request.AddBody(noteRequestDto, ContentType.Json);
			var response = client.Execute(request);
			var noteResponseDto = JsonConvert.DeserializeObject<NoteResponseTo>(response.Content);
			return noteResponseDto;
		}
	}
}
