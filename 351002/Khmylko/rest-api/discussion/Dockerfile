FROM php:8.3-cli

RUN apt-get update && apt-get install -y --no-install-recommends \
    git \
    librdkafka-dev \
    && docker-php-ext-install sockets \
    && pecl install rdkafka \
    && docker-php-ext-enable rdkafka \
    && rm -rf /var/lib/apt/lists/*

WORKDIR /app

COPY composer.json ./
RUN php -r "copy('https://getcomposer.org/installer', 'composer-setup.php');" \
    && php composer-setup.php --quiet \
    && php -r "unlink('composer-setup.php');" \
    && php composer.phar install --no-dev --no-interaction

COPY . .

EXPOSE 24130

CMD ["php", "-S", "0.0.0.0:24130", "-t", "public", "public/index.php"]
