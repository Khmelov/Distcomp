use axum::{
    Json,
    extract::{Path, State},
    http::StatusCode,
    response::IntoResponse,
};
use common::{MessageRequestTo, MessageResponseTo};
use domain::entities::{IDType, message::Message};
use serde_json::json;
use validator::Validate;

use crate::service::ServiceState;

/// POST /api/v1.0/messages
pub async fn create_message(
    State(state): State<ServiceState>,
    Json(payload): Json<MessageRequestTo>,
) -> impl IntoResponse {
    if payload.content.is_empty() {
        return (StatusCode::BAD_REQUEST, Json(json!({})));
    }

    let message: Message = match payload.try_into() {
        Ok(m) => m,
        Err(_) => return (StatusCode::BAD_REQUEST, Json(json!({}))),
    };

    if message.validate().is_err() {
        return (StatusCode::BAD_REQUEST, Json(json!({})));
    }

    match state.message_storage.save(message).await {
        Ok(saved_message) => (
            StatusCode::CREATED,
            Json(json!(MessageResponseTo::from(saved_message))),
        ),
        Err(_) => (StatusCode::INTERNAL_SERVER_ERROR, Json(json!({}))),
    }
}

/// GET /api/v1.0/messages
pub async fn list_messages(State(state): State<ServiceState>) -> impl IntoResponse {
    match state.message_storage.list().await {
        Ok(messages) => {
            let dtos: Vec<MessageResponseTo> =
                messages.into_iter().map(MessageResponseTo::from).collect();
            (StatusCode::OK, Json(dtos))
        }
        Err(_) => (StatusCode::INTERNAL_SERVER_ERROR, Json(Vec::new())),
    }
}

/// GET /api/v1.0/messages/{id}
pub async fn get_message(
    State(state): State<ServiceState>,
    Path(id): Path<IDType>,
) -> impl IntoResponse {
    match state.message_storage.get(id).await {
        Ok(Some(message)) => (
            StatusCode::OK,
            Json(json!(MessageResponseTo::from(message))),
        ),
        Ok(None) => (StatusCode::NOT_FOUND, Json(json!({}))),
        Err(_) => (StatusCode::INTERNAL_SERVER_ERROR, Json(json!({}))),
    }
}

/// PUT /api/v1.0/messages/{id} or /api/v1.0/messages/
pub async fn update_message_id(
    State(state): State<ServiceState>,
    Path(id): Path<IDType>,
    Json(payload): Json<MessageRequestTo>,
) -> impl IntoResponse {
    let message: Message = match payload.try_into() {
        Ok(m) => m,
        Err(_) => return (StatusCode::BAD_REQUEST, Json(json!({}))),
    };

    if message.validate().is_err() {
        return (StatusCode::BAD_REQUEST, Json(json!({})));
    }

    match state.message_storage.get(id).await {
        Ok(Some(_)) => match state.message_storage.update(id, message).await {
            Ok(updated) => (
                StatusCode::OK,
                Json(json!(MessageResponseTo::from(updated))),
            ),
            Err(_) => (StatusCode::INTERNAL_SERVER_ERROR, Json(json!({}))),
        },
        Ok(None) => (StatusCode::NOT_FOUND, Json(json!({}))),
        Err(_) => (StatusCode::INTERNAL_SERVER_ERROR, Json(json!({}))),
    }
}

/// DELETE /api/v1.0/messages/{id}
pub async fn delete_message(
    State(state): State<ServiceState>,
    Path(id): Path<IDType>,
) -> impl IntoResponse {
    match state.message_storage.delete(id).await {
        Ok(()) => StatusCode::NO_CONTENT,
        Err(_) => StatusCode::NOT_FOUND,
    }
}
