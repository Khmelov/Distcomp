FROM php:8.3-cli

WORKDIR /app

COPY composer.json ./
RUN php -r "copy('https://getcomposer.org/installer', 'composer-setup.php');" \
    && php composer-setup.php --quiet \
    && php -r "unlink('composer-setup.php');" \
    && php composer.phar install --no-dev

COPY . .

EXPOSE 24130

CMD ["php", "-S", "0.0.0.0:24130", "public/index.php"]