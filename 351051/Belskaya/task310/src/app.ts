import express from 'express';
import apiV1Router from './controllers';
import { errorHandler } from './middlewares/errorHandler';

const app = express();

app.use(express.json());

app.use('/api/v1.0', apiV1Router);

app.use(errorHandler);

export default app;