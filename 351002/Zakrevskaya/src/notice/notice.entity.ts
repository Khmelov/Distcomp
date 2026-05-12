export class Notice {
  id!: number;
  content!: string;
  articleId!: number;
  country!: string;
}

export const NoticeTableSchema = `
  CREATE TABLE IF NOT EXISTS tbl_notice (
    id bigint,
    article_id bigint,
    country text,
    content text,
    PRIMARY KEY ((country), article_id, id)
  );
`;