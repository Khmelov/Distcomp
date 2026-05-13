use domain::entities::{IDType, label::Label};

/// Label request DTO
#[derive(Debug, serde::Deserialize)]
pub struct LabelRequestTo {
    pub name: String,
}

/// Label response DTO
#[derive(Debug, serde::Serialize)]
pub struct LabelResponseTo {
    pub id: IDType,
    pub name: String,
}

impl Into<Label> for LabelRequestTo {
    fn into(self) -> Label {
        Label {
            id: IDType::default(),
            name: self.name,
        }
    }
}
impl From<Label> for LabelRequestTo {
    fn from(value: Label) -> Self {
        Self { name: value.name }
    }
}

impl Into<Label> for LabelResponseTo {
    fn into(self) -> Label {
        Label {
            id: self.id,
            name: self.name,
        }
    }
}
impl From<Label> for LabelResponseTo {
    fn from(value: Label) -> Self {
        Self {
            id: value.id,
            name: value.name,
        }
    }
}
