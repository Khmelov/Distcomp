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
    match state
        .notice_storage
        .write()
        .unwrap()
        .create(payload.try_into().unwrap())
    {
        Ok(notice) => (
            StatusCode::CREATED,
            Json(json!(NoticeResponseTo::from(notice))),
        ),
        Err(_) => (StatusCode::INTERNAL_SERVER_ERROR, Json(json!({}))),
    }
}

/// GET /api/v1.0/notices
pub async fn list_notices(State(state): State<ServiceState>) -> impl IntoResponse {
    match state.notice_storage.read().unwrap().list() {
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
    match state.notice_storage.read().unwrap().get(id) {
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
    let notice: Notice = payload.into();
    match notice.validate() {
        Ok(()) => {
            let res = state
                .notice_storage
                .write()
                .unwrap()
                .update(id, notice)
                .unwrap();
            (StatusCode::OK, Json(json!(NoticeResponseTo::from(res))))
        }
        Err(_) => (StatusCode::BAD_REQUEST, Json(json!({}))),
    }
}

/// DELETE /api/v1.0/notices/{id}
pub async fn delete_notice(
    State(state): State<ServiceState>,
    Path(id): Path<IDType>,
) -> impl IntoResponse {
    match state.notice_storage.write().unwrap().delete(id) {
        Ok(()) => StatusCode::NO_CONTENT,
        Err(_) => StatusCode::NOT_FOUND,
    }
}
