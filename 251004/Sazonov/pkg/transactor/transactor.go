package transactor

import (
	"context"
	"database/sql"

	"github.com/jmoiron/sqlx"
)

type txKey struct{}

func ExtractTx(ctx context.Context) *sqlx.Tx {
	if tx, ok := ctx.Value(txKey{}).(*sqlx.Tx); ok {
		return tx
	}

	return nil
}

func InjectTx(ctx context.Context, tx *sqlx.Tx) context.Context {
	return context.WithValue(ctx, txKey{}, tx)
}

type Transactor struct {
	db *sqlx.DB
}

func New(db *sqlx.DB) *Transactor {
	return &Transactor{
		db: db,
	}
}

// TODO: add transaction options
func (tr *Transactor) WithinTransaction(
	ctx context.Context,
	f func(ctx context.Context) error,
) error {
	tx, err := tr.db.BeginTxx(ctx, &sql.TxOptions{})
	if err != nil {
		return err
	}
	defer tx.Rollback()

	ctx = InjectTx(ctx, tx)
	if err := f(ctx); err != nil {
		return nil
	}

	if err := tx.Commit(); err != nil {
		return err
	}

	return nil
}
