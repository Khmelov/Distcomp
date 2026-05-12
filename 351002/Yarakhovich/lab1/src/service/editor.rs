use super::ServiceState;
use axum::{
    Json,
    extract::{Path, State},
    http::StatusCode,
    response::IntoResponse,
};

use common::dto::editor::{EditorRequestTo, EditorResponseTo};
use domain::entities::{IDType, editor::Editor};
use serde_json::json;
use validator::Validate;

/// POST /api/v1.0/editors
pub async fn create_editor(
    State(state): State<ServiceState>,
    Json(payload): Json<EditorRequestTo>,
) -> impl IntoResponse {
    match state
        .editor_storage
        .write()
        .unwrap()
        .create(payload.try_into().unwrap())
    {
        Ok(editor) => (
            StatusCode::CREATED,
            Json(json!(EditorResponseTo::from(editor))),
        ),
        Err(_) => (StatusCode::INTERNAL_SERVER_ERROR, Json(json!({}))),
    }
}

/// GET /api/v1.0/editors
pub async fn list_editors(State(state): State<ServiceState>) -> impl IntoResponse {
    match state.editor_storage.read().unwrap().list() {
        Ok(editors) => {
            let dtos: Vec<EditorResponseTo> =
                editors.into_iter().map(EditorResponseTo::from).collect();
            (StatusCode::OK, Json(dtos))
        }
        Err(_) => (StatusCode::INTERNAL_SERVER_ERROR, Json(Vec::new())),
    }
}

/// GET /api/v1.0/editors/{id}
pub async fn get_editor(
    State(state): State<ServiceState>,
    Path(id): Path<IDType>,
) -> impl IntoResponse {
    match state.editor_storage.read().unwrap().get(id) {
        Ok(Some(editor)) => (StatusCode::OK, Json(json!(EditorResponseTo::from(editor)))),
        Ok(None) => (StatusCode::NOT_FOUND, Json(json!({}))),
        Err(_) => (StatusCode::INTERNAL_SERVER_ERROR, Json(json!({}))),
    }
}

/// PUT /api/v1.0/editors/{id} or /api/v1.0/editors/
pub async fn update_editor_id(
    State(state): State<ServiceState>,
    Path(id): Path<IDType>,
    Json(payload): Json<EditorRequestTo>,
) -> impl IntoResponse {
    let editor: Editor = payload.into();
    match editor.validate() {
        Ok(()) => {
            let res = state
                .editor_storage
                .write()
                .unwrap()
                .update(id, editor)
                .unwrap();
            (StatusCode::OK, Json(json!(EditorResponseTo::from(res))))
        }
        Err(_) => (StatusCode::BAD_REQUEST, Json(json!({}))),
    }
}

/// DELETE /api/v1.0/editors/{id}
pub async fn delete_editor(
    State(state): State<ServiceState>,
    Path(id): Path<IDType>,
) -> impl IntoResponse {
    match state.editor_storage.write().unwrap().delete(id) {
        Ok(()) => StatusCode::NO_CONTENT,
        Err(_) => StatusCode::NOT_FOUND,
    }
}
