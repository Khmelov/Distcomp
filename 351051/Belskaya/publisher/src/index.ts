import app from './app';
import { startKafkaRequestReply } from './kafka/requestReply';

const PORT = 24110;

async function bootstrap() {
  await startKafkaRequestReply();

  app.listen(PORT, '0.0.0.0', () => {
    console.log(`[publisher] Server is running on http://0.0.0.0:${PORT}`);
  });
}

bootstrap().catch((err) => {
  console.error('[publisher] Failed to start:', err);
  process.exit(1);
});
