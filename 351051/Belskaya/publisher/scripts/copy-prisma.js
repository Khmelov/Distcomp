const fs = require('fs');
const path = require('path');

const source = path.join(__dirname, '../src/generated/prisma');
const destination = path.join(__dirname, '../dist/generated/prisma');

fs.cpSync(source, destination, { recursive: true });

console.log('Prisma Client copied to dist/generated/prisma');
