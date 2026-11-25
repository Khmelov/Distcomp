import { NextFunction, Request, Response, Router } from 'express';
import { MessageRequestTo } from '../dtos/requests';
import { messageService } from '../services';
import { parsePaginationQuery } from '../utils/queryParser';

const router = Router();

router.post('/', async (req: Request, res: Response, next: NextFunction) => {
  try {
    const dto = req.body as MessageRequestTo;
    const result = await messageService.create(dto);
    res.status(201).json(result);
  } catch (err) {
    next(err);
  }
});

router.get('/', async (req: Request, res: Response, next: NextFunction) => {
  try {
    const params = parsePaginationQuery(req);
    const result = await messageService.findAll(params);
    res.status(200).json(result);
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
      res.status(200).json(result);
    } catch (err) {
      next(err);
    }
  }
);

router.get('/:id', async (req: Request, res: Response, next: NextFunction) => {
  try {
    const id = Number(req.params.id);
    const result = await messageService.findById(id);
    res.status(200).json(result);
  } catch (err) {
    next(err);
  }
});

router.put('/', async (req: Request, res: Response, next: NextFunction) => {
  try {
    const id = Number(req.body.id);
    const { id: _id, ...dto } = req.body as MessageRequestTo & {
      id: number;
    };

    const result = await messageService.update(id, dto);
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
      await messageService.delete(id);
      res.status(204).send();
    } catch (err) {
      next(err);
    }
  }
);

export default router;
