FROM --platform=linux/amd64 php:8.3-cli

# Установка PostgreSQL драйвера
RUN apt-get update && apt-get install -y \
    libpq-dev \
    && docker-php-ext-install pdo pdo_pgsql

WORKDIR /app

COPY composer.json ./

RUN php -r "copy('https://getcomposer.org/installer', 'composer-setup.php');" \
    && php composer-setup.php --quiet \
    && php -r "unlink('composer-setup.php');" \
    && php composer.phar install --no-dev

COPY .. .

EXPOSE 24110

CMD ["php", "-S", "0.0.0.0:24110", "/app/public/index.php"]