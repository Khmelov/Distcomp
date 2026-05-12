import { Injectable, NotFoundException, BadRequestException } from '@nestjs/common';
import { MarkerRepository } from './marker.repository';
import { CreateMarkerDto, MarkerResponseDto } from './dto/marker.dto';
import { Marker } from './marker.entity';

@Injectable()
export class MarkerService {
  constructor(private readonly markerRepository: MarkerRepository) {}

  async create(createMarkerDto: CreateMarkerDto): Promise<MarkerResponseDto> {
    this.validateMarkerDto(createMarkerDto);
    const marker = await this.markerRepository.create(createMarkerDto);
    return this.toResponseDto(marker);
  }

  async update(id: number, updateMarkerDto: CreateMarkerDto): Promise<MarkerResponseDto> {
    this.validateMarkerDto(updateMarkerDto);
    const marker = await this.markerRepository.update(id, updateMarkerDto);
    return this.toResponseDto(marker);
  }

  async delete(id: number): Promise<void> {
    const marker = await this.markerRepository.findById(id);
    if (!marker) throw new NotFoundException('Marker not found');
    await this.markerRepository.delete(id);
  }

  async findById(id: number): Promise<MarkerResponseDto> {
    const marker = await this.markerRepository.findById(id);
    if (!marker) throw new NotFoundException('Marker not found');
    return this.toResponseDto(marker);
  }

  async findAll(page: number = 1, limit: number = 10): Promise<MarkerResponseDto[]> {
    const [markers] = await this.markerRepository.findAll({ page, limit });
    return markers.map(marker => this.toResponseDto(marker));
  }

  private validateMarkerDto(dto: CreateMarkerDto) {
    if (dto.name.length < 2 || dto.name.length > 32) {
      throw new BadRequestException('Name must be 2-32 characters');
    }
  }

  private toResponseDto(marker: Marker): MarkerResponseDto {
    return {
      id: marker.id,
      name: marker.name,
    };
  }
}