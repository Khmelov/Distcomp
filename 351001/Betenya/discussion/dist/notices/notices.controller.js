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
var __param = (this && this.__param) || function (paramIndex, decorator) {
    return function (target, key) { decorator(target, key, paramIndex); }
};
Object.defineProperty(exports, "__esModule", { value: true });
exports.NoticesController = void 0;
const common_1 = require("@nestjs/common");
const swagger_1 = require("@nestjs/swagger");
const notices_service_1 = require("./notices.service");
const NoticeRequestTo_dto_1 = require("../dto/NoticeRequestTo.dto");
const NoticeResponseTo_dto_1 = require("../dto/NoticeResponseTo.dto");
let NoticesController = class NoticesController {
    constructor(noticesService) {
        this.noticesService = noticesService;
    }
    async createNotice(dto) {
        return this.noticesService.createNotice(dto);
    }
    async getAllNotices() {
        return this.noticesService.getAll();
    }
    async getNotice(id) {
        return this.noticesService.getNotice(id);
    }
    async updateNotice(id, dto) {
        return this.noticesService.updateNotice(id, dto);
    }
    async deleteNotice(id) {
        return this.noticesService.deleteNotice(id);
    }
};
exports.NoticesController = NoticesController;
__decorate([
    (0, common_1.Post)(),
    (0, swagger_1.ApiOperation)({ summary: 'Create new notice' }),
    (0, swagger_1.ApiBody)({ type: NoticeRequestTo_dto_1.NoticeRequestTo }),
    (0, swagger_1.ApiResponse)({ status: 201, type: NoticeResponseTo_dto_1.NoticeResponseTo }),
    __param(0, (0, common_1.Body)()),
    __metadata("design:type", Function),
    __metadata("design:paramtypes", [NoticeRequestTo_dto_1.NoticeRequestTo]),
    __metadata("design:returntype", Promise)
], NoticesController.prototype, "createNotice", null);
__decorate([
    (0, common_1.Get)(),
    (0, swagger_1.ApiOperation)({ summary: 'Get all notices' }),
    (0, swagger_1.ApiResponse)({ status: 200, type: [NoticeResponseTo_dto_1.NoticeResponseTo] }),
    __metadata("design:type", Function),
    __metadata("design:paramtypes", []),
    __metadata("design:returntype", Promise)
], NoticesController.prototype, "getAllNotices", null);
__decorate([
    (0, common_1.Get)(':id'),
    (0, swagger_1.ApiOperation)({ summary: 'Get notice by ID' }),
    (0, swagger_1.ApiParam)({ name: 'id', type: Number }),
    (0, swagger_1.ApiResponse)({ status: 200, type: NoticeResponseTo_dto_1.NoticeResponseTo }),
    (0, swagger_1.ApiResponse)({ status: 404, description: 'Notice not found' }),
    __param(0, (0, common_1.Param)('id', common_1.ParseIntPipe)),
    __metadata("design:type", Function),
    __metadata("design:paramtypes", [Number]),
    __metadata("design:returntype", Promise)
], NoticesController.prototype, "getNotice", null);
__decorate([
    (0, common_1.Put)(':id'),
    (0, swagger_1.ApiOperation)({ summary: 'Update notice' }),
    (0, swagger_1.ApiParam)({ name: 'id', type: Number }),
    (0, swagger_1.ApiBody)({ type: NoticeRequestTo_dto_1.NoticeRequestTo }),
    (0, swagger_1.ApiResponse)({ status: 200, type: NoticeResponseTo_dto_1.NoticeResponseTo }),
    (0, swagger_1.ApiResponse)({ status: 404, description: 'Notice not found' }),
    __param(0, (0, common_1.Param)('id', common_1.ParseIntPipe)),
    __param(1, (0, common_1.Body)()),
    __metadata("design:type", Function),
    __metadata("design:paramtypes", [Number, NoticeRequestTo_dto_1.NoticeRequestTo]),
    __metadata("design:returntype", Promise)
], NoticesController.prototype, "updateNotice", null);
__decorate([
    (0, common_1.HttpCode)(204),
    (0, common_1.Delete)(':id'),
    (0, swagger_1.ApiOperation)({ summary: 'Delete notice' }),
    (0, swagger_1.ApiParam)({ name: 'id', type: Number }),
    (0, swagger_1.ApiResponse)({ status: 204 }),
    (0, swagger_1.ApiResponse)({ status: 404, description: 'Notice not found' }),
    __param(0, (0, common_1.Param)('id', common_1.ParseIntPipe)),
    __metadata("design:type", Function),
    __metadata("design:paramtypes", [Number]),
    __metadata("design:returntype", Promise)
], NoticesController.prototype, "deleteNotice", null);
exports.NoticesController = NoticesController = __decorate([
    (0, swagger_1.ApiTags)('notices'),
    (0, common_1.Controller)(),
    __metadata("design:paramtypes", [notices_service_1.NoticesService])
], NoticesController);
//# sourceMappingURL=notices.controller.js.map