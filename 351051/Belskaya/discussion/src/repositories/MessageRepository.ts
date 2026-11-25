import { cassandraClient } from "../db/cassandra";

const BUCKET_COUNT = 4;

const getBucket = (id: number) => id % BUCKET_COUNT;

export interface Message {
  id: number;
  topicId: number;
  country?: string;
  content: string;
}

export class MessageRepository {
  async save(message: Message): Promise<void> {
    const bucket = getBucket(message.id);

    const queries = [
      {
        query: `
          INSERT INTO distcomp.tbl_message
            (id, topic_id, country, content)
          VALUES (?, ?, ?, ?)
        `,
        params: [
          message.id,
          message.topicId,
          message.country ?? null,
          message.content,
        ],
      },
      {
        query: `
          INSERT INTO distcomp.tbl_message_by_topic
            (topic_id, bucket, id, country, content)
          VALUES (?, ?, ?, ?, ?)
        `,
        params: [
          message.topicId,
          bucket,
          message.id,
          message.country ?? null,
          message.content,
        ],
      },
    ];

    await cassandraClient.batch(queries, { prepare: true });
  }

  async findById(id: number): Promise<Message | undefined> {
    const query = `
      SELECT * FROM distcomp.tbl_message
      WHERE id = ?
    `;

    const result = await cassandraClient.execute(query, [id], {
      prepare: true,
    });

    const row = result.first();

    if (!row) return undefined;

    return {
      id: Number(row.id),
      topicId: Number(row.topic_id),
      country: row.country ?? undefined,
      content: row.content,
    };
  }

  async findByTopicId(
    topicId: number,
    sortOrder: "asc" | "desc" = "asc",
  ): Promise<Message[]> {
    const order = sortOrder === "desc" ? "DESC" : "ASC";

    const queries = Array.from({ length: BUCKET_COUNT }, (_, bucket) =>
      cassandraClient.execute(
        `
            SELECT * FROM distcomp.tbl_message_by_topic
            WHERE topic_id = ? AND bucket = ?
            ORDER BY id ${order}
          `,
        [topicId, bucket],
        { prepare: true },
      ),
    );

    const results = await Promise.all(queries);

    const messages: Message[] = results.flatMap((result) =>
      result.rows.map((row) => ({
        id: Number(row.id),
        topicId: Number(row.topic_id),
        country: row.country ?? undefined,
        content: row.content,
      })),
    );

    messages.sort((a, b) => (sortOrder === "desc" ? b.id - a.id : a.id - b.id));

    return messages;
  }

  async findAll(): Promise<Message[]> {
    const query = `
      SELECT * FROM distcomp.tbl_message
    `;

    const result = await cassandraClient.execute(query);

    return result.rows.map((row) => ({
      id: Number(row.id),
      topicId: Number(row.topic_id),
      country: row.country ?? undefined,
      content: row.content,
    }));
  }

  async update(
    id: number,
    message: Partial<Message>,
  ): Promise<Message | undefined> {
    const existing = await this.findById(id);

    if (!existing) return undefined;

    const updated: Message = {
      ...existing,
      ...message,
      id,
    };

    const bucket = getBucket(id);

    const queries = [
      {
        query: `
          UPDATE distcomp.tbl_message
          SET topic_id = ?, country = ?, content = ?
          WHERE id = ?
        `,
        params: [updated.topicId, updated.country ?? null, updated.content, id],
      },
      {
        query: `
          DELETE FROM distcomp.tbl_message_by_topic
          WHERE topic_id = ? AND bucket = ? AND id = ?
        `,
        params: [existing.topicId, bucket, id],
      },
      {
        query: `
          INSERT INTO distcomp.tbl_message_by_topic
            (topic_id, bucket, id, country, content)
          VALUES (?, ?, ?, ?, ?)
        `,
        params: [
          updated.topicId,
          bucket,
          id,
          updated.country ?? null,
          updated.content,
        ],
      },
    ];

    await cassandraClient.batch(queries, { prepare: true });

    return updated;
  }

  async delete(id: number): Promise<boolean> {
    const existing = await this.findById(id);

    if (!existing) return false;

    const bucket = getBucket(id);

    const queries = [
      {
        query: `
          DELETE FROM distcomp.tbl_message
          WHERE id = ?
        `,
        params: [id],
      },
      {
        query: `
          DELETE FROM distcomp.tbl_message_by_topic
          WHERE topic_id = ? AND bucket = ? AND id = ?
        `,
        params: [existing.topicId, bucket, id],
      },
    ];

    await cassandraClient.batch(queries, { prepare: true });

    return true;
  }
}
