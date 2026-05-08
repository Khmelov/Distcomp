package logger

import (
	"go.uber.org/zap"
)

func New() (*zap.Logger, error) {
	logger, err := zap.NewProduction()
	if err != nil {
		return nil, err
	}
	return logger, nil
}
