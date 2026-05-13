use chrono::Utc;
use dashmap::DashMap;
use std::io::Error;
use std::sync::Arc;
use std::sync::atomic::{AtomicI64, Ordering};

use domain::entities::{IDType, story::*};

#[derive(Clone)]
pub struct InMemoryStoryRepo {
    store: Arc<DashMap<IDType, Story>>,
    next_id: Arc<AtomicI64>,
}

impl InMemoryStoryRepo {
    pub fn new() -> Self {
        Self {
            store: Arc::new(DashMap::new()),
            next_id: Arc::new(AtomicI64::new(1)),
        }
    }
}

impl InMemoryStoryRepo {
    pub fn create(&mut self, mut entity: Story) -> Result<Story, Error> {
        let id = self.next_id.fetch_add(1, Ordering::SeqCst);
        entity.id = id;
        entity.created = Utc::now();
        entity.modified = entity.created;
        self.store.insert(id, entity.clone());
        Ok(entity)
    }
    pub fn get(&self, id: IDType) -> Result<Option<Story>, Error> {
        Ok(self.store.get(&id).map(|entity| entity.clone()))
    }
    pub fn update(&mut self, id: IDType, mut entity: Story) -> Result<Story, Error> {
        if let Some(ent) = self.store.get(&id) {
            entity.id = ent.id;
            entity.created = ent.created;
            entity.modified = Utc::now();
            Ok(entity)
        } else {
            Err(Error::new(
                std::io::ErrorKind::NotSeekable,
                "Update storage error",
            ))
        }
    }
    pub fn delete(&mut self, id: IDType) -> Result<(), Error> {
        if self.store.contains_key(&id) {
            self.store.remove(&id);
            Ok(())
        } else {
            Err(Error::new(
                std::io::ErrorKind::NotSeekable,
                "Delete storage error",
            ))
        }
    }
    pub fn list(&self) -> Result<Vec<Story>, Error> {
        Ok(self
            .store
            .iter()
            .map(|entity| entity.clone())
            .collect::<Vec<Story>>())
    }
}
