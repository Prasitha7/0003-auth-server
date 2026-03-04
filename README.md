# Spring Boot Authorization Server Microservice

Production-oriented authentication and authorization microservice built with Spring Boot 3, Spring Security 6, and Spring Authorization Server.

## Features
- OAuth2 Authorization Server (Authorization Code + PKCE, Client Credentials)
- JWT access tokens signed with RSA key pair
- JWK Set endpoint (`/oauth2/jwks`)
- Refresh token rotation and DB persistence
- Token revocation endpoint (`/api/v1/tokens/revoke`)
- RBAC (`ROLE_USER`, `ROLE_ADMIN`)
- Self-registration and admin user management endpoints
- Account lockout after 5 failed logins
- Flyway migrations for MySQL
- Actuator liveness/readiness probes
- OpenAPI docs via springdoc

## Project Structure
```
src/main/java/ac/nsbm/authserver
├── config
├── controller
├── domain
├── dto
├── exception
├── mapper
├── repository
├── security
└── service
```

## Run locally
```bash
./mvnw spring-boot:run
```

## Build container
```bash
docker build -t auth-server:latest .
```

## Kubernetes readiness/liveness
- `GET /actuator/health/liveness`
- `GET /actuator/health/readiness`

## Default OAuth clients
- `web-client` / `web-client-secret` (authorization_code + refresh_token + PKCE)
- `service-client` / `service-client-secret` (client_credentials)

## Example cURL requests

### 1) Register user
```bash
curl -X POST http://localhost:9000/api/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"alice01","email":"alice@example.com","password":"pass1234A"}'
```

### 2) Client Credentials token
```bash
curl -X POST http://localhost:9000/oauth2/token \
  -u service-client:service-client-secret \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "grant_type=client_credentials&scope=users.read"
```

### 3) Authorization Code with PKCE (outline)
1. Generate `code_verifier` + `code_challenge`.
2. Redirect user to:
```text
http://localhost:9000/oauth2/authorize?response_type=code&client_id=web-client&scope=openid%20profile%20users.read&redirect_uri=http://127.0.0.1:8081/login/oauth2/code/web-client&code_challenge=...&code_challenge_method=S256
```
3. Exchange code:
```bash
curl -X POST http://localhost:9000/oauth2/token \
  -u web-client:web-client-secret \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "grant_type=authorization_code&code=AUTH_CODE&redirect_uri=http://127.0.0.1:8081/login/oauth2/code/web-client&code_verifier=..."
```

### 4) Revoke refresh token (logout)
```bash
curl -X POST "http://localhost:9000/api/v1/tokens/revoke?refresh_token=REFRESH_TOKEN" \
  -H "Authorization: Bearer ACCESS_TOKEN"
```

### 5) Admin: list users
```bash
curl -X GET "http://localhost:9000/api/v1/admin/users?page=0&size=20" \
  -H "Authorization: Bearer ADMIN_ACCESS_TOKEN"
```

### 6) Admin: promote to admin
```bash
curl -X PATCH "http://localhost:9000/api/v1/admin/users/2/promote" \
  -H "Authorization: Bearer ADMIN_ACCESS_TOKEN"
```

### 7) Admin: disable user
```bash
curl -X PATCH "http://localhost:9000/api/v1/admin/users/2/disable" \
  -H "Authorization: Bearer ADMIN_ACCESS_TOKEN"
```
