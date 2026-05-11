use std::sync::Arc;

use domain::entities::{IDType, message::Message};
use scylla::prepared_statement::PreparedStatement;
use scylla::transport::errors::QueryError;
use scylla::{Session, SessionBuilder};

#[derive(Clone)]
pub struct CassandraMessageRepo {
    session: Arc<Session>,
    insert_stmt: PreparedStatement,
    select_stmt: PreparedStatement,
    delete_stmt: PreparedStatement,
    select_all_stmt: PreparedStatement,
}

impl CassandraMessageRepo {
    pub async fn new(contact_points: &str, keyspace: &str) -> Result<Self, QueryError> {
        let session = SessionBuilder::new()
            .known_node(contact_points)
            .build()
            .await
            .unwrap();

        let create_keyspace = format!(
            "CREATE KEYSPACE IF NOT EXISTS {}
             WITH REPLICATION = {{ 'class' : 'SimpleStrategy', 'replication_factor' : 1 }}",
            keyspace
        );
        session.query(create_keyspace, &[]).await?;
        session.use_keyspace(keyspace, false).await?;

        let create_table = r#"
            CREATE TABLE IF NOT EXISTS messages (
                id bigint PRIMARY KEY,
                story_id bigint,
                content text
            )
        "#;
        session.query(create_table, &[]).await?;

        let insert_stmt = session
            .prepare("INSERT INTO messages (id, story_id, content) VALUES (?, ?, ?)")
            .await?;
        let select_stmt = session
            .prepare("SELECT id, story_id, content FROM messages WHERE id = ?")
            .await?;
        let delete_stmt = session.prepare("DELETE FROM messages WHERE id = ?").await?;
        let select_all_stmt = session
            .prepare("SELECT id, story_id, content FROM messages")
            .await?;

        Ok(Self {
            session: Arc::new(session),
            insert_stmt,
            select_stmt,
            delete_stmt,
            select_all_stmt,
        })
    }

    fn generate_id(&self) -> IDType {
        use std::time::{SystemTime, UNIX_EPOCH};
        let now = SystemTime::now()
            .duration_since(UNIX_EPOCH)
            .unwrap()
            .as_micros();
        (now % 1_000_000_000_000) as i64
    }

    pub async fn save(&self, mut entity: Message) -> Result<Message, QueryError> {
        if entity.id == 0 {
            entity.id = self.generate_id();
        }
        self.session
            .execute(
                &self.insert_stmt,
                (entity.id, entity.story_id, &entity.content),
            )
            .await?;
        Ok(entity)
    }

    pub async fn get(&self, id: IDType) -> Result<Option<Message>, QueryError> {
        let rows = self
            .session
            .execute(&self.select_stmt, (id,))
            .await?
            .rows
            .unwrap_or_default();

        if let Some(row) = rows.into_iter().next() {
            let message: Message = row.into_typed::<Message>().unwrap();
            Ok(Some(message))
        } else {
            Ok(None)
        }
    }

    pub async fn update(&self, id: IDType, entity: Message) -> Result<Message, QueryError> {
        self.session
            .execute(&self.insert_stmt, (id, entity.story_id, &entity.content))
            .await?;
        Ok(entity)
    }

    pub async fn delete(&self, id: IDType) -> Result<(), QueryError> {
        let exists = self.get(id).await?.is_some();
        if !exists {
            return Err(QueryError::from(std::io::Error::new(
                std::io::ErrorKind::NotFound,
                "Row not found",
            )));
        }
        let _ = self.session.execute(&self.delete_stmt, (id,)).await?;
        Ok(())
    }

    pub async fn list(&self) -> Result<Vec<Message>, QueryError> {
        let rows = self
            .session
            .execute(&self.select_all_stmt, &[])
            .await?
            .rows
            .unwrap_or_default();

        let mut messages = Vec::with_capacity(rows.len());
        for row in rows {
            let msg: Message = row.into_typed::<Message>().unwrap();
            messages.push(msg);
        }
        Ok(messages)
    }
}
