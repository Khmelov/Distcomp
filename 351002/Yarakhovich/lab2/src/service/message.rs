use axum::{
    Json,
    extract::{Path, State},
    http::StatusCode,
    response::IntoResponse,
};
use common::{MessageRequestTo, MessageResponseTo};
use domain::entities::{IDType, message::Message};
use serde_json::json;
use sqlx::Error::Database;
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
    let message: Message = payload.try_into().unwrap();
    if message.validate() != Ok(()) {
        return (StatusCode::BAD_REQUEST, Json(json!({})));
    }

    match state.story_storage.read().await.get(message.story_id).await {
        Ok(Some(_story)) => match state.message_storage.write().await.create(message).await {
            Ok(message) => (
                StatusCode::CREATED,
                Json(json!(MessageResponseTo::from(message))),
            ),
            Err(Database(e)) if e.code().as_deref() == Some("23505") => {
                (StatusCode::CONFLICT, Json(json!({})))
            }
            Err(_) => (StatusCode::INTERNAL_SERVER_ERROR, Json(json!({}))),
        },
        Ok(None) => (StatusCode::NOT_FOUND, Json(json!({}))),
        Err(_) => (StatusCode::INTERNAL_SERVER_ERROR, Json(json!({}))),
    }
}

/// GET /api/v1.0/messages
pub async fn list_messages(State(state): State<ServiceState>) -> impl IntoResponse {
    match state.message_storage.read().await.list().await {
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
    match state.message_storage.read().await.get(id).await {
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
    let message: Message = payload.into();
    match message.validate() {
        Ok(()) => {
            let res = state
                .message_storage
                .write()
                .await
                .update(id, message)
                .await
                .unwrap();
            (StatusCode::OK, Json(json!(MessageResponseTo::from(res))))
        }
        Err(_) => (StatusCode::BAD_REQUEST, Json(json!({}))),
    }
}

/// DELETE /api/v1.0/messages/{id}
pub async fn delete_message(
    State(state): State<ServiceState>,
    Path(id): Path<IDType>,
) -> impl IntoResponse {
    match state.message_storage.write().await.delete(id).await {
        Ok(()) => StatusCode::NO_CONTENT,
        Err(_) => StatusCode::NOT_FOUND,
    }
}
