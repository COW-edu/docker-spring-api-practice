# docker-spring-api-practice

Docker 세션(10~15분) 시연 및 실습용 레포입니다. DB는 Docker Compose로 띄우고, Spring Boot는 IntelliJ에서 직접 실행해서 Postman으로 API를 호출해봅니다.

## 구성도

```
Postman ---> localhost:8080 ---> Spring Boot(IntelliJ 실행)
                                        |
                                        v
                              localhost:3306
                                        |
                                        v
                         Docker MySQL 컨테이너(practice-mysql)
                                        |
                                        v
                                 Volume(mysql-data)
```

## 준비물

- Docker Desktop
- JDK 17
- IntelliJ
- Postman

## 실행 순서

1. MySQL 컨테이너 띄우기
   ```bash
   docker compose up -d
   ```
2. 컨테이너 상태 확인 (healthy 될 때까지 대기)
   ```bash
   docker compose ps
   ```
   또는 Docker Desktop에서 `practice-mysql` 컨테이너 상태 확인
3. IntelliJ에서 `PracticeApplication` 실행 (localhost:8080)
4. Postman에서 `postman/docker-spring-api-practice.postman_collection.json` import 후 요청 호출

## 실습 시나리오

1. **POST /todos** — 할 일 하나 생성 (201 응답으로 생성된 id 확인)
2. **GET /todos** — 방금 만든 할 일이 목록에 있는지 확인
3. **DB에서 직접 확인** — 컨테이너 안에 들어가서 실제로 저장됐는지 확인
   ```bash
   docker exec -it practice-mysql mysql --default-character-set=utf8mb4 -ucow -pcowpass practice -e "SELECT * FROM todos;"
   ```
   (`--default-character-set=utf8mb4`를 빼면 터미널에 한글이 깨져 보일 수 있습니다. DB에는 정상적으로 저장돼 있습니다.)

### 볼륨 효과 확인

```bash
docker compose restart
```

재시작 후 `GET /todos`로 다시 조회해서 데이터가 그대로 남아있는지 확인해보세요. (named volume 덕분에 컨테이너를 껐다 켜도 데이터는 유지됩니다.)

## 미션

- **미션 A**: `GET /todos/9999` 호출 → 없는 id라 `404` 응답이 오는지 확인
- **미션 B**: `POST /todos`에 `title`을 빈 문자열로 보내기 → 검증 실패로 `400` 응답이 오는지 확인

## API 목록

| 메서드 | 경로 | 설명 | 응답 |
| --- | --- | --- | --- |
| GET | /hello | 헬로 체크 | 200 |
| GET | /todos | 목록 조회 | 200 |
| GET | /todos/{id} | 단건 조회 | 200 / 404 |
| POST | /todos | 생성 (title 필수) | 201 / 400 |
| PUT | /todos/{id} | 수정 | 200 / 404 |
| DELETE | /todos/{id} | 삭제 | 204 / 404 |

## 정리

- 컨테이너만 종료하고 데이터는 유지
  ```bash
  docker compose down
  ```
- 데이터(볼륨)까지 완전히 삭제
  ```bash
  docker compose down -v
  ```

## 트러블슈팅

| 증상 | 원인 / 해결 |
| --- | --- |
| `3306 포트가 이미 사용 중` | 로컬에 이미 MySQL이 떠 있는지 확인 (`lsof -i :3306`), 필요하면 기존 프로세스 종료 |
| `Connection refused` | `docker compose up -d`로 MySQL을 먼저 띄웠는지, `docker compose ps`에서 healthy 상태인지 확인 |
| 로그인 실패 (Access denied) | `application.yml`의 `username`/`password`가 `docker-compose.yml`의 `MYSQL_USER`/`MYSQL_PASSWORD`와 정확히 일치하는지 확인 |
