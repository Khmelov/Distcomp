use std::sync::Arc;

use domain::entities::{IDType, message::Message};
use sqlx::{Error, PgPool};

#[derive(Clone)]
pub struct PgMessageRepo {
    store: Arc<PgPool>,
}

impl PgMessageRepo {
    pub fn new(store: Arc<PgPool>) -> Self {
        Self { store }
    }
}

impl PgMessageRepo {
    pub async fn create(&self, entity: Message) -> Result<Message, Error> {
        sqlx::query_as::<_, Message>(
            r#"INSERT INTO tbl_message (story_id, content)
               VALUES ($1, $2)
               RETURNING id, story_id, content"#,
        )
        .bind(entity.story_id)
        .bind(entity.content)
        .fetch_one(&*self.store)
        .await
    }

    pub async fn get(&self, id: IDType) -> Result<Option<Message>, Error> {
        sqlx::query_as::<_, Message>("SELECT id, story_id, content FROM tbl_message WHERE id = $1")
            .bind(id)
            .fetch_optional(&*self.store)
            .await
    }

    pub async fn update(&self, id: IDType, entity: Message) -> Result<Message, Error> {
        sqlx::query_as::<_, Message>(
            r#"UPDATE tbl_message
               SET story_id = $2, content = $3
               WHERE id = $1
               RETURNING id, story_id, content"#,
        )
        .bind(id)
        .bind(entity.story_id)
        .bind(entity.content)
        .fetch_one(&*self.store)
        .await
    }

    pub async fn delete(&self, id: IDType) -> Result<(), Error> {
        let result = sqlx::query("DELETE FROM tbl_message WHERE id = $1")
            .bind(id)
            .execute(&*self.store)
            .await?;
        if result.rows_affected() == 0 {
            return Err(Error::RowNotFound);
        }
        Ok(())
    }

    pub async fn list(&self) -> Result<Vec<Message>, Error> {
        sqlx::query_as::<_, Message>("SELECT id, story_id, content FROM tbl_message")
            .fetch_all(&*self.store)
            .await
    }
}
