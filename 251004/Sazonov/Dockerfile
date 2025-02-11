ARG GO_VERSION=1.23.0
FROM golang:${GO_VERSION}-alpine AS build

WORKDIR /src
RUN --mount=type=cache,target=/go/pkg/mod/ \
  --mount=type=bind,source=go.mod,target=go.mod \
  --mount=type=bind,source=go.sum,target=go.sum \
  go mod download -x

RUN --mount=type=cache,target=/go/pkg/mod/ \
  --mount=type=bind,target=. \
  CGO_ENABLED=0 go build -o /bin/app ./cmd/api/.

FROM alpine:3.20.0 AS development

ARG UID=10001
RUN adduser -H -D --uid "${UID}" app
USER app

COPY --from=build --chown=app:app /bin/app /bin/

EXPOSE 24110

ENTRYPOINT [ "/bin/app" ]
