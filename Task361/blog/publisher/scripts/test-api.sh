#!/bin/bash

# Тестовые скрипты для проверки API Blog Publisher

BASE_URL="http://localhost:24110"
JWT_TOKEN=""

echo "=== Blog API Testing Script ==="
echo "Base URL: $BASE_URL"
echo ""

# Функция для красивого вывода JSON
pretty_json() {
    python3 -m json.tool 2>/dev/null || cat
}

# 1. Проверка доступности сервиса
echo "1. Testing service health..."
curl -s "$BASE_URL/api/health" | pretty_json
echo ""

# 2. Получение информации о версиях API
echo "2. Getting API versions..."
curl -s "$BASE_URL/api/versions" | pretty_json
echo ""

# 3. Тест публичного endpoint v1.0
echo "3. Testing v1.0 public endpoint..."
curl -s "$BASE_URL/api/v1.0/test/public" | pretty_json
echo ""

# 4. Тест публичного endpoint v2.0
echo "4. Testing v2.0 public endpoint..."
curl -s "$BASE_URL/api/v2.0/test/public" | pretty_json
echo ""

# 5. Регистрация нового пользователя
echo "5. Registering new user..."
REGISTER_RESPONSE=$(curl -s -X POST "$BASE_URL/api/v2.0/editors" \
  -H "Content-Type: application/json" \
  -d '{
    "login": "testuser",
    "password": "testuser123",
    "firstname": "Test",
    "lastname": "User",
    "role": "CUSTOMER"
  }')

echo "$REGISTER_RESPONSE" | pretty_json
echo ""

# 6. Аутентификация для получения JWT токена
echo "6. Authenticating to get JWT token..."
LOGIN_RESPONSE=$(curl -s -X POST "$BASE_URL/api/v2.0/login" \
  -H "Content-Type: application/json" \
  -d '{
    "login": "customer",
    "password": "customer123"
  }')

echo "$LOGIN_RESPONSE" | pretty_json

# Извлекаем токен из ответа
JWT_TOKEN=$(echo "$LOGIN_RESPONSE" | grep -o '"accessToken":"[^"]*"' | cut -d'"' -f4)
echo "JWT Token extracted: ${JWT_TOKEN:0:30}..."
echo ""

if [ -n "$JWT_TOKEN" ]; then
    # 7. Тест защищенного endpoint с JWT
    echo "7. Testing protected endpoint with JWT..."
    curl -s "$BASE_URL/api/v2.0/test/protected" \
      -H "Authorization: Bearer $JWT_TOKEN" | pretty_json
    echo ""

    # 8. Тест endpoint только для CUSTOMER
    echo "8. Testing CUSTOMER-only endpoint..."
    curl -s "$BASE_URL/api/v2.0/test/customer" \
      -H "Authorization: Bearer $JWT_TOKEN" | pretty_json
    echo ""

    # 9. Попытка доступа к ADMIN endpoint (должна вернуть 403)
    echo "9. Testing ADMIN endpoint (should fail for CUSTOMER)..."
    curl -s -w " HTTP Status: %{http_code}\n" "$BASE_URL/api/v2.0/test/admin" \
      -H "Authorization: Bearer $JWT_TOKEN" | pretty_json
    echo ""
else
    echo "Failed to get JWT token, skipping protected endpoints tests"
fi

# 10. Тест с базовой аутентификацией (если включена)
echo "10. Testing with Basic Authentication..."
echo "Testing as customer..."
curl -s -u customer:customer123 "$BASE_URL/api/v2.0/test/protected" | pretty_json
echo ""

echo "Testing as admin..."
curl -s -u admin:admin123 "$BASE_URL/api/v2.0/test/admin" | pretty_json
echo ""

echo "=== Testing completed ==="