use sqlx::prelude::FromRow;
use validator::Validate;

use crate::entities::IDType;

/// Editor entity
#[derive(Debug, Clone, Validate, FromRow)]
pub struct Editor {
    pub id: IDType,
    #[validate(length(min = 2, max = 64))]
    pub login: String,
    #[validate(length(min = 8, max = 128))]
    pub password: String,
    #[validate(length(min = 2, max = 64))]
    pub firstname: String,
    #[validate(length(min = 2, max = 64))]
    pub lastname: String,
}
