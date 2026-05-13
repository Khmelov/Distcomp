use chrono::{DateTime, Utc};
use sqlx::prelude::FromRow;
use validator::Validate;

use crate::entities::IDType;

/// Story entity
#[derive(Debug, Clone, Validate, FromRow)]
pub struct Story {
    pub id: IDType,
    pub editor_id: IDType,
    #[validate(length(min = 2, max = 64))]
    pub title: String,
    #[validate(length(min = 4, max = 2048))]
    pub content: String,
    /// ISO8601
    pub created: DateTime<Utc>,
    /// ISO8601
    pub modified: DateTime<Utc>,
}
