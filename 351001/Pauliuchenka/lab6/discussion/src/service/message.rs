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

pub async fn do_create_notice(
    state: &ServiceState,
    payload: NoticeRequestTo,
) -> Result<NoticeResponseTo, (StatusCode, String)> {
    eprintln!("do_create_notice: {:#?}", payload);
    if payload.content.is_empty() {
        return Err((StatusCode::BAD_REQUEST, "Content empty".into()));
    }
    let notice: Notice = payload
        .try_into()
        .map_err(|_| (StatusCode::BAD_REQUEST, "Invalid data".into()))?;
    notice
        .validate()
        .map_err(|_| (StatusCode::BAD_REQUEST, "Validation failed".into()))?;
    state
        .notice_storage
        .save(notice)
        .await
        .map(|s| s.into())
        .map_err(|_| (StatusCode::INTERNAL_SERVER_ERROR, "DB error".into()))
}

pub async fn do_list_notices(state: &ServiceState) -> Result<Vec<NoticeResponseTo>, StatusCode> {
    eprintln!("lost notices calles");
    state
        .notice_storage
        .list()
        .await
        .map(|v| v.into_iter().map(|m| m.into()).collect())
        .map_err(|_| StatusCode::INTERNAL_SERVER_ERROR)
}

pub async fn do_get_notice(
    state: &ServiceState,
    id: IDType,
) -> Result<Option<NoticeResponseTo>, StatusCode> {
    eprintln!("do_get_notice: {:#?}", id);
    match state.notice_storage.get(id).await {
        Ok(Some(m)) => Ok(Some(m.into())),
        Ok(None) => Ok(None),
        Err(_) => Err(StatusCode::INTERNAL_SERVER_ERROR),
    }
}

pub async fn do_update_notice(
    state: &ServiceState,
    id: IDType,
    payload: NoticeRequestTo,
) -> Result<NoticeResponseTo, (StatusCode, String)> {
    let notice: Notice = payload
        .try_into()
        .map_err(|_| (StatusCode::BAD_REQUEST, "Invalid data".into()))?;
    notice
        .validate()
        .map_err(|_| (StatusCode::BAD_REQUEST, "Validation failed".into()))?;
    if state
        .notice_storage
        .get(id)
        .await
        .map_err(|_| (StatusCode::INTERNAL_SERVER_ERROR, "DB error".into()))?
        .is_none()
    {
        return Err((StatusCode::NOT_FOUND, "Not found".into()));
    }
    state
        .notice_storage
        .update(id, notice)
        .await
        .map(|m| m.into())
        .map_err(|_| (StatusCode::INTERNAL_SERVER_ERROR, "Update failed".into()))
}

pub async fn do_delete_notice(state: &ServiceState, id: IDType) -> Result<(), StatusCode> {
    state
        .notice_storage
        .delete(id)
        .await
        .map_err(|_| StatusCode::NOT_FOUND)
}

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
        Ok(Some(_)) => match state.notice_storage.update(id, notice).await {
            Ok(updated) => (
                StatusCode::OK,
                Json(json!(NoticeResponseTo::from(updated))),
            ),
            Err(_) => (StatusCode::INTERNAL_SERVER_ERROR, Json(json!({}))),
        },
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
