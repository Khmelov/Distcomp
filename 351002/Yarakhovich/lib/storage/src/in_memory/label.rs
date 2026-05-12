use dashmap::DashMap;
use std::io::Error;
use std::sync::Arc;
use std::sync::atomic::{AtomicI64, Ordering};

use domain::entities::{IDType, label::*};

#[derive(Clone)]
pub struct InMemoryLabelRepo {
    store: Arc<DashMap<IDType, Label>>,
    next_id: Arc<AtomicI64>,
}

impl InMemoryLabelRepo {
    pub fn new() -> Self {
        Self {
            store: Arc::new(DashMap::new()),
            next_id: Arc::new(AtomicI64::new(1)),
        }
    }
}

impl InMemoryLabelRepo {
    pub fn create(&mut self, mut entity: Label) -> Result<Label, Error> {
        let id = self.next_id.fetch_add(1, Ordering::SeqCst);
        entity.id = id;
        self.store.insert(id, entity.clone());
        Ok(entity)
    }
    pub fn get(&self, id: IDType) -> Result<Option<Label>, Error> {
        Ok(self.store.get(&id).map(|entity| entity.clone()))
    }
    pub fn update(&mut self, id: IDType, mut entity: Label) -> Result<Label, Error> {
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
    pub fn list(&self) -> Result<Vec<Label>, Error> {
        Ok(self
            .store
            .iter()
            .map(|entity| entity.clone())
            .collect::<Vec<Label>>())
    }
}
