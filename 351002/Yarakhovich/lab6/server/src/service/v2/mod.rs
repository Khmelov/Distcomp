use crate::ServiceState;
use axum::{
    Router,
    routing::{get, post},
};

mod editor;
mod label;
mod message;
mod story;
#[allow(unused)]
mod auth;

pub fn router() -> Router<ServiceState> {
    let messages_router = Router::new()
        .route(
            "/",
            post(message::create_message)
                .get(message::list_messages)
                .put(message::update_message_id),
        )
        .route(
            "/{id}",
            get(message::get_message)
                .put(message::update_message_id)
                .delete(message::delete_message),
        );

    let editors_router = Router::new()
        .route("/", post(editor::create_editor).get(editor::list_editors))
        .route(
            "/{id}",
            get(editor::get_editor)
                .put(editor::update_editor_id)
                .delete(editor::delete_editor),
        );

    let labels_router = Router::new()
        .route("/", post(label::create_label).get(label::list_labels))
        .route(
            "/{id}",
            get(label::get_label)
                .put(label::update_label_id)
                .delete(label::delete_label),
        );

    let stories_router = Router::new()
        .route("/", post(story::create_story).get(story::list_stories))
        .route(
            "/{id}",
            get(story::get_story)
                .put(story::update_story_id)
                .delete(story::delete_story),
        );

    Router::new()
        .nest("/editors", editors_router)
        .nest("/labels", labels_router)
        .nest("/messages", messages_router)
        .nest("/stories", stories_router)
}
