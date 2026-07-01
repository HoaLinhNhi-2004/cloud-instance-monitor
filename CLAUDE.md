# cloud-instance-monitor

Backend REST API cho hệ thống giám sát cloud instance, viết một mình theo phong cách
đề bài OJT thực tế của TechValley (Cloud & SaaS consulting). Đề bài chỉ dùng làm
nguồn cảm hứng — không có team, không có deadline cứng, mục tiêu là portfolio cá nhân.

## Tech stack

- Java 21, Spring Boot 3.5 (Maven, dùng Maven Wrapper `./mvnw` — không cần cài Maven)
- Spring Data JPA + H2 in-memory
- Spring Security + JJWT (`io.jsonwebtoken` 0.12.6) cho JWT auth
- springdoc-openapi-starter-webmvc-ui 2.6.0 cho Swagger UI (`/swagger-ui/index.html`)
- Lombok

Build/run:
```
./mvnw compile
./mvnw spring-boot:run
```
H2 console: `/h2-console` (jdbc:h2:mem:monitordb, user `sa`, no password).

## Kiến trúc — layer rules

```
domain        Entity JPA, map đúng ERD (docs/erd.md)
domain.enums  Enum cho status/role/category
repository    Spring Data JPA interfaces, không chứa logic
service       TOÀN BỘ business logic nằm ở đây (dedupe, forecast, SLA, validation)
controller    Mỏng — chỉ validate input + gọi service, KHÔNG chứa logic nghiệp vụ
dto           Request/Response DTO, không expose Entity trực tiếp ra controller
security      JWT filter, SecurityConfig, role-based access
exception     Custom exception + GlobalExceptionHandler (@ControllerAdvice)
common        ApiResponse<T> wrapper dùng cho mọi response
```

Mọi response phải theo format thống nhất: `{ "status": 200, "message": "success", "data": {...} }`
qua `common.ApiResponse<T>`.

## ERD

Xem chi tiết + rationale tại [docs/erd.md](docs/erd.md). 6 bảng:
`members`, `clients`, `instances`, `alerts`, `cost_snapshots`, và bảng **tự thêm**
`instance_status_logs` (không có trong đề gốc — cần để tính SLA chính xác, xem
rationale trong erd.md).

## Business rules cần nhớ khi code service layer

1. **Auth**: ADMIN quản lý toàn bộ; CLIENT_MANAGER chỉ thấy client được gán (`managerId`).
2. **Alert dedupe**: trước khi tạo alert mới (CPU_HIGH / ERROR_DETECTED), phải check
   chưa tồn tại alert `isResolved = false` cùng loại cho cùng instance.
3. **Cost forecast**: đơn giá SMALL $50 / MEDIUM $120 / LARGE $250 mỗi tháng, chỉ tính
   trên instance đang RUNNING.
4. **SLA uptime**: tính từ `instance_status_logs` (tổng giờ RUNNING / tổng giờ trong
   tháng), so với ngưỡng theo `contractPlan`: PREMIUM 99.9% / STANDARD 99% / BASIC 95%.
5. **Instance deletion**: RUNNING → chặn, ném `ActiveInstanceException` (409 Conflict);
   STOPPED/ERROR → cho phép.
6. Mỗi lần `instance.status` đổi → ghi 1 dòng vào `instance_status_logs` (đừng quên,
   nếu thiếu thì SLA sẽ sai).

## API endpoints (theo đề gốc TechValley)

`/api/auth/login` · `/api/instances` (CRUD) · `/api/monitor/{warnings,errors,long-stopped,report}`
· `/api/alerts` + `/api/alerts/{id}/resolve` · `/api/clients` + `/cost`, `/cost-forecast`, `/sla`
· LLM: `GET /api/instances/{id}/diagnosis` (auto-generate nguyên nhân + hành động cho instance ERROR)

## Git Flow (solo — GitHub Flow)

`main` là nhánh duy nhất (không có `develop`). Mỗi buổi làm trên `feature/{module}`,
xong PR → merge thẳng vào `main`. Tự review diff trước khi merge. Commit convention:
`feat:` / `fix:` / `docs:` / `refactor:` / `test:`.

## Tiến độ hiện tại

- [x] Buổi 1: ERD + skeleton project + package structure + git init, push GitHub
- [x] Buổi 2: Entity (6 bảng) + Repository + `ApiResponse<T>` + `GlobalExceptionHandler`
- [ ] Buổi 3: Auth (JWT + Spring Security + role-based)
- [ ] Buổi 4: Client API
- [ ] Buổi 5: Instance CRUD (+ ghi `instance_status_logs`, deletion rule)
- [ ] Buổi 6: Monitoring API
- [ ] Buổi 7: Alert API + dedupe
- [ ] Buổi 8-9: Cost forecast + SLA (viết unit test riêng cho 2 logic này)
- [ ] Buổi 10: LLM diagnosis + hoàn thiện Swagger
- [ ] Buổi 11: Integration test, merge develop → main
- [ ] Buổi 12: README + dọn code
