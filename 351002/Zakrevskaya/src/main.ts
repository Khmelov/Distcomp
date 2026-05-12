import { NestFactory } from '@nestjs/core';
import { AppModule } from './app.module';
import { ValidationPipe } from '@nestjs/common';
import { AllExceptionsFilter } from './common/filters/http-exception.filter';

async function bootstrap() {
  const app = await NestFactory.create(AppModule);
  
  app.useGlobalPipes(new ValidationPipe({
    transform: true,
    whitelist: true,
  }));
  
  app.useGlobalFilters(new AllExceptionsFilter());
  app.enableCors();

  // Основной порт
  await app.listen(24110);
  console.log('Port 24110 active');

  // Слушаем второй порт 24130 на том же Express инстансе
  const server = app.getHttpAdapter().getInstance();
  server.listen(24130, () => {
    console.log('Port 24130 active');
  });
}
bootstrap();