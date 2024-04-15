using System;
using System.Collections.Generic;
using System.Numerics;
using Cassandra;
using Discussion.Entities;
using Discussion.Repositories;

public class NoteDbRepository : IRepository<Note>
{
	private readonly Cassandra.ISession _session;

	public NoteDbRepository(Cassandra.ISession session)
	{
		_session = session;
	}

	public Note GetById(int id)
	{
		var query = $"SELECT * FROM tbl_notes WHERE id = {id}";
		var row = _session.Execute(query).FirstOrDefault();
		if (row != null)
		{
			return new Note
			{
				Id = (int)row.GetValue<long>("id"),
				issueid = (int)row.GetValue<long>("issueid"),
				Content = row.GetValue<string>("content"),
			};
		}
		else
		{
			return null;
		}
	}

	public List<Note> GetAll()
	{
		var query = "SELECT * FROM tbl_notes";
		var rows = _session.Execute(query);

		var notes = new List<Note>();
		foreach (var row in rows)
		{
			notes.Add(new Note
			{
				Id = (int)row.GetValue<long>("id"),
				issueid = (int)row.GetValue<long>("issueid"),
				Content = row.GetValue<string>("content"),
			});
		}

		return notes;
	}

	public Note Add(Note entity)
	{
		var query = $"INSERT INTO tbl_notes (id, content, issueid) VALUES ({entity.Id}, '{entity.Content}', {entity.issueid})";
		_session.Execute(query);

		return entity;
	}

	public Note Update(int id, Note entity)
	{
		var query = $"UPDATE tbl_notes SET content = '{entity.Content}' WHERE id = {id} AND issueid = {entity.issueid}";
		_session.Execute(query);

		return entity;
	}

	public bool Delete(int id)
	{
		var query = $"DELETE FROM tbl_notes WHERE id = {id}";
		try
		{
			_session.Execute(query);

			return true;
		}
		catch (Exception)
		{
			return false;
		}
	}

	public int GetCurrentId()
	{
		throw new NotImplementedException();
	}
}
