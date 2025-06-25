# UserManagement

## 1. Giới thiệu dự án

**User Management** là một ứng dụng quản lý người dùng đơn giản, được xây dựng nhằm phục vụ cho các hệ thống có yêu cầu tạo, đọc, cập nhật và xoá (CRUD) thông tin người dùng. Dự án này giúp người quản trị dễ dàng theo dõi danh sách người dùng, phân quyền, và xử lý thông tin người dùng một cách hiệu quả.

## 2. Các chức năng chính

- **Đăng ký, xác minh và đăng nhập người dùng**– Hệ thống đăng ký tài khoản mới với xác minh email qua mã OTP, đăng nhập bảo mật, làm mới token và đăng xuất an toàn với cookie HTTP-only.

- **Quản lý người dùng toàn diện (CRUD)** – Cung cấp khả năng tìm kiếm, phân trang, lọc và sắp xếp danh sách người dùng theo từ khóa một cách linh hoạt và hiệu quả.

- **Quản lý thông tin chi tiết** – Cho phép xem chi tiết và cập nhật thông tin cá nhân với hỗ trợ upload ảnh đại diện, tạo trải nghiệm người dùng hoàn chỉnh.

- **Tính năng xóa mềm và khôi phục thông minh** – Hỗ trợ "xóa mềm" để ẩn người dùng tạm thời và khả năng khôi phục dễ dàng, đảm bảo tính linh hoạt trong quản lý.
- **Tự quản lý thông tin cá nhân** – Người dùng có thể tự chủ thay đổi mật khẩu, cập nhật thông tin cá nhân và xem thông tin của chính mình một cách độc lập.
- **Hệ thống phân quyền rõ ràng và bảo mật** – Phân biệt rõ ràng quyền hạn giữa ADMIN (quản lý toàn bộ người dùng, bao gồm xóa vĩnh viễn) và USER (chỉ quản lý thông tin cá nhân), được bảo vệ bằng @PreAuthorize để đảm bảo an toàn tuyệt đối.

## 3. Công nghệ

### 3.1 Công nghệ sử dụng

- Java Spring Boot

- MySQL
  
- Redis

- OAuth 2.0

- JWT

- Postman

- Cloudinary

- Docker

### 3.2. Cấu trúc dự án

```java
UserManagement (Backend - Spring Boot)
│── src/main/java/com/example/UserManagement
│   ├── Configuration  # Cấu hình Spring Boot (Security, CORS, etc.)
│   ├── Controller     # Xử lý request từ client
│   ├── Dto            # Định nghĩa request/response DTOs
│   ├── Entity         # Các class ánh xạ database
│   ├── Enums          # Các định nghĩa chung của hệ thống
│   ├── Exception      # Xử lý các lỗi của hệ thống
│   ├── Repository     # Tầng truy vấn dữ liệu (JPA Repository)
│   ├── Service        # Xử lý logic nghiệp vụ
│   ├── Utils          # Định nghĩa các logic chung dùng được trong hệ thống
│   ├── Validation     # Chứa các anotation tự tạo
│── src/main/resources/application  # File chứa các thông tin của hệ thống
│── docker-compose.yml #Nơi chưa cấu hình Docker dùng để chạy Redis
```

## 4.Các bước cài đặt môi trường
### 4.1.Khởi tạo cơ sở dữ liệu trong MySql Workbench
- Chạy MySql Workbench tạo 1 Schema tên "usermanagement" sau đó chạy code tải về trên intellij,cơ sở dữ liệu sẽ tự động được tạo trong MySql Workbench.
### 4.2.Khởi Tạo redis trên Docker
- Tải Docker về máy và chạy lên,sau đó lấy code về chạy trên Intellij sau đó bấm vào Terminal và gõ lệnh "dockercompose up -d",Docker sẽ tự động tạo một file tên "redisUserManagement" để chạy redis trên máy.
