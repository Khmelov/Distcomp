const express = require("express");
const helmet = require("helmet");
const cors = require("cors");
const logger = require("./utils/logger");
const errorHandler = require("./middleware/errorHandler");

const app = express();
app.use(helmet());
app.use(cors());
app.use(express.json());

app.use("/api/v1.0/users", require("./routes/users"));
app.use("/api/v1.0/news", require("./routes/news"));
app.use("/api/v1.0/labels", require("./routes/labels"));
app.use("/api/v1.0/notices", require("./routes/notices"));

app.use(errorHandler);

app.use(require("./middleware/rateLimiter"));

app.listen(24110, () => logger.info("App started on 24110"));
