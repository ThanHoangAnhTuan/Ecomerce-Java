# Sử dụng Node.js 22 để build
FROM node:22-alpine AS build

# Thiết lập thư mục làm việc
WORKDIR /app

# Copy và cài đặt dependencies
COPY ./frontend/package*.json ./
RUN npm install --legacy-peer-deps

# Copy toàn bộ mã nguồn
COPY ./frontend ./

# Build ứng dụng Next.js
RUN npm run build

# Production image
FROM node:22-alpine AS production

# Thiết lập thư mục làm việc
WORKDIR /app

# Copy các file build từ giai đoạn trước
COPY --from=build /app ./

# Expose port 3000 cho ứng dụng Next.js
EXPOSE 3000

# Khởi động ứng dụng trong chế độ production
CMD ["npm", "run", "start"]
