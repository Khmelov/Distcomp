package by.bsuir.dc.lab4.filters;

import by.bsuir.dc.lab4.dto.MarkerRequestTo;
import by.bsuir.dc.lab4.entities.Marker;
import by.bsuir.dc.lab4.services.interfaces.MarkerService;
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
public class MarkerValidationAspect {
    @Autowired
    private MarkerService service;

    @Around("execution(* by.bsuir.dc.lab4.controllers.MarkerController.add(..))")
    public ResponseEntity<Marker> filterBeforePostRequest(ProceedingJoinPoint joinPoint) throws Throwable {
        Object[] args = joinPoint.getArgs();
        MarkerRequestTo marker = (MarkerRequestTo) args[0];
        Marker markerWithDuplicateLogin = service.getByName(marker.getName());
        if(markerWithDuplicateLogin != null){
            return new ResponseEntity<>(new Marker(), HttpStatus.valueOf(403));
        }
        int markerNameLength = marker.getName().length();
        boolean valid = markerNameLength >= 2 && markerNameLength <= 32;
        if(!valid) {
            return new ResponseEntity<>(new Marker(), HttpStatus.valueOf(400));
        } else {
            return (ResponseEntity<Marker>) joinPoint.proceed();
        }
    }

    @Around("execution(* by.bsuir.dc.lab4.controllers.MarkerController.update(..))")
    public ResponseEntity<Marker> filterBeforePutRequest(ProceedingJoinPoint joinPoint) throws Throwable {
        Object[] args = joinPoint.getArgs();
        MarkerRequestTo marker = (MarkerRequestTo) args[0];
        Marker markerWithDuplicateLogin = service.getByName(marker.getName());
        if(markerWithDuplicateLogin != null){
            return new ResponseEntity<>(new Marker(), HttpStatus.valueOf(403));
        }
        int markerNameLength = marker.getName().length();
        boolean valid = markerNameLength >= 2 && markerNameLength <= 32;
        if(!valid) {
            return new ResponseEntity<>(new Marker(), HttpStatus.valueOf(400));
        } else {
            return (ResponseEntity<Marker>) joinPoint.proceed();
        }
    }
}
