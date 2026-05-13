use std::sync::Arc;

use domain::entities::{IDType, notice::Notice};
use sqlx::{Error, PgPool};

#[derive(Clone)]
pub struct PgNoticeRepo {
    store: Arc<PgPool>,
}

impl PgNoticeRepo {
    pub fn new(store: Arc<PgPool>) -> Self {
        Self { store }
    }
}

impl PgNoticeRepo {
    pub async fn create(&self, entity: Notice) -> Result<Notice, Error> {
        sqlx::query_as::<_, Notice>(
            r#"INSERT INTO tbl_notice (topic_id, content)
               VALUES ($1, $2)
               RETURNING id, topic_id, content"#,
        )
        .bind(entity.topic_id)
        .bind(entity.content)
        .fetch_one(&*self.store)
        .await
    }

    pub async fn get(&self, id: IDType) -> Result<Option<Notice>, Error> {
        sqlx::query_as::<_, Notice>("SELECT id, topic_id, content FROM tbl_notice WHERE id = $1")
            .bind(id)
            .fetch_optional(&*self.store)
            .await
    }

    pub async fn update(&self, id: IDType, entity: Notice) -> Result<Notice, Error> {
        sqlx::query_as::<_, Notice>(
            r#"UPDATE tbl_notice
               SET topic_id = $2, content = $3
               WHERE id = $1
               RETURNING id, topic_id, content"#,
        )
        .bind(id)
        .bind(entity.topic_id)
        .bind(entity.content)
        .fetch_one(&*self.store)
        .await
    }

    pub async fn delete(&self, id: IDType) -> Result<(), Error> {
        let result = sqlx::query("DELETE FROM tbl_notice WHERE id = $1")
            .bind(id)
            .execute(&*self.store)
            .await?;
        if result.rows_affected() == 0 {
            return Err(Error::RowNotFound);
        }
        Ok(())
    }

    pub async fn list(&self) -> Result<Vec<Notice>, Error> {
        sqlx::query_as::<_, Notice>("SELECT id, topic_id, content FROM tbl_notice")
            .fetch_all(&*self.store)
            .await
    }
}
