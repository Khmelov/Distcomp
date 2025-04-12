import { Injectable, OnModuleInit } from '@nestjs/common';
import { mapping } from 'cassandra-driver';
import { CassandraService } from 'src/cassandra/cassandra.service';
import { Note } from './Note.model';

@Injectable()
export class NoteRepository implements OnModuleInit {
  constructor(private cassandraService: CassandraService) {}

  noteMapper: mapping.ModelMapper<Note>;

  onModuleInit() {
    const mappingOptions: mapping.MappingOptions = {
      models: {
        Note: {
          tables: ['tbl_note'],
          mappings: new mapping.UnderscoreCqlToCamelCaseMappings(),
        },
      },
    };

    this.noteMapper = this.cassandraService
      .createMapper(mappingOptions)
      .forModel('Note');
  }

  async getAllNotes() {
    return (await this.noteMapper.findAll()).toArray();
  }

  async getNoteById(id: number) {
    return await this.noteMapper.find({ id });
  }
}
