mod service;

use std::sync::Arc;

use axum::Router;
use sqlx::{Executor, PgPool, postgres::PgPoolOptions};
use storage::postgres::{PgCreatorRepo, PgLabelRepo, PgNoticeRepo, PgTopicRepo};
use tokio::sync::RwLock;

#[derive(Clone)]
pub struct ServiceState {
    pub creator_storage: Arc<RwLock<PgCreatorRepo>>,
    pub label_storage: Arc<RwLock<PgLabelRepo>>,
    pub notice_storage: Arc<RwLock<PgNoticeRepo>>,
    pub topic_storage: Arc<RwLock<PgTopicRepo>>,
}
impl ServiceState {
    pub fn new(pool: Arc<PgPool>) -> Self {
        ServiceState {
            creator_storage: Arc::new(RwLock::new(PgCreatorRepo::new(pool.clone()))),
            label_storage: Arc::new(RwLock::new(PgLabelRepo::new(pool.clone()))),
            notice_storage: Arc::new(RwLock::new(PgNoticeRepo::new(pool.clone()))),
            topic_storage: Arc::new(RwLock::new(PgTopicRepo::new(pool))),
        }
    }
}

#[tokio::main]
async fn main() {
    let pool1 = PgPoolOptions::new()
        .after_connect(|conn, _| {
            Box::pin(async move {
                conn.execute("SET search_path TO distcomp").await?;
                Ok(())
            })
        })
        .connect("postgres://postgres:postgres@172.17.0.1/distcomp")
        .await
        .unwrap();
    let pool2 = PgPoolOptions::new()
        .after_connect(|conn, _| {
            Box::pin(async move {
                conn.execute("SET search_path TO distcomp").await?;
                Ok(())
            })
        })
        .connect("postgres://postgres:postgres@172.17.0.1/distcomp")
        .await
        .unwrap();
    let state1 = ServiceState::new(Arc::new(pool1));
    let state2 = ServiceState::new(Arc::new(pool2));

    let serve = service::router();
    let api = Router::new().nest("/api/v1.0", serve);

    let listener1 = tokio::net::TcpListener::bind("0.0.0.0:24110")
        .await
        .unwrap();
    let listener2 = tokio::net::TcpListener::bind("0.0.0.0:24130")
        .await
        .unwrap();

    tokio::select! {
        res = axum::serve(listener1, api.clone().with_state(state1)) => {
            res.unwrap();
        }
        res = axum::serve(listener2, api.with_state(state2)) => {
            res.unwrap();
        }
    }
}

// #[tokio::main]
// async fn main() {
//     let pool = PgPoolOptions::new()
//         .after_connect(|conn, _| {
//             Box::pin(async move {
//                 conn.execute("SET search_path TO distcomp").await?;
//                 Ok(())
//             })
//         })
//         .connect("postgres://postgres:postgres@172.17.0.1/distcomp")
//         .await
//         .unwrap();

//     let state = ServiceState::new(Arc::new(pool));

//     let serve = service::router();
//     let api = Router::new().nest("/api/v1.0", serve);

//     let listener1 = tokio::net::TcpListener::bind("0.0.0.0:24110")
//         .await
//         .unwrap();

//     axum::serve(listener1, api.clone().with_state(state.clone())).await.unwrap()
// }
