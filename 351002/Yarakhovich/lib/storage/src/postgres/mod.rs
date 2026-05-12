pub use crate::postgres::{
    editor::PgEditorRepo, label::PgLabelRepo, message::PgMessageRepo, story::PgStoryRepo,
};

mod editor;
mod label;
mod message;
mod story;
