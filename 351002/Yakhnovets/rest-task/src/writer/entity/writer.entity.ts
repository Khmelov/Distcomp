import { Column, Entity, OneToMany, PrimaryGeneratedColumn } from 'typeorm';
import { Issue } from '../../issue/entity/issue.entity';

@Entity('tbl_writer')
export class Writer {
  @PrimaryGeneratedColumn()
  id: number;

  @Column({ type: 'varchar', length: 64, unique: true })
  login: string;

  @Column({ type: 'varchar', length: 128 })
  password: string;

  @Column({ type: 'varchar', length: 64 })
  firstname: string;

  @Column({ type: 'varchar', length: 64 })
  lastname: string;

  @OneToMany(() => Issue, (issue) => issue.writer)
  issues: Issue[];
}
