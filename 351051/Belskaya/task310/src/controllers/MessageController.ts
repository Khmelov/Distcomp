import { NextFunction, Request, Response, Router } from 'express';
import { MessageRequestTo } from '../dtos/requests';
import { messageService } from '../services';

const router = Router();

router.post('/', (req: Request, res: Response, next: NextFunction) => {
  try {
    const dto = req.body as MessageRequestTo;
    const result = messageService.create(dto);
    res.status(201).json({ message: result });
  } catch (err) {
    next(err);
  }
});

router.get('/', (req: Request, res: Response, next: NextFunction) => {
  try {
    const result = messageService.findAll();
    res.status(200).json(result.map((m) => ({ message: m })));
  } catch (err) {
    next(err);
  }
});

router.get('/:id', (req: Request, res: Response, next: NextFunction) => {
  try {
    const id = Number(req.params.id);
    const result = messageService.findById(id);
    res.status(200).json({ message: result });
  } catch (err) {
    next(err);
  }
});

router.put('/:id', (req: Request, res: Response, next: NextFunction) => {
  try {
    const id = Number(req.params.id);
    const dto = req.body as MessageRequestTo;
    const result = messageService.update(id, dto);
    res.status(200).json({ message: result });
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

router.get(
  '/by-topic/:topicId',
  (req: Request, res: Response, next: NextFunction) => {
    try {
      const topicId = Number(req.params.topicId);
      const result = messageService.findByTopicId(topicId);
      res.status(200).json(result.map((m) => ({ message: m })));
    } catch (err) {
      next(err);
    }
  }
);

export default router;
