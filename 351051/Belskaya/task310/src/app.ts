import express from 'express';
import apiV1Router from './controllers';
import { errorHandler } from './middlewares/errorHandler';
import { NotFoundError } from './errors/AppError';

const app = express();

app.use(express.json());

app.use('/api/v1.0', apiV1Router);

app.use((req, res, next) => {
  next(
    new NotFoundError(
      `Route ${req.method} ${req.originalUrl} not found`,
      '40402'
    )
  );
});

app.use(errorHandler);

export default app;
