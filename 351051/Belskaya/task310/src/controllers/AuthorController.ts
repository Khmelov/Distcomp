import { NextFunction, Request, Response, Router } from 'express';
import { AuthorRequestTo } from '../dtos/requests';
import { authorService } from '../services';

const router = Router();

router.post('/', (req: Request, res: Response, next: NextFunction) => {
  try {
    const dto = req.body as AuthorRequestTo;
    const result = authorService.create(dto);
    res.status(201).json({ author: result });
  } catch (err) {
    next(err);
  }
});

router.get('/', (_: Request, res: Response, next: NextFunction) => {
  try {
    const result = authorService.findAll();
    res.status(200).json(result.map((a) => ({ author: a })));
  } catch (err) {
    next(err);
  }
});

router.get('/:id', (req: Request, res: Response, next: NextFunction) => {
  try {
    const id = Number(req.params.id);
    const result = authorService.findById(id);
    res.status(200).json({ author: result });
  } catch (err) {
    next(err);
  }
});

router.put('/:id', (req: Request, res: Response, next: NextFunction) => {
  try {
    const id = Number(req.params.id);
    const dto = req.body as AuthorRequestTo;
    const result = authorService.update(id, dto);
    res.status(200).json({ author: result });
  } catch (err) {
    next(err);
  }
});

router.delete('/:id', (req: Request, res: Response, next: NextFunction) => {
  try {
    const id = Number(req.params.id);
    authorService.delete(id);
    res.status(204).send();
  } catch (err) {
    next(err);
  }
});

router.get(
  '/by-topic/:topicId',
  (req: Request, res: Response, next: NextFunction) => {
    try {
      const topicId = Number(req.params.topicId);
      const result = authorService.findByTopicId(topicId);
      res.status(200).json({ author: result });
    } catch (err) {
      next(err);
    }
  }
);

export default router;
