package auth

import "testing"

func TestJWT_MintParse(t *testing.T) {
	s := NewJWTService("lab6-unit-test-secret-key------------", 60)
	tok, err := s.Mint("reader1", "CUSTOMER")
	if err != nil {
		t.Fatal(err)
	}
	cl, err := s.Parse(tok)
	if err != nil {
		t.Fatal(err)
	}
	if cl.Subject != "reader1" || cl.Role != "CUSTOMER" {
		t.Fatalf("claims: sub=%q role=%q", cl.Subject, cl.Role)
	}
}
