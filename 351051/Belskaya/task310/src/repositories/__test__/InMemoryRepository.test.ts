import { InMemoryRepository } from '../InMemoryRepository';

class TestEntity {
  constructor(public id: number) {}
}

class TestRepository extends InMemoryRepository<TestEntity> {
  public testUpdate(id: number, data: Partial<TestEntity>) {
    return this.update(id, data);
  }
}

describe('InMemoryRepository', () => {
  test('update возвращает undefined для несуществующего ID', () => {
    const repo = new TestRepository();
    const result = repo.testUpdate(999, { id: 999 });
    expect(result).toBeUndefined();
  });
});
