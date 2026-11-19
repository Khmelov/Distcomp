import express from 'express';

const app = express();
const PORT = 24110;

app.use(express.json());

app.get('/', (req, res) => {
  res.send('Entities API is running');
});

app.listen(PORT, () => {
  console.log(`Server is running on http://localhost:${PORT}`);
});