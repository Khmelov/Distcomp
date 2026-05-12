mod service;

use std::sync::{Arc, RwLock};

use axum::Router;
use storage::in_memory::{
    InMemoryCreatorRepo, InMemoryLabelRepo, InMemoryNoticeRepo, InMemoryTopicRepo,
};

#[derive(Clone)]
pub struct ServiceState {
    pub creator_storage: Arc<RwLock<InMemoryCreatorRepo>>,
    pub label_storage: Arc<RwLock<InMemoryLabelRepo>>,
    pub notice_storage: Arc<RwLock<InMemoryNoticeRepo>>,
    pub topic_storage: Arc<RwLock<InMemoryTopicRepo>>,
}
impl ServiceState {
    pub fn new() -> Self {
        ServiceState {
            creator_storage: Arc::new(RwLock::new(InMemoryCreatorRepo::new())),
            label_storage: Arc::new(RwLock::new(InMemoryLabelRepo::new())),
            notice_storage: Arc::new(RwLock::new(InMemoryNoticeRepo::new())),
            topic_storage: Arc::new(RwLock::new(InMemoryTopicRepo::new())),
        }
    }
}

#[tokio::main]
async fn main() {
    let state = ServiceState::new();

    let serve = service::router();
    let api = Router::new().nest("/api/v1.0", serve);

    let listener = tokio::net::TcpListener::bind("0.0.0.0:24110")
        .await
        .unwrap();

    let _serve = axum::serve(listener, api.with_state(state)).await.unwrap();
}
