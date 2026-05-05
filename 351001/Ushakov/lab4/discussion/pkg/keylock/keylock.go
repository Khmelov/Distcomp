package keylock

import "sync"

type KeyLock struct {
	locks sync.Map
}

func New() *KeyLock {
	return &KeyLock{}
}

func (k *KeyLock) Lock(key string) {
	m, _ := k.locks.LoadOrStore(key, &sync.Mutex{})
	m.(*sync.Mutex).Lock()
}

func (k *KeyLock) Unlock(key string) {
	m, ok := k.locks.Load(key)
	if !ok {
		return
	}
	m.(*sync.Mutex).Unlock()
}
