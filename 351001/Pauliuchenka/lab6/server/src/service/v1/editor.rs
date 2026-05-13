use super::ServiceState;
use axum::{
    Json,
    extract::{Path, State},
    http::StatusCode,
    response::IntoResponse,
};

use common::dto::creator::{CreatorRequestTo, CreatorResponseTo};
use domain::entities::{IDType, creator::Creator};
use serde_json::json;
use sqlx::Error::Database;
use validator::Validate;

/// POST /api/v1.0/creators
pub async fn create_creator(
    State(state): State<ServiceState>,
    Json(payload): Json<CreatorRequestTo>,
) -> impl IntoResponse {
    let creator: Creator = payload.into();
    match creator.validate() {
        Ok(()) => {
            match state.creator_storage.create(creator).await {
                Ok(creator) => (
                    StatusCode::CREATED,
                    Json(json!(CreatorResponseTo::from(creator))),
                ),
                Err(Database(e)) if e.code().as_deref() == Some("23505") => {
                    // 23505 - unique violation в PostgreSQL
                    (StatusCode::FORBIDDEN, Json(json!({})))
                }
                Err(_) => (StatusCode::INTERNAL_SERVER_ERROR, Json(json!({}))),
            }
        }
        Err(_) => (StatusCode::BAD_REQUEST, Json(json!({}))),
    }
}

/// GET /api/v1.0/creators
pub async fn list_creators(State(state): State<ServiceState>) -> impl IntoResponse {
    match state.creator_storage.list().await {
        Ok(creators) => {
            let dtos: Vec<CreatorResponseTo> =
                creators.into_iter().map(CreatorResponseTo::from).collect();
            (StatusCode::OK, Json(dtos))
        }
        Err(_) => (StatusCode::INTERNAL_SERVER_ERROR, Json(Vec::new())),
    }
}

/// GET /api/v1.0/creators/{id}
pub async fn get_creator(
    State(state): State<ServiceState>,
    Path(id): Path<IDType>,
) -> impl IntoResponse {
    match state.creator_storage.get(id).await {
        Ok(Some(creator)) => (StatusCode::OK, Json(json!(CreatorResponseTo::from(creator)))),
        Ok(None) => (StatusCode::NOT_FOUND, Json(json!({}))),
        Err(_) => (StatusCode::INTERNAL_SERVER_ERROR, Json(json!({}))),
    }
}

/// PUT /api/v1.0/creators/{id} or /api/v1.0/creators/
pub async fn update_creator_id(
    State(state): State<ServiceState>,
    Path(id): Path<IDType>,
    Json(payload): Json<CreatorRequestTo>,
) -> impl IntoResponse {
    let creator: Creator = payload.into();
    match creator.validate() {
        Ok(()) => {
            let res = state.creator_storage.update(id, creator).await.unwrap();
            (StatusCode::OK, Json(json!(CreatorResponseTo::from(res))))
        }
        Err(_) => (StatusCode::BAD_REQUEST, Json(json!({}))),
    }
}

/// DELETE /api/v1.0/creators/{id}
pub async fn delete_creator(
    State(state): State<ServiceState>,
    Path(id): Path<IDType>,
) -> impl IntoResponse {
    match state.creator_storage.delete(id).await {
        Ok(()) => StatusCode::NO_CONTENT,
        Err(_) => StatusCode::NOT_FOUND,
    }
}
