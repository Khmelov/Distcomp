const router = require("express").Router();
const controller = require("../controllers/notice.controller");

router.post("/", controller.create);
router.get("/", controller.findAll);
router.get("/:id", controller.findById);
router.put("/:id", controller.update);
router.delete("/:id", controller.delete);

module.exports = router;