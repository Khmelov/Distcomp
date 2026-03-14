package mapper

import (
	postModel "github.com/Khmelov/Distcomp/351001/Ushakov/lab3/publisher/internal/model"
)

func MapHTTPPostToModel(msg postModel.Post) postModel.Post {
	return postModel.Post{
		ID:      msg.ID,
		IssueID: msg.IssueID,
		Content: msg.Content,
	}
}

func MapModelToHTTPPost(msg postModel.Post) postModel.Post {
	return postModel.Post{
		ID:      msg.ID,
		IssueID: msg.IssueID,
		Content: msg.Content,
	}
}
