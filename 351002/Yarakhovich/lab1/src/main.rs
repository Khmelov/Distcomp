mod service;

use std::sync::{Arc, RwLock};

use axum::Router;
use storage::in_memory::{
    InMemoryEditorRepo, InMemoryLabelRepo, InMemoryMessageRepo, InMemoryStoryRepo,
};

#[derive(Clone)]
pub struct ServiceState {
    pub editor_storage: Arc<RwLock<InMemoryEditorRepo>>,
    pub label_storage: Arc<RwLock<InMemoryLabelRepo>>,
    pub message_storage: Arc<RwLock<InMemoryMessageRepo>>,
    pub story_storage: Arc<RwLock<InMemoryStoryRepo>>,
}
impl ServiceState {
    pub fn new() -> Self {
        ServiceState {
            editor_storage: Arc::new(RwLock::new(InMemoryEditorRepo::new())),
            label_storage: Arc::new(RwLock::new(InMemoryLabelRepo::new())),
            message_storage: Arc::new(RwLock::new(InMemoryMessageRepo::new())),
            story_storage: Arc::new(RwLock::new(InMemoryStoryRepo::new())),
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
