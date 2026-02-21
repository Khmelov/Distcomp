package controller

import (
	"bytes"
	"encoding/json"
	"errors"
	"io"
	"strconv"

	"github.com/gin-gonic/gin"
)

// ParseID конвертирует строковый ID из параметра URL в int64
func ParseID(c *gin.Context) (int64, error) {
	idStr := c.Param("id")
	if idStr == "" {
		return 0, errors.New("id is required in URL path")
	}
	id, err := strconv.ParseInt(idStr, 10, 64)
	if err != nil {
		return 0, errors.New("invalid id format")
	}
	return id, nil
}

// ParseInt64Array конвертирует массив строк в массив int64
func ParseInt64Array(strArray []string) ([]int64, error) {
	result := make([]int64, 0, len(strArray))
	for _, str := range strArray {
		id, err := strconv.ParseInt(str, 10, 64)
		if err != nil {
			return nil, errors.New("invalid markerId format")
		}
		result = append(result, id)
	}
	return result, nil
}

// ValidateNoIDInBody проверяет, что в теле запроса нет поля id
func ValidateNoIDInBody(c *gin.Context) error {
	// Читаем тело запроса
	bodyBytes, err := io.ReadAll(c.Request.Body)
	if err != nil {
		return nil // Если не можем прочитать, пропускаем валидацию
	}
	
	// Восстанавливаем тело для дальнейшего использования
	c.Request.Body = io.NopCloser(bytes.NewBuffer(bodyBytes))
	
	// Проверяем наличие поля id
	var body map[string]interface{}
	if err := json.Unmarshal(bodyBytes, &body); err == nil {
		if _, exists := body["id"]; exists {
			return errors.New("id field is not allowed in request body")
		}
	}
	
	return nil
}

