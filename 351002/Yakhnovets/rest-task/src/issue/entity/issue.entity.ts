import {
  Column,
  CreateDateColumn,
  Entity,
  JoinColumn,
  JoinTable,
  ManyToMany,
  ManyToOne,
  PrimaryGeneratedColumn,
  UpdateDateColumn,
} from 'typeorm';
import { Writer } from '../../writer/entity/writer.entity';
import { Mark } from '../../mark/entity/mark.entity';

@Entity('tbl_issue')
export class Issue {
  @PrimaryGeneratedColumn()
  id: number;

  @Column({ name: 'writer_id', type: 'int' })
  writerId: number;

  @Column({ type: 'varchar', length: 64, unique: true })
  title: string;

  @Column({ type: 'varchar', length: 2048 })
  content: string;

  @CreateDateColumn({ type: 'timestamp', name: 'created' })
  created: Date;

  @UpdateDateColumn({ type: 'timestamp', name: 'modified' })
  modified: Date;

  @ManyToOne(() => Writer, (writer) => writer.issues, { onDelete: 'CASCADE' })
  @JoinColumn({ name: 'writer_id' })
  writer: Writer;

  @ManyToMany(() => Mark, (mark) => mark.issues)
  @JoinTable({
    name: 'tbl_issue_mark',
    joinColumn: { name: 'issue_id', referencedColumnName: 'id' },
    inverseJoinColumn: { name: 'mark_id', referencedColumnName: 'id' },
  })
  marks: Mark[];
}
