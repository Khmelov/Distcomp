use serde::{Deserialize, Serialize};

#[derive(Serialize, Deserialize)]
pub struct KafkaRequest {
    pub correlation_id: String,
    pub method: String,
    pub path: String,
    pub headers: Vec<(String, String)>,
    pub body: Option<Vec<u8>>,
}

#[derive(Serialize, Deserialize, Clone)]
pub struct KafkaResponse {
    pub correlation_id: String,
    pub status: u16,
    pub headers: Vec<(String, String)>,
    pub body: Option<Vec<u8>>,
}
