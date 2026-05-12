use sqlx::prelude::FromRow;
use validator::Validate;

use crate::entities::IDType;

/// Notice entity
#[derive(Debug, Clone, Validate, FromRow, scylla::FromRow)]
pub struct Notice {
    pub id: IDType,
    pub topic_id: IDType,
    #[validate(length(min = 2, max = 2048))]
    pub content: String,
}
