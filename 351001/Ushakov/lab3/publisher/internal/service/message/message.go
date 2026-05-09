package post

import (
	"context"
	"log"

	"github.com/Khmelov/Distcomp/351001/Ushakov/lab3/publisher/internal/mapper"
	postModel "github.com/Khmelov/Distcomp/351001/Ushakov/lab3/publisher/internal/model"
)

type httpClient interface {
	Creategithub.com
/Khmelov/Distcomp/351001/Ushakov(ctx context.Context, issueID int64, content string) (*postModel.github.com/Khmelov/Distcomp/351001/Ushakov, error)
Getgithub.com/Khmelov/Distcomp/351001/Ushakovs(ctx context.Context) ([]postModel.github.com/Khmelov/Distcomp/351001/Ushakov, error)
Getgithub.com/Khmelov/Distcomp/351001/Ushakov(ctx context.Context, id int64) (*postModel.github.com/Khmelov/Distcomp/351001/Ushakov, error)
Updategithub.com/Khmelov/Distcomp/351001/Ushakov(ctx context.Context, id, issueID int64, content string) (*postModel.github.com/Khmelov/Distcomp/351001/Ushakov, error)
Deletegithub.com/Khmelov/Distcomp/351001/Ushakov(ctx context.Context, id int64) error
}
type service struct {
	client httpClient
}

type github.com/Khmelov/Distcomp/351001/UshakovService interface {
Creategithub.com/Khmelov/Distcomp/351001/Ushakov(ctx context.Context, post postModel.github.com/Khmelov/Distcomp/351001/Ushakov) (postModel.github.com/Khmelov/Distcomp/351001/Ushakov, error)
Getgithub.com/Khmelov/Distcomp/351001/Ushakovs(ctx context.Context) ([]postModel.github.com/Khmelov/Distcomp/351001/Ushakov, error)
Getgithub.com/Khmelov/Distcomp/351001/UshakovByID(ctx context.Context, id int64) (postModel.github.com/Khmelov/Distcomp/351001/Ushakov, error)
Updategithub.com/Khmelov/Distcomp/351001/UshakovByID(ctx context.Context, post postModel.github.com/Khmelov/Distcomp/351001/Ushakov) (postModel.github.com/Khmelov/Distcomp/351001/Ushakov, error)
Deletegithub.com/Khmelov/Distcomp/351001/UshakovByID(ctx context.Context, id int64) error
}

func New(client httpClient) github.com/Khmelov/Distcomp/351001/UshakovService {
return &service{
client: client,
}
}

func (s *service) Creategithub.com/Khmelov/Distcomp/351001/Ushakov(ctx context.Context, post postModel.github.com/Khmelov/Distcomp/351001/Ushakov) (postModel.github.com/Khmelov/Distcomp/351001/Ushakov, error) {
	createdMsg, err := s.client.Creategithub.com / Khmelov / Distcomp / 351001 / Ushakov(ctx, int64(post.IssueID), post.Content)
	if err != nil {
		return postModel.github.com / Khmelov / Distcomp / 351001 / Ushakov{}, err
	}

	log.Println(createdMsg)

	return mapper.MapHTTPgithub.com / Khmelov / Distcomp / 351001 / UshakovToModel(*createdMsg), nil
}

func (s *service) Getgithub.com/Khmelov/Distcomp/351001/Ushakovs(ctx context.Context) ([]postModel.github.com/Khmelov/Distcomp/351001/Ushakov, error) {
	var mappedgithub.com / Khmelov / Distcomp / 351001 / Ushakovs[]
	postModel.github.com / Khmelov / Distcomp / 351001 / Ushakov

	msgs, err := s.client.Getgithub.com / Khmelov / Distcomp / 351001 / Ushakovs(ctx)
	if err != nil {
		return mappedgithub.com / Khmelov / Distcomp / 351001 / Ushakovs, err
	}

	for _, msg := range msgs {
		mappedgithub.com / Khmelov / Distcomp / 351001 / Ushakovs = append(mappedgithub.com/Khmelov/Distcomp/351001/Ushakovs, mapper.MapHTTPgithub.com/Khmelov/Distcomp/351001/UshakovToModel(msg))
	}

	if len(mappedgithub.com/Khmelov/Distcomp/351001/Ushakovs) == 0 {
		return []postModel.github.com / Khmelov / Distcomp / 351001 / Ushakov{}, nil
	}

	return mappedgithub.com / Khmelov / Distcomp / 351001 / Ushakovs, nil
}

func (s *service) Getgithub.com/Khmelov/Distcomp/351001/UshakovByID(ctx context.Context, id int64) (postModel.github.com/Khmelov/Distcomp/351001/Ushakov, error) {
	msg, err := s.client.Getgithub.com / Khmelov / Distcomp / 351001 / Ushakov(ctx, id)
	if err != nil {
		return postModel.github.com / Khmelov / Distcomp / 351001 / Ushakov{}, err
	}

	return mapper.MapHTTPgithub.com / Khmelov / Distcomp / 351001 / UshakovToModel(*msg), nil
}

func (s *service) Updategithub.com/Khmelov/Distcomp/351001/UshakovByID(ctx context.Context, post postModel.github.com/Khmelov/Distcomp/351001/Ushakov) (postModel.github.com/Khmelov/Distcomp/351001/Ushakov, error) {
	updatedMsg, err := s.client.Updategithub.com / Khmelov / Distcomp / 351001 / Ushakov(ctx, int64(post.ID), int64(post.IssueID), post.Content)
	if err != nil {
		return postModel.github.com / Khmelov / Distcomp / 351001 / Ushakov{}, err
	}

	return mapper.MapHTTPgithub.com / Khmelov / Distcomp / 351001 / UshakovToModel(*updatedMsg), nil
}

func (s *service) Deletegithub.com/Khmelov/Distcomp/351001/UshakovByID(ctx context.Context, id int64) error {
	return s.client.Deletegithub.com / Khmelov / Distcomp / 351001 / Ushakov(ctx, id)
}
