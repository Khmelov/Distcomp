import { Entity, Column, PrimaryGeneratedColumn, OneToMany } from 'typeorm';
import { Article } from '../article/article.entity';

export enum UserRole {
  ADMIN = 'ADMIN',
  CUSTOMER = 'CUSTOMER',
}

@Entity({ name: 'tbl_user' })
export class User {
  @PrimaryGeneratedColumn()
  id!: number;

  @Column({ type: 'varchar', length: 64, unique: true })
  login!: string;

  @Column({ type: 'varchar', length: 128 })
  password!: string;

  @Column({ type: 'varchar', length: 64 })
  firstname!: string;

  @Column({ type: 'varchar', length: 64 })
  lastname!: string;

  @Column({ type: 'varchar', length: 16, default: UserRole.CUSTOMER })
  role!: string;

  @OneToMany(() => Article, (article) => article.user, { cascade: true, onDelete: 'CASCADE' })
  articles!: Article[];
}