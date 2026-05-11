use std::sync::Arc;

use domain::entities::{IDType, story::Story};
use sqlx::{Error, PgPool};

#[derive(Clone)]
pub struct PgStoryRepo {
    store: Arc<PgPool>,
}

impl PgStoryRepo {
    pub fn new(store: Arc<PgPool>) -> Self {
        Self { store }
    }
}

impl PgStoryRepo {
    pub async fn create(&self, entity: Story) -> Result<Story, Error> {
        sqlx::query_as::<_, Story>(
            r#"INSERT INTO tbl_story (editor_id, title, content, created, modified)
                VALUES ($1, $2, $3, NOW(), NOW())
                RETURNING id, editor_id, title, content, created, modified"#,
        )
        .bind(entity.editor_id)
        .bind(entity.title)
        .bind(entity.content)
        .fetch_one(&*self.store)
        .await
    }

    pub async fn get(&self, id: IDType) -> Result<Option<Story>, Error> {
        sqlx::query_as::<_, Story>(
            "SELECT id, editor_id, title, content, created, modified FROM tbl_story WHERE id = $1",
        )
        .bind(id)
        .fetch_optional(&*self.store)
        .await
    }

    pub async fn update(&self, id: IDType, entity: Story) -> Result<Story, Error> {
        sqlx::query_as::<_, Story>(
            r#"UPDATE tbl_story
               SET editor_id = $2, title = $3, content = $4, modified = NOW()
               WHERE id = $1
               RETURNING id, editor_id, title, content, created, modified"#,
        )
        .bind(id)
        .bind(entity.editor_id)
        .bind(entity.title)
        .bind(entity.content)
        .fetch_one(&*self.store)
        .await
    }

    pub async fn delete(&self, id: IDType) -> Result<(), Error> {
        let result = sqlx::query("DELETE FROM tbl_story WHERE id = $1")
            .bind(id)
            .execute(&*self.store)
            .await?;
        if result.rows_affected() == 0 {
            return Err(Error::RowNotFound);
        }
        Ok(())
    }

    pub async fn list(&self) -> Result<Vec<Story>, Error> {
        sqlx::query_as::<_, Story>(
            "SELECT id, editor_id, title, content, created, modified FROM tbl_story",
        )
        .fetch_all(&*self.store)
        .await
    }
}
