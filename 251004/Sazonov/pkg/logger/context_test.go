package logger

import (
	"context"
	"reflect"
	"testing"
)

func TestFromToContext(t *testing.T) {
	t.Run("Test empty context", func(t *testing.T) {
		if !reflect.DeepEqual(CreateLogger(), fromContext(context.Background())) {
			t.Errorf("failed get logger from empty context")
		}
	})

	t.Run("Test custom logger from context", func(t *testing.T) {
		want := CreateLogger().Named("name")
		ctx := toContext(context.Background(), want)
		if !reflect.DeepEqual(want, fromContext(ctx)) {
			t.Errorf("failed get custom logger from context")
		}
	})
}
