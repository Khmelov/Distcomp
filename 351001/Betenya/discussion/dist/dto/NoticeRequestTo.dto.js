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
exports.NoticeRequestTo = void 0;
const swagger_1 = require("@nestjs/swagger");
const class_validator_1 = require("class-validator");
class NoticeRequestTo {
}
exports.NoticeRequestTo = NoticeRequestTo;
__decorate([
    (0, swagger_1.ApiProperty)({
        example: 'Hello, i want to say that...',
        description: 'Content of the Notice',
    }),
    (0, class_validator_1.IsString)(),
    (0, class_validator_1.MinLength)(2, { message: 'The notice content must be at least 2 characters long' }),
    (0, class_validator_1.MaxLength)(2048, { message: 'The notice content must be no more than 2048 characters long' }),
    __metadata("design:type", String)
], NoticeRequestTo.prototype, "content", void 0);
__decorate([
    (0, swagger_1.ApiProperty)({ example: 1, description: 'Article ID', minimum: 1 }),
    (0, class_validator_1.IsInt)(),
    (0, class_validator_1.Min)(1, { message: 'Article ID must be at least 1' }),
    __metadata("design:type", Number)
], NoticeRequestTo.prototype, "articleId", void 0);
//# sourceMappingURL=NoticeRequestTo.dto.js.map