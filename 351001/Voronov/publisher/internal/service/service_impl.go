package service

import (
	"context"
	"time"

	"publisher/internal/errors"
	"publisher/internal/gateway"
	"publisher/internal/model"
	"publisher/internal/repository"
	"publisher/internal/transport/dto/request"
	"publisher/internal/transport/dto/response"

	"go.uber.org/zap"
)

type userServiceImpl struct {
	repo   repository.UserRepository
	mapper Mapper
}

func NewUserService(repo repository.UserRepository, mapper Mapper) UserService {
	return &userServiceImpl{repo: repo, mapper: mapper}
}

func (s *userServiceImpl) FindByID(ctx context.Context, id int64) (*response.UserResponseTo, error) {
	user, err := s.repo.FindByID(ctx, id)
	if err != nil {
		return nil, err
	}
	return s.mapper.ToUserResponse(user), nil
}

func (s *userServiceImpl) FindAll(ctx context.Context) ([]*response.UserResponseTo, error) {
	users, _, err := s.repo.FindAll(ctx, repository.NewQueryOptions())
	if err != nil {
		return nil, err
	}
	result := make([]*response.UserResponseTo, 0, len(users))
	for _, u := range users {
		result = append(result, s.mapper.ToUserResponse(u))
	}
	return result, nil
}

func (s *userServiceImpl) Create(ctx context.Context, req *request.UserRequestTo) (*response.UserResponseTo, error) {
	if len(req.Login) < 2 || len(req.Login) > 64 {
		return nil, errors.ErrBadRequest
	}
	if len(req.Password) < 8 || len(req.Password) > 128 {
		return nil, errors.ErrBadRequest
	}
	if len(req.Firstname) < 2 || len(req.Firstname) > 64 {
		return nil, errors.ErrBadRequest
	}
	if len(req.Lastname) < 2 || len(req.Lastname) > 64 {
		return nil, errors.ErrBadRequest
	}
	created, err := s.repo.Create(ctx, s.mapper.ToUserModel(req))
	if err != nil {
		return nil, errors.FromDBError(err)
	}
	return s.mapper.ToUserResponse(created), nil
}

func (s *userServiceImpl) Update(ctx context.Context, id int64, req *request.UserRequestTo) (*response.UserResponseTo, error) {
	if len(req.Login) < 2 || len(req.Login) > 64 {
		return nil, errors.ErrBadRequest
	}
	if len(req.Password) < 8 || len(req.Password) > 128 {
		return nil, errors.ErrBadRequest
	}
	if len(req.Firstname) < 2 || len(req.Firstname) > 64 {
		return nil, errors.ErrBadRequest
	}
	if len(req.Lastname) < 2 || len(req.Lastname) > 64 {
		return nil, errors.ErrBadRequest
	}
	updated, err := s.repo.Update(ctx, id, s.mapper.ToUserModel(req))
	if err != nil {
		return nil, errors.FromDBError(err)
	}
	return s.mapper.ToUserResponse(updated), nil
}

func (s *userServiceImpl) Delete(ctx context.Context, id int64) error {
	return s.repo.Delete(ctx, id)
}

type issueServiceImpl struct {
	issueRepo       repository.IssueRepository
	userRepo        repository.UserRepository
	labelRepo       repository.LabelRepository
	reactionSvc     ReactionService
	mapper          Mapper
}

func NewIssueService(
	issueRepo repository.IssueRepository,
	userRepo repository.UserRepository,
	labelRepo repository.LabelRepository,
	reactionSvc ReactionService,
	mapper Mapper,
) IssueService {
	return &issueServiceImpl{
		issueRepo:   issueRepo,
		userRepo:    userRepo,
		labelRepo:   labelRepo,
		reactionSvc: reactionSvc,
		mapper:      mapper,
	}
}

func (s *issueServiceImpl) FindByID(ctx context.Context, id int64) (*response.IssueResponseTo, error) {
	issue, err := s.issueRepo.FindByID(ctx, id)
	if err != nil {
		return nil, err
	}
	if user, err := s.userRepo.FindByID(ctx, issue.UserID); err == nil {
		issue.User = user
	}
	return s.mapper.ToIssueResponse(issue), nil
}

func (s *issueServiceImpl) FindAll(ctx context.Context) ([]*response.IssueResponseTo, error) {
	issues, _, err := s.issueRepo.FindAll(ctx, repository.NewQueryOptions())
	if err != nil {
		return nil, err
	}
	result := make([]*response.IssueResponseTo, 0, len(issues))
	for _, i := range issues {
		if user, err := s.userRepo.FindByID(ctx, i.UserID); err == nil {
			i.User = user
		}
		result = append(result, s.mapper.ToIssueResponse(i))
	}
	return result, nil
}

func (s *issueServiceImpl) Create(ctx context.Context, req *request.IssueRequestTo) (*response.IssueResponseTo, error) {
	if req.UserID == 0 {
		return nil, errors.ErrBadRequest
	}
	if len(req.Title) < 2 || len(req.Title) > 64 {
		return nil, errors.ErrBadRequest
	}
	if len(req.Content) < 4 || len(req.Content) > 2048 {
		return nil, errors.ErrBadRequest
	}
	if _, err := s.userRepo.FindByID(ctx, req.UserID); err != nil {
		return nil, errors.ErrBadRequest
	}

	issue := s.mapper.ToIssueModel(req)
	issue.Created = time.Now().UTC()
	issue.Modified = time.Now().UTC()

	created, err := s.issueRepo.Create(ctx, issue)
	if err != nil {
		return nil, errors.FromDBError(err)
	}

	s.attachLabels(ctx, created, req.Labels)

	if user, err := s.userRepo.FindByID(ctx, created.UserID); err == nil {
		created.User = user
	}
	return s.mapper.ToIssueResponse(created), nil
}

func (s *issueServiceImpl) Update(ctx context.Context, id int64, req *request.IssueRequestTo) (*response.IssueResponseTo, error) {
	if req.UserID == 0 {
		return nil, errors.ErrBadRequest
	}
	if len(req.Title) < 2 || len(req.Title) > 64 {
		return nil, errors.ErrBadRequest
	}
	if len(req.Content) < 4 || len(req.Content) > 2048 {
		return nil, errors.ErrBadRequest
	}

	issue := s.mapper.ToIssueModel(req)
	issue.Modified = time.Now().UTC()

	updated, err := s.issueRepo.Update(ctx, id, issue)
	if err != nil {
		return nil, errors.FromDBError(err)
	}

	if len(req.Labels) > 0 {
		s.attachLabels(ctx, updated, req.Labels)
	}

	if user, err := s.userRepo.FindByID(ctx, updated.UserID); err == nil {
		updated.User = user
	}
	return s.mapper.ToIssueResponse(updated), nil
}

func (s *issueServiceImpl) Delete(ctx context.Context, id int64) error {
	labels, _ := s.labelRepo.FindByIssueID(ctx, id)

	if err := s.issueRepo.Delete(ctx, id); err != nil {
		return err
	}

	for _, label := range labels {
		others, _ := s.labelRepo.FindIssuesByLabelID(ctx, label.ID)
		if len(others) == 0 {
			_ = s.labelRepo.Delete(ctx, label.ID)
		}
	}
	return nil
}

func (s *issueServiceImpl) FindByUserID(ctx context.Context, issueID int64) (*response.UserResponseTo, error) {
	issue, err := s.issueRepo.FindByID(ctx, issueID)
	if err != nil {
		return nil, err
	}
	user, err := s.userRepo.FindByID(ctx, issue.UserID)
	if err != nil {
		return nil, err
	}
	return s.mapper.ToUserResponse(user), nil
}

func (s *issueServiceImpl) FindByIssueID(ctx context.Context, issueID int64) ([]*response.LabelResponseTo, []*response.ReactionResponseTo, error) {
	if _, err := s.issueRepo.FindByID(ctx, issueID); err != nil {
		return nil, nil, err
	}

	labels, err := s.labelRepo.FindByIssueID(ctx, issueID)
	if err != nil {
		return nil, nil, err
	}
	labelResults := make([]*response.LabelResponseTo, 0, len(labels))
	for _, l := range labels {
		labelResults = append(labelResults, s.mapper.ToLabelResponse(l))
	}

	reactions, err := s.reactionSvc.FindByIssueID(ctx, issueID)
	if err != nil {
		return nil, nil, err
	}
	reactionResults := make([]*response.ReactionResponseTo, 0, len(reactions))
	for _, r := range reactions {
		reactionResults = append(reactionResults, r)
	}

	return labelResults, reactionResults, nil
}

func (s *issueServiceImpl) SearchIssues(ctx context.Context, labelNames []string, labelIDs []int64, userLogin, title, content string) ([]*response.IssueResponseTo, error) {
	issues, _, err := s.issueRepo.FindAll(ctx, repository.NewQueryOptions())
	if err != nil {
		return nil, err
	}

	results := make([]*response.IssueResponseTo, 0)
	for _, issue := range issues {
		if title != "" && issue.Title != title {
			continue
		}
		if content != "" && issue.Content != content {
			continue
		}
		if userLogin != "" {
			user, err := s.userRepo.FindByID(ctx, issue.UserID)
			if err != nil || user.Login != userLogin {
				continue
			}
		}
		if user, err := s.userRepo.FindByID(ctx, issue.UserID); err == nil {
			issue.User = user
		}
		results = append(results, s.mapper.ToIssueResponse(issue))
	}
	return results, nil
}

func (s *issueServiceImpl) attachLabels(ctx context.Context, issue *model.Issue, names []string) {
	for _, name := range names {
		if len(name) < 2 || len(name) > 32 {
			continue
		}
		label, err := s.labelRepo.Create(ctx, &model.Label{Name: name})
		if err != nil {
			existing, findErr := s.labelRepo.FindByName(ctx, name)
			if findErr != nil {
				continue
			}
			label = existing
		}
		_ = s.labelRepo.AddLabelToIssue(ctx, issue.ID, label.ID)
		issue.Labels = append(issue.Labels, label)
	}
}

type labelServiceImpl struct {
	repo   repository.LabelRepository
	mapper Mapper
}

func NewLabelService(repo repository.LabelRepository, mapper Mapper) LabelService {
	return &labelServiceImpl{repo: repo, mapper: mapper}
}

func (s *labelServiceImpl) FindByID(ctx context.Context, id int64) (*response.LabelResponseTo, error) {
	label, err := s.repo.FindByID(ctx, id)
	if err != nil {
		return nil, err
	}
	return s.mapper.ToLabelResponse(label), nil
}

func (s *labelServiceImpl) FindAll(ctx context.Context) ([]*response.LabelResponseTo, error) {
	labels, _, err := s.repo.FindAll(ctx, repository.NewQueryOptions())
	if err != nil {
		return nil, err
	}
	result := make([]*response.LabelResponseTo, 0, len(labels))
	for _, l := range labels {
		result = append(result, s.mapper.ToLabelResponse(l))
	}
	return result, nil
}

func (s *labelServiceImpl) Create(ctx context.Context, req *request.LabelRequestTo) (*response.LabelResponseTo, error) {
	if len(req.Name) < 2 || len(req.Name) > 32 {
		return nil, errors.ErrBadRequest
	}
	created, err := s.repo.Create(ctx, s.mapper.ToLabelModel(req))
	if err != nil {
		return nil, errors.FromDBError(err)
	}
	return s.mapper.ToLabelResponse(created), nil
}

func (s *labelServiceImpl) Update(ctx context.Context, id int64, req *request.LabelRequestTo) (*response.LabelResponseTo, error) {
	if len(req.Name) < 2 || len(req.Name) > 32 {
		return nil, errors.ErrBadRequest
	}
	updated, err := s.repo.Update(ctx, id, s.mapper.ToLabelModel(req))
	if err != nil {
		return nil, errors.FromDBError(err)
	}
	return s.mapper.ToLabelResponse(updated), nil
}

func (s *labelServiceImpl) Delete(ctx context.Context, id int64) error {
	return s.repo.Delete(ctx, id)
}

type reactionGatewayService struct {
	client    gateway.ReactionGateway
	issueRepo repository.IssueRepository
	producer  kafkaProducerPort
	logger    *zap.Logger
}

type kafkaProducerPort interface {
	Publish(ctx context.Context, r *model.Reaction) error
}

func NewReactionService(
	client gateway.ReactionGateway,
	issueRepo repository.IssueRepository,
	producer kafkaProducerPort,
	logger *zap.Logger,
) ReactionService {
	return &reactionGatewayService{
		client:    client,
		issueRepo: issueRepo,
		producer:  producer,
		logger:    logger,
	}
}

func (s *reactionGatewayService) FindByID(ctx context.Context, id int64) (*response.ReactionResponseTo, error) {
	return s.client.FindByID(ctx, id)
}

func (s *reactionGatewayService) FindAll(ctx context.Context) ([]*response.ReactionResponseTo, error) {
	return s.client.FindAll(ctx)
}

func (s *reactionGatewayService) FindByIssueID(ctx context.Context, issueID int64) ([]*response.ReactionResponseTo, error) {
	return s.client.FindByIssueID(ctx, issueID)
}

func (s *reactionGatewayService) Create(ctx context.Context, req *request.ReactionRequestTo) (*response.ReactionResponseTo, error) {
	if req.IssueID == 0 {
		return nil, errors.ErrBadRequest
	}
	if len(req.Content) < 2 || len(req.Content) > 2048 {
		return nil, errors.ErrBadRequest
	}
	if _, err := s.issueRepo.FindByID(ctx, req.IssueID); err != nil {
		return nil, errors.ErrBadRequest
	}
	resp, err := s.client.Create(ctx, req)
	if err != nil {
		return nil, err
	}
	resp.State = model.ReactionStatePending
	r := &model.Reaction{
		ID:      resp.ID,
		IssueID: resp.IssueID,
		Content: resp.Content,
		State:   model.ReactionStatePending,
	}
	if pubErr := s.producer.Publish(ctx, r); pubErr != nil {
		s.logger.Error("kafka publish failed on create", zap.Error(pubErr), zap.Int64("id", r.ID))
	}
	return resp, nil
}

func (s *reactionGatewayService) Update(ctx context.Context, id int64, req *request.ReactionRequestTo) (*response.ReactionResponseTo, error) {
	if req.IssueID != 0 {
		if _, err := s.issueRepo.FindByID(ctx, req.IssueID); err != nil {
			return nil, errors.ErrBadRequest
		}
	}
	resp, err := s.client.Update(ctx, id, req)
	if err != nil {
		return nil, err
	}
	resp.State = model.ReactionStatePending
	r := &model.Reaction{
		ID:      resp.ID,
		IssueID: resp.IssueID,
		Content: resp.Content,
		State:   model.ReactionStatePending,
	}
	if pubErr := s.producer.Publish(ctx, r); pubErr != nil {
		s.logger.Error("kafka publish failed on update", zap.Error(pubErr), zap.Int64("id", r.ID))
	}
	return resp, nil
}

func (s *reactionGatewayService) Delete(ctx context.Context, id int64) error {
	return s.client.Delete(ctx, id)
}
