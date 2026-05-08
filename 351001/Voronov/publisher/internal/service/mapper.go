package service

import (
	"publisher/internal/model"
	"publisher/internal/transport/dto/request"
	"publisher/internal/transport/dto/response"
)

type MapperImpl struct{}

func NewMapper() MapperImpl {
	return MapperImpl{}
}

func (m MapperImpl) ToUserResponse(user *model.User) *response.UserResponseTo {
	if user == nil {
		return nil
	}
	return &response.UserResponseTo{
		ID:        user.ID,
		Login:     user.Login,
		Firstname: user.Firstname,
		Lastname:  user.Lastname,
	}
}

func (m MapperImpl) ToUserModel(req *request.UserRequestTo) *model.User {
	return &model.User{
		Login:     req.Login,
		Password:  req.Password,
		Firstname: req.Firstname,
		Lastname:  req.Lastname,
	}
}

func (m MapperImpl) ToIssueResponse(issue *model.Issue) *response.IssueResponseTo {
	if issue == nil {
		return nil
	}
	labels := make([]response.LabelResponseTo, 0, len(issue.Labels))
	for _, l := range issue.Labels {
		labels = append(labels, response.LabelResponseTo{
			ID:   l.ID,
			Name: l.Name,
		})
	}
	var userResp response.UserResponseTo
	if issue.User != nil {
		userResp = response.UserResponseTo{
			ID:        issue.User.ID,
			Login:     issue.User.Login,
			Firstname: issue.User.Firstname,
			Lastname:  issue.User.Lastname,
		}
	}
	return &response.IssueResponseTo{
		ID:       issue.ID,
		UserID:   issue.UserID,
		User:     userResp,
		Title:    issue.Title,
		Content:  issue.Content,
		Created:  issue.Created,
		Modified: issue.Modified,
		Labels:   labels,
	}
}

func (m MapperImpl) ToIssueModel(req *request.IssueRequestTo) *model.Issue {
	return &model.Issue{
		UserID:  req.UserID,
		Title:   req.Title,
		Content: req.Content,
	}
}

func (m MapperImpl) ToLabelResponse(label *model.Label) *response.LabelResponseTo {
	if label == nil {
		return nil
	}
	return &response.LabelResponseTo{
		ID:   label.ID,
		Name: label.Name,
	}
}

func (m MapperImpl) ToLabelModel(req *request.LabelRequestTo) *model.Label {
	return &model.Label{
		Name: req.Name,
	}
}

func (m MapperImpl) ToReactionResponse(reaction *model.Reaction) *response.ReactionResponseTo {
	if reaction == nil {
		return nil
	}
	return &response.ReactionResponseTo{
		ID:      reaction.ID,
		IssueID: reaction.IssueID,
		Content: reaction.Content,
	}
}

func (m MapperImpl) ToReactionModel(req *request.ReactionRequestTo) *model.Reaction {
	return &model.Reaction{
		IssueID: req.IssueID,
		Content: req.Content,
	}
}
