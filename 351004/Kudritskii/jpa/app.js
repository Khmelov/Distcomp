const express = require("express");
const { sequelize } = require("./models");
require("dotenv").config();

const app = express();
app.use(express.json());

app.use("/api/v1.0/users", require("./routes/user.routes"));

const PORT = process.env.PORT || 24110;

(async () => {
  await sequelize.sync(); // или через liquibase отдельно
  app.listen(PORT, () => console.log(`Server started on ${PORT}`));
})();
