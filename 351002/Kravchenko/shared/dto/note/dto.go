package notedto

import notemodel "labs/shared/model/note"

type CreateNoteRequest struct {
	ID      int64  `json:"id"`
	IssueID int64  `json:"issueId" binding:"required"`
	Content string `json:"content" binding:"required,min=2,max=2048"`
}

type UpdateNoteRequest struct {
	Content *string `json:"content,omitempty" binding:"omitempty,min=2,max=2048"`
}

type NoteResponse struct {
	ID      int64  `json:"id"`
	IssueID int64  `json:"issueId"`
	Content string `json:"content"`
	State   string `json:"state"`
}

func ToResponse(n *notemodel.Note) *NoteResponse {
	if n == nil {
		return nil
	}
	return &NoteResponse{
		ID:      n.ID,
		IssueID: n.IssueID,
		Content: n.Content,
		State:   n.State,
	}
}

func ToResponseList(list []*notemodel.Note) []*NoteResponse {
	res := make([]*NoteResponse, len(list))
	for i, v := range list {
		res[i] = ToResponse(v)
	}
	return res
}
