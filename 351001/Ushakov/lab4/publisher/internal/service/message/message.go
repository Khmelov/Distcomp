package message

import (
	"context"
	"log"
	"time"

	"github.com/Khmelov/Distcomp/351001/Ushakov/labX/publisher/internal/mapper"
	messageModel "github.com/Khmelov/Distcomp/351001/Ushakov/labX/publisher/internal/model"
	"github.com/Khmelov/Distcomp/351001/Ushakov/labX/publisher/internal/storage"
)

type httpClient interface {
	CreatePost(ctx context.Context, issueID int64, content string) (*messageModel.Post, error)
	GetPosts(ctx context.Context) ([]messageModel.Post, error)
	GetPost(ctx context.Context, id int64) (*messageModel.Post, error)
	UpdatePost(ctx context.Context, id, issueID int64, content string) (*messageModel.Post, error)
	DeletePost(ctx context.Context, id int64) error
}

type service struct {
	client httpClient
	cache  storage.Cache
}

type PostService interface {
	CreatePost(ctx context.Context, message messageModel.Post) (messageModel.Post, error)
	GetPosts(ctx context.Context) ([]messageModel.Post, error)
	GetPostByID(ctx context.Context, id int64) (messageModel.Post, error)
	UpdatePostByID(ctx context.Context, message messageModel.Post) (messageModel.Post, error)
	DeletePostByID(ctx context.Context, id int64) error
}

func New(client httpClient, cache storage.Cache) PostService {
	return &service{
		cache:  cache,
		client: client,
	}
}

func (s *service) CreatePost(ctx context.Context, message messageModel.Post) (messageModel.Post, error) {
	createdMsg, err := s.client.CreatePost(ctx, int64(message.IssueID), message.Content)
	if err != nil {
		return messageModel.Post{}, err
	}

	log.Println(createdMsg)

	return mapper.MapHTTPPostToModel(*createdMsg), nil
}

func (s *service) GetPosts(ctx context.Context) ([]messageModel.Post, error) {
	cachedMsgs, err := s.cache.DB.GetAllPosts(ctx)
	if err == nil && len(cachedMsgs) > 0 {
		return cachedMsgs, nil
	}

	msgs, err := s.client.GetPosts(ctx)
	if err != nil {
		return nil, err
	}

	var mappedPosts []messageModel.Post
	for _, msg := range msgs {
		mappedMsg := mapper.MapHTTPPostToModel(msg)
		mappedPosts = append(mappedPosts, mappedMsg)

		if err := s.cache.DB.SavePost(ctx, &mappedMsg, 24*time.Hour); err != nil {
			log.Printf("failed to cache message %d: %v", mappedMsg.ID, err)
		}
	}

	if len(mappedPosts) == 0 {
		return []messageModel.Post{}, nil
	}

	return mappedPosts, nil
}

func (s *service) GetPostByID(ctx context.Context, id int64) (messageModel.Post, error) {
	cachedMsg, err := s.cache.DB.GetPost(ctx, int(id))
	if err == nil && cachedMsg != nil {
		return *cachedMsg, nil
	}

	msg, err := s.client.GetPost(ctx, id)
	if err != nil {
		return messageModel.Post{}, err
	}

	mappedMsg := mapper.MapHTTPPostToModel(*msg)

	if err := s.cache.DB.SavePost(ctx, &mappedMsg, 24*time.Hour); err != nil {
		log.Printf("failed to cache message %d: %v", id, err)
	}

	return mappedMsg, nil
}

func (s *service) UpdatePostByID(ctx context.Context, message messageModel.Post) (messageModel.Post, error) {
	updatedMsg, err := s.client.UpdatePost(ctx, int64(message.ID), int64(message.IssueID), message.Content)
	if err != nil {
		return messageModel.Post{}, err
	}

	mappedMsg := mapper.MapHTTPPostToModel(*updatedMsg)

	if err := s.cache.DB.SavePost(ctx, &mappedMsg, 24*time.Hour); err != nil {
		log.Printf("failed to update cached message %d: %v", mappedMsg.ID, err)
	}

	return mappedMsg, nil
}

func (s *service) DeletePostByID(ctx context.Context, id int64) error {
	if err := s.client.DeletePost(ctx, id); err != nil {
		return err
	}

	if err := s.cache.DB.DeletePost(ctx, int(id)); err != nil {
		log.Printf("failed to delete cached message %d: %v", id, err)
	}

	return nil
}
