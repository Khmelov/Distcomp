use serde::{Deserialize, Serialize};
use sqlx::prelude::FromRow;
use validator::Validate;

use crate::entities::IDType;

/// Message entity
#[derive(Debug, Clone, Validate, FromRow, scylla::FromRow, Deserialize, Serialize)]
pub struct Message {
    pub id: IDType,
    pub story_id: IDType,
    #[validate(length(min = 2, max = 2048))]
    pub content: String,
}
