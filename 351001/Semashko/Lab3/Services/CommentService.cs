using Cassandra;
using Cassandra.Mapping;
using lab_1.Context;
using lab_1.Dtos.RequestDtos;
using lab_1.Dtos.RequestDtos.RequestConverters;
using lab_1.Dtos.ResponseDtos;
using lab_1.Dtos.ResponseDtos.ResponseConverters;
using lab_1.Entities;
using Microsoft.EntityFrameworkCore;
using ISession = Cassandra.ISession;

namespace lab_1.Services;

public class CommentService : IBaseService<CommentRequestDto, CommentResponseDto>
{
    private ISession _context;
    private Mapper mapper;
    private BaseRequest<TblComment, CommentRequestDto> _request;
    private BaseResponse<CommentResponseDto, TblComment> _response;


    public CommentService()
    {
        _context = Cluster.Builder().AddContactPoint("localhost").WithPort(55001).Build().Connect("distcomp");
        _context.Execute(
            "CREATE  TABLE if not exists tbl_comments (country text,storyId  bigint,id bigint, content text, primary key ((country), id));");
        _request = new();
        _response = new();
        mapper = new (_context);

    }

    public CommentResponseDto Create(CommentRequestDto dto)
    {
        dto.Id = _context.Execute("select count(*) from tbl_comments").FirstOrDefault().GetValue<long>(0);
        var entity = _request.FromDto(dto);
        mapper.Insert(entity);
        return _response.ToDto(entity);
    }

    public CommentResponseDto? Read(long id)
    {
       return _response.ToDto(mapper.Single<TblComment>("where id = ? and country = \'Belarus\' ", id));
    }

    public CommentResponseDto? Update(CommentRequestDto dto)
    {
        var entity = _request.FromDto(dto);
        mapper.Update(entity);
        return _response.ToDto(entity);
    }

    public bool Delete(long id)
    {
        mapper.Delete(mapper.Single<TblComment>("where id = ? and country = \'Belarus\' ", id));
        return true;
    }

    public IEnumerable<CommentResponseDto> GetAll()
    {
            foreach (var entity in mapper.Fetch<TblComment>())
            {
                yield return _response.ToDto(entity);
            }
    }
}