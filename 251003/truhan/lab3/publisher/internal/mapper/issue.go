package mapper

import (
	"time"

	issue "github.com/Khmelov/Distcomp/251003/truhan/lab3/publisher/internal/model"
	"github.com/Khmelov/Distcomp/251003/truhan/lab3/publisher/internal/storage/model"
)

func MapIssueToModel(i issue.Issue) (model.Issue, error) {
	var err error

	modified := time.Time{}
	if i.Modified != "" {
		modified, err = time.Parse(time.RFC3339, i.Modified)
		if err != nil {
			return model.Issue{}, err
		}
	}

	return model.Issue{
		ID:        i.ID,
		CreatorID: i.CreatorID,
		Title:     i.Title,
		Content:   i.Content,
		Modified:  &modified,
		Marks:     i.Marks,
	}, nil
}

func MapModelToIssue(i model.Issue) issue.Issue {
	var modified string

	if i.Modified != nil {
		modified = i.Modified.Format(time.RFC3339)
	}

	return issue.Issue{
		ID:        i.ID,
		CreatorID: i.CreatorID,
		Title:     i.Title,
		Content:   i.Content,
		Created:   i.Created.Format(time.RFC3339),
		Modified:  modified,
	}
}
