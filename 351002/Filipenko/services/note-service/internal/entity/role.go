package entity

const (
	RoleAdmin    = "ADMIN"
	RoleCustomer = "CUSTOMER"
)

func ValidRole(r string) bool {
	return r == RoleAdmin || r == RoleCustomer
}
