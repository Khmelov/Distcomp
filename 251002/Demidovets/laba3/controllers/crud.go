package controllers

import (
	db "laba3/config"
	models "laba3/models"
)

func getCommentByID(id int) (*models.Comment, error) {

	var comment models.Comment

	// var country string
	// var newsId int64

	err := db.Session.Query(`SELECT country, newsId FROM tbl_comment WHERE id = ? ALLOW FILTERING;`, id).Scan(&comment.Country,
		&comment.NewsID)

	if err != nil {
		return &comment, err
	}

	// comment.Country = country
	// comment.NewsID = newsId

	return &comment, nil

}
