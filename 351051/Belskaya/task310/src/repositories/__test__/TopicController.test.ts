import { TopicRepository } from '../TopicRepository';

describe('TopicRepository', () => {
  test('findByAuthorId', () => {
    const repo = new TopicRepository();
    repo.save({
      id: 1,
      authorId: 5,
      title: 'T',
      content: 'C',
      created: new Date(),
      modified: new Date(),
    });
    const result = repo.findByAuthorId(5);
    expect(result).toHaveLength(1);
  });
});
