use dashmap::DashMap;
use std::io::Error;
use std::sync::Arc;
use std::sync::atomic::{AtomicI64, Ordering};

use domain::entities::{IDType, notice::*};

#[derive(Clone)]
pub struct InMemoryNoticeRepo {
    store: Arc<DashMap<IDType, Notice>>,
    next_id: Arc<AtomicI64>,
}

impl InMemoryNoticeRepo {
    pub fn new() -> Self {
        Self {
            store: Arc::new(DashMap::new()),
            next_id: Arc::new(AtomicI64::new(1)),
        }
    }
}

impl InMemoryNoticeRepo {
    pub fn create(&mut self, mut entity: Notice) -> Result<Notice, Error> {
        let id = self.next_id.fetch_add(1, Ordering::SeqCst);
        entity.id = id;
        self.store.insert(id, entity.clone());
        Ok(entity)
    }
    pub fn get(&self, id: IDType) -> Result<Option<Notice>, Error> {
        Ok(self.store.get(&id).map(|entity| entity.clone()))
    }
    pub fn update(&mut self, id: IDType, mut entity: Notice) -> Result<Notice, Error> {
        if self.store.contains_key(&id) {
            entity.id = id;
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
    pub fn list(&self) -> Result<Vec<Notice>, Error> {
        Ok(self
            .store
            .iter()
            .map(|entity| entity.clone())
            .collect::<Vec<Notice>>())
    }
}
