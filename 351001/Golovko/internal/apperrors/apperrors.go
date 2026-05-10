package apperrors

type ErrorResponse struct {
	ErrorMessage string `json:"errorMessage"`
	ErrorCode    int    `json:"errorCode"`
}

func New(message string, code int) ErrorResponse {
	return ErrorResponse{
		ErrorMessage: message,
		ErrorCode:    code,
	}
}

const (
	CodeBadRequest          = 40001
	CodeValidationFailed    = 40002
	CodeNotFound            = 40401
	CodeInternalServerError = 50001
)
