package stickerdto

import stickermodel "labs/shared/model/sticker"

type CreateStickerRequest struct {
	Name string `json:"name" binding:"required,min=2,max=32"`
}

type UpdateStickerRequest struct {
	Name *string `json:"name,omitempty" binding:"omitempty,min=2,max=32"`
}

type StickerResponse struct {
	ID   int64  `json:"id"`
	Name string `json:"name"`
}

func ToResponse(s *stickermodel.Sticker) *StickerResponse {
	if s == nil {
		return nil
	}
	return &StickerResponse{
		ID:   s.ID,
		Name: s.Name,
	}
}

func ToResponseList(list []*stickermodel.Sticker) []*StickerResponse {
	res := make([]*StickerResponse, len(list))
	for i, v := range list {
		res[i] = ToResponse(v)
	}
	return res
}
