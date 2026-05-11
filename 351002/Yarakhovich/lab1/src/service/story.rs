use axum::{
    Json,
    extract::{Path, State},
    http::StatusCode,
    response::IntoResponse,
};
use common::{StoryRequestTo, StoryResponseTo};
use domain::entities::{IDType, story::Story};
use serde_json::json;
use validator::Validate;

use crate::service::ServiceState;

/// POST /api/v1.0/stories
pub async fn create_story(
    State(state): State<ServiceState>,
    Json(payload): Json<StoryRequestTo>,
) -> impl IntoResponse {
    match state
        .story_storage
        .write()
        .unwrap()
        .create(payload.try_into().unwrap())
    {
        Ok(story) => (
            StatusCode::CREATED,
            Json(json!(StoryResponseTo::from(story))),
        ),
        Err(_) => (StatusCode::INTERNAL_SERVER_ERROR, Json(json!({}))),
    }
}

/// GET /api/v1.0/stories
pub async fn list_stories(State(state): State<ServiceState>) -> impl IntoResponse {
    match state.story_storage.read().unwrap().list() {
        Ok(stories) => {
            let dtos: Vec<StoryResponseTo> =
                stories.into_iter().map(StoryResponseTo::from).collect();
            (StatusCode::OK, Json(dtos))
        }
        Err(_) => (StatusCode::INTERNAL_SERVER_ERROR, Json(Vec::new())),
    }
}

/// GET /api/v1.0/stories/{id}
pub async fn get_story(
    State(state): State<ServiceState>,
    Path(id): Path<IDType>,
) -> impl IntoResponse {
    match state.story_storage.read().unwrap().get(id) {
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
                .unwrap()
                .update(id, story)
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
    match state.story_storage.write().unwrap().delete(id) {
        Ok(()) => StatusCode::NO_CONTENT,
        Err(_) => StatusCode::NOT_FOUND,
    }
}
