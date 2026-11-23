import { NextFunction, Request, Response, Router } from 'express';
import { AuthorRequestTo } from '../dtos/requests';
import { authorService } from '../services';

const router = Router();

router.post('/', async (req: Request, res: Response, next: NextFunction) => {
  try {
    const result = await authorService.create(req.body);
    res.status(201).json({ author: result });
  } catch (err) {
    next(err);
  }
});

router.get('/', async (req: Request, res: Response, next: NextFunction) => {
  try {
    const result = await authorService.findAll();
    res.status(200).json(result.map((a) => ({ author: a })));
  } catch (err) {
    next(err);
  }
});

router.get(
  '/by-topic/:topicId',
  async (req: Request, res: Response, next: NextFunction) => {
    try {
      const topicId = Number(req.params.topicId);
      const result = await authorService.findByTopicId(topicId);
      res.status(200).json(result);
    } catch (err) {
      next(err);
    }
  }
);

router.get('/:id', async (req: Request, res: Response, next: NextFunction) => {
  try {
    const id = Number(req.params.id);
    const result = await authorService.findById(id);
    res.status(200).json(result);
  } catch (err) {
    next(err);
  }
});

router.put('/:id', async (req: Request, res: Response, next: NextFunction) => {
  try {
    const id = Number(req.params.id);
    const dto = req.body as AuthorRequestTo;
    const result = await authorService.update(id, dto);
    res.status(200).json(result);
  } catch (err) {
    next(err);
  }
});

router.delete(
  '/:id',
  async (req: Request, res: Response, next: NextFunction) => {
    try {
      const id = Number(req.params.id);
      await authorService.delete(id);
      res.status(204).send();
    } catch (err) {
      next(err);
    }
  }
);

export default router;
