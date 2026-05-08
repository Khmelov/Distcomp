package controllers

import (
	"net/http"
	"strconv"

	"github.com/gin-gonic/gin"
)

func respondBadRequest(c *gin.Context, message string) {
	c.JSON(http.StatusBadRequest, gin.H{
		"errorMessage": message,
		"errorCode":    "40000",
	})
}

func parseInt64Param(c *gin.Context, param, label string) (int64, bool) {
	value, err := strconv.ParseInt(c.Param(param), 10, 64)
	if err != nil || value < 1 {
		respondBadRequest(c, "Invalid "+label+" format")
		return 0, false
	}
	return value, true
}

func parsePagination(c *gin.Context) (int, int, bool) {
	limit, err := strconv.Atoi(c.DefaultQuery("limit", "20"))
	if err != nil || limit < 1 {
		respondBadRequest(c, "Invalid limit format")
		return 0, 0, false
	}

	offset, err := strconv.Atoi(c.DefaultQuery("offset", "0"))
	if err != nil || offset < 0 {
		respondBadRequest(c, "Invalid offset format")
		return 0, 0, false
	}

	return limit, offset, true
}
