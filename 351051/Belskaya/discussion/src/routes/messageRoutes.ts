import { Router } from "express";
import { MessageController } from "../controllers/MessageController";

const router = Router();

const controller = new MessageController();

router.post("/", controller.create);

router.get("/", controller.findAll);

router.get("/by-topic/:topicId", controller.findByTopicId);

router.get("/:id", controller.findById);

router.put("/:id", controller.update);

router.delete("/:id", controller.delete);

export default router;
