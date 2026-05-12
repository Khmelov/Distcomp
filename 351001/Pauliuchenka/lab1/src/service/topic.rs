use axum::{
    Json,
    extract::{Path, State},
    http::StatusCode,
    response::IntoResponse,
};
use common::{TopicRequestTo, TopicResponseTo};
use domain::entities::{IDType, topic::Topic};
use serde_json::json;
use validator::Validate;

use crate::service::ServiceState;

/// POST /api/v1.0/topics
pub async fn create_topic(
    State(state): State<ServiceState>,
    Json(payload): Json<TopicRequestTo>,
) -> impl IntoResponse {
    match state
        .topic_storage
        .write()
        .unwrap()
        .create(payload.try_into().unwrap())
    {
        Ok(topic) => (
            StatusCode::CREATED,
            Json(json!(TopicResponseTo::from(topic))),
        ),
        Err(_) => (StatusCode::INTERNAL_SERVER_ERROR, Json(json!({}))),
    }
}

/// GET /api/v1.0/topics
pub async fn list_topics(State(state): State<ServiceState>) -> impl IntoResponse {
    match state.topic_storage.read().unwrap().list() {
        Ok(topics) => {
            let dtos: Vec<TopicResponseTo> =
                topics.into_iter().map(TopicResponseTo::from).collect();
            (StatusCode::OK, Json(dtos))
        }
        Err(_) => (StatusCode::INTERNAL_SERVER_ERROR, Json(Vec::new())),
    }
}

/// GET /api/v1.0/topics/{id}
pub async fn get_topic(
    State(state): State<ServiceState>,
    Path(id): Path<IDType>,
) -> impl IntoResponse {
    match state.topic_storage.read().unwrap().get(id) {
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
                .unwrap()
                .update(id, topic)
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
    match state.topic_storage.write().unwrap().delete(id) {
        Ok(()) => StatusCode::NO_CONTENT,
        Err(_) => StatusCode::NOT_FOUND,
    }
}
