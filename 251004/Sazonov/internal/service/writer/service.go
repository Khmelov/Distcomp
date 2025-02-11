package writer

type writerRepo interface{}

type writerService struct {
	writer writerRepo
}

func New(writer writerRepo) *writerService {
	return &writerService{
		writer: writer,
	}
}
