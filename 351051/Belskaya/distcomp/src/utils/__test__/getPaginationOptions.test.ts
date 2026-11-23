import { getPaginationOptions } from '../getPaginationOptions';

describe('getPaginationOptions', () => {
  it('should return default pagination options', () => {
    expect(getPaginationOptions()).toEqual({
      skip: 0,
      take: 10,
      orderBy: { id: 'asc' },
    });
  });

  it('should calculate skip using page and size', () => {
    expect(getPaginationOptions({ page: 3, size: 5 })).toEqual({
      skip: 10,
      take: 5,
      orderBy: { id: 'asc' },
    });
  });

  it('should use minimum page and size values', () => {
    expect(getPaginationOptions({ page: 0, size: -5 })).toEqual({
      skip: 0,
      take: 1,
      orderBy: { id: 'asc' },
    });
  });

  it('should use custom sort field', () => {
    expect(
      getPaginationOptions({
        page: 1,
        size: 10,
        sortBy: 'title',
      })
    ).toEqual({
      skip: 0,
      take: 10,
      orderBy: { title: 'asc' },
    });
  });

  it('should use descending sort order', () => {
    expect(
      getPaginationOptions({
        sortBy: 'created',
        sortOrder: 'desc',
      })
    ).toEqual({
      skip: 0,
      take: 10,
      orderBy: { created: 'desc' },
    });
  });

  it('should use default sort order when sortOrder is not provided', () => {
    expect(
      getPaginationOptions({
        sortBy: 'title',
      })
    ).toEqual({
      skip: 0,
      take: 10,
      orderBy: { title: 'asc' },
    });
  });
});
