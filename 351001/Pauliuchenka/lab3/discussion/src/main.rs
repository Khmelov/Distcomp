mod service;

use std::sync::Arc;

use axum::Router;
use storage::cassandra::message::CassandraNoticeRepo;

#[derive(Clone)]
pub struct ServiceState {
    pub notice_storage: Arc<CassandraNoticeRepo>,
}
impl ServiceState {
    pub async fn new() -> Self {
        ServiceState {
            notice_storage: Arc::new(
                CassandraNoticeRepo::new("localhost:9042", "distcomp")
                    .await
                    .unwrap(),
            ),
        }
    }
}

#[tokio::main]
async fn main() {
    let state = ServiceState::new();

    let serve = service::router();
    let api = Router::new().nest("/api/v1.0", serve);

    let listener1 = tokio::net::TcpListener::bind("0.0.0.0:24130")
        .await
        .unwrap();

    axum::serve(listener1, api.clone().with_state(state.await))
        .await
        .unwrap()
}
