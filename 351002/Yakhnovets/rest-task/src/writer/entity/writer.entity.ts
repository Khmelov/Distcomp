import { Column, Entity, OneToMany, PrimaryGeneratedColumn } from 'typeorm';
import { Issue } from '../../issue/entity/issue.entity';
import { WriterRole } from '../../security/role.enum';

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

  @Column({ type: 'varchar', length: 16, default: WriterRole.CUSTOMER })
  role: WriterRole;

  @OneToMany(() => Issue, (issue) => issue.writer)
  issues: Issue[];
}
