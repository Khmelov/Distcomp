use crate::ServiceState;
use crate::KafkaRequest;
use axum::{
    body::Body,
    extract::{Path, State},
    http::{HeaderMap, Method, Request, StatusCode, header},
    response::{IntoResponse, Response},
};
use domain::entities::IDType;
use rdkafka::producer::{FutureRecord};
use std::time::Duration;
use uuid::Uuid;

const KAFKA_REQUEST_TOPIC: &str = "service_requests";
const REQUEST_TIMEOUT_SECS: u64 = 10;

async fn proxy_request(
    state: &ServiceState,
    method: Method,
    path: String,
    headers: HeaderMap,
    body: Body,
) -> Response {
    let body_bytes = match axum::body::to_bytes(body, usize::MAX).await {
        Ok(b) if !b.is_empty() => Some(b.to_vec()),
        Ok(_) => None,
        Err(_) => return (StatusCode::BAD_REQUEST, "Invalid request body").into_response(),
    };
    let headers_vec: Vec<(String, String)> = headers
        .iter()
        .filter_map(|(name, value)| {
            let name_str = name.as_str();
            if matches!(
                name_str,
                "host"
                    | "content-length"
                    | "transfer-encoding"
                    | "connection"
                    | "upgrade"
                    | "http2-settings"
                    | "keep-alive"
                    | "proxy-connection"
                    | "expect"
            ) {
                None
            } else {
                value
                    .to_str()
                    .ok()
                    .map(|v| (name_str.to_string(), v.to_string()))
            }
        })
        .collect();

    let cid = Uuid::new_v4().to_string();

    let (tx, rx) = tokio::sync::oneshot::channel();

    {
        let mut pending = state.pending_responses.lock().await;
        pending.insert(cid.clone(), tx);
    }

    let request = KafkaRequest {
        correlation_id: cid.clone(),
        method: method.as_str().to_string(),
        path,
        headers: headers_vec,
        body: body_bytes,
    };
    let payload = serde_json::to_vec(&request).unwrap();

    let record = FutureRecord::to(KAFKA_REQUEST_TOPIC)
        .key(&cid)
        .payload(&payload);
    match state
        .kafka_producer
        .send(record, Duration::from_secs(5))
        .await
    {
        Ok(_) => (),
        Err((e, _)) => {
            let mut pending = state.pending_responses.lock().await;
            pending.remove(&cid);
            return (
                StatusCode::BAD_GATEWAY,
                format!("Kafka produce error: {}", e),
            )
                .into_response();
        }
    };

    let kafka_resp = match tokio::time::timeout(
        std::time::Duration::from_secs(REQUEST_TIMEOUT_SECS),
        rx,
    )
    .await
    {
        Ok(Ok(resp)) => resp,
        Ok(Err(_)) => {
            let mut pending = state.pending_responses.lock().await;
            pending.remove(&cid);
            return (
                StatusCode::BAD_GATEWAY,
                "Internal error: response channel closed",
            )
                .into_response();
        }
        Err(_) => {
            // таймаут
            let mut pending = state.pending_responses.lock().await;
            pending.remove(&cid);
            return (StatusCode::GATEWAY_TIMEOUT, "Upstream timeout").into_response();
        }
    };

    let status =
        StatusCode::from_u16(kafka_resp.status).unwrap_or(StatusCode::INTERNAL_SERVER_ERROR);
    let mut builder = Response::builder().status(status);
    for (name, value) in kafka_resp.headers {
        if let Ok(name) = header::HeaderName::from_bytes(name.as_bytes()) {
            if let Ok(value) = header::HeaderValue::from_str(&value) {
                builder = builder.header(name, value);
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
