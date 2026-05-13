use chrono::{DateTime, Utc};
use domain::entities::{IDType, topic::Topic};

/// Topic request DTO
#[derive(Debug, serde::Deserialize)]
pub struct TopicRequestTo {
    #[serde(rename = "creatorId")]
    pub creator_id: IDType,
    pub title: String,
    pub content: String,
}

/// Topic response DTO
#[derive(Debug, serde::Serialize)]
pub struct TopicResponseTo {
    pub id: IDType,
    #[serde(rename = "creatorId")]
    pub creator_id: IDType,
    pub title: String,
    pub content: String,
    pub created: DateTime<Utc>,
    pub modified: DateTime<Utc>,
}

impl Into<Topic> for TopicRequestTo {
    fn into(self) -> Topic {
        Topic {
            id: IDType::default(),
            creator_id: self.creator_id,
            title: self.title,
            content: self.content,
            created: DateTime::default(),
            modified: DateTime::default(),
        }
    }
}
impl From<Topic> for TopicRequestTo {
    fn from(value: Topic) -> Self {
        Self {
            creator_id: value.creator_id,
            title: value.title,
            content: value.content,
        }
    }
}

impl Into<Topic> for TopicResponseTo {
    fn into(self) -> Topic {
        Topic {
            id: self.id,
            creator_id: self.creator_id,
            title: self.title,
            content: self.content,
            created: self.created,
            modified: self.modified,
        }
    }
}
impl From<Topic> for TopicResponseTo {
    fn from(value: Topic) -> Self {
        Self {
            id: value.id,
            creator_id: value.creator_id,
            title: value.title,
            content: value.content,
            created: value.created,
            modified: value.modified,
        }
    }
}
