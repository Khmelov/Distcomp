import { Request } from 'express';
import { PaginationParams } from '../repositories/IRepository';

export function parsePaginationQuery(req: Request): PaginationParams {
  return {
    page: req.query.page ? Number(req.query.page) : undefined,
    size: req.query.size ? Number(req.query.size) : undefined,
    sortBy: req.query.sortBy ? String(req.query.sortBy) : undefined,
    sortOrder: req.query.sortOrder === 'desc' ? 'desc' : 'asc',
  };
}
