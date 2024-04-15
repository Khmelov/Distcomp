using AutoMapper;
using Discussion.Common;
using Discussion.PostEntity.Interface;
using Microsoft.AspNetCore.Mvc;

namespace Discussion.PostEntity
{
    [Route("api/v1.0/posts")]
    [ApiController]
    public class PostController(IPostService PostService, ILogger<PostController> Logger, IMapper Mapper)
        : AbstractController<Post, PostRequestTO, PostResponseTO>(PostService, Logger, Mapper)
    { }
}
