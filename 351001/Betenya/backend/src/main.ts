import { NestFactory } from '@nestjs/core';
import { SwaggerModule, DocumentBuilder } from '@nestjs/swagger';
import { AppModule } from './app.module';
import { ValidationPipe, VersioningType } from '@nestjs/common';
import { BigIntInterceptor } from './services/big-int.interceptor';

async function bootstrap() {
  const app = await NestFactory.create(AppModule);

  app.enableCors({
    origin: ['http://localhost:24100', 'http://localhost:3000'],
    methods: ['GET', 'POST', 'PUT', 'DELETE', 'PATCH', 'OPTIONS'],
    allowedHeaders: ['Content-Type', 'Authorization', 'Accept'],
    credentials: true,
  });

  app.enableVersioning({
    type: VersioningType.URI,
  });

  app.useGlobalInterceptors(new BigIntInterceptor());

  app.setGlobalPrefix('api');

  const config = new DocumentBuilder()
    .setTitle('Multi-version API')
    .setDescription('Support multiple API versions')
    .setVersion('1.0')
    .addServer('', 'Version 1.0')
    .build();

  const document = SwaggerModule.createDocument(app, config);

  SwaggerModule.setup('api/docs', app, document);

  app.useGlobalPipes(
    new ValidationPipe({
      whitelist: true,
      forbidNonWhitelisted: true,
      transform: true,
      transformOptions: {
        enableImplicitConversion: true,
      },
    }),
  );
  // app.useGlobalInterceptors(
  //   new ClassSerializerInterceptor(app.get(Reflector), {
  //     strategy: 'excludeAll',
  //   }),
  // );

  await app.listen(process.env.PORT ?? 24110);
}

bootstrap().catch(console.error);
