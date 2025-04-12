//package by.ryabchikov.tweet_service.mapper;
//
//import by.ryabchikov.tweet_service.dto.comment.CommentRequestTo;
//import by.ryabchikov.tweet_service.dto.comment.CommentResponseTo;
//import by.ryabchikov.tweet_service.entity.Comment;
//import org.mapstruct.Mapper;
//import org.mapstruct.Mapping;
//
//@Mapper(componentModel = "spring")
//public interface CommentMapper {
//
//    @Mapping(target = "tweet.id", source = "tweetId")
//    Comment toComment(CommentRequestTo commentRequestTo);
//
//    @Mapping(target = "tweetId", source = "tweet.id")
//    CommentResponseTo toCommentResponseTo(Comment comment);
//}
