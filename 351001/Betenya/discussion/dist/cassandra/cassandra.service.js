"use strict";
var __decorate = (this && this.__decorate) || function (decorators, target, key, desc) {
    var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
    if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
    else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
    return c > 3 && r && Object.defineProperty(target, key, r), r;
};
var __metadata = (this && this.__metadata) || function (k, v) {
    if (typeof Reflect === "object" && typeof Reflect.metadata === "function") return Reflect.metadata(k, v);
};
var CassandraService_1;
Object.defineProperty(exports, "__esModule", { value: true });
exports.CassandraService = void 0;
const common_1 = require("@nestjs/common");
const cassandra_driver_1 = require("cassandra-driver");
const KEYSPACE = 'distcomp';
let CassandraService = CassandraService_1 = class CassandraService {
    constructor() {
        this.logger = new common_1.Logger(CassandraService_1.name);
        const host = process.env.CASSANDRA_HOST ?? 'localhost';
        const port = parseInt(process.env.CASSANDRA_PORT ?? '9042', 10);
        this.client = new cassandra_driver_1.Client({
            contactPoints: [`${host}:${port}`],
            localDataCenter: 'datacenter1',
        });
    }
    async onModuleInit() {
        await this.connectWithRetry();
        await this.initSchema();
    }
    async onModuleDestroy() {
        await this.client.shutdown();
    }
    async connectWithRetry(attempts = 15, delayMs = 5000) {
        for (let i = 1; i <= attempts; i++) {
            try {
                await this.client.connect();
                this.logger.log('Connected to Cassandra');
                return;
            }
            catch (err) {
                this.logger.warn(`Cassandra not ready (attempt ${i}/${attempts}): ${err.message}`);
                if (i === attempts)
                    throw err;
                await new Promise((r) => setTimeout(r, delayMs));
            }
        }
    }
    async initSchema() {
        await this.client.execute(`CREATE KEYSPACE IF NOT EXISTS ${KEYSPACE}
       WITH replication = {'class': 'SimpleStrategy', 'replication_factor': 1}`);
        await this.client.execute(`CREATE TABLE IF NOT EXISTS ${KEYSPACE}.tbl_notice (
         id         bigint PRIMARY KEY,
         article_id bigint,
         content    text
       )`);
        await this.client.execute(`CREATE TABLE IF NOT EXISTS ${KEYSPACE}.tbl_counter (
         name  text PRIMARY KEY,
         value counter
       )`);
        this.logger.log('Cassandra schema initialised');
    }
};
exports.CassandraService = CassandraService;
exports.CassandraService = CassandraService = CassandraService_1 = __decorate([
    (0, common_1.Injectable)(),
    __metadata("design:paramtypes", [])
], CassandraService);
//# sourceMappingURL=cassandra.service.js.map