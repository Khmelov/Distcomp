use domain::entities::{IDType, creator::Creator};

/// Creator request DTO
#[derive(Debug, serde::Deserialize)]
pub struct CreatorRequestTo {
    pub login: String,
    pub password: String,
    pub firstname: String,
    pub lastname: String,
}
/// Creator response DTO
#[derive(Debug, serde::Serialize)]
pub struct CreatorResponseTo {
    pub id: IDType,
    pub login: String,
    pub password: String,
    pub firstname: String,
    pub lastname: String,
}

impl Into<Creator> for CreatorRequestTo {
    fn into(self) -> Creator {
        Creator {
            id: IDType::default(),
            login: self.login,
            password: self.password,
            firstname: self.firstname,
            lastname: self.lastname,
        }
    }
}
impl From<Creator> for CreatorRequestTo {
    fn from(value: Creator) -> Self {
        Self {
            login: value.login,
            password: value.password,
            firstname: value.firstname,
            lastname: value.lastname,
        }
    }
}

impl Into<Creator> for CreatorResponseTo {
    fn into(self) -> Creator {
        Creator {
            id: self.id,
            login: self.login,
            password: self.password,
            firstname: self.firstname,
            lastname: self.lastname,
        }
    }
}
impl From<Creator> for CreatorResponseTo {
    fn from(value: Creator) -> Self {
        Self {
            id: value.id,
            login: value.login,
            password: value.password,
            firstname: value.firstname,
            lastname: value.lastname,
        }
    }
}
