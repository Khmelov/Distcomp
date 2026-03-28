import {
  CallHandler,
  ExecutionContext,
  Injectable,
  NestInterceptor,
} from '@nestjs/common';
import { map, Observable } from 'rxjs';

@Injectable()
export class BigIntInterceptor implements NestInterceptor {
  intercept(context: ExecutionContext, next: CallHandler): Observable<any> {
    return next.handle().pipe(map((data) => this.serialize(data)));
  }

  private serialize(data: any): any {
    if (typeof data === 'bigint') {
      return Number(data);
    }
    if (Array.isArray(data)) {
      return data.map((item) => this.serialize(item));
    }
    if (typeof data === 'object' && data !== null) {
      return Object.fromEntries(
        Object.entries(data).map(([key, value]) => [
          key,
          this.serialize(value),
        ]),
      );
    }
    return data;
  }
}
