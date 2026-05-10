import { Column, Entity, ManyToMany, PrimaryGeneratedColumn } from 'typeorm';
import { Issue } from '../../issue/entity/issue.entity';

@Entity('tbl_mark')
export class Mark {
  @PrimaryGeneratedColumn()
  id: number;

  @Column({ type: 'varchar', length: 32, unique: true })
  name: string;

  @ManyToMany(() => Issue, (issue) => issue.marks)
  issues: Issue[];
}
