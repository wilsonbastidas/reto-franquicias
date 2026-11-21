#!/bin/sh
# ---------------------------------------------------------------------------
# Crea (o actualiza) en LocalStack el secreto con las credenciales de
# PostgreSQL. Se ejecuta desde el contenedor "localstack-secret-init".
# ---------------------------------------------------------------------------
set -eu

AWS_ENDPOINT_URL="${AWS_ENDPOINT_URL:-http://localstack:4566}"
SECRET_NAME="${SECRET_NAME:-franchises/db-credentials}"
DB_HOST="${DB_HOST:-localhost}"
DB_PORT="${DB_PORT:-5432}"
DB_NAME="${DB_NAME:-franchises_db}"
DB_USER="${DB_USER:-postgres}"
DB_PASSWORD="${DB_PASSWORD:-postgres}"

aws() {
  command aws --endpoint-url "$AWS_ENDPOINT_URL" "$@"
}

SECRET_STRING=$(cat <<JSON
{
  "host": "${DB_HOST}",
  "port": ${DB_PORT},
  "database": "${DB_NAME}",
  "schema": "${DB_SCHEMA:-public}",
  "username": "${DB_USER}",
  "password": "${DB_PASSWORD}"
}
JSON
)

echo "Esperando a que LocalStack responda en ${AWS_ENDPOINT_URL} ..."
attempt=0
until aws secretsmanager list-secrets >/dev/null 2>&1; do
  attempt=$((attempt + 1))
  if [ "$attempt" -ge 30 ]; then
    echo "ERROR: LocalStack no respondió después de ${attempt} intentos." >&2
    exit 1
  fi
  sleep 2
done

if aws secretsmanager describe-secret --secret-id "$SECRET_NAME" >/dev/null 2>&1; then
  echo "El secreto '${SECRET_NAME}' ya existe. Actualizando su valor ..."
  aws secretsmanager put-secret-value \
    --secret-id "$SECRET_NAME" \
    --secret-string "$SECRET_STRING" >/dev/null
else
  echo "Creando el secreto '${SECRET_NAME}' ..."
  aws secretsmanager create-secret \
    --name "$SECRET_NAME" \
    --description "Credenciales de conexión a PostgreSQL (${DB_NAME})" \
    --secret-string "$SECRET_STRING" >/dev/null
fi

echo "Listo: secreto '${SECRET_NAME}' disponible en LocalStack."
