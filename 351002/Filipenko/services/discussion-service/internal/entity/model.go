package entity

type Comment struct {
	ID      int64  `cql:"id"`
	TopicID int64  `cql:"topic_id"`
	Country string `cql:"country"`
	Content string `cql:"content"`
	State   string `cql:"state"`
}
