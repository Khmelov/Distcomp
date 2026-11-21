import { errorHandler } from '../../middlewares/errorHandler';
import { TopicMarkerRepository } from '../TopicMarkerRepository';

describe('TopicMarkerRepository', () => {
  let repo: TopicMarkerRepository;

  beforeEach(() => {
    repo = new TopicMarkerRepository();
  });

  test('removeLink удаляет связь', () => {
    repo.addLink(1, 1);
    repo.removeLink(1, 1);
    expect(repo.getMarkerIdsByTopicId(1)).toEqual([]);
    expect(repo.getTopicIdsByMarkerId(1)).toEqual([]);
  });

  test('removeTopic очищает связи темы', () => {
    repo.addLink(1, 1);
    repo.addLink(1, 2);
    repo.removeTopic(1);
    expect(repo.getMarkerIdsByTopicId(1)).toEqual([]);
    expect(repo.getTopicIdsByMarkerId(1)).toEqual([]);
    expect(repo.getTopicIdsByMarkerId(2)).toEqual([]);
  });

  test('removeMarker очищает связи маркера', () => {
    repo.addLink(1, 1);
    repo.addLink(2, 1);
    repo.removeMarker(1);
    expect(repo.getMarkerIdsByTopicId(1)).toEqual([]);
    expect(repo.getMarkerIdsByTopicId(2)).toEqual([]);
    expect(repo.getTopicIdsByMarkerId(1)).toEqual([]);
  });
});

describe('errorHandler', () => {
  test('возвращает 500 для непредвиденной ошибки', () => {
    const err = new Error('Unexpected');
    const req = {} as any;
    const res = {
      status: jest.fn().mockReturnThis(),
      json: jest.fn(),
    } as any;
    const next = jest.fn();

    const consoleSpy = jest
      .spyOn(console, 'error')
      .mockImplementation(() => {});

    errorHandler(err, req, res, next);

    expect(res.status).toHaveBeenCalledWith(500);
    expect(res.json).toHaveBeenCalledWith({
      errorMessage: 'Internal server error',
      errorCode: '50001',
    });

    consoleSpy.mockRestore();
  });
});
