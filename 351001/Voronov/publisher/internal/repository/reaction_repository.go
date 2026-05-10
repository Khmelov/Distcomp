package repository

import (
	"context"

	"publisher/internal/model"

	"github.com/jackc/pgx/v5/pgxpool"
)

type postgresReactionRepo struct {
	pool *pgxpool.Pool
}

func NewReactionRepository(pool *pgxpool.Pool) ReactionRepository {
	return &postgresReactionRepo{pool: pool}
}

func (r *postgresReactionRepo) UpdateState(ctx context.Context, id int64, state model.ReactionState) error {
	_, err := r.pool.Exec(ctx,
		`UPDATE distcomp.tbl_reaction SET state = $1 WHERE id = $2`,
		string(state), id,
	)
	return err
}
