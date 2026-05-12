use super::ServiceState;
use axum::{
    Router,
    routing::{get, post},
};

pub mod message;

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

    let router = Router::new().nest("/messages", messages_router);
    router
}
