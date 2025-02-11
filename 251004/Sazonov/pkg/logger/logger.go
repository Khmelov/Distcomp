package logger

import (
	"context"

	"go.uber.org/zap"
)

func Debug(ctx context.Context, msg string, fields ...zap.Field) {
	fromContext(ctx).Debug(msg, fields...)
}

func Info(ctx context.Context, msg string, fields ...zap.Field) {
	fromContext(ctx).Info(msg, fields...)
}

func Warn(ctx context.Context, msg string, fields ...zap.Field) {
	fromContext(ctx).Warn(msg, fields...)
}

func Error(ctx context.Context, msg string, fields ...zap.Field) {
	fromContext(ctx).Error(msg, fields...)
}
