FROM golang:1.26-alpine AS builder

WORKDIR /usr/local/src

COPY go.mod go.sum ./
RUN go mod download

COPY . .
RUN go build -o ./bin/app cmd/app/main.go

FROM alpine:latest

WORKDIR /app

COPY --from=builder /usr/local/src/bin/app /app/
COPY --from=builder /usr/local/src/.env /app/.env

EXPOSE 24110

CMD ["./app"]