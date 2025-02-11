package logger

import (
	"context"

	"go.uber.org/zap"
)

type loggerContextKey struct{}

func toContext(ctx context.Context, logger *zap.Logger) context.Context {
	return context.WithValue(ctx, loggerContextKey{}, logger)
}

func fromContext(ctx context.Context) *zap.Logger {
	if logger, ok := ctx.Value(loggerContextKey{}).(*zap.Logger); ok {
		return logger
	}

	return CreateLogger()
}
