use domain::entities::{IDType, message::Message};

/// Message request DTO
#[derive(Debug, serde::Deserialize)]
pub struct MessageRequestTo {
    #[serde(rename = "storyId")]
    pub story_id: IDType,
    pub content: String,
}

/// Message response DTO
#[derive(Debug, serde::Serialize)]
pub struct MessageResponseTo {
    pub id: IDType,
    #[serde(rename = "storyId")]
    pub story_id: IDType,
    pub content: String,
}

impl Into<Message> for MessageRequestTo {
    fn into(self) -> Message {
        Message {
            id: IDType::default(),
            story_id: self.story_id,
            content: self.content,
        }
    }
}
impl From<Message> for MessageRequestTo {
    fn from(value: Message) -> Self {
        Self {
            story_id: value.story_id,
            content: value.content,
        }
    }
}

impl Into<Message> for MessageResponseTo {
    fn into(self) -> Message {
        Message {
            id: self.id,
            story_id: self.story_id,
            content: self.content,
        }
    }
}
impl From<Message> for MessageResponseTo {
    fn from(value: Message) -> Self {
        Self {
            id: value.id,
            story_id: value.story_id,
            content: value.content,
        }
    }
}
