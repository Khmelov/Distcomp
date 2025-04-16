package mapper

import (
	messageModel "github.com/Khmelov/Distcomp/251003/truhan/lab3/publisher/internal/model"
)

func MapHTTPMessageToModel(msg messageModel.Message) messageModel.Message {
	return messageModel.Message{
		ID:      msg.ID,
		IssueID: msg.IssueID,
		Content: msg.Content,
	}
}

func MapModelToHTTPMessage(msg messageModel.Message) messageModel.Message {
	return messageModel.Message{
		ID:      msg.ID,
		IssueID: msg.IssueID,
		Content: msg.Content,
	}
}
