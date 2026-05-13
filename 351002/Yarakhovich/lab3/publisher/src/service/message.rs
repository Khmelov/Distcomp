use axum::{
    body::Body,
    extract::{Path, State},
    http::{HeaderMap, Method, Request, StatusCode},
    response::{IntoResponse, Response},
};
use domain::entities::IDType;
use reqwest::Client;

use crate::ServiceState;

async fn proxy_request(
    client: &Client,
    url: String,
    method: Method,
    headers: HeaderMap,
    body: Body,
) -> Response {
    let body_bytes = match axum::body::to_bytes(body, usize::MAX).await {
        Ok(b) => b,
        Err(_) => return (StatusCode::BAD_REQUEST, "Invalid request body").into_response(),
    };

    let reqwest_method = match method {
        Method::GET => reqwest::Method::GET,
        Method::POST => reqwest::Method::POST,
        Method::PUT => reqwest::Method::PUT,
        Method::DELETE => reqwest::Method::DELETE,
        _ => reqwest::Method::GET,
    };

    let mut req_builder = client.request(reqwest_method, &url);

    for (name, value) in headers.iter() {
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
            continue;
        }
        req_builder = req_builder.header(name, value);
    }

    req_builder = req_builder.header("Connection", "close");

    let req_builder = if matches!(method, Method::POST | Method::PUT) {
        req_builder.body(body_bytes.to_vec())
    } else {
        req_builder
    };

    match req_builder.send().await {
        Ok(resp) => {
            let status = resp.status();
            let mut response_builder = Response::builder().status(status);
            for (name, value) in resp.headers().iter() {
                response_builder = response_builder.header(name, value);
            }
            match resp.bytes().await {
                Ok(body_bytes) => response_builder.body(Body::from(body_bytes)).unwrap(),
                Err(_) => {
                    (StatusCode::BAD_GATEWAY, "Failed to read upstream response").into_response()
                }
            }
        }
        Err(err) => (StatusCode::BAD_GATEWAY, format!("Upstream error: {}", err)).into_response(),
    }
}

/// POST /api/v1.0/messages
pub async fn create_message(
    State(state): State<ServiceState>,
    req: Request<Body>,
) -> impl IntoResponse {
    let url = format!("{}/api/v1.0/messages", state.discussion_service_url);
    let (parts, body) = req.into_parts();
    proxy_request(&state.http_client, url, parts.method, parts.headers, body).await
}

/// GET /api/v1.0/messages
pub async fn list_messages(
    State(state): State<ServiceState>,
    req: Request<Body>,
) -> impl IntoResponse {
    let url = format!("{}/api/v1.0/messages", state.discussion_service_url);
    let (parts, body) = req.into_parts();
    proxy_request(&state.http_client, url, parts.method, parts.headers, body).await
}

/// GET /api/v1.0/messages/{id}
pub async fn get_message(
    State(state): State<ServiceState>,
    Path(id): Path<IDType>,
    req: Request<Body>,
) -> impl IntoResponse {
    let url = format!("{}/api/v1.0/messages/{}", state.discussion_service_url, id);
    let (parts, body) = req.into_parts();
    proxy_request(&state.http_client, url, parts.method, parts.headers, body).await
}

/// PUT /api/v1.0/messages/{id}
pub async fn update_message_id(
    State(state): State<ServiceState>,
    Path(id): Path<IDType>,
    req: Request<Body>,
) -> impl IntoResponse {
    let url = format!("{}/api/v1.0/messages/{}", state.discussion_service_url, id);
    let (parts, body) = req.into_parts();
    proxy_request(&state.http_client, url, parts.method, parts.headers, body).await
}

/// DELETE /api/v1.0/messages/{id}
pub async fn delete_message(
    State(state): State<ServiceState>,
    Path(id): Path<IDType>,
    req: Request<Body>,
) -> impl IntoResponse {
    let url = format!("{}/api/v1.0/messages/{}", state.discussion_service_url, id);
    let (parts, body) = req.into_parts();
    proxy_request(&state.http_client, url, parts.method, parts.headers, body).await
}
