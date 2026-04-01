package mapper

import (
	postModel "github.com/Khmelov/Distcomp/351001/Ushakov/lab3/publisher/internal/model"
)

func MapHTTPgithub.com/Khmelov/Distcomp/351001/UshakovToModel(msg postModel.github.com/Khmelov/Distcomp/351001/Ushakov) postModel.github.com/Khmelov/Distcomp/351001/Ushakov {
	return postModel.github.com / Khmelov / Distcomp / 351001 / Ushakov{
		ID:      msg.ID,
		IssueID: msg.IssueID,
		Content: msg.Content,
	}
}

func MapModelToHTTPgithub.com/Khmelov/Distcomp/351001/Ushakov(msg postModel.github.com/Khmelov/Distcomp/351001/Ushakov) postModel.github.com/Khmelov/Distcomp/351001/Ushakov {
	return postModel.github.com / Khmelov / Distcomp / 351001 / Ushakov{
		ID:      msg.ID,
		IssueID: msg.IssueID,
		Content: msg.Content,
	}
}
