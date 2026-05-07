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
Object.defineProperty(exports, "__esModule", { value: true });
exports.NoticesService = void 0;
const common_1 = require("@nestjs/common");
const cassandra_driver_1 = require("cassandra-driver");
const cassandra_service_1 = require("../cassandra/cassandra.service");
const KS = 'distcomp';
let NoticesService = class NoticesService {
    constructor(cassandra) {
        this.cassandra = cassandra;
    }
    row(row) {
        return {
            id: row['id'].toNumber(),
            content: row['content'],
            articleId: row['article_id'].toNumber(),
        };
    }
    async nextId() {
        await this.cassandra.client.execute(`UPDATE ${KS}.tbl_counter SET value = value + 1 WHERE name = 'notice_id'`, [], { prepare: true });
        const result = await this.cassandra.client.execute(`SELECT value FROM ${KS}.tbl_counter WHERE name = 'notice_id'`, [], { prepare: true });
        return result.rows[0]['value'].toNumber();
    }
    async createNotice(dto) {
        const id = await this.nextId();
        await this.cassandra.client.execute(`INSERT INTO ${KS}.tbl_notice (id, article_id, content) VALUES (?, ?, ?)`, [cassandra_driver_1.types.Long.fromNumber(id), cassandra_driver_1.types.Long.fromNumber(dto.articleId), dto.content], { prepare: true });
        return { id, content: dto.content, articleId: dto.articleId };
    }
    async getAll() {
        const result = await this.cassandra.client.execute(`SELECT id, article_id, content FROM ${KS}.tbl_notice`, [], { prepare: true });
        return result.rows.map((r) => this.row(r));
    }
    async getNotice(id) {
        const result = await this.cassandra.client.execute(`SELECT id, article_id, content FROM ${KS}.tbl_notice WHERE id = ?`, [cassandra_driver_1.types.Long.fromNumber(id)], { prepare: true });
        if (!result.rows.length) {
            throw new common_1.NotFoundException('Notice not found');
        }
        return this.row(result.rows[0]);
    }
    async updateNotice(id, dto) {
        const existing = await this.cassandra.client.execute(`SELECT id FROM ${KS}.tbl_notice WHERE id = ?`, [cassandra_driver_1.types.Long.fromNumber(id)], { prepare: true });
        if (!existing.rows.length) {
            throw new common_1.NotFoundException('Notice not found');
        }
        try {
            await this.cassandra.client.execute(`UPDATE ${KS}.tbl_notice SET article_id = ?, content = ? WHERE id = ?`, [cassandra_driver_1.types.Long.fromNumber(dto.articleId), dto.content, cassandra_driver_1.types.Long.fromNumber(id)], { prepare: true });
        }
        catch {
            throw new common_1.InternalServerErrorException('Database error occurred');
        }
        return { id, content: dto.content, articleId: dto.articleId };
    }
    async deleteNotice(id) {
        const existing = await this.cassandra.client.execute(`SELECT id FROM ${KS}.tbl_notice WHERE id = ?`, [cassandra_driver_1.types.Long.fromNumber(id)], { prepare: true });
        if (!existing.rows.length) {
            throw new common_1.NotFoundException('Notice not found');
        }
        await this.cassandra.client.execute(`DELETE FROM ${KS}.tbl_notice WHERE id = ?`, [cassandra_driver_1.types.Long.fromNumber(id)], { prepare: true });
    }
};
exports.NoticesService = NoticesService;
exports.NoticesService = NoticesService = __decorate([
    (0, common_1.Injectable)(),
    __metadata("design:paramtypes", [cassandra_service_1.CassandraService])
], NoticesService);
//# sourceMappingURL=notices.service.js.map