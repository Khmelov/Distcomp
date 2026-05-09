//package com.bsuir.romanmuhtasarov.exceptions;
//
//import jakarta.validation.ConstraintViolationException;
//import org.springframework.dao.DataIntegrityViolationException;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.ControllerAdvice;
//import org.springframework.web.bind.annotation.ExceptionHandler;
//import org.springframework.web.bind.annotation.ResponseBody;
//
//@ControllerAdvice
//public class GlobalExceptionsHandler {
//
//    @ResponseBody
//    @ExceptionHandler(NoSuchWriterException.class)
//    public ResponseEntity<ErrorResponseTo> handleNotFoundException(NoSuchWriterException exception) {
//        ErrorResponseTo errorResponseTo = new ErrorResponseTo(
//                exception.getComment(),
//                HttpStatus.NOT_FOUND.value() + ExceptionStatus.NO_SUCH_CREATOR_EXCEPTION_STATUS.getValue()
//        );
//        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponseTo);
//    }
//
//    @ResponseBody
//    @ExceptionHandler(ConstraintViolationException.class)
//    public ResponseEntity<ErrorResponseTo> handleConstraintViolationException(ConstraintViolationException exception) {
//        // Правильный способ получить сообщение из ConstraintViolationException
//        String message = exception.getConstraintViolations().iterator().next().getMessage();
//        ErrorResponseTo errorResponseTo = new ErrorResponseTo(
//                message,
//                HttpStatus.BAD_REQUEST.value() + ExceptionStatus.VALIDATION_ERROR_EXCEPTION_STATUS.getValue()
//        );
//        return new ResponseEntity<>(errorResponseTo, HttpStatus.BAD_REQUEST);
//    }
//
//    @ResponseBody
//    @ExceptionHandler(DataIntegrityViolationException.class)
//    public ResponseEntity<ErrorResponseTo> handleDataIntegrityViolationException(DataIntegrityViolationException exception) {
//        // Правильный способ получить сообщение из DataIntegrityViolationException
//        String message = exception.getMostSpecificCause().getMessage();
//        ErrorResponseTo errorResponseTo = new ErrorResponseTo(
//                message,
//                HttpStatus.FORBIDDEN.value() + ExceptionStatus.DB_CONSTRAINTS_EXCEPTION_STATUS.getValue()
//        );
//        return new ResponseEntity<>(errorResponseTo, HttpStatus.FORBIDDEN);
//    }
//
//    @ResponseBody
//    @ExceptionHandler(NoSuchNewsException.class)
//    public ResponseEntity<ErrorResponseTo> handleNoSuchTweetException(NoSuchNewsException exception) {
//        ErrorResponseTo errorResponseTo = new ErrorResponseTo(
//                exception.getComment(),
//                HttpStatus.NOT_FOUND.value() + ExceptionStatus.NO_SUCH_TWEET_EXCEPTION_STATUS.getValue()
//        );
//        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponseTo);
//    }
//
//    @ResponseBody
//    @ExceptionHandler(NoSuchCommentException.class)
//    public ResponseEntity<ErrorResponseTo> handleNoSuchCommentException(NoSuchCommentException exception) {
//        ErrorResponseTo errorResponseTo = new ErrorResponseTo(
//                exception.getComment(),
//                HttpStatus.NOT_FOUND.value() + ExceptionStatus.NO_SUCH_MESSAGE_EXCEPTION_STATUS.getValue()
//        );
//        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponseTo);
//    }
//
//    @ResponseBody
//    @ExceptionHandler(NoSuchTagException.class)
//    public ResponseEntity<ErrorResponseTo> handleNoSuchStickerException(NoSuchTagException exception) {
//        ErrorResponseTo errorResponseTo = new ErrorResponseTo(
//                exception.getComment(),
//                HttpStatus.NOT_FOUND.value() + ExceptionStatus.NO_SUCH_STICKER_EXCEPTION_STATUS.getValue()
//        );
//        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponseTo);
//    }
//}