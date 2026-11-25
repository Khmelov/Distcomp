import express from "express";
import { errorHandler } from "./middlewares/errorHandler";
import messageRoutes from "./routes/messageRoutes";

const app = express();

app.use(express.json());

app.use("/api/v1.0/messages", messageRoutes);

app.use(errorHandler);

export default app;
