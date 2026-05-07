const express = require("express")
const app = express()
const port = 24110

const usersRouter = require("./users")
const newsRouter = require("./news")
const noticesRouter = require("./notices")
const labelsRouter = require("./labels")

app.use(express.json());

app.use('/api/v1.0', usersRouter, newsRouter, noticesRouter, labelsRouter);

app.listen(port, () => {
  console.log(`Example app listening on port ${port}`)
})