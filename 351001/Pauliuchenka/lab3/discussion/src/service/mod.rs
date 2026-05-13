use super::ServiceState;
use axum::{
    Router,
    routing::{get, post},
};

mod notice;

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

    let router = Router::new().nest("/notices", notices_router);
    router
}
