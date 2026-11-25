import { PaginationParams } from '../repositories/IRepository';

export function getPaginationOptions(params?: PaginationParams) {
  const page = Math.max(1, params?.page || 1);
  const size = Math.max(1, params?.size || 10);
  const skip = (page - 1) * size;

  const orderBy = params?.sortBy
    ? { [params.sortBy]: params.sortOrder || 'asc' }
    : { id: 'asc' as const };

  return {
    skip,
    take: size,
    orderBy,
  };
}
