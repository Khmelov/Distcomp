import { NextFunction, Request, Response, Router } from 'express';
import { topicService } from '../services';
import { TopicRequestTo } from '../dtos/requests';

const router = Router();

router.post('/', (req: Request, res: Response, next: NextFunction) => {
  try {
    const dto = req.body as TopicRequestTo;
    const result = topicService.create(dto);
    res.status(201).json({ topic: result });
  } catch (err) {
    next(err);
  }
});

router.get('/', (req: Request, res: Response, next: NextFunction) => {
  try {
    const result = topicService.findAll();
    res.status(200).json(result.map((t) => ({ topic: t })));
  } catch (err) {
    next(err);
  }
});

router.get('/:id', (req: Request, res: Response, next: NextFunction) => {
  try {
    const id = Number(req.params.id);
    const result = topicService.findById(id);
    res.status(200).json({ topic: result });
  } catch (err) {
    next(err);
  }
});

router.put('/:id', (req: Request, res: Response, next: NextFunction) => {
  try {
    const id = Number(req.params.id);
    const dto = req.body as TopicRequestTo;
    const result = topicService.update(id, dto);
    res.status(200).json({ topic: result });
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