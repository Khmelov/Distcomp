package dto

type ErrorResponse struct {
	ErrorMessage string `json:"errorMessage" example:"User not found"`
	ErrorCode    string `json:"errorCode" example:"40401"`
}
