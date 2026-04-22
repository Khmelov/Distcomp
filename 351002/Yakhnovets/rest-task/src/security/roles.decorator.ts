import { SetMetadata } from '@nestjs/common';
import { WriterRole } from './role.enum';

export const ROLES_KEY = 'roles';
export const Roles = (...roles: WriterRole[]) => SetMetadata(ROLES_KEY, roles);
