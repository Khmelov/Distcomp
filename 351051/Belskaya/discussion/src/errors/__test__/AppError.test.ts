import {
  AppError,
  ConflictError,
  NotFoundError,
  UnprocessableEntityError,
  ValidationError,
} from "../AppError";

describe("AppError", () => {
  it("should create an error with provided properties", () => {
    const error = new AppError(500, "50001", "Internal server error");

    expect(error).toBeInstanceOf(Error);
    expect(error).toBeInstanceOf(AppError);
    expect(error.statusCode).toBe(500);
    expect(error.errorCode).toBe("50001");
    expect(error.message).toBe("Internal server error");
    expect(error.name).toBe("AppError");
    expect(error.stack).toBeDefined();
  });
});

describe("NotFoundError", () => {
  it("should create error with default errorCode", () => {
    const error = new NotFoundError("Message not found");

    expect(error).toBeInstanceOf(AppError);
    expect(error.statusCode).toBe(404);
    expect(error.errorCode).toBe("40401");
    expect(error.message).toBe("Message not found");
    expect(error.name).toBe("NotFoundError");
  });

  it("should accept custom errorCode", () => {
    const error = new NotFoundError("Author not found", "40402");

    expect(error.errorCode).toBe("40402");
  });
});

describe("ValidationError", () => {
  it("should create error with default errorCode", () => {
    const error = new ValidationError("Invalid data");

    expect(error).toBeInstanceOf(AppError);
    expect(error.statusCode).toBe(400);
    expect(error.errorCode).toBe("40001");
    expect(error.message).toBe("Invalid data");
    expect(error.name).toBe("ValidationError");
  });

  it("should accept custom errorCode", () => {
    const error = new ValidationError("Invalid topicId", "40002");

    expect(error.errorCode).toBe("40002");
  });
});

describe("ConflictError", () => {
  it("should create error with default errorCode", () => {
    const error = new ConflictError("Conflict occurred");

    expect(error).toBeInstanceOf(AppError);
    expect(error.statusCode).toBe(403);
    expect(error.errorCode).toBe("40301");
    expect(error.message).toBe("Conflict occurred");
    expect(error.name).toBe("ConflictError");
  });

  it("should accept custom errorCode", () => {
    const error = new ConflictError("Duplicate author", "40302");

    expect(error.errorCode).toBe("40302");
  });
});

describe("UnprocessableEntityError", () => {
  it("should create error with default errorCode", () => {
    const error = new UnprocessableEntityError("Unprocessable entity");

    expect(error).toBeInstanceOf(AppError);
    expect(error.statusCode).toBe(422);
    expect(error.errorCode).toBe("42201");
    expect(error.message).toBe("Unprocessable entity");
    expect(error.name).toBe("UnprocessableEntityError");
  });

  it("should accept custom errorCode", () => {
    const error = new UnprocessableEntityError("Invalid message", "42202");

    expect(error.errorCode).toBe("42202");
  });
});
