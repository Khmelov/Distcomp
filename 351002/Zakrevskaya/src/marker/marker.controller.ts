import { Controller, Get, Post, Put, Delete, Body, Param, Query, HttpCode, HttpStatus, UseGuards, ParseIntPipe } from '@nestjs/common';
import { MarkerService } from './marker.service';
import { CreateMarkerDto, MarkerResponseDto } from './dto/marker.dto';
import { JwtAuthGuard } from '../auth/jwt-auth.guard';
import { RolesGuard } from '../auth/roles.guard';
import { Roles } from '../auth/roles.decorator';

@Controller('api/v1.0/markers')
export class MarkerController {
  constructor(private readonly markerService: MarkerService) {}

  @Post()
  @HttpCode(HttpStatus.CREATED)
  async create(@Body() createMarkerDto: CreateMarkerDto): Promise<MarkerResponseDto> {
    return this.markerService.create(createMarkerDto);
  }

  @Get()
  async findAll(
    @Query('page') page: number = 1,
    @Query('limit') limit: number = 10,
  ): Promise<MarkerResponseDto[]> {
    return this.markerService.findAll(page, limit);
  }

  @Get(':id')
  async findById(@Param('id', ParseIntPipe) id: number): Promise<MarkerResponseDto> {
    return this.markerService.findById(id);
  }

  @Put(':id')
  async update(@Param('id', ParseIntPipe) id: number, @Body() updateMarkerDto: CreateMarkerDto): Promise<MarkerResponseDto> {
    return this.markerService.update(id, updateMarkerDto);
  }

  @Delete(':id')
  @HttpCode(HttpStatus.NO_CONTENT)
  async delete(@Param('id', ParseIntPipe) id: number): Promise<void> {
    return this.markerService.delete(id);
  }
}

@Controller('api/v2.0/markers')
export class MarkerV2Controller {
  constructor(private readonly markerService: MarkerService) {}

  @Post()
  @HttpCode(HttpStatus.CREATED)
  @UseGuards(JwtAuthGuard, RolesGuard)
  @Roles('ADMIN')
  async create(@Body() createMarkerDto: CreateMarkerDto): Promise<MarkerResponseDto> {
    return this.markerService.create(createMarkerDto);
  }

  @Get()
  @UseGuards(JwtAuthGuard)
  async findAll(
    @Query('page') page: number = 1,
    @Query('limit') limit: number = 10,
  ): Promise<MarkerResponseDto[]> {
    return this.markerService.findAll(page, limit);
  }

  @Get(':id')
  @UseGuards(JwtAuthGuard)
  async findById(@Param('id', ParseIntPipe) id: number): Promise<MarkerResponseDto> {
    return this.markerService.findById(id);
  }

  @Put(':id')
  @UseGuards(JwtAuthGuard, RolesGuard)
  @Roles('ADMIN')
  async update(@Param('id', ParseIntPipe) id: number, @Body() updateMarkerDto: CreateMarkerDto): Promise<MarkerResponseDto> {
    return this.markerService.update(id, updateMarkerDto);
  }

  @Delete(':id')
  @HttpCode(HttpStatus.NO_CONTENT)
  @UseGuards(JwtAuthGuard, RolesGuard)
  @Roles('ADMIN')
  async delete(@Param('id', ParseIntPipe) id: number): Promise<void> {
    return this.markerService.delete(id);
  }
}