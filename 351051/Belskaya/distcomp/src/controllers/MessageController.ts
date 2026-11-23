import { NextFunction, Request, Response, Router } from 'express';
import { MessageRequestTo } from '../dtos/requests';
import { messageService } from '../services';
import { parsePaginationQuery } from '../utils/queryParser';

const router = Router();

router.post('/', (req: Request, res: Response, next: NextFunction) => {
  try {
    const dto = req.body as MessageRequestTo;
    const result = messageService.create(dto);
    res.status(201).json(result);
  } catch (err) {
    next(err);
  }
});

router.get('/', async (req: Request, res: Response, next: NextFunction) => {
  try {
    const params = parsePaginationQuery(req);
    const result = await messageService.findAll(params);
    res.status(200).json(result.map((m) => m));
  } catch (err) {
    next(err);
  }
});

router.get(
  '/by-topic/:topicId',
  async (req: Request, res: Response, next: NextFunction) => {
    try {
      const topicId = Number(req.params.topicId);
      const result = await messageService.findByTopicId(topicId);
      res.status(200).json(result.map((m) => m));
    } catch (err) {
      next(err);
    }
  }
);

router.get('/:id', (req: Request, res: Response, next: NextFunction) => {
  try {
    const id = Number(req.params.id);
    const result = messageService.findById(id);
    res.status(200).json(result);
  } catch (err) {
    next(err);
  }
});

router.put('/:id', (req: Request, res: Response, next: NextFunction) => {
  try {
    const id = Number(req.params.id);
    const dto = req.body as MessageRequestTo;
    const result = messageService.update(id, dto);
    res.status(200).json(result);
  } catch (err) {
    next(err);
  }
});

router.delete('/:id', (req: Request, res: Response, next: NextFunction) => {
  try {
    const id = Number(req.params.id);
    messageService.delete(id);
    res.status(204).send();
  } catch (err) {
    next(err);
  }
});

export default router;
