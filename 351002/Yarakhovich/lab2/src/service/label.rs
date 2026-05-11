use super::ServiceState;
use axum::{
    Json,
    extract::{Path, State},
    http::StatusCode,
    response::IntoResponse,
};
use common::dto::label::{LabelRequestTo, LabelResponseTo};
use domain::entities::{IDType, label::Label};
use serde_json::json;
use sqlx::Error::Database;
use validator::Validate;

/// POST /api/v1.0/labels
pub async fn create_label(
    State(state): State<ServiceState>,
    Json(payload): Json<LabelRequestTo>,
) -> impl IntoResponse {
    let label: Label = payload.try_into().unwrap(); // Assuming you need to handle conversion properly
    match label.validate() {
        Ok(()) => match state.label_storage.write().await.create(label).await {
            Ok(label) => (
                StatusCode::CREATED,
                Json(json!(LabelResponseTo::from(label))),
            ),
            Err(Database(e)) if e.code().as_deref() == Some("23505") => {
                (StatusCode::FORBIDDEN, Json(json!({})))
            }
            Err(_) => (StatusCode::INTERNAL_SERVER_ERROR, Json(json!({}))),
        },
        Err(_) => (StatusCode::BAD_REQUEST, Json(json!({}))),
    }
}

/// GET /api/v1.0/labels
pub async fn list_labels(State(state): State<ServiceState>) -> impl IntoResponse {
    match state.label_storage.read().await.list().await {
        Ok(labels) => {
            let dtos: Vec<LabelResponseTo> =
                labels.into_iter().map(LabelResponseTo::from).collect();
            (StatusCode::OK, Json(dtos))
        }
        Err(_) => (StatusCode::INTERNAL_SERVER_ERROR, Json(Vec::new())),
    }
}

/// GET /api/v1.0/labels/{id}
pub async fn get_label(
    State(state): State<ServiceState>,
    Path(id): Path<IDType>,
) -> impl IntoResponse {
    match state.label_storage.read().await.get(id).await {
        Ok(Some(label)) => (StatusCode::OK, Json(json!(LabelResponseTo::from(label)))),
        Ok(None) => (StatusCode::NOT_FOUND, Json(json!({}))),
        Err(_) => (StatusCode::INTERNAL_SERVER_ERROR, Json(json!({}))),
    }
}

/// PUT /api/v1.0/labels/{id} or /api/v1.0/labels/
pub async fn update_label_id(
    State(state): State<ServiceState>,
    Path(id): Path<IDType>,
    Json(payload): Json<LabelRequestTo>,
) -> impl IntoResponse {
    let label: Label = payload.into();
    match label.validate() {
        Ok(()) => {
            let res = state
                .label_storage
                .write()
                .await
                .update(id, label)
                .await
                .unwrap();
            (StatusCode::OK, Json(json!(LabelResponseTo::from(res))))
        }
        Err(_) => (StatusCode::BAD_REQUEST, Json(json!({}))),
    }
}

/// DELETE /api/v1.0/labels/{id}
pub async fn delete_label(
    State(state): State<ServiceState>,
    Path(id): Path<IDType>,
) -> impl IntoResponse {
    match state.label_storage.write().await.delete(id).await {
        Ok(()) => StatusCode::NO_CONTENT,
        Err(_) => StatusCode::NOT_FOUND,
    }
}
