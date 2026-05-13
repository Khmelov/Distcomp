use sqlx::prelude::FromRow;
use validator::Validate;

use crate::entities::IDType;

/// Label entity
#[derive(Debug, Clone, Validate, FromRow)]
pub struct Label {
    pub id: IDType,
    #[validate(length(min = 2, max = 32))]
    pub name: String,
}
