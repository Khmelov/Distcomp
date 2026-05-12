use axum::{
    Json,
    extract::{Path, State, rejection::JsonRejection},
    http::StatusCode,
    response::IntoResponse,
};
use common::{TopicRequestTo, TopicResponseTo};
use domain::entities::{IDType, topic::Topic};
use serde_json::json;
use sqlx::Error::Database;
use validator::Validate;

use crate::service::ServiceState;

/// POST /api/v1.0/topics
pub async fn create_topic(
    State(state): State<ServiceState>,
    payload: Result<Json<TopicRequestTo>, JsonRejection>,
) -> impl IntoResponse {
    let payload = if let Ok(Json(payload)) = payload {
        payload
    } else {
        return (StatusCode::BAD_REQUEST, Json(json!({})));
    };
    if payload.title.is_empty() {
        return (StatusCode::BAD_REQUEST, Json(json!({})));
    }
    let topic: Topic = payload.try_into().unwrap();
    if topic.validate().is_err() {
        return (StatusCode::BAD_REQUEST, Json(json!({})));
    }

    match state.topic_storage.write().await.create(topic).await {
        Ok(topic) => (
            StatusCode::CREATED,
            Json(json!(TopicResponseTo::from(topic))),
        ),
        Err(Database(e)) if e.code().as_deref() == Some("23505") => {
            (StatusCode::FORBIDDEN, Json(json!({})))
        }
        Err(Database(e)) if e.code().as_deref() == Some("23503") => {
            (StatusCode::FORBIDDEN, Json(json!({})))
        }
        Err(_) => (StatusCode::INTERNAL_SERVER_ERROR, Json(json!({}))),
    }
}

/// GET /api/v1.0/topics
pub async fn list_topics(State(state): State<ServiceState>) -> impl IntoResponse {
    match state.topic_storage.read().await.list().await {
        Ok(topics) => {
            let dtos: Vec<TopicResponseTo> =
                topics.into_iter().map(TopicResponseTo::from).collect();
            (StatusCode::OK, Json(dtos))
        }
        Err(e) => {
            eprintln!("❌ list_topics error: {}", e);
            (
                StatusCode::INTERNAL_SERVER_ERROR,
                Json(Vec::<TopicResponseTo>::new()),
            )
        }
    }
}

/// GET /api/v1.0/topics/{id}
pub async fn get_topic(
    State(state): State<ServiceState>,
    Path(id): Path<IDType>,
) -> impl IntoResponse {
    match state.topic_storage.read().await.get(id).await {
        Ok(Some(topic)) => (StatusCode::OK, Json(json!(TopicResponseTo::from(topic)))),
        Ok(None) => (StatusCode::NOT_FOUND, Json(json!({}))),
        Err(_) => (StatusCode::INTERNAL_SERVER_ERROR, Json(json!({}))),
    }
}

/// PUT /api/v1.0/topics/{id} or /api/v1.0/topics/
pub async fn update_topic_id(
    State(state): State<ServiceState>,
    Path(id): Path<IDType>,
    Json(payload): Json<TopicRequestTo>,
) -> impl IntoResponse {
    let topic: Topic = payload.into();
    match topic.validate() {
        Ok(()) => {
            let res = state
                .topic_storage
                .write()
                .await
                .update(id, topic)
                .await
                .unwrap();
            (StatusCode::OK, Json(json!(TopicResponseTo::from(res))))
        }
        Err(_) => (StatusCode::BAD_REQUEST, Json(json!({}))),
    }
}

/// DELETE /api/v1.0/topics/{id}
pub async fn delete_topic(
    State(state): State<ServiceState>,
    Path(id): Path<IDType>,
) -> impl IntoResponse {
    match state.topic_storage.write().await.delete(id).await {
        Ok(()) => StatusCode::NO_CONTENT,
        Err(_) => StatusCode::NOT_FOUND,
    }
}
