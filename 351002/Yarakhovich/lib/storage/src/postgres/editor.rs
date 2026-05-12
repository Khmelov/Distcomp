use std::sync::Arc;

use domain::entities::{IDType, editor::Editor};
use sqlx::{Error, PgPool};

#[derive(Clone)]
pub struct PgEditorRepo {
    store: Arc<PgPool>,
}

impl PgEditorRepo {
    pub fn new(store: Arc<PgPool>) -> Self {
        Self { store }
    }
}

impl PgEditorRepo {
    pub async fn create(&self, entity: Editor) -> Result<Editor, Error> {
        sqlx::query_as::<_, Editor>(
            r#"INSERT INTO tbl_editor (login, password, firstname, lastname)
               VALUES ($1, $2, $3, $4)
               RETURNING id, login, password, firstname, lastname"#,
        )
        .bind(entity.login)
        .bind(entity.password)
        .bind(entity.firstname)
        .bind(entity.lastname)
        .fetch_one(&*self.store)
        .await
    }

    pub async fn get(&self, id: IDType) -> Result<Option<Editor>, Error> {
        sqlx::query_as::<_, Editor>(
            "SELECT id, login, password, firstname, lastname FROM tbl_editor WHERE id = $1",
        )
        .bind(id)
        .fetch_optional(&*self.store)
        .await
    }

    pub async fn update(&self, id: IDType, entity: Editor) -> Result<Editor, Error> {
        sqlx::query_as::<_, Editor>(
            r#"UPDATE tbl_editor
               SET login = $2, password = $3, firstname = $4, lastname = $5
               WHERE id = $1
               RETURNING id, login, password, firstname, lastname"#,
        )
        .bind(id)
        .bind(entity.login)
        .bind(entity.password)
        .bind(entity.firstname)
        .bind(entity.lastname)
        .fetch_one(&*self.store)
        .await
    }

    pub async fn delete(&self, id: IDType) -> Result<(), Error> {
        let result = sqlx::query("DELETE FROM tbl_editor WHERE id = $1")
            .bind(id)
            .execute(&*self.store)
            .await?;
        if result.rows_affected() == 0 {
            return Err(Error::RowNotFound);
        }
        Ok(())
    }

    pub async fn list(&self) -> Result<Vec<Editor>, Error> {
        sqlx::query_as::<_, Editor>(
            "SELECT id, login, password, firstname, lastname FROM tbl_editor",
        )
        .fetch_all(&*self.store)
        .await
    }
}
