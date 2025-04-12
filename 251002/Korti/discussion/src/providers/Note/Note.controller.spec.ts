import { Test, TestingModule } from '@nestjs/testing';
import { NoteService } from './Note.service';
import { getRepositoryToken } from '@nestjs/typeorm';
import { Note } from '../../entities/Note';
import { Article } from '../../entities/Article';
import { Repository } from 'typeorm';
import { NoteRequestTo, UpdateNoteTo } from './Dto/NoteRequestTo';
import { HttpException } from '@nestjs/common';

describe('NoteService', () => {
  let service: NoteService;
  let noteRepository: Repository<Note>;
  let articleRepository: Repository<Article>;

  const mockNoteRepository = {
    find: jest.fn(),
    findOne: jest.fn(),
    create: jest.fn(),
    save: jest.fn(),
    delete: jest.fn(),
    update: jest.fn(),
  };

  const mockArticleRepository = {
    findOne: jest.fn(),
  };

  beforeEach(async () => {
    const module: TestingModule = await Test.createTestingModule({
      providers: [
        NoteService,
        {
          provide: getRepositoryToken(Note),
          useValue: mockNoteRepository,
        },
        {
          provide: getRepositoryToken(Article),
          useValue: mockArticleRepository,
        },
      ],
    }).compile();

    service = module.get<NoteService>(NoteService);
    noteRepository = module.get<Repository<Note>>(getRepositoryToken(Note));
    articleRepository = module.get<Repository<Article>>(
      getRepositoryToken(Article),
    );
  });

  describe('getAllNotes', () => {
    it('should return all notes', async () => {
      const mockNotes: Note[] = [
        { id: 1, content: 'Test Note', articleId: 1, article: {} as Article },
      ];

      jest.spyOn(noteRepository, 'find').mockResolvedValue(mockNotes);

      const result = await service.getAllNotes();
      expect(result).toHaveLength(1);
      expect(result[0]).toHaveProperty('id', 1);
    });
  });

  describe('createNote', () => {
    it('should create a note successfully', async () => {
      const noteRequest: NoteRequestTo = {
        articleId: 1,
        content: 'New Note',
      };
      jest.spyOn(articleRepository, 'findOne').mockResolvedValue({} as Article);
      jest.spyOn(noteRepository, 'create').mockReturnValue(noteRequest as Note);
      jest.spyOn(noteRepository, 'save').mockResolvedValue({
        ...noteRequest,
        id: 1,
      } as Note);

      const result = await service.createNote(noteRequest);
      expect(result).toHaveProperty('id');
    });

    it('should throw error if article does not exist', async () => {
      const noteRequest: NoteRequestTo = {
        articleId: 999,
        content: 'New Note',
      };
      jest.spyOn(articleRepository, 'findOne').mockResolvedValue(null);

      await expect(service.createNote(noteRequest)).rejects.toThrow(
        HttpException,
      );
    });

    it('should handle unique constraint violation', async () => {
      const noteRequest: NoteRequestTo = {
        articleId: 1,
        content: 'New Note',
      };
      jest.spyOn(articleRepository, 'findOne').mockResolvedValue({} as Article);
      jest.spyOn(noteRepository, 'save').mockRejectedValue({
        code: '23505',
      });
      await expect(service.createNote(noteRequest)).rejects.toThrow(
        HttpException,
      );
    });
  });

  describe('getNoteById', () => {
    it('should return note when found', async () => {
      const mockNote: Note = {
        id: 1,
        content: 'Test Note',
        articleId: 1,
        article: {} as Article,
      };

      jest.spyOn(noteRepository, 'findOne').mockResolvedValue(mockNote);

      const result = await service.getNoteById(1);
      expect(result).toHaveProperty('id', 1);
    });

    it('should throw error when note not found', async () => {
      jest.spyOn(noteRepository, 'findOne').mockResolvedValue(null);

      await expect(service.getNoteById(999)).rejects.toThrow(HttpException);
    });
  });

  describe('deleteNote', () => {
    it('should delete note successfully', async () => {
      const mockNote: Note = {
        id: 1,
        content: 'Test Note',
        articleId: 1,
        article: {} as Article,
      };

      jest.spyOn(noteRepository, 'findOne').mockResolvedValue(mockNote);
      //
      await expect(service.deleteNote(1)).resolves.not.toThrow();
    });

    it('should throw error when note not found', async () => {
      jest.spyOn(noteRepository, 'findOne').mockResolvedValue(null);

      await expect(service.deleteNote(999)).rejects.toThrow(HttpException);
    });
  });

  describe('updateNote', () => {
    it('should update note successfully', async () => {
      const updateRequest: UpdateNoteTo = {
        id: 1,
        articleId: 1,
        content: 'Updated Note',
      };
      jest.spyOn(articleRepository, 'findOne').mockResolvedValue({} as Article);
      jest
        .spyOn(noteRepository, 'findOne')
        .mockResolvedValueOnce({} as Note)
        .mockResolvedValueOnce({
          id: 1,
          content: 'Updated Note',
          articleId: 1,
        } as Note);
      //jest.spyOn(noteRepository, 'update').mockResolvedValue(undefined);

      const result = await service.updateNote(updateRequest);
      expect(result).toHaveProperty('content', 'Updated Note');
    });

    it('should throw error when article not found', async () => {
      const updateRequest: UpdateNoteTo = {
        id: 1,
        articleId: 999,
        content: 'Updated Note',
      };

      jest.spyOn(articleRepository, 'findOne').mockResolvedValue(null);

      await expect(service.updateNote(updateRequest)).rejects.toThrow(
        HttpException,
      );
    });

    it('should throw error when note not found', async () => {
      const updateRequest: UpdateNoteTo = {
        id: 999,
        articleId: 1,
        content: 'Updated Note',
      };
      jest.spyOn(articleRepository, 'findOne').mockResolvedValue({} as Article);
      jest.spyOn(noteRepository, 'findOne').mockResolvedValue(null);
      await expect(service.updateNote(updateRequest)).rejects.toThrow(
        HttpException,
      );
    });
  });

  describe('getNotes', () => {
    it('should return notes for an article', async () => {
      const mockNotes: Note[] = [
        { id: 1, content: 'Note 1', articleId: 1, article: {} as Article },
        { id: 2, content: 'Note 2', articleId: 1, article: {} as Article },
      ];
      jest.spyOn(articleRepository, 'findOne').mockResolvedValue({} as Article);
      jest.spyOn(noteRepository, 'find').mockResolvedValue(mockNotes);
      const result = await service.getNotes(1);
      expect(result).toHaveLength(2);
    });

    it('should throw error when article not found', async () => {
      jest.spyOn(articleRepository, 'findOne').mockResolvedValue(null);
      await expect(service.getNotes(999)).rejects.toThrow(HttpException);
    });
  });
});
