package editordto

import editormodel "labs/shared/model/editorV2"

type CreateEditorRequest struct {
	Login     string `json:"login" binding:"required,min=2,max=64"`
	Password  string `json:"password" binding:"required,min=8,max=128"`
	Firstname string `json:"firstname" binding:"required,min=2,max=64"`
	Lastname  string `json:"lastname" binding:"required,min=2,max=64"`
	Role      string `json:"role" binding:"required,oneof=ADMIN CUSTOMER"`
}

type LoginRequest struct {
	Login    string `json:"login" binding:"required"`
	Password string `json:"password" binding:"required"`
}

type LoginResponse struct {
	AccessToken string `json:"access_token"`
	TypeToken   string `json:"type_token"`
}

type UpdateEditorRequest struct {
	ID        int64   `json:"id" binding:"required"`
	Login     *string `json:"login,omitempty" binding:"omitempty,min=2,max=64"`
	Password  *string `json:"password,omitempty" binding:"omitempty,min=8,max=128"`
	Firstname *string `json:"firstname,omitempty" binding:"omitempty,min=2,max=64"`
	Lastname  *string `json:"lastname,omitempty" binding:"omitempty,min=2,max=64"`
	Role      *string `json:"role,omitempty" binding:"omitempty,oneof=ADMIN CUSTOMER"`
}

type EditorResponse struct {
	ID        int64  `json:"id"`
	Login     string `json:"login"`
	Firstname string `json:"firstname"`
	Lastname  string `json:"lastname"`
	Role      string `json:"role"`
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
		Role:      e.Role,
	}
}

func ToResponseList(list []*editormodel.Editor) []*EditorResponse {
	res := make([]*EditorResponse, len(list))
	for i, v := range list {
		res[i] = ToResponse(v)
	}
	return res
}
