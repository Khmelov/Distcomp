# ── Stage 1: build ──────────────────────────────────────────────────────────
FROM node:22-alpine AS builder
WORKDIR /app

COPY package*.json ./
RUN npm ci

COPY . .

# Generate Prisma client (output: generated/prisma)
RUN npx prisma generate

# Compile TypeScript → dist/
RUN npm run build

# ── Stage 2: production image ────────────────────────────────────────────────
FROM node:22-alpine AS runner
WORKDIR /app

COPY package*.json ./
RUN npm ci --omit=dev

# Compiled app (includes dist/src/main.js and dist/generated/prisma client)
COPY --from=builder /app/dist ./dist

# Schema needed for `prisma db push` at startup
COPY --from=builder /app/prisma ./prisma

EXPOSE 24110

# Push schema to DB (creates tables if absent), then start the app
CMD ["sh", "-c", "npx prisma db push --url=\"$DATABASE_URL\" && node dist/src/main"]
