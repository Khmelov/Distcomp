import express from "express";
import request from "supertest";
import messageRoutes from "../messageRoutes";

jest.mock("../../controllers/MessageController", () => ({
  MessageController: jest.fn().mockImplementation(() => ({
    create: jest.fn((_req, res) => res.status(201).json({ action: "create" })),
    findAll: jest.fn((_req, res) =>
      res.status(200).json({ action: "findAll" }),
    ),
    findByTopicId: jest.fn((_req, res) =>
      res.status(200).json({ action: "findByTopicId" }),
    ),
    findById: jest.fn((_req, res) =>
      res.status(200).json({ action: "findById" }),
    ),
    update: jest.fn((_req, res) => res.status(200).json({ action: "update" })),
    delete: jest.fn((_req, res) => res.status(204).send()),
  })),
}));

describe("Message routes", () => {
  const app = express();

  beforeAll(() => {
    app.use(express.json());
    app.use("/api/v1.0/messages", messageRoutes);
  });

  beforeEach(() => {
    jest.clearAllMocks();
  });

  it("POST / should call create", async () => {
    const response = await request(app)
      .post("/api/v1.0/messages")
      .send({ topicId: 10, content: "Test message" });

    expect(response.status).toBe(201);
    expect(response.body).toEqual({ action: "create" });
  });

  it("GET / should call findAll", async () => {
    const response = await request(app).get("/api/v1.0/messages");

    expect(response.status).toBe(200);
    expect(response.body).toEqual({ action: "findAll" });
  });

  it("GET /by-topic/:topicId should call findByTopicId", async () => {
    const response = await request(app).get(
      "/api/v1.0/messages/by-topic/10?sortOrder=desc",
    );

    expect(response.status).toBe(200);
    expect(response.body).toEqual({ action: "findByTopicId" });
  });

  it("GET /:id should call findById", async () => {
    const response = await request(app).get("/api/v1.0/messages/100");

    expect(response.status).toBe(200);
    expect(response.body).toEqual({ action: "findById" });
  });

  it("PUT /:id should call update", async () => {
    const response = await request(app)
      .put("/api/v1.0/messages/100")
      .send({ topicId: 10, content: "Updated message" });

    expect(response.status).toBe(200);
    expect(response.body).toEqual({ action: "update" });
  });

  it("DELETE /:id should call delete", async () => {
    const response = await request(app).delete("/api/v1.0/messages/100");

    expect(response.status).toBe(204);
  });

  it("should return 404 for an unknown route", async () => {
    const response = await request(app).get("/api/v1.0/messages/unknown/path");

    expect(response.status).toBe(404);
  });
});
