package entity

type CustomError struct {
	Status  int
	SubCode string
	Message string
}

func (e *CustomError) Error() string  { return e.Message }
func NewErr(s int, c, m string) error { return &CustomError{s, c, m} }

type ErrorResponse struct {
	ErrorCode    string `json:"errorCode"`
	ErrorMessage string `json:"errorMessage"`
}
