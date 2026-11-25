const mockExecute = jest.fn();

const mockClientConstructor = jest.fn().mockImplementation(() => ({
  execute: mockExecute,
}));

jest.mock("cassandra-driver", () => ({
  __esModule: true,
  default: {
    Client: mockClientConstructor,
  },
}));

import { initCassandra } from "../cassandra";

describe("Cassandra configuration", () => {
  beforeEach(() => {
    jest.clearAllMocks();
    mockExecute.mockResolvedValue({});
    jest.spyOn(console, "log").mockImplementation(() => {});
    jest.spyOn(console, "error").mockImplementation(() => {});
  });

  afterEach(() => {
    jest.restoreAllMocks();
  });

  it("should execute three initialization queries", async () => {
    await initCassandra();

    expect(mockExecute).toHaveBeenCalledTimes(3);
  });

  it("should create distcomp keyspace", async () => {
    await initCassandra();

    expect(mockExecute.mock.calls[0][0]).toContain(
      "CREATE KEYSPACE IF NOT EXISTS distcomp",
    );
  });

  it("should create tbl_message table", async () => {
    await initCassandra();

    expect(mockExecute.mock.calls[1][0]).toContain(
      "CREATE TABLE IF NOT EXISTS distcomp.tbl_message",
    );
  });

  it("should create tbl_message_by_topic table", async () => {
    await initCassandra();

    expect(mockExecute.mock.calls[2][0]).toContain(
      "CREATE TABLE IF NOT EXISTS distcomp.tbl_message_by_topic",
    );
  });

  it("should log successful initialization", async () => {
    await initCassandra();

    expect(console.log).toHaveBeenCalledWith(
      "Cassandra initialized successfully.",
    );
  });

  it("should log and rethrow initialization errors", async () => {
    const error = new Error("Cassandra connection failed");
    mockExecute.mockRejectedValueOnce(error);

    await expect(initCassandra()).rejects.toThrow(
      "Cassandra connection failed",
    );

    expect(console.error).toHaveBeenCalledWith(
      "Failed to initialize Cassandra:",
      error,
    );
  });
});
