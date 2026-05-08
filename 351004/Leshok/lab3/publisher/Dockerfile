FROM node:18-alpine
WORKDIR /app
COPY package*.json ./
RUN npm install   
COPY . .
EXPOSE 24110
CMD ["node", "src/index.js"]