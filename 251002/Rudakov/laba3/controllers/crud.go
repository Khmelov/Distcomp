package controllers

import (
	db "laba3/config"
	models "laba3/models"
)

func getMessageByID(id int) (*models.Message, error) {

	var message models.Message

	// var country string
	// var storyId int64

	err := db.Session.Query(`SELECT country, storyId FROM tbl_message WHERE id = ? ALLOW FILTERING;`, id).Scan(&message.Country,
		&message.StoryID)

	if err != nil {
		return &message, err
	}

	// message.Country = country
	// message.StoryID = storyId

	return &message, nil

}
