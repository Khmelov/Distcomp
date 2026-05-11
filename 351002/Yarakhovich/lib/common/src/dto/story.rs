use chrono::{DateTime, Utc};
use domain::entities::{IDType, story::Story};

/// Story request DTO
#[derive(Debug, serde::Deserialize)]
pub struct StoryRequestTo {
    #[serde(rename = "editorId")]
    pub editor_id: IDType,
    pub title: String,
    pub content: String,
}

/// Story response DTO
#[derive(Debug, serde::Serialize)]
pub struct StoryResponseTo {
    pub id: IDType,
    #[serde(rename = "editorId")]
    pub editor_id: IDType,
    pub title: String,
    pub content: String,
    pub created: DateTime<Utc>,
    pub modified: DateTime<Utc>,
}

impl Into<Story> for StoryRequestTo {
    fn into(self) -> Story {
        Story {
            id: IDType::default(),
            editor_id: self.editor_id,
            title: self.title,
            content: self.content,
            created: DateTime::default(),
            modified: DateTime::default(),
        }
    }
}
impl From<Story> for StoryRequestTo {
    fn from(value: Story) -> Self {
        Self {
            editor_id: value.editor_id,
            title: value.title,
            content: value.content,
        }
    }
}

impl Into<Story> for StoryResponseTo {
    fn into(self) -> Story {
        Story {
            id: self.id,
            editor_id: self.editor_id,
            title: self.title,
            content: self.content,
            created: self.created,
            modified: self.modified,
        }
    }
}
impl From<Story> for StoryResponseTo {
    fn from(value: Story) -> Self {
        Self {
            id: value.id,
            editor_id: value.editor_id,
            title: value.title,
            content: value.content,
            created: value.created,
            modified: value.modified,
        }
    }
}
