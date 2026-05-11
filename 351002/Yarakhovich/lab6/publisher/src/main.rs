mod kafka_types;
mod service;

use axum::Router;
use rdkafka::ClientConfig;
use rdkafka::consumer::{Consumer, StreamConsumer};
use rdkafka::message::Message;
use rdkafka::producer::FutureProducer;
use sqlx::postgres::PgPoolOptions;
use sqlx::{Executor, PgPool};
use std::collections::HashMap;
use std::sync::Arc;
use storage::postgres::{PgEditorRepo, PgLabelRepo, PgStoryRepo};
use tokio::sync::{Mutex, oneshot};
use tokio_stream::StreamExt;

use kafka_types::*;

#[derive(Clone)]
pub struct ServiceState {
    pub redis_client: redis::Client,
    pub kafka_producer: FutureProducer,
    pub kafka_response_consumer: Arc<StreamConsumer>,
    pub pending_responses: Arc<Mutex<HashMap<String, oneshot::Sender<KafkaResponse>>>>,
    pub editor_storage: Arc<PgEditorRepo>,
    pub label_storage: Arc<PgLabelRepo>,
    pub story_storage: Arc<PgStoryRepo>,
}

impl ServiceState {
    pub fn new(
        redis_client: redis::Client,
        pool: Arc<PgPool>,
        kafka_producer: FutureProducer,
        kafka_response_consumer: StreamConsumer,
        pending_responses: Arc<Mutex<HashMap<String, oneshot::Sender<KafkaResponse>>>>
    ) -> Self {
        ServiceState {
            redis_client,
            editor_storage: Arc::new(PgEditorRepo::new(pool.clone())),
            label_storage: Arc::new(PgLabelRepo::new(pool.clone())),
            story_storage: Arc::new(PgStoryRepo::new(pool)),
            kafka_producer,
            kafka_response_consumer: Arc::new(kafka_response_consumer),
            pending_responses,
        }
    }
}

// ---------- Main ----------
#[tokio::main]
async fn main() -> Result<(), ()> {
    let pool = PgPoolOptions::new()
        .after_connect(|conn, _| {
            Box::pin(async move {
                conn.execute("SET search_path TO distcomp").await?;
                Ok(())
            })
        })
        .connect("postgres://postgres:postgres@172.17.0.1/distcomp")
        .await
        .expect("main.rs PgPool connect error");
    let pool = Arc::new(pool);

    let redis_client =
        redis::Client::open("redis://127.0.0.1:6379/").expect("Failed to create Redis client");

    let kafka_brokers = "localhost:9092";
    let response_topic = "service_responses";

    let producer: FutureProducer = ClientConfig::new()
        .set("bootstrap.servers", kafka_brokers)
        .set("message.timeout.ms", "5000")
        .create()
        .expect("main.rs producer create error");

    let consumer: StreamConsumer = ClientConfig::new()
        .set("bootstrap.servers", kafka_brokers)
        .set("group.id", "my_service_response_group")
        .set("enable.auto.commit", "true")
        .set("auto.offset.reset", "earliest")
        .create()
        .expect("main.rs consumer create error");
    consumer
        .subscribe(&[response_topic])
        .expect("main.rs consumer subscribe error");

    let pending_responses: Arc<Mutex<HashMap<String, oneshot::Sender<KafkaResponse>>>> =
        Arc::new(Mutex::new(HashMap::new()));

    let state = ServiceState::new(
        redis_client,
        pool,
        producer,
        consumer,
        pending_responses.clone()
    );

    let response_consumer = state.kafka_response_consumer.clone();
    tokio::spawn(async move {
        let mut stream = response_consumer.stream();
        while let Some(msg) = stream.next().await {
            eprintln!("{:#?}", msg);
            match msg {
                Err(e) => eprintln!("Kafka consumer error: {}", e),
                Ok(msg) => {
                    if let Some(payload) = msg.payload() {
                        if let Ok(resp) = serde_json::from_slice::<KafkaResponse>(payload) {
                            let cid = resp.correlation_id.clone();
                            if let Some(sender) = pending_responses.lock().await.remove(&cid) {
                                let _ = sender.send(resp);
                            }
                        }
                    }
                }
            }
        }
    });

    // 6. Запуск HTTP сервера Axum
    let app = Router::new()
        .nest("/api/v1.0", service::router())
        .with_state(state);

    let listener = tokio::net::TcpListener::bind("0.0.0.0:24110")
        .await
        .expect("main.rs TCPlistener create error");
    println!("Server listening on port 24110");
    axum::serve(listener, app)
        .await
        .expect("main.rs axum serve from listener");

    Ok(())
}
