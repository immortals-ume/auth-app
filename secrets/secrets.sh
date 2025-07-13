#!/bin/bash

VAULT_CONTAINER_NAME="spring_boot_vault_container"
VAULT_PATH="secret/auth-app"
VAULT_ADDR="http://127.0.0.1:8200"

echo "🔐 Writing secrets to Vault path: $VAULT_PATH"

# shellcheck disable=SC1009
docker exec -e VAULT_ADDR=$VAULT_ADDR -e VAULT_TOKEN=root $VAULT_CONTAINER_NAME vault kv put $VAULT_PATH \
  spring.liquibase.default-schema=<YOUR_DEFAULT_SCHEMA> \
  spring.liquibase.liquibase-schema=<YOUR_LIQUIBASE_SCHEMA> \
  spring.liquibase.change-log=<YOUR_CHANGE_LOG_PATH> \
  spring.liquibase.enabled=<LIQUIBASE_ENABLED_TRUE_OR_FALSE> \
  spring.liquibase.url=<YOUR_LIQUIBASE_DB_URL> \
  spring.liquibase.user=<YOUR_LIQUIBASE_DB_USER> \
  spring.liquibase.password=<YOUR_LIQUIBASE_DB_PASSWORD> \
  spring.liquibase.driver-class-name=<YOUR_DB_DRIVER_CLASS_NAME> \
  spring.jpa.show-sql=<SHOW_SQL_TRUE_OR_FALSE> \
  spring.jpa.properties.hibernate.format_sql=<FORMAT_SQL_TRUE_OR_FALSE> \
  spring.jpa.properties.hibernate.dialect=<YOUR_HIBERNATE_DIALECT> \
  spring.jpa.properties.hibernate.envers.audit_table_suffix=<YOUR_AUDIT_TABLE_SUFFIX> \
  spring.jpa.properties.hibernate.envers.revision_field_name=<YOUR_REVISION_FIELD_NAME> \
  spring.jpa.properties.hibernate.envers.revision_type_field_name=<YOUR_REVISION_TYPE_FIELD_NAME> \
  spring.jpa.properties.hibernate.envers.store_data_at_delete=<STORE_DATA_AT_DELETE_TRUE_OR_FALSE> \
  spring.jpa.properties.hibernate.envers.default_schema=<YOUR_AUDIT_SCHEMA> \
  datasource.write.url=<YOUR_WRITE_DB_URL> \
  datasource.write.username=<YOUR_WRITE_DB_USERNAME> \
  datasource.write.password=<YOUR_WRITE_DB_PASSWORD> \
  datasource.write.driver-class-name=<YOUR_WRITE_DB_DRIVER_CLASS_NAME> \
  datasource.read.url=<YOUR_READ_DB_URL> \
  datasource.read.username=<YOUR_READ_DB_USERNAME> \
  datasource.read.password=<YOUR_READ_DB_PASSWORD> \
  datasource.read.driver-class-name=<YOUR_READ_DB_DRIVER_CLASS_NAME> \
  auth.jwt-issuer=<YOUR_JWT_ISSUER_URL> \
  auth.access-token-expiry-ms=<ACCESS_TOKEN_EXPIRY_MS> \
  auth.refresh-token-expiry-ms=<REFRESH_TOKEN_EXPIRY_MS> \
  jwt-private-key="<CONTENTS_OF_YOUR_PRIVATE_KEY_FILE>" \
  jwt-public-key="<CONTENTS_OF_YOUR_PUBLIC_KEY_FILE>" \
  cache.redis.host=<YOUR_REDIS_HOST> \
  cache.redis.port=<YOUR_REDIS_PORT> \
  cache.redis.database=<YOUR_REDIS_DATABASE_NUMBER> \
  cache.redis.command-timeout=<YOUR_REDIS_COMMAND_TIMEOUT> \
  cache.redis.use-ssl=<REDIS_USE_SSL_TRUE_OR_FALSE> \
  cache.redis.pool-max-total=<REDIS_POOL_MAX_TOTAL> \
  cache.redis.pool-max-idle=<REDIS_POOL_MAX_IDLE> \
  cache.redis.pool-min-idle=<REDIS_POOL_MIN_IDLE> \
  cache.redis.pool-max-wait=<REDIS_POOL_MAX_WAIT> \
  cache.redis.auto-reconnect=<REDIS_AUTO_RECONNECT_TRUE_OR_FALSE> \
  cache.redis.enabled=<REDIS_ENABLED_TRUE_OR_FALSE>
