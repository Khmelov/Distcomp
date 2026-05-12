use domain::entities::{IDType, notice::Notice};

/// Notice request DTO
#[derive(Debug, serde::Deserialize)]
pub struct NoticeRequestTo {
    #[serde(rename = "topicId")]
    pub topic_id: IDType,
    pub content: String,
}

/// Notice response DTO
#[derive(Debug, serde::Serialize)]
pub struct NoticeResponseTo {
    pub id: IDType,
    #[serde(rename = "topicId")]
    pub topic_id: IDType,
    pub content: String,
}

impl Into<Notice> for NoticeRequestTo {
    fn into(self) -> Notice {
        Notice {
            id: IDType::default(),
            topic_id: self.topic_id,
            content: self.content,
        }
    }
}
impl From<Notice> for NoticeRequestTo {
    fn from(value: Notice) -> Self {
        Self {
            topic_id: value.topic_id,
            content: value.content,
        }
    }
}

impl Into<Notice> for NoticeResponseTo {
    fn into(self) -> Notice {
        Notice {
            id: self.id,
            topic_id: self.topic_id,
            content: self.content,
        }
    }
}
impl From<Notice> for NoticeResponseTo {
    fn from(value: Notice) -> Self {
        Self {
            id: value.id,
            topic_id: value.topic_id,
            content: value.content,
        }
    }
}
