import { Injectable } from '@nestjs/common';
import { ConfigService } from '@nestjs/config';
import { Client, mapping } from 'cassandra-driver';
import { config } from 'dotenv';
config();

@Injectable()
export class CassandraService {
  public client: Client;
  mapper: mapping.Mapper;

  constructor(private configService: ConfigService) {
    this.createClient();
  }

  private createClient() {
    this.client = new Client({
      contactPoints: ['127.0.0.1:9042'],
      localDataCenter: 'datacenter1',
      keyspace: 'distcomp',
    });
  }

  createMapper(mappingOptions: mapping.MappingOptions) {
    if (this.client == undefined) {
      this.createClient();
    }
    return new mapping.Mapper(this.client, mappingOptions);
  }
}
