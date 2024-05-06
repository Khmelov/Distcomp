package by.bsuir.dc.lab1.service.abst;

import by.bsuir.dc.lab1.dto.*;

import java.math.BigInteger;
import java.util.List;

public interface ICommentService {
        CommentResponseTo create(CommentRequestTo postTo);
        CommentResponseTo getById(BigInteger id);
        List<CommentResponseTo> getAll();
        CommentResponseTo update(CommentRequestTo postTo);
        boolean delete(BigInteger id);
}
