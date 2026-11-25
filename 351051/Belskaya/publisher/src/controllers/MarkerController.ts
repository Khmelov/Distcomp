import { NextFunction, Request, Response, Router } from 'express';
import { MarkerRequestTo } from '../dtos/requests';
import { markerService } from '../services';
import { parsePaginationQuery } from '../utils/queryParser';

const router = Router();

router.post('/', async (req: Request, res: Response, next: NextFunction) => {
  try {
    const dto = req.body as MarkerRequestTo;
    const result = await markerService.create(dto);
    res.status(201).json(result);
  } catch (err) {
    next(err);
  }
});

router.get('/', async (req: Request, res: Response, next: NextFunction) => {
  try {
    const params = parsePaginationQuery(req);
    const result = await markerService.findAll(params);
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
      const result = await markerService.findByTopicId(topicId);
      res.status(200).json(result.map((m) => m));
    } catch (err) {
      next(err);
    }
  }
);

router.get('/:id', async (req: Request, res: Response, next: NextFunction) => {
  try {
    const id = Number(req.params.id);
    const result = await markerService.findById(id);
    res.status(200).json(result);
  } catch (err) {
    next(err);
  }
});

router.put('/:id', async (req: Request, res: Response, next: NextFunction) => {
  try {
    const id = Number(req.params.id);
    const dto = req.body as MarkerRequestTo;
    const result = await markerService.update(id, dto);
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
      await markerService.delete(id);
      res.status(204).send();
    } catch (err) {
      next(err);
    }
  }
);

export default router;
