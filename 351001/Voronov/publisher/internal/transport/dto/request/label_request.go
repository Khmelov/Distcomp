package request

type LabelRequestTo struct {
	Name string `json:"name" validate:"required,min=2,max=32"`
}
