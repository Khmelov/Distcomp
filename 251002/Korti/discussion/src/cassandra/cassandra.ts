import { Injectable } from '@nestjs/common';
import { Client, mapping } from 'cassandra-driver';

@Injectable()
export class Cassandra {
  private cassandraClient: Client;
  private mapper: mapping.Mapper;

  constructor() {
    const contactPoints = ['localhost'];
    const keyspace = 'distcomp';

    this.cassandraClient = new Client({
      contactPoints,
      localDataCenter: 'discussion',
      keyspace,
    });
    this.cassandraClient
      .connect()
      .then(() => {
        console.log('Успешное подключение к Cassandra');
        this.mapper = new mapping.Mapper(this.cassandraClient, {
          models: {
            Note: {
              tables: ['tbl_note'],
              columns: {
                articleId: 'articleid',
                id: 'id',
                content: 'content',
                country: 'country',
              },
            },
          },
        });
      })
      .catch((error) =>
        console.error('Ошибка подключения к Cassandra:', error),
      );
  }

  getMapper(): mapping.Mapper {
    return this.mapper;
  }
}
