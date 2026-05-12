use std::sync::Arc;

use domain::entities::{IDType, creator::Creator};
use sqlx::{Error, PgPool};

#[derive(Clone)]
pub struct PgCreatorRepo {
    store: Arc<PgPool>,
}

impl PgCreatorRepo {
    pub fn new(store: Arc<PgPool>) -> Self {
        Self { store }
    }
}

impl PgCreatorRepo {
    pub async fn create(&self, entity: Creator) -> Result<Creator, Error> {
        sqlx::query_as::<_, Creator>(
            r#"INSERT INTO tbl_creator (login, password, firstname, lastname)
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

    pub async fn get(&self, id: IDType) -> Result<Option<Creator>, Error> {
        sqlx::query_as::<_, Creator>(
            "SELECT id, login, password, firstname, lastname FROM tbl_creator WHERE id = $1",
        )
        .bind(id)
        .fetch_optional(&*self.store)
        .await
    }

    pub async fn update(&self, id: IDType, entity: Creator) -> Result<Creator, Error> {
        sqlx::query_as::<_, Creator>(
            r#"UPDATE tbl_creator
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
        let result = sqlx::query("DELETE FROM tbl_creator WHERE id = $1")
            .bind(id)
            .execute(&*self.store)
            .await?;
        if result.rows_affected() == 0 {
            return Err(Error::RowNotFound);
        }
        Ok(())
    }

    pub async fn list(&self) -> Result<Vec<Creator>, Error> {
        sqlx::query_as::<_, Creator>(
            "SELECT id, login, password, firstname, lastname FROM tbl_creator",
        )
        .fetch_all(&*self.store)
        .await
    }
}
