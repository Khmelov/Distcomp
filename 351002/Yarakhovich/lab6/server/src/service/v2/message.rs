use crate::ServiceState;
use crate::{KafkaRequest, KafkaResponse};
use axum::{
    body::Body,
    extract::{Path, State},
    http::{HeaderMap, Method, Request, StatusCode, header},
    response::{IntoResponse, Response},
};
use domain::entities::IDType;
use rdkafka::producer::{FutureRecord};
use redis::Commands;
use tokio::sync::oneshot;
use std::time::Duration;
use uuid::Uuid;

const KAFKA_REQUEST_TOPIC: &str = "service_requests";
const REQUEST_TIMEOUT_SECS: u64 = 10;
const CACHE_TTL_SECS: usize = 60;

async fn proxy_request(
    state: &ServiceState,
    method: Method,
    path: String,
    headers: HeaderMap,
    body: Body,
) -> Response {
    let cache_key = format!("cache:{}", path);

    if method == Method::GET {
        let mut conn = state.redis_client.clone();
        if let Ok(cached_val) = conn.get::<_, Vec<u8>>(&cache_key) {
            if !cached_val.is_empty() {
                if let Ok(resp) = serde_json::from_slice::<KafkaResponse>(&cached_val) {
                    return build_response(resp);
                }
            }
        }
    }

    // Подготовка тела для Kafka
    let body_bytes = match axum::body::to_bytes(body, usize::MAX).await {
        Ok(b) if !b.is_empty() => Some(b.to_vec()),
        Ok(_) => None,
        Err(_) => return (StatusCode::BAD_REQUEST, "Invalid request body").into_response(),
    };

    let cid = Uuid::new_v4().to_string();
    let (tx, rx) = oneshot::channel();
    state.pending_responses.lock().await.insert(cid.clone(), tx);

    let kafka_request = KafkaRequest {
        correlation_id: cid.clone(),
        method: method.to_string(),
        path: path.clone(),
        headers: headers
            .iter()
            .map(|(k, v)| (k.to_string(), v.to_str().unwrap_or("").to_string()))
            .collect(),
        body: body_bytes,
    };

    let payload = serde_json::to_vec(&kafka_request).unwrap();
    let record = FutureRecord::to(KAFKA_REQUEST_TOPIC)
        .key(&cid)
        .payload(&payload);

    if let Err((e, _)) = state.kafka_producer.send(record, Duration::from_secs(0)).await {
        state.pending_responses.lock().await.remove(&cid);
        return (StatusCode::INTERNAL_SERVER_ERROR, format!("Kafka error: {}", e)).into_response();
    }

    let kafka_resp = match tokio::time::timeout(Duration::from_secs(REQUEST_TIMEOUT_SECS), rx).await {
        Ok(Ok(resp)) => resp,
        _ => {
            state.pending_responses.lock().await.remove(&cid);
            return (StatusCode::GATEWAY_TIMEOUT, "Backend timeout").into_response();
        }
    };

    let mut conn = state.redis_client.clone();
    if method == Method::GET && kafka_resp.status == 200 {
        if let Ok(serialized) = serde_json::to_vec(&kafka_resp) {
            let _: Result<(), _> = conn.set_ex(&cache_key, serialized, CACHE_TTL_SECS);
        }
    } else if method != Method::GET {
        let _: Result<(), _> = conn.del(&cache_key);
        if path.contains("/messages/") {
            let _: Result<(), _> = conn.del("cache:/api/v1.0/messages");
        }
    }

    build_response(kafka_resp)
}

fn build_response(kafka_resp: KafkaResponse) -> Response {
    let status = StatusCode::from_u16(kafka_resp.status).unwrap_or(StatusCode::INTERNAL_SERVER_ERROR);
    let mut builder = Response::builder().status(status);

    if let Some(headers) = builder.headers_mut() {
        for (k, v) in kafka_resp.headers {
            if let (Ok(name), Ok(value)) = (header::HeaderName::from_bytes(k.as_bytes()), header::HeaderValue::from_str(&v)) {
                headers.insert(name, value);
            }
        }
    }

    let body = kafka_resp.body.map(Body::from).unwrap_or_else(Body::empty);
    builder.body(body).unwrap()
}

pub async fn create_message(
    State(state): State<ServiceState>,
    req: Request<Body>,
) -> impl IntoResponse {
    let (parts, body) = req.into_parts();
    proxy_request(
        &state,
        parts.method,
        "/api/v1.0/messages".to_string(),
        parts.headers,
        body,
    )
    .await
}

pub async fn list_messages(
    State(state): State<ServiceState>,
    req: Request<Body>,
) -> impl IntoResponse {
    let (parts, body) = req.into_parts();
    proxy_request(
        &state,
        parts.method,
        "/api/v1.0/messages".to_string(),
        parts.headers,
        body,
    )
    .await
}

pub async fn get_message(
    State(state): State<ServiceState>,
    Path(id): Path<IDType>,
    req: Request<Body>,
) -> impl IntoResponse {
    let path = format!("/api/v1.0/messages/{}", id);
    let (parts, body) = req.into_parts();
    proxy_request(&state, parts.method, path, parts.headers, body).await
}

pub async fn update_message_id(
    State(state): State<ServiceState>,
    Path(id): Path<IDType>,
    req: Request<Body>,
) -> impl IntoResponse {
    let path = format!("/api/v1.0/messages/{}", id);
    let (parts, body) = req.into_parts();
    proxy_request(&state, parts.method, path, parts.headers, body).await
}

pub async fn delete_message(
    State(state): State<ServiceState>,
    Path(id): Path<IDType>,
    req: Request<Body>,
) -> impl IntoResponse {
    let path = format!("/api/v1.0/messages/{}", id);
    let (parts, body) = req.into_parts();
    proxy_request(&state, parts.method, path, parts.headers, body).await
}
