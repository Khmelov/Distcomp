import { Router } from 'express';
import authorRouter from './AuthorController';
import markerRouter from './MarkerController';
import messageRouter from './MessageController';
import topicRouter from './TopicController';

const apiV1Router = Router();

apiV1Router.use('/authors', authorRouter);
apiV1Router.use('/topics', topicRouter);
apiV1Router.use('/markers', markerRouter);
apiV1Router.use('/messages', messageRouter);

export default apiV1Router;