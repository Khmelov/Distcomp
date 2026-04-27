using Mapster;
using ServerApp.Models.DTOs.Requests;
using ServerApp.Models.DTOs.Responses;
using ServerApp.Models.Entities;

namespace ServerApp.Services.Mapping;

public class MappingConfig : IRegister
{
    public void Register(TypeAdapterConfig config)
    {
        // 1. Маппинг для Author
        config.NewConfig<AuthorRequestTo, Author>();
        config.NewConfig<Author, AuthorResponseTo>();

        // 2. Маппинг для Article
        config.NewConfig<ArticleRequestTo, Article>()
            .AfterMapping(dest =>
            {
                // При создании статьи устанавливаем даты
                var now = DateTime.UtcNow;
                dest.Created = now;
                dest.Modified = now;
            });
            
        config.NewConfig<Article, ArticleResponseTo>();

        // 3. Маппинг для Message
        config.NewConfig<MessageRequestTo, Message>();
        config.NewConfig<Message, MessageResponseTo>();

        // 4. Маппинг для Sticker
        config.NewConfig<StickerRequestTo, Sticker>();
        config.NewConfig<Sticker, StickerResponseTo>();
    }
}