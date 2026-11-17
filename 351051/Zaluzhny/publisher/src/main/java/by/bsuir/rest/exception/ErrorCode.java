package by.bsuir.rest.exception;

public enum ErrorCode {

    VALIDATION(40001),
    RELATED_NOT_FOUND(40002),
    ID_REQUIRED(40003),
    ID_MISMATCH(40004),
    TYPE_MISMATCH(40005),
    MALFORMED_JSON(40006),
    DUPLICATE(40301),
    NOT_FOUND(40401),
    METHOD_NOT_ALLOWED(40501),
    UNSUPPORTED_MEDIA(41501),
    INTERNAL(50000);

    private final int code;

    ErrorCode(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public static ErrorCode fromCode(int code) {
        for (ErrorCode value : values()) {
            if (value.code == code) {
                return value;
            }
        }
        return null;
    }
}
