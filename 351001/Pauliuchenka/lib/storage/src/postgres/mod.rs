pub use crate::postgres::{
    creator::PgCreatorRepo, label::PgLabelRepo, notice::PgNoticeRepo, topic::PgTopicRepo,
};

mod creator;
mod label;
mod notice;
mod topic;
