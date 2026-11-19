export class IdGenerator {
  private static counters: Map<string, number> = new Map();

  static next(entityName: string): number {
    const current = this.counters.get(entityName) || 0;
    const next = current + 1;
    this.counters.set(entityName, next);
    return next;
  }
}