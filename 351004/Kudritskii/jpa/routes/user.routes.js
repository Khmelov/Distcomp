const router = require("express").Router();
const ctrl = require("../controllers/user.controller");

router.post("/", ctrl.create);
router.get("/", ctrl.getAll);
router.get("/:id", ctrl.getById);
router.put("/:id", ctrl.update);
router.delete("/:id", ctrl.delete);

module.exports = router;
