mod kafka_types;
mod service;

use std::sync::Arc;
use std::time::Duration;

use axum::Router;
use rdkafka::ClientConfig;
use rdkafka::consumer::{Consumer, StreamConsumer};
use rdkafka::message::Message;
use rdkafka::producer::{FutureProducer, FutureRecord};
use serde_json::json;
use storage::cassandra::message::CassandraMessageRepo;
use tokio_stream::StreamExt;

use kafka_types::{KafkaRequest, KafkaResponse};
use service::message::{
    do_create_message, do_delete_message, do_get_message, do_list_messages, do_update_message,
};

const KAFKA_REQUESTS_TOPIC: &str = "service_requests";
const KAFKA_RESPONSES_TOPIC: &str = "service_responses";

#[derive(Clone)]
pub struct ServiceState {
    pub message_storage: Arc<CassandraMessageRepo>,
}

impl ServiceState {
    pub async fn new() -> Self {
        ServiceState {
            message_storage: Arc::new(
                CassandraMessageRepo::new("localhost:9042", "distcomp")
                    .await
                    .unwrap(),
            ),
        }
    }
}

async fn handle_kafka_request(state: &ServiceState, req: KafkaRequest) -> Option<KafkaResponse> {
    let method = req.method.as_str();
    let path = req.path.as_str();
    let body_bytes = req.body.clone();

    let parse_json = |data: &Option<Vec<u8>>| -> Option<serde_json::Value> {
        data.as_ref().and_then(|b| serde_json::from_slice(b).ok())
    };

    let (status, headers, body) = match (method, path) {
        ("POST", "/api/v1.0/messages") => {
            if let Some(val) = parse_json(&body_bytes) {
                match serde_json::from_value::<common::MessageRequestTo>(val) {
                    Ok(payload) => match do_create_message(state, payload).await {
                        Ok(dto) => (
                            201,
                            vec![("content-type".into(), "application/json".into())],
                            serde_json::to_vec(&dto).ok(),
                        ),
                        Err((code, msg)) => (
                            code.as_u16(),
                            vec![],
                            serde_json::to_vec(&json!({ "error": msg })).ok(),
                        ),
                    },
                    Err(_) => (400, vec![], Some(b"Invalid JSON".to_vec())),
                }
            } else {
                (400, vec![], Some(b"Missing body".to_vec()))
            }
        }
        ("GET", "/api/v1.0/messages") => match do_list_messages(state).await {
            Ok(dtos) => (
                200,
                vec![("content-type".into(), "application/json".into())],
                serde_json::to_vec(&dtos).ok(),
            ),
            Err(code) => (code.as_u16(), vec![], None),
        },
        ("GET", path) if path.starts_with("/api/v1.0/messages/") => {
            let id_str = &path["/api/v1.0/messages/".len()..];
            if let Ok(id) = id_str.parse::<domain::entities::IDType>() {
                match do_get_message(state, id).await {
                    Ok(Some(dto)) => (
                        200,
                        vec![("content-type".into(), "application/json".into())],
                        serde_json::to_vec(&dto).ok(),
                    ),
                    Ok(None) => (404, vec![], None),
                    Err(code) => (code.as_u16(), vec![], None),
                }
            } else {
                (400, vec![], Some(b"Invalid ID".to_vec()))
            }
        }
        ("PUT", path) if path.starts_with("/api/v1.0/messages/") => {
            let id_str = &path["/api/v1.0/messages/".len()..];
            if let Ok(id) = id_str.parse::<domain::entities::IDType>() {
                if let Some(val) = parse_json(&body_bytes) {
                    match serde_json::from_value::<common::MessageRequestTo>(val) {
                        Ok(payload) => match do_update_message(state, id, payload).await {
                            Ok(dto) => (
                                200,
                                vec![("content-type".into(), "application/json".into())],
                                serde_json::to_vec(&dto).ok(),
                            ),
                            Err((code, msg)) => (
                                code.as_u16(),
                                vec![],
                                serde_json::to_vec(&json!({ "error": msg })).ok(),
                            ),
                        },
                        Err(_) => (400, vec![], Some(b"Invalid JSON".to_vec())),
                    }
                } else {
                    (400, vec![], Some(b"Missing body".to_vec()))
                }
            } else {
                (400, vec![], Some(b"Invalid ID".to_vec()))
            }
        }
        ("DELETE", path) if path.starts_with("/api/v1.0/messages/") => {
            let id_str = &path["/api/v1.0/messages/".len()..];
            if let Ok(id) = id_str.parse::<domain::entities::IDType>() {
                match do_delete_message(state, id).await {
                    Ok(()) => (204, vec![], None),
                    Err(code) => (code.as_u16(), vec![], None),
                }
            } else {
                (400, vec![], Some(b"Invalid ID".to_vec()))
            }
        }
        _ => (404, vec![], Some(b"Not Found".to_vec())),
    };

    Some(KafkaResponse {
        correlation_id: req.correlation_id,
        status,
        headers,
        body,
    })
}

#[tokio::main]
async fn main() {
    let state = ServiceState::new().await;

    // Kafka
    let producer: FutureProducer = ClientConfig::new()
        .set("bootstrap.servers", "localhost:9092")
        .set("message.timeout.ms", "5000")
        .create()
        .expect("Failed to create Kafka producer");

    let consumer: StreamConsumer = ClientConfig::new()
        .set("bootstrap.servers", "localhost:9092")
        .set("group.id", "messages_backend_group")
        .set("auto.offset.reset", "earliest")
        .create()
        .expect("Failed to create Kafka consumer");
    consumer
        .subscribe(&[KAFKA_REQUESTS_TOPIC])
        .expect("Failed to subscribe");

    let producer_clone = producer.clone();
    let state_clone = state.clone();
    tokio::spawn(async move {
        let mut stream = consumer.stream();
        while let Some(msg) = stream.next().await {
            if let Ok(borrowed) = msg {
                if let Some(payload) = borrowed.payload() {
                    if let Ok(req) = serde_json::from_slice::<KafkaRequest>(payload) {
                        if let Some(resp) = handle_kafka_request(&state_clone, req).await {
                            if let Ok(payload) = serde_json::to_vec(&resp) {
                                let record = FutureRecord::to(KAFKA_RESPONSES_TOPIC)
                                    .key(&resp.correlation_id)
                                    .payload(&payload);
                                let _ = producer_clone.send(record, Duration::from_secs(5)).await;
                            }
                        }
                    }
                }
            }
        }
    });

    // REST (без изменений)
    let serve = service::router();
    let api = Router::new().nest("/api/v1.0", serve);
    let listener = tokio::net::TcpListener::bind("0.0.0.0:24130")
        .await
        .unwrap();
    axum::serve(listener, api.with_state(state)).await.unwrap();
}
