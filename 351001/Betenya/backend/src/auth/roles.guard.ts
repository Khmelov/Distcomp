import {
  CanActivate,
  ExecutionContext,
  HttpException,
  HttpStatus,
  Injectable,
} from '@nestjs/common';
import { Reflector } from '@nestjs/core';
import { ROLES_KEY } from './roles.decorator';

@Injectable()
export class RolesGuard implements CanActivate {
  constructor(private reflector: Reflector) {}

  canActivate(context: ExecutionContext): boolean {
    const requiredRoles = this.reflector.getAllAndOverride<string[]>(ROLES_KEY, [
      context.getHandler(),
      context.getClass(),
    ]);

    if (!requiredRoles) return true;

    const { user } = context.switchToHttp().getRequest();
    if (!user) {
      throw new HttpException(
        { errorMessage: 'Access denied', errorCode: 40300 },
        HttpStatus.FORBIDDEN,
      );
    }

    if (!requiredRoles.includes(user.role)) {
      throw new HttpException(
        { errorMessage: 'Insufficient permissions', errorCode: 40300 },
        HttpStatus.FORBIDDEN,
      );
    }

    return true;
  }
}
