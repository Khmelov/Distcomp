package httperrors

const (
	ErrCodeInvalidRequestBody = 40001
	ErrCodeInvalidID          = 40002
	ErrCodeInvalidParam       = 40003
	ErrCodeMissingID          = 40004
	ErrCodeInvalidField       = 40005
	ErrCodeNotFound           = 40401
	ErrCodeConflict           = 40901
	ErrCodeInternalError      = 50001
)

const (
	ErrMessageInvalidRequestBody = "Invalid request body"
	ErrMessageInvalidID          = "Invalid ID"
	ErrMessageInvalidParam       = "Invalid parameter"
	ErrMessageMissingID          = "ID is required"
	ErrMessageInvalidField       = "Invalid field"
	ErrMessageNotFound           = "Resource not found"
	ErrMessageConflict           = "Resource already exists"
	ErrMessageInternalError      = "Internal server error"
)

type ErrorResponse struct {
	ErrorMessage string `json:"errorMessage"`
	ErrorCode    int    `json:"errorCode"`
}
