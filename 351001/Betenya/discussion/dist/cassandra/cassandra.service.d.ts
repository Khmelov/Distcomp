import { OnModuleDestroy, OnModuleInit } from '@nestjs/common';
import { Client } from 'cassandra-driver';
export declare class CassandraService implements OnModuleInit, OnModuleDestroy {
    private readonly logger;
    readonly client: Client;
    constructor();
    onModuleInit(): Promise<void>;
    onModuleDestroy(): Promise<void>;
    private connectWithRetry;
    private initSchema;
}
