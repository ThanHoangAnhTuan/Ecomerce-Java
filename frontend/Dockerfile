# Sử dụng Node.js 22 làm base image
FROM node:22-alpine AS build

# Thiết lập thư mục làm việc
WORKDIR /app

# Cài đặt dependencies
COPY ./frontend/package*.json ./
RUN npm install --legacy-peer-deps

# Copy mã nguồn vào container
COPY ./frontend ./

RUN rm -rf .next

# Build ứng dụng Next.js
RUN npm run build

EXPOSE 3000
CMD ["npm", "run", "start"]