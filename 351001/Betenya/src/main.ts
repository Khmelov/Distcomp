import { NestFactory } from '@nestjs/core';
import { SwaggerModule, DocumentBuilder } from '@nestjs/swagger';
import { AppModule } from './app.module';
import { ValidationPipe, VersioningType } from '@nestjs/common';
import { Request, Response, NextFunction } from 'express';

declare module 'express' {
  interface Request {
    version?: string;
  }
}

async function bootstrap() {
  const app = await NestFactory.create(AppModule);

  app.enableVersioning({
    type: VersioningType.URI,
  });

  const supportedVersions = ['v1.0.0'];

  app.use(
    '/api/:version',
    (req: Request, res: Response, next: NextFunction) => {
      const version: string = req.params.version as string;

      if (!supportedVersions.includes(version)) {
        return res.status(400).json({
          error: `Unsupported API version. Supported: ${supportedVersions.join(', ')}`,
        });
      }

      req.version = version;
      next();
    },
  );

  const config = new DocumentBuilder()
    .setTitle('Multi-version API')
    .setDescription('Support multiple API versions')
    .setVersion('1.0')
    .addServer('/api/v1.0', 'Version 1.0')
    .build();

  const document = SwaggerModule.createDocument(app, config);
  SwaggerModule.setup('docs', app, document);

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

  await app.listen(process.env.PORT ?? 24110);
}

bootstrap().catch(console.error);
