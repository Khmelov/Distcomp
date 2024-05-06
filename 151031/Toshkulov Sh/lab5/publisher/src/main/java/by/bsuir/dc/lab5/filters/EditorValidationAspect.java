package by.bsuir.dc.lab5.filters;

import by.bsuir.dc.lab5.dto.EditorRequestTo;
import by.bsuir.dc.lab5.entities.Editor;
import by.bsuir.dc.lab5.services.interfaces.EditorService;
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
public class EditorValidationAspect {

    @Autowired
    private EditorService service;

    @Around("execution(* by.bsuir.dc.lab5.controllers.EditorController.add(..))")
    public ResponseEntity<Editor> filterBeforePostRequest(ProceedingJoinPoint joinPoint) throws Throwable {
        Object[] args = joinPoint.getArgs();
        EditorRequestTo editor = (EditorRequestTo) args[0];
        Editor editorWithDuplicateLogin = service.getByLogin(editor.getLogin());
        if(editorWithDuplicateLogin != null){
            return new ResponseEntity<>(new Editor(), HttpStatus.valueOf(403));
        }
        boolean valid = true;
        int editorFirstnameLength = editor.getFirstname().length();
        int editorLastnameLength = editor.getLastname().length();
        int editorLoginLength = editor.getLogin().length();
        int editorPasswordLength = editor.getPassword().length();
        valid &= editorLoginLength >= 2 && editorLoginLength <= 64;
        valid &= editorPasswordLength >= 8 && editorPasswordLength <= 128;
        valid &= editorFirstnameLength >= 2 && editorFirstnameLength <= 64;
        valid &= editorLastnameLength >= 2 && editorLastnameLength <= 64;
        if(!valid) {
            return new ResponseEntity<>(new Editor(), HttpStatus.valueOf(400));
        } else {
            return (ResponseEntity<Editor>) joinPoint.proceed();
        }
    }

    @Around("execution(* by.bsuir.dc.lab5.controllers.EditorController.update(..))")
    public ResponseEntity<Editor> filterBeforePutRequest(ProceedingJoinPoint joinPoint) throws Throwable {
        Object[] args = joinPoint.getArgs();
        EditorRequestTo editor = (EditorRequestTo) args[0];
        Editor editorWithDuplicateLogin = service.getByLogin(editor.getLogin());
        if(editorWithDuplicateLogin != null){
            return new ResponseEntity<>(new Editor(), HttpStatus.valueOf(403));
        }
        boolean valid = true;
        int editorFirstnameLength = editor.getFirstname().length();
        int editorLastnameLength = editor.getLastname().length();
        int editorLoginLength = editor.getLogin().length();
        int editorPasswordLength = editor.getPassword().length();
        valid &= editorLoginLength >= 2 && editorLoginLength <= 64;
        valid &= editorPasswordLength >= 8 && editorPasswordLength <= 128;
        valid &= editorFirstnameLength >= 2 && editorFirstnameLength <= 64;
        valid &= editorLastnameLength >= 2 && editorLastnameLength <= 64;
        if(!valid) {
            return new ResponseEntity<>(new Editor(), HttpStatus.valueOf(400));
        } else {
            return (ResponseEntity<Editor>) joinPoint.proceed();
        }
    }
}
