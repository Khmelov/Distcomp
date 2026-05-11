use domain::entities::{IDType, editor::Editor};

/// Editor request DTO
#[derive(Debug, serde::Deserialize)]
pub struct EditorRequestTo {
    pub login: String,
    pub password: String,
    pub firstname: String,
    pub lastname: String,
}
/// Editor response DTO
#[derive(Debug, serde::Serialize)]
pub struct EditorResponseTo {
    pub id: IDType,
    pub login: String,
    pub password: String,
    pub firstname: String,
    pub lastname: String,
}

impl Into<Editor> for EditorRequestTo {
    fn into(self) -> Editor {
        Editor {
            id: IDType::default(),
            login: self.login,
            password: self.password,
            firstname: self.firstname,
            lastname: self.lastname,
        }
    }
}
impl From<Editor> for EditorRequestTo {
    fn from(value: Editor) -> Self {
        Self {
            login: value.login,
            password: value.password,
            firstname: value.firstname,
            lastname: value.lastname,
        }
    }
}

impl Into<Editor> for EditorResponseTo {
    fn into(self) -> Editor {
        Editor {
            id: self.id,
            login: self.login,
            password: self.password,
            firstname: self.firstname,
            lastname: self.lastname,
        }
    }
}
impl From<Editor> for EditorResponseTo {
    fn from(value: Editor) -> Self {
        Self {
            id: value.id,
            login: value.login,
            password: value.password,
            firstname: value.firstname,
            lastname: value.lastname,
        }
    }
}
