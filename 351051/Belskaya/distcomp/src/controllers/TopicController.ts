import { NextFunction, Request, Response, Router } from 'express';
import { TopicRequestTo } from '../dtos/requests';
import { topicService } from '../services';
import { parsePaginationQuery } from '../utils/queryParser';

const router = Router();

router.post('/', (req: Request, res: Response, next: NextFunction) => {
  try {
    const dto = req.body as TopicRequestTo;
    const result = topicService.create(dto);
    res.status(201).json(result);
  } catch (err) {
    next(err);
  }
});

router.get('/', async (req: Request, res: Response, next: NextFunction) => {
  try {
    const params = parsePaginationQuery(req);
    const result = await topicService.findAll(params);
    res.status(200).json(result.map((t) => t));
  } catch (err) {
    next(err);
  }
});

router.get(
  '/by-filters',
  async (req: Request, res: Response, next: NextFunction) => {
    try {
      const markerNames = req.query.markerNames
        ? String(req.query.markerNames).split(',')
        : undefined;
      const markerIds = req.query.markerIds
        ? String(req.query.markerIds).split(',').map(Number)
        : undefined;
      const authorLogin = req.query.authorLogin
        ? String(req.query.authorLogin)
        : undefined;
      const title = req.query.title ? String(req.query.title) : undefined;
      const content = req.query.content ? String(req.query.content) : undefined;

      const result = await topicService.findByFilters({
        markerNames,
        markerIds,
        authorLogin,
        title,
        content,
      });

      res.status(200).json(result.map((t) => t));
    } catch (err) {
      next(err);
    }
  }
);

router.get('/:id', (req: Request, res: Response, next: NextFunction) => {
  try {
    const id = Number(req.params.id);
    const result = topicService.findById(id);
    res.status(200).json(result);
  } catch (err) {
    next(err);
  }
});

router.put('/:id', (req: Request, res: Response, next: NextFunction) => {
  try {
    const id = Number(req.params.id);
    const dto = req.body as TopicRequestTo;
    const result = topicService.update(id, dto);
    res.status(200).json(result);
  } catch (err) {
    next(err);
  }
});

router.delete('/:id', (req: Request, res: Response, next: NextFunction) => {
  try {
    const id = Number(req.params.id);
    topicService.delete(id);
    res.status(204).send();
  } catch (err) {
    next(err);
  }
});

export default router;
