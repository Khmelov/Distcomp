use std::sync::Arc;

use domain::entities::{IDType, label::Label};
use sqlx::{Error, PgPool};

#[derive(Clone)]
pub struct PgLabelRepo {
    store: Arc<PgPool>,
}

impl PgLabelRepo {
    pub fn new(store: Arc<PgPool>) -> Self {
        Self { store }
    }
}

impl PgLabelRepo {
    pub async fn create(&self, entity: Label) -> Result<Label, Error> {
        sqlx::query_as::<_, Label>(
            r#"INSERT INTO tbl_label (name)
               VALUES ($1)
               RETURNING id, name"#,
        )
        .bind(entity.name)
        .fetch_one(&*self.store)
        .await
    }

    pub async fn get(&self, id: IDType) -> Result<Option<Label>, Error> {
        sqlx::query_as::<_, Label>("SELECT id, name FROM tbl_label WHERE id = $1")
            .bind(id)
            .fetch_optional(&*self.store)
            .await
    }

    pub async fn update(&self, id: IDType, entity: Label) -> Result<Label, Error> {
        sqlx::query_as::<_, Label>(
            r#"UPDATE tbl_label
               SET name = $2
               WHERE id = $1
               RETURNING id, name"#,
        )
        .bind(id)
        .bind(entity.name)
        .fetch_one(&*self.store)
        .await
    }

    pub async fn delete(&self, id: IDType) -> Result<(), Error> {
        let result = sqlx::query("DELETE FROM tbl_label WHERE id = $1")
            .bind(id)
            .execute(&*self.store)
            .await?;
        if result.rows_affected() == 0 {
            return Err(Error::RowNotFound);
        }
        Ok(())
    }

    pub async fn list(&self) -> Result<Vec<Label>, Error> {
        sqlx::query_as::<_, Label>("SELECT id, name FROM tbl_label")
            .fetch_all(&*self.store)
            .await
    }
}
