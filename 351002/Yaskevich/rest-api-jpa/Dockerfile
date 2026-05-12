FROM node:18-alpine
WORKDIR /app

# Копируем файлы зависимостей из корня
COPY package*.json ./
RUN npm install

# Копируем всё содержимое проекта (включая папку src)
COPY . .

EXPOSE 24110

# Запускаем server.js, который лежит внутри папки src
CMD ["node", "src/server.js"]