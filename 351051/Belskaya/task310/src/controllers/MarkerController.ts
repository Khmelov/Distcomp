import { NextFunction, Request, Response, Router } from 'express';
import { MarkerRequestTo } from '../dtos/requests';
import { markerService } from '../services';

const router = Router();

router.post('/', (req: Request, res: Response, next: NextFunction) => {
  try {
    const dto = req.body as MarkerRequestTo;
    const result = markerService.create(dto);
    res.status(201).json({ marker: result });
  } catch (err) {
    next(err);
  }
});

router.get('/', (req: Request, res: Response, next: NextFunction) => {
  try {
    const result = markerService.findAll();
    res.status(200).json(result.map((m) => ({ marker: m })));
  } catch (err) {
    next(err);
  }
});

router.get('/:id', (req: Request, res: Response, next: NextFunction) => {
  try {
    const id = Number(req.params.id);
    const result = markerService.findById(id);
    res.status(200).json({ marker: result });
  } catch (err) {
    next(err);
  }
});

router.put('/:id', (req: Request, res: Response, next: NextFunction) => {
  try {
    const id = Number(req.params.id);
    const dto = req.body as MarkerRequestTo;
    const result = markerService.update(id, dto);
    res.status(200).json({ marker: result });
  } catch (err) {
    next(err);
  }
});

router.delete('/:id', (req: Request, res: Response, next: NextFunction) => {
  try {
    const id = Number(req.params.id);
    markerService.delete(id);
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
      const result = markerService.findByTopicId(topicId);
      res.status(200).json(result.map((m) => ({ marker: m })));
    } catch (err) {
      next(err);
    }
  }
);

export default router;
