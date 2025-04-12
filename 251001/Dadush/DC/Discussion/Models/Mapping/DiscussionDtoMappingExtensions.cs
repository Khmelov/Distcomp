using AutoMapper;

namespace Discussion.Models.Mapping {
    public static class DiscussionDtoMappingExtensions {

        public static void AddDtoMapping(this IServiceCollection services) {
            var configuration = new MapperConfiguration(config => {
                config.CreateMap<CommentInDto, Comment>();
                config.CreateMap<Comment, CommentOutDto>();
            });
            services.AddSingleton(configuration.CreateMapper());
        }
    }
}
