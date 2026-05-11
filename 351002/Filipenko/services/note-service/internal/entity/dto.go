package entity

import (
	"encoding/json"
	"time"
)

type AuthorRequestTo struct {
	Login     string `json:"login"`
	Password  string `json:"password"`
	Firstname string `json:"firstname"`
	Lastname  string `json:"lastname"`
	Role      string `json:"role,omitempty"`
}

type AuthorResponseTo struct {
	ID        int64  `json:"id"`
	Login     string `json:"login"`
	Firstname string `json:"firstname"`
	Lastname  string `json:"lastname"`
	Role      string `json:"role,omitempty"`
}

// AuthorRegisterRequestTo — POST /api/v2.0/authors (регистрация).
// Принимает и camelCase (Task361), и нижний регистр firstname/lastname (автотесты платформы).
type AuthorRegisterRequestTo struct {
	Login     string `json:"login"`
	Password  string `json:"password"`
	FirstName string `json:"firstName"`
	LastName  string `json:"lastName"`
	Role      string `json:"role"`
}

func (r *AuthorRegisterRequestTo) UnmarshalJSON(data []byte) error {
	var aux struct {
		Login     string `json:"login"`
		Password  string `json:"password"`
		Role      string `json:"role"`
		FirstName string `json:"firstName"`
		LastName  string `json:"lastName"`
		Firstname string `json:"firstname"`
		Lastname  string `json:"lastname"`
	}
	if err := json.Unmarshal(data, &aux); err != nil {
		return err
	}
	r.Login = aux.Login
	r.Password = aux.Password
	r.Role = aux.Role
	r.FirstName = aux.FirstName
	r.LastName = aux.LastName
	if r.FirstName == "" {
		r.FirstName = aux.Firstname
	}
	if r.LastName == "" {
		r.LastName = aux.Lastname
	}
	return nil
}

type LoginRequestTo struct {
	Login    string `json:"login"`
	Password string `json:"password"`
}

type LoginResponseTo struct {
	AccessToken string `json:"access_token"`
	TypeToken   string `json:"type_token,omitempty"`
}

type MarkerRequestTo struct {
	Name string `json:"name"`
}

type MarkerResponseTo struct {
	ID   int64  `json:"id"`
	Name string `json:"name"`
}

type TopicRequestTo struct {
	AuthorID  int64   `json:"authorId"`
	Title     string  `json:"title"`
	Content   string  `json:"content"`
	MarkerIDs []int64 `json:"markerIds,omitempty"`
}

type TopicResponseTo struct {
	ID        int64     `json:"id"`
	AuthorID  int64     `json:"authorId"`
	Title     string    `json:"title"`
	Content   string    `json:"content"`
	MarkerIDs []int64   `json:"markerIds,omitempty"`
	Created   time.Time `json:"created"`
	Modified  time.Time `json:"modified"`
}

type CommentRequestTo struct {
	ID      int64  `json:"id,omitempty"`
	TopicID int64  `json:"topicId"`
	Country string `json:"country"`
	Content string `json:"content"`
}

type CommentResponseTo struct {
	ID      int64  `json:"id"`
	TopicID int64  `json:"topicId"`
	Country string `json:"country"`
	Content string `json:"content"`
	State   string `json:"state"`
}

type KafkaCommentMessage struct {
	ID            int64  `json:"id"`
	TopicID       int64  `json:"topicId"`
	Country       string `json:"country"`
	Content       string `json:"content"`
	State         string `json:"state"`
	Operation     string `json:"operation"`
	CorrelationID string `json:"correlationId"`
}
