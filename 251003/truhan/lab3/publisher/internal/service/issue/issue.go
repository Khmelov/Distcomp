package issue

import (
	"context"
	"log"

	"github.com/Khmelov/Distcomp/251003/truhan/lab3/publisher/internal/mapper"
	issueModel "github.com/Khmelov/Distcomp/251003/truhan/lab3/publisher/internal/model"
	dbIssueModel "github.com/Khmelov/Distcomp/251003/truhan/lab3/publisher/internal/storage/model"
)

type service struct {
	db IssueDB
}

type IssueDB interface {
	CreateIssue(ctx context.Context, issue dbIssueModel.Issue) (dbIssueModel.Issue, error)
	GetIssues(ctx context.Context) ([]dbIssueModel.Issue, error)
	GetIssueByID(ctx context.Context, id int64) (dbIssueModel.Issue, error)
	UpdateIssueByID(ctx context.Context, issue dbIssueModel.Issue) (dbIssueModel.Issue, error)
	DeleteIssueByID(ctx context.Context, id int64) error
}

type IssueService interface {
	CreateIssue(ctx context.Context, issue issueModel.Issue) (issueModel.Issue, error)
	GetIssues(ctx context.Context) ([]issueModel.Issue, error)
	GetIssueByID(ctx context.Context, id int64) (issueModel.Issue, error)
	UpdateIssueByID(ctx context.Context, issue issueModel.Issue) (issueModel.Issue, error)
	DeleteIssueByID(ctx context.Context, id int64) error
}

func New(db IssueDB) IssueService {
	return &service{
		db: db,
	}
}

func (s *service) CreateIssue(ctx context.Context, issue issueModel.Issue) (issueModel.Issue, error) {
	mappedIssue, err := mapper.MapIssueToModel(issue)
	if err != nil {
		log.Println(err)
		return issueModel.Issue{}, err
	}

	mappedData, err := s.db.CreateIssue(ctx, mappedIssue)
	if err != nil {
		return issueModel.Issue{}, err
	}

	return mapper.MapModelToIssue(mappedData), nil
}

func (s *service) GetIssues(ctx context.Context) ([]issueModel.Issue, error) {
	issues, err := s.db.GetIssues(ctx)
	if err != nil {
		return nil, err
	}

	var mappedIssues []issueModel.Issue

	for _, issue := range issues {
		mappedIssues = append(mappedIssues, mapper.MapModelToIssue(issue))
	}

	if len(mappedIssues) == 0 {
		mappedIssues = []issueModel.Issue{}
	}

	return mappedIssues, nil
}

func (s *service) GetIssueByID(ctx context.Context, id int64) (issueModel.Issue, error) {
	issue, err := s.db.GetIssueByID(ctx, id)
	if err != nil {
		return issueModel.Issue{}, err
	}

	return mapper.MapModelToIssue(issue), nil
}

func (s *service) UpdateIssueByID(ctx context.Context, issue issueModel.Issue) (issueModel.Issue, error) {
	mappedIssue, err := mapper.MapIssueToModel(issue)
	if err != nil {
		return issueModel.Issue{}, err
	}

	updatedIssue, err := s.db.UpdateIssueByID(ctx, mappedIssue)
	if err != nil {
		return issueModel.Issue{}, err
	}

	return mapper.MapModelToIssue(updatedIssue), nil
}

func (s *service) DeleteIssueByID(ctx context.Context, id int64) error {
	return s.db.DeleteIssueByID(ctx, id)
}
