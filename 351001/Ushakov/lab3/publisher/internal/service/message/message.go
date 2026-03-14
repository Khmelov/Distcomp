package post

import (
	"context"
	"log"

	"github.com/Khmelov/Distcomp/351001/Ushakov/lab3/publisher/internal/mapper"
	postModel "github.com/Khmelov/Distcomp/351001/Ushakov/lab3/publisher/internal/model"
)

type httpClient interface {
	CreatePost(ctx context.Context, issueID int64, content string) (*postModel.Post, error)
	GetPosts(ctx context.Context) ([]postModel.Post, error)
	GetPost(ctx context.Context, id int64) (*postModel.Post, error)
	UpdatePost(ctx context.Context, id, issueID int64, content string) (*postModel.Post, error)
	DeletePost(ctx context.Context, id int64) error
}

type service struct {
	client httpClient
}

type PostService interface {
	CreatePost(ctx context.Context, post postModel.Post) (postModel.Post, error)
	GetPosts(ctx context.Context) ([]postModel.Post, error)
	GetPostByID(ctx context.Context, id int64) (postModel.Post, error)
	UpdatePostByID(ctx context.Context, post postModel.Post) (postModel.Post, error)
	DeletePostByID(ctx context.Context, id int64) error
}

func New(client httpClient) PostService {
	return &service{
		client: client,
	}
}

func (s *service) CreatePost(ctx context.Context, post postModel.Post) (postModel.Post, error) {
	createdMsg, err := s.client.CreatePost(ctx, int64(post.IssueID), post.Content)
	if err != nil {
		return postModel.Post{}, err
	}

	log.Println(createdMsg)

	return mapper.MapHTTPPostToModel(*createdMsg), nil
}

func (s *service) GetPosts(ctx context.Context) ([]postModel.Post, error) {
	var mappedPosts []postModel.Post

	msgs, err := s.client.GetPosts(ctx)
	if err != nil {
		return mappedPosts, err
	}

	for _, msg := range msgs {
		mappedPosts = append(mappedPosts, mapper.MapHTTPPostToModel(msg))
	}

	if len(mappedPosts) == 0 {
		return []postModel.Post{}, nil
	}

	return mappedPosts, nil
}

func (s *service) GetPostByID(ctx context.Context, id int64) (postModel.Post, error) {
	msg, err := s.client.GetPost(ctx, id)
	if err != nil {
		return postModel.Post{}, err
	}

	return mapper.MapHTTPPostToModel(*msg), nil
}

func (s *service) UpdatePostByID(ctx context.Context, post postModel.Post) (postModel.Post, error) {
	updatedMsg, err := s.client.UpdatePost(ctx, int64(post.ID), int64(post.IssueID), post.Content)
	if err != nil {
		return postModel.Post{}, err
	}

	return mapper.MapHTTPPostToModel(*updatedMsg), nil
}

func (s *service) DeletePostByID(ctx context.Context, id int64) error {
	return s.client.DeletePost(ctx, id)
}
