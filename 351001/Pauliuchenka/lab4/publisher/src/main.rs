mod service;
mod kafka_types;

use std::collections::HashMap;
use std::sync::Arc;
use axum::Router;
use rdkafka::consumer::{Consumer, StreamConsumer};
use rdkafka::producer::FutureProducer;
use rdkafka::ClientConfig;
use sqlx::postgres::PgPoolOptions;
use sqlx::{Executor, PgPool};
use storage::postgres::{PgCreatorRepo, PgLabelRepo, PgNoticeRepo, PgTopicRepo};
use tokio::sync::{Mutex, oneshot};
use rdkafka::message::Message;
use tokio_stream::StreamExt;

use kafka_types::*;

#[derive(Clone)]
pub struct ServiceState {
    pub kafka_producer: FutureProducer,
    pub kafka_response_consumer: Arc<StreamConsumer>,
    pub pending_responses: Arc<Mutex<HashMap<String, oneshot::Sender<KafkaResponse>>>>,
    pub creator_storage: Arc<PgCreatorRepo>,
    pub label_storage: Arc<PgLabelRepo>,
    pub notice_storage: Arc<PgNoticeRepo>,
    pub topic_storage: Arc<PgTopicRepo>,
}

impl ServiceState {
    pub fn new(
        pool: Arc<PgPool>,
        kafka_producer: FutureProducer,
        kafka_response_consumer: StreamConsumer,
        pending_responses: Arc<Mutex<HashMap<String, oneshot::Sender<KafkaResponse>>>>,
    ) -> Self {
        ServiceState {
            creator_storage: Arc::new(PgCreatorRepo::new(pool.clone())),
            label_storage: Arc::new(PgLabelRepo::new(pool.clone())),
            notice_storage: Arc::new(PgNoticeRepo::new(pool.clone())),
            topic_storage: Arc::new(PgTopicRepo::new(pool)),
            kafka_producer,
            kafka_response_consumer: Arc::new(kafka_response_consumer),
            pending_responses,
        }
    }
}

// ---------- Main ----------
#[tokio::main]
async fn main() -> Result<(), ()> {
    // 1. PostgreSQL pool
    let pool = PgPoolOptions::new()
        .after_connect(|conn, _| {
            Box::pin(async move {
                conn.execute("SET search_path TO distcomp").await?;
                Ok(())
            })
        })
        .connect("postgres://postgres:postgres@172.17.0.1/distcomp")
        .await.expect("main.rs PgPool connect error");
    let pool = Arc::new(pool);

    let kafka_brokers = "localhost:9092";
    let response_topic = "service_responses";

    let producer: FutureProducer = ClientConfig::new()
        .set("bootstrap.servers", kafka_brokers)
        .set("message.timeout.ms", "5000")
        .create().expect("main.rs producer create error");

    let consumer: StreamConsumer = ClientConfig::new()
        .set("bootstrap.servers", kafka_brokers)
        .set("group.id", "my_service_response_group")
        .set("enable.auto.commit", "true")
        .set("auto.offset.reset", "earliest")
        .create().expect("main.rs consumer create error");
    consumer.subscribe(&[response_topic]).expect("main.rs consumer subscribe error");

    let pending_responses: Arc<Mutex<HashMap<String, oneshot::Sender<KafkaResponse>>>> =
        Arc::new(Mutex::new(HashMap::new()));

    let state = ServiceState::new(
        pool,
        producer,
        consumer,
        pending_responses.clone(),
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

    let app = Router::new()
        .nest("/api/v1.0", service::router())
        .with_state(state);

    let listener = tokio::net::TcpListener::bind("0.0.0.0:24110").await.expect("main.rs TCPlistener create error");
    println!("Server listening on port 24110");
    axum::serve(listener, app).await.expect("main.rs axum serve from listener");

    Ok(())
}