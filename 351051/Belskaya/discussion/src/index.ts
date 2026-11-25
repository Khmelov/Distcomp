import app from "./app";
import { initCassandra } from "./db/cassandra";
import { startKafkaConsumer } from "./kafka/consumer";

const PORT = 24130;

async function start() {
  try {
    await initCassandra();
    await startKafkaConsumer();

    app.listen(PORT, "0.0.0.0", () => {
      console.log(`Discussion server is running on http://0.0.0.0:${PORT}`);
    });
  } catch (err) {
    console.error("Failed to start Discussion:", err);
    process.exit(1);
  }
}

start();
