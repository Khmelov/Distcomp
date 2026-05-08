package validator

import (
	"sync"

	"github.com/go-playground/validator/v10"
)

var (
	v    *validator.Validate
	once sync.Once
)

func Validtor() *validator.Validate {
	once.Do(func() {
		v = validator.New(
			validator.WithRequiredStructEnabled(),
			validator.WithPrivateFieldValidation(),
		)
	})

	return v
}
