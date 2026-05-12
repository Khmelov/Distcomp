package entity

type CustomError struct {
	Status  int
	SubCode string
	Message string
}

func (e *CustomError) Error() string {
	return e.Message
}

func NewErr(status int, subCode, message string) error {
	return &CustomError{Status: status, SubCode: subCode, Message: message}
}

type ErrorResponse struct {
	ErrorCode    string `json:"errorCode"`
	ErrorMessage string `json:"errorMessage"`
}
