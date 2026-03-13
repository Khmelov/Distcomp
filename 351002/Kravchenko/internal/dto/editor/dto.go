package editordto

import editormodel "lab1/internal/model/editor"

type CreateEditorRequest struct {
	Login     string `json:"login" binding:"required,min=2,max=64"`
	Password  string `json:"password" binding:"required,min=8,max=128"`
	Firstname string `json:"firstname" binding:"required,min=2,max=64"`
	Lastname  string `json:"lastname" binding:"required,min=2,max=64"`
}

type UpdateEditorRequest struct {
	ID        int64   `json:"id" binding:"required"`
	Login     *string `json:"login,omitempty" binding:"omitempty,min=2,max=64"`
	Password  *string `json:"password,omitempty" binding:"omitempty,min=8,max=128"`
	Firstname *string `json:"firstname,omitempty" binding:"omitempty,min=2,max=64"`
	Lastname  *string `json:"lastname,omitempty" binding:"omitempty,min=2,max=64"`
}

type EditorResponse struct {
	ID        int64  `json:"id"`
	Login     string `json:"login"`
	Firstname string `json:"firstname"`
	Lastname  string `json:"lastname"`
}

func ToResponse(e *editormodel.Editor) *EditorResponse {
	if e == nil {
		return nil
	}
	return &EditorResponse{
		ID:        e.ID,
		Login:     e.Login,
		Firstname: e.Firstname,
		Lastname:  e.Lastname,
	}
}

func ToResponseList(list []*editormodel.Editor) []*EditorResponse {
	res := make([]*EditorResponse, len(list))
	for i, v := range list {
		res[i] = ToResponse(v)
	}
	return res
}
