package issuedto

import (
	"time"

	stickerdto "lab1/internal/dto/sticker"
	issuemodel "lab1/internal/model/issue"
)

type CreateIssueRequest struct {
	EditorID int64   `json:"editorId" binding:"required"`
	Title    string  `json:"title" binding:"required,min=2,max=64"`
	Content  string  `json:"content" binding:"required,min=4,max=2048"`
	Stickers []int64 `json:"stickers,omitempty"`
}

type UpdateIssueRequest struct {
	ID       int64   `json:"id" binding:"required"`
	EditorID int64   `json:"editorId" binding:"required"`
	Title    *string `json:"title,omitempty" binding:"omitempty,min=2,max=64"`
	Content  *string `json:"content,omitempty" binding:"omitempty,min=4,max=2048"`
	Stickers []int64 `json:"stickers,omitempty"`
}

type IssueResponse struct {
	ID       int64                        `json:"id"`
	EditorID int64                        `json:"editorId"`
	Title    string                       `json:"title"`
	Content  string                       `json:"content"`
	Created  string                       `json:"created"`
	Modified string                       `json:"modified"`
	Stickers []stickerdto.StickerResponse `json:"stickers"`
}

func ToResponse(i *issuemodel.Issue) *IssueResponse {
	if i == nil {
		return nil
	}

	stickersResp := make([]stickerdto.StickerResponse, 0, len(i.Stickers))
	for _, s := range i.Stickers {
		stickerCopy := s
		stickersResp = append(stickersResp, *stickerdto.ToResponse(&stickerCopy))
	}

	return &IssueResponse{
		ID:       i.ID,
		EditorID: i.EditorID,
		Title:    i.Title,
		Content:  i.Content,
		Created:  i.Created.Format(time.RFC3339),
		Modified: i.Modified.Format(time.RFC3339),
		Stickers: stickersResp,
	}
}

func ToResponseList(list []*issuemodel.Issue) []*IssueResponse {
	res := make([]*IssueResponse, len(list))
	for i, v := range list {
		res[i] = ToResponse(v)
	}
	return res
}
