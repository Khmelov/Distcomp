mod service;

use std::sync::Arc;

use axum::Router;
use reqwest::Client;
use sqlx::{Executor, PgPool, postgres::PgPoolOptions};
use storage::postgres::{PgCreatorRepo, PgLabelRepo, PgNoticeRepo, PgTopicRepo};

#[derive(Clone)]
pub struct ServiceState {
    pub http_client: Client,
    discussion_service_url: String,
    pub creator_storage: Arc<PgCreatorRepo>,
    pub label_storage: Arc<PgLabelRepo>,
    pub notice_storage: Arc<PgNoticeRepo>,
    pub topic_storage: Arc<PgTopicRepo>,
}
impl ServiceState {
    pub fn new(pool: Arc<PgPool>) -> Self {
        ServiceState {
            http_client: Client::new(),
            discussion_service_url: "http://172.17.0.1:24130".to_string(),
            creator_storage: Arc::new(PgCreatorRepo::new(pool.clone())),
            label_storage: Arc::new(PgLabelRepo::new(pool.clone())),
            notice_storage: Arc::new(PgNoticeRepo::new(pool.clone())),
            topic_storage: Arc::new(PgTopicRepo::new(pool)),
        }
    }
    pub fn discussion_service_url(&self) -> &str {
        &self.discussion_service_url
    }
}

#[tokio::main]
async fn main() {
    let pool = PgPoolOptions::new()
        .after_connect(|conn, _| {
            Box::pin(async move {
                conn.execute("SET search_path TO distcomp").await?;
                Ok(())
            })
        })
        .connect("postgres://postgres:postgres@172.17.0.1/distcomp")
        .await
        .unwrap();

    let state = ServiceState::new(Arc::new(pool));

    let serve = service::router();
    let api = Router::new().nest("/api/v1.0", serve);

    let listener1 = tokio::net::TcpListener::bind("0.0.0.0:24110")
        .await
        .unwrap();

    axum::serve(listener1, api.clone().with_state(state.clone())).await.unwrap()
}
