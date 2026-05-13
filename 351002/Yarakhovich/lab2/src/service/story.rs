use axum::{
    Json,
    extract::{Path, State, rejection::JsonRejection},
    http::StatusCode,
    response::IntoResponse,
};
use common::{StoryRequestTo, StoryResponseTo};
use domain::entities::{IDType, story::Story};
use serde_json::json;
use sqlx::Error::Database;
use validator::Validate;

use crate::service::ServiceState;

/// POST /api/v1.0/stories
pub async fn create_story(
    State(state): State<ServiceState>,
    payload: Result<Json<StoryRequestTo>, JsonRejection>,
) -> impl IntoResponse {
    let payload = if let Ok(Json(payload)) = payload {
        payload
    } else {
        return (StatusCode::BAD_REQUEST, Json(json!({})));
    };
    if payload.title.is_empty() {
        return (StatusCode::BAD_REQUEST, Json(json!({})));
    }
    let story: Story = payload.try_into().unwrap();
    if story.validate().is_err() {
        return (StatusCode::BAD_REQUEST, Json(json!({})));
    }

    match state.story_storage.write().await.create(story).await {
        Ok(story) => (
            StatusCode::CREATED,
            Json(json!(StoryResponseTo::from(story))),
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

/// GET /api/v1.0/stories
pub async fn list_stories(State(state): State<ServiceState>) -> impl IntoResponse {
    match state.story_storage.read().await.list().await {
        Ok(stories) => {
            let dtos: Vec<StoryResponseTo> =
                stories.into_iter().map(StoryResponseTo::from).collect();
            (StatusCode::OK, Json(dtos))
        }
        Err(e) => {
            eprintln!("❌ list_stories error: {}", e);
            (
                StatusCode::INTERNAL_SERVER_ERROR,
                Json(Vec::<StoryResponseTo>::new()),
            )
        }
    }
}

/// GET /api/v1.0/stories/{id}
pub async fn get_story(
    State(state): State<ServiceState>,
    Path(id): Path<IDType>,
) -> impl IntoResponse {
    match state.story_storage.read().await.get(id).await {
        Ok(Some(story)) => (StatusCode::OK, Json(json!(StoryResponseTo::from(story)))),
        Ok(None) => (StatusCode::NOT_FOUND, Json(json!({}))),
        Err(_) => (StatusCode::INTERNAL_SERVER_ERROR, Json(json!({}))),
    }
}

/// PUT /api/v1.0/stories/{id} or /api/v1.0/stories/
pub async fn update_story_id(
    State(state): State<ServiceState>,
    Path(id): Path<IDType>,
    Json(payload): Json<StoryRequestTo>,
) -> impl IntoResponse {
    let story: Story = payload.into();
    match story.validate() {
        Ok(()) => {
            let res = state
                .story_storage
                .write()
                .await
                .update(id, story)
                .await
                .unwrap();
            (StatusCode::OK, Json(json!(StoryResponseTo::from(res))))
        }
        Err(_) => (StatusCode::BAD_REQUEST, Json(json!({}))),
    }
}

/// DELETE /api/v1.0/stories/{id}
pub async fn delete_story(
    State(state): State<ServiceState>,
    Path(id): Path<IDType>,
) -> impl IntoResponse {
    match state.story_storage.write().await.delete(id).await {
        Ok(()) => StatusCode::NO_CONTENT,
        Err(_) => StatusCode::NOT_FOUND,
    }
}
