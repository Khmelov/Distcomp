-include .env
export

LDFLAGS := -s -w


PROJECT_DIR := .


APP_NAME ?= note_service


TEST_DIR := $(PROJECT_DIR)/test


all: init-utils mod lint build


.PHONY: init-utils
init-utils:
	@echo "Installing tools..."
	go install golang.org/x/tools/cmd/goimports@latest
	go install github.com/golang/mock/mockgen@v1.6.0
	go install github.com/cweill/gotests/gotests@latest
	go install github.com/golangci/golangci-lint/cmd/golangci-lint@v1.61.0
	go install github.com/pressly/goose/v3/cmd/goose@latest
	go install github.com/swaggo/swag/cmd/swag@latest
	go install github.com/air-verse/air@latest


.PHONY: mod
mod:
	@echo "Updating dependencies..."
	go mod tidy -v


.PHONY: mod-update
mod-update:
	@echo "Updating dependencies..."
	go get -u ./...


.PHONY: lint
lint:
	@echo "Checking code for errors..."
	golangci-lint run ./...


.PHONY: build
build:
	@echo "Build application..."
	go build -ldflags="$(LDFLAGS)" -buildvcs=false -o ./tmp/main ./cmd/$(APP_NAME)/main.go


.PHONY: run
run:
	@echo "Running application with Air..."
	air


.PHONY: run-dev
run-dev:
	@echo "Running application in dev mode..."
	go run ./cmd/$(APP_NAME)/main.go


.PHONY: install-docs
install-docs:
	@echo "Generating Swagger documentation..."
	swag init -g ./cmd/$(APP_NAME)/main.go -o ./docs


.PHONY: test
test:
	@echo "Running tests..."
	go test -v $(TEST_DIR)/...


.PHONY: test-unit
test-unit:
	@echo "Running unit tests..."
	go test -tags=unit -v $(TEST_DIR)/unit/...


.PHONY: test-integ
test-integ:
	@echo "Running integration tests..."
	go test -tags=integration -v $(TEST_DIR)/integration/...


.PHONY: test-race
test-race:
	@echo "Running race condition tests..."
	go test -v -race $(TEST_DIR)/...


.PHONY: test-cover
test-cover:
	@echo "Running tests with coverage profiling..."
	go test -coverprofile=coverage.out ./...
	go tool cover -func=coverage.out
	go tool cover -html=coverage.out