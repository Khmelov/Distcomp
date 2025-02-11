package writer

import "sync/atomic"

type WriterRepo struct {
	id atomic.Int64
}

func (w *WriterRepo) NextID() int64 {
	return w.id.Add(1)
}
