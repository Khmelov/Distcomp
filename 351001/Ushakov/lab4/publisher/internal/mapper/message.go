package mapper

import (
	messageModel "github.com/Khmelov/Distcomp/351001/Ushakov/labX/publisher/internal/model"
)

func MapHTTPPostToModel(msg messageModel.Post) messageModel.Post {
	return messageModel.Post{
		ID:      msg.ID,
		IssueID: msg.IssueID,
		Content: msg.Content,
	}
}

func MapModelToHTTPPost(msg messageModel.Post) messageModel.Post {
	return messageModel.Post{
		ID:      msg.ID,
		IssueID: msg.IssueID,
		Content: msg.Content,
	}
}
