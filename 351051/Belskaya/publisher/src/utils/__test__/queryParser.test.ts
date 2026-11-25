import { Request } from 'express';
import { parsePaginationQuery } from '../queryParser';

describe('parsePaginationQuery', () => {
  const createRequest = (query: Record<string, unknown>) =>
    ({ query }) as unknown as Request;

  it('should return default values for empty query', () => {
    expect(parsePaginationQuery(createRequest({}))).toEqual({
      page: undefined,
      size: undefined,
      sortBy: undefined,
      sortOrder: 'asc',
    });
  });

  it('should parse all pagination parameters', () => {
    expect(
      parsePaginationQuery(
        createRequest({
          page: '2',
          size: '15',
          sortBy: 'created',
          sortOrder: 'desc',
        })
      )
    ).toEqual({
      page: 2,
      size: 15,
      sortBy: 'created',
      sortOrder: 'desc',
    });
  });

  it('should convert page and size to numbers', () => {
    const result = parsePaginationQuery(
      createRequest({
        page: '3',
        size: '20',
      })
    );

    expect(result.page).toBe(3);
    expect(result.size).toBe(20);
    expect(typeof result.page).toBe('number');
    expect(typeof result.size).toBe('number');
  });

  it('should use ascending order when sortOrder is not desc', () => {
    expect(
      parsePaginationQuery(
        createRequest({
          sortOrder: 'invalid',
        })
      ).sortOrder
    ).toBe('asc');
  });

  it('should use ascending order when sortOrder is missing', () => {
    expect(parsePaginationQuery(createRequest({})).sortOrder).toBe('asc');
  });
});
