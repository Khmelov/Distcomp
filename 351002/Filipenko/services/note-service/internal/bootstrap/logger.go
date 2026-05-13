package bootstrap

import (
	"go.uber.org/zap"
	"go.uber.org/zap/zapcore"
	"os"
)

func newLogger() *zap.Logger {
	encoderConfig := zapcore.EncoderConfig{
		TimeKey:        "time",
		LevelKey:       "level",
		MessageKey:     "message",
		CallerKey:      "caller",
		EncodeLevel:    zapcore.CapitalColorLevelEncoder,
		EncodeTime:     zapcore.ISO8601TimeEncoder,
		EncodeCaller:   zapcore.ShortCallerEncoder,
		EncodeDuration: zapcore.StringDurationEncoder,
	}

	consoleEncoder := zapcore.NewConsoleEncoder(encoderConfig)
	logWriter := zapcore.Lock(os.Stdout)

	core := zapcore.NewCore(consoleEncoder, logWriter, zapcore.DebugLevel)

	logger := zap.New(core, zap.AddCaller(), zap.AddCallerSkip(1))

	return logger
}
