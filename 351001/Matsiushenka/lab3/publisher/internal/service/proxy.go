package service

import (
	"bytes"
	"io"
	"net/http"
)

func ProxyToDiscussion(method string, path string, body io.ReadCloser) ([]byte, int, error) {
	url := "http://localhost:24130/api/v1.0/notes" + path

	var bodyBytes []byte
	if body != nil {
		bodyBytes, _ = io.ReadAll(body)
	}

	req, err := http.NewRequest(method, url, bytes.NewBuffer(bodyBytes))
	if err != nil {
		return nil, 500, err
	}

	req.Header.Set("Content-Type", "application/json")

	client := &http.Client{}
	resp, err := client.Do(req)
	if err != nil {
		return nil, 500, err
	}
	defer resp.Body.Close()

	respData, _ := io.ReadAll(resp.Body)
	return respData, resp.StatusCode, nil
}
