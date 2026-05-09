// В Cargo.toml добавьте зависимости:
// redis = { version = "0.23", features = ["tokio-comp", "connection-manager", "serde"] }
// serde = { version = "1.0", features = ["derive"] }
// serde_json = "1.0"

use redis::aio::ConnectionManager;
use redis::{AsyncCommands, RedisError};
use serde::{Deserialize, Serialize};
use sqlx::Error as SqlxError;
use std::sync::Arc;
use tracing::{error, warn};

use crate::postgres::PgMessageRepo;
use domain::entities::{IDType, message::Message};

#[derive(Clone)]
pub struct CachedMessageRepo {
    pg_repo: PgMessageRepo,
    redis: Arc<tokio::sync::Mutex<ConnectionManager>>,
    ttl_seconds: usize,
}

impl CachedMessageRepo {
    pub async fn new(
        pg_repo: PgMessageRepo,
        redis_client: redis::Client,
        ttl_seconds: usize,
    ) -> Result<Self, RedisError> {
        let connection = redis_client.get_connection_manager().await.unwrap();
        Ok(Self {
            pg_repo,
            redis: Arc::new(tokio::sync::Mutex::new(connection)),
            ttl_seconds,
        })
    }

    async fn get_from_cache<T: for<'de> Deserialize<'de>>(&self, key: &str) -> Option<T> {
        let mut conn = self.redis.lock().await;
        let result: Result<String, RedisError> = conn.get(key).await;
        match result {
            Ok(json_str) => match serde_json::from_str(&json_str) {
                Ok(data) => Some(data),
                Err(e) => {
                    error!("Failed to deserialize cached value for key {}: {}", key, e);
                    None
                }
            },
            Err(e) => {
                warn!("Redis GET error for key {}: {}", key, e);
                None
            }
        }
    }

    async fn set_in_cache<T: Serialize>(&self, key: &str, value: &T) {
        let json_str = match serde_json::to_string(value) {
            Ok(s) => s,
            Err(e) => {
                error!("Failed to serialize value for key {}: {}", key, e);
                return;
            }
        };
        let mut conn = self.redis.lock().await;
        let res: Result<(), RedisError> = conn.set_ex(key, json_str, self.ttl_seconds).await;
        if let Err(e) = res {
            warn!("Redis SET error for key {}: {}", key, e);
        }
    }

    async fn invalidate_cache(&self, key: &str) {
        let mut conn = self.redis.lock().await;
        let res: Result<(), RedisError> = conn.del(key).await;
        if let Err(e) = res {
            warn!("Redis DEL error for key {}: {}", key, e);
        }
    }

    pub async fn get(&self, id: IDType) -> Result<Option<Message>, SqlxError> {
        let cache_key = format!("message:{}", id);
        if let Some(msg) = self.get_from_cache::<Message>(&cache_key).await {
            return Ok(Some(msg));
        }

        let msg_opt = self.pg_repo.get(id).await?;
        if let Some(ref msg) = msg_opt {
            self.set_in_cache(&cache_key, msg).await;
        }
        Ok(msg_opt)
    }

    pub async fn list(&self) -> Result<Vec<Message>, SqlxError> {
        const CACHE_KEY: &str = "messages:all";
        if let Some(messages) = self.get_from_cache::<Vec<Message>>(CACHE_KEY).await {
            return Ok(messages);
        }

        let messages = self.pg_repo.list().await?;
        self.set_in_cache(CACHE_KEY, &messages).await;
        Ok(messages)
    }

    pub async fn create(&self, entity: Message) -> Result<Message, SqlxError> {
        let created = self.pg_repo.create(entity).await?;
        self.invalidate_cache("messages:all").await;
        self.set_in_cache(&format!("message:{}", created.id), &created)
            .await;
        Ok(created)
    }

    pub async fn update(&self, id: IDType, entity: Message) -> Result<Message, SqlxError> {
        let updated = self.pg_repo.update(id, entity).await?;
        self.invalidate_cache(&format!("message:{}", id)).await;
        self.invalidate_cache("messages:all").await;
        // Можно также сохранить обновлённую версию в кэш:
        self.set_in_cache(&format!("message:{}", id), &updated)
            .await;
        Ok(updated)
    }

    pub async fn delete(&self, id: IDType) -> Result<(), SqlxError> {
        self.pg_repo.delete(id).await?;
        self.invalidate_cache(&format!("message:{}", id)).await;
        self.invalidate_cache("messages:all").await;
        Ok(())
    }
}
