FROM golang:1.26-alpine AS builder

WORKDIR /usr/local/src

COPY go.mod go.sum ./
RUN go mod download

COPY . .
RUN go build -o /bin/publisher cmd/publisher/main.go
RUN go build -o /bin/discussion cmd/discussion/main.go

FROM alpine:latest AS publisher

WORKDIR /app
COPY --from=builder /bin/publisher /app/
COPY .env /app/.env
EXPOSE 24110
CMD ["./publisher"]

FROM alpine:latest AS discussion

WORKDIR /app
COPY --from=builder /bin/discussion /app/
COPY .env /app/.env
EXPOSE 24130
CMD ["./discussion"]