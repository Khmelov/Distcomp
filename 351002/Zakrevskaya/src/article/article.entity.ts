import { 
  Entity, 
  Column, 
  PrimaryGeneratedColumn, 
  ManyToOne, 
  ManyToMany, 
  JoinTable,
  JoinColumn,
  Unique
} from 'typeorm';
import { User } from '../user/user.entity';
import { Marker } from '../marker/marker.entity';

@Entity({ name: 'tbl_article' })
@Unique(['title'])
export class Article {
  @PrimaryGeneratedColumn()
  id!: number;

  @Column({ type: 'varchar', length: 64, unique: true })
  title!: string;

  @Column({ type: 'text' })
  content!: string;

  @Column({ type: 'timestamp', default: () => 'CURRENT_TIMESTAMP' })
  created!: Date;

  @Column({ type: 'timestamp', default: () => 'CURRENT_TIMESTAMP', onUpdate: 'CURRENT_TIMESTAMP' })
  modified!: Date;

  @ManyToOne(() => User, (user) => user.articles, { onDelete: 'CASCADE' })
  @JoinColumn({ name: 'user_id' })
  user!: User;

  @ManyToMany(() => Marker)
  @JoinTable({
    name: 'tbl_article_marker',
    joinColumn: { name: 'article_id', referencedColumnName: 'id' },
    inverseJoinColumn: { name: 'marker_id', referencedColumnName: 'id' },
  })
  markers!: Marker[];
}