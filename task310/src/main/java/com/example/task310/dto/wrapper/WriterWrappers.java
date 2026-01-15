package com.example.task310.dto.wrapper;

import com.example.task310.dto.request.WriterRequestTo;
import com.example.task310.dto.response.WriterResponseTo;
import java.util.List;

public class WriterWrappers {
    public record WriterRequestWrapper(WriterRequestTo writer) {}
    public record WriterResponseWrapper(WriterResponseTo writer) {}
    public record WritersResponseWrapper(List<WriterResponseTo> writers) {}
}
