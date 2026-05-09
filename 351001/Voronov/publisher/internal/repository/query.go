package repository

type Pagination struct {
	Page     int
	PageSize int
}

type Filter struct {
	Field string
	Value interface{}
}

type Sort struct {
	Field     string
	Direction string
}

type QueryOptions struct {
	Pagination *Pagination
	Filters    []Filter
	Sort       *Sort
}

func NewQueryOptions() *QueryOptions {
	return &QueryOptions{
		Pagination: &Pagination{Page: 1, PageSize: 10},
		Filters:    []Filter{},
		Sort:       &Sort{Field: "id", Direction: "ASC"},
	}
}

func sortParams(s *Sort) (field, dir string) {
	field, dir = "id", "ASC"
	if s == nil {
		return
	}
	if s.Field != "" {
		field = s.Field
	}
	if s.Direction == "DESC" {
		dir = "DESC"
	}
	return
}
