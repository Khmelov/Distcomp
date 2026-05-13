use super::ServiceState;
use axum::{
    Router,
    routing::{get, post},
};

mod creator;
mod label;
mod notice;
mod topic;

pub fn router() -> Router<ServiceState> {
    let notices_router = Router::new()
        .route(
            "/",
            post(notice::create_notice)
                .get(notice::list_notices)
                .put(notice::update_notice_id),
        )
        .route(
            "/{id}",
            get(notice::get_notice)
                .put(notice::update_notice_id)
                .delete(notice::delete_notice),
        );

    let creators_router = Router::new()
        .route("/", post(creator::create_creator).get(creator::list_creators))
        .route(
            "/{id}",
            get(creator::get_creator)
                .put(creator::update_creator_id)
                .delete(creator::delete_creator),
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
        .route("/", post(topic::create_topic).get(topic::list_stories))
        .route(
            "/{id}",
            get(topic::get_topic)
                .put(topic::update_topic_id)
                .delete(topic::delete_topic),
        );

    Router::new()
        .nest("/creators", creators_router)
        .nest("/labels", labels_router)
        .nest("/notices", notices_router)
        .nest("/stories", stories_router)
}
