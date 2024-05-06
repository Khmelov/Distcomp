package by.bsuir.dc.lab4.filters;


import by.bsuir.dc.lab4.dto.CommentRequestTo;
import by.bsuir.dc.lab4.entities.Comment;
import by.bsuir.dc.lab4.services.interfaces.CommentDiscService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Aspect
@Order(1)
@Component
public class CommentValidationAspect {
    @Autowired
    private CommentDiscService service;

    @Around("execution(* by.bsuir.dc.lab4.controllers.CommentController.add(..))")
    public ResponseEntity<Comment> filterBeforePostRequest(ProceedingJoinPoint joinPoint) throws Throwable {
        Object[] args = joinPoint.getArgs();
        CommentRequestTo comment = (CommentRequestTo) args[0];
        int commentContentLength = comment.getContent().length();
        boolean valid = commentContentLength >= 2 && commentContentLength <= 2048;
        if(!valid) {
            return new ResponseEntity<>(new Comment(), HttpStatus.valueOf(400));
        } else {
            return (ResponseEntity<Comment>) joinPoint.proceed();
        }
    }

    @Around("execution(* by.bsuir.dc.lab4.controllers.CommentController.update(..))")
    public ResponseEntity<Comment> filterBeforePutRequest(ProceedingJoinPoint joinPoint) throws Throwable {
        Object[] args = joinPoint.getArgs();
        CommentRequestTo comment = (CommentRequestTo) args[0];
        int commentContentLength = comment.getContent().length();
        boolean valid = commentContentLength >= 2 && commentContentLength <= 2048;
        if(!valid) {
            return new ResponseEntity<>(new Comment(), HttpStatus.valueOf(400));
        } else {
            return (ResponseEntity<Comment>) joinPoint.proceed();
        }
    }
}
