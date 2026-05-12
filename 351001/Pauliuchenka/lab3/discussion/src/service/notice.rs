use axum::{
    Json,
    extract::{Path, State},
    http::StatusCode,
    response::IntoResponse,
};
use common::{NoticeRequestTo, NoticeResponseTo};
use domain::entities::{IDType, notice::Notice};
use serde_json::json;
use validator::Validate;

use crate::service::ServiceState;

/// POST /api/v1.0/notices
pub async fn create_notice(
    State(state): State<ServiceState>,
    Json(payload): Json<NoticeRequestTo>,
) -> impl IntoResponse {
    if payload.content.is_empty() {
        return (StatusCode::BAD_REQUEST, Json(json!({})));
    }

    let notice: Notice = match payload.try_into() {
        Ok(m) => m,
        Err(_) => return (StatusCode::BAD_REQUEST, Json(json!({}))),
    };

    if notice.validate().is_err() {
        return (StatusCode::BAD_REQUEST, Json(json!({})));
    }

    match state.notice_storage.save(notice).await {
        Ok(saved_notice) => (
            StatusCode::CREATED,
            Json(json!(NoticeResponseTo::from(saved_notice))),
        ),
        Err(_) => (StatusCode::INTERNAL_SERVER_ERROR, Json(json!({}))),
    }
}

/// GET /api/v1.0/notices
pub async fn list_notices(State(state): State<ServiceState>) -> impl IntoResponse {
    match state.notice_storage.list().await {
        Ok(notices) => {
            let dtos: Vec<NoticeResponseTo> =
                notices.into_iter().map(NoticeResponseTo::from).collect();
            (StatusCode::OK, Json(dtos))
        }
        Err(_) => (StatusCode::INTERNAL_SERVER_ERROR, Json(Vec::new())),
    }
}

/// GET /api/v1.0/notices/{id}
pub async fn get_notice(
    State(state): State<ServiceState>,
    Path(id): Path<IDType>,
) -> impl IntoResponse {
    match state.notice_storage.get(id).await {
        Ok(Some(notice)) => (
            StatusCode::OK,
            Json(json!(NoticeResponseTo::from(notice))),
        ),
        Ok(None) => (StatusCode::NOT_FOUND, Json(json!({}))),
        Err(_) => (StatusCode::INTERNAL_SERVER_ERROR, Json(json!({}))),
    }
}

/// PUT /api/v1.0/notices/{id} or /api/v1.0/notices/
pub async fn update_notice_id(
    State(state): State<ServiceState>,
    Path(id): Path<IDType>,
    Json(payload): Json<NoticeRequestTo>,
) -> impl IntoResponse {
    let notice: Notice = match payload.try_into() {
        Ok(m) => m,
        Err(_) => return (StatusCode::BAD_REQUEST, Json(json!({}))),
    };

    if notice.validate().is_err() {
        return (StatusCode::BAD_REQUEST, Json(json!({})));
    }

    match state.notice_storage.get(id).await {
        Ok(Some(_)) => {
            match state
                .notice_storage
                .update(id, notice)
                .await
            {
                Ok(updated) => (
                    StatusCode::OK,
                    Json(json!(NoticeResponseTo::from(updated))),
                ),
                Err(_) => (StatusCode::INTERNAL_SERVER_ERROR, Json(json!({}))),
            }
        }
        Ok(None) => (StatusCode::NOT_FOUND, Json(json!({}))),
        Err(_) => (StatusCode::INTERNAL_SERVER_ERROR, Json(json!({}))),
    }
}

/// DELETE /api/v1.0/notices/{id}
pub async fn delete_notice(
    State(state): State<ServiceState>,
    Path(id): Path<IDType>,
) -> impl IntoResponse {
    match state.notice_storage.delete(id).await {
        Ok(()) => StatusCode::NO_CONTENT,
        Err(_) => StatusCode::NOT_FOUND,
    }
}
