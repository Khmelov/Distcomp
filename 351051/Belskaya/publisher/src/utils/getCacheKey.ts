import { PaginationParams } from '../repositories/IRepository';

export function getPaginationCacheKey(params?: PaginationParams): string {
  if (!params) return 'default';

  return `page_${params.page ?? 0}_size_${params.size ?? 0}_sort_${params.sortBy ?? ''}_${params.sortOrder ?? ''}`;
}
