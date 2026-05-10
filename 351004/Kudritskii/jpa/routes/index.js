const router = require("express").Router();

router.use("/users", require("./user.routes"));
router.use("/news", require("./news.routes"));
router.use("/notices", require("./notice.routes"));
router.use("/labels", require("./label.routes"));

module.exports = router;