@echo off
REM Тестовые скрипты для проверки API Blog Publisher (Windows)

set BASE_URL=http://localhost:24110
set JWT_TOKEN=

echo === Blog API Testing Script ===
echo Base URL: %BASE_URL%
echo.

REM 1. Проверка доступности сервиса
echo 1. Testing service health...
curl -s "%BASE_URL%/api/health"
echo.

REM 2. Получение информации о версиях API
echo 2. Getting API versions...
curl -s "%BASE_URL%/api/versions"
echo.

REM 3. Тест публичного endpoint v1.0
echo 3. Testing v1.0 public endpoint...
curl -s "%BASE_URL%/api/v1.0/test/public"
echo.

REM 4. Тест публичного endpoint v2.0
echo 4. Testing v2.0 public endpoint...
curl -s "%BASE_URL%/api/v2.0/test/public"
echo.

REM 5. Регистрация нового пользователя
echo 5. Registering new user...
curl -s -X POST "%BASE_URL%/api/v2.0/editors" ^
  -H "Content-Type: application/json" ^
  -d "{\"login\":\"testuser\",\"password\":\"testuser123\",\"firstname\":\"Test\",\"lastname\":\"User\",\"role\":\"CUSTOMER\"}"
echo.

REM 6. Аутентификация для получения JWT токена
echo 6. Authenticating to get JWT token...
for /f "tokens=*" %%i in ('curl -s -X POST "%BASE_URL%/api/v2.0/login" ^
  -H "Content-Type: application/json" ^
  -d "{\"login\":\"customer\",\"password\":\"customer123\"}"') do set LOGIN_RESPONSE=%%i

echo %LOGIN_RESPONSE%
echo.

REM Извлекаем токен из ответа (упрощенная версия)
for /f "tokens=2 delims=:," %%i in ('echo %LOGIN_RESPONSE% ^| findstr "accessToken"') do set JWT_TOKEN=%%i
set JWT_TOKEN=%JWT_TOKEN:"=%
echo JWT Token extracted: %JWT_TOKEN:~0,30%...
echo.

if not "%JWT_TOKEN%"=="" (
    REM 7. Тест защищенного endpoint с JWT
    echo 7. Testing protected endpoint with JWT...
    curl -s "%BASE_URL%/api/v2.0/test/protected" ^
      -H "Authorization: Bearer %JWT_TOKEN%"
    echo.

    REM 8. Тест endpoint только для CUSTOMER
    echo 8. Testing CUSTOMER-only endpoint...
    curl -s "%BASE_URL%/api/v2.0/test/customer" ^
      -H "Authorization: Bearer %JWT_TOKEN%"
    echo.

    REM 9. Попытка доступа к ADMIN endpoint
    echo 9. Testing ADMIN endpoint (should fail for CUSTOMER)...
    curl -s "%BASE_URL%/api/v2.0/test/admin" ^
      -H "Authorization: Bearer %JWT_TOKEN%" ^
      -w " HTTP Status: %%{http_code}"
    echo.
) else (
    echo Failed to get JWT token, skipping protected endpoints tests
    echo.
)

REM 10. Тест с базовой аутентификацией
echo 10. Testing with Basic Authentication...
echo Testing as customer...
curl -s -u customer:customer123 "%BASE_URL%/api/v2.0/test/protected"
echo.

echo Testing as admin...
curl -s -u admin:admin123 "%BASE_URL%/api/v2.0/test/admin"
echo.

echo === Testing completed ===
pause