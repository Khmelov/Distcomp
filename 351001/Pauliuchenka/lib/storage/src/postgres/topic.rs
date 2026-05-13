use std::sync::Arc;

use domain::entities::{IDType, topic::Topic};
use sqlx::{Error, PgPool};

#[derive(Clone)]
pub struct PgTopicRepo {
    store: Arc<PgPool>,
}

impl PgTopicRepo {
    pub fn new(store: Arc<PgPool>) -> Self {
        Self { store }
    }
}

impl PgTopicRepo {
    pub async fn create(&self, entity: Topic) -> Result<Topic, Error> {
        sqlx::query_as::<_, Topic>(
            r#"INSERT INTO tbl_topic (creator_id, title, content, created, modified)
                VALUES ($1, $2, $3, NOW(), NOW())
                RETURNING id, creator_id, title, content, created, modified"#,
        )
        .bind(entity.creator_id)
        .bind(entity.title)
        .bind(entity.content)
        .fetch_one(&*self.store)
        .await
    }

    pub async fn get(&self, id: IDType) -> Result<Option<Topic>, Error> {
        sqlx::query_as::<_, Topic>(
            "SELECT id, creator_id, title, content, created, modified FROM tbl_topic WHERE id = $1",
        )
        .bind(id)
        .fetch_optional(&*self.store)
        .await
    }

    pub async fn update(&self, id: IDType, entity: Topic) -> Result<Topic, Error> {
        sqlx::query_as::<_, Topic>(
            r#"UPDATE tbl_topic
               SET creator_id = $2, title = $3, content = $4, modified = NOW()
               WHERE id = $1
               RETURNING id, creator_id, title, content, created, modified"#,
        )
        .bind(id)
        .bind(entity.creator_id)
        .bind(entity.title)
        .bind(entity.content)
        .fetch_one(&*self.store)
        .await
    }

    pub async fn delete(&self, id: IDType) -> Result<(), Error> {
        let result = sqlx::query("DELETE FROM tbl_topic WHERE id = $1")
            .bind(id)
            .execute(&*self.store)
            .await?;
        if result.rows_affected() == 0 {
            return Err(Error::RowNotFound);
        }
        Ok(())
    }

    pub async fn list(&self) -> Result<Vec<Topic>, Error> {
        sqlx::query_as::<_, Topic>(
            "SELECT id, creator_id, title, content, created, modified FROM tbl_topic",
        )
        .fetch_all(&*self.store)
        .await
    }
}
