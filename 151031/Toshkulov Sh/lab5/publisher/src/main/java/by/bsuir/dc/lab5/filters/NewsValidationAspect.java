package by.bsuir.dc.lab5.filters;


import by.bsuir.dc.lab5.dto.NewsRequestTo;
import by.bsuir.dc.lab5.entities.News;
import by.bsuir.dc.lab5.services.interfaces.NewsService;
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
public class NewsValidationAspect {
    @Autowired
    private NewsService service;

    @Around("execution(* by.bsuir.dc.lab5.controllers.NewsController.add(..))")
    public ResponseEntity<News> filterBeforePostRequest(ProceedingJoinPoint joinPoint) throws Throwable {
        Object[] args = joinPoint.getArgs();
        NewsRequestTo news = (NewsRequestTo) args[0];
        News newsWithDuplicateLogin = service.getByTitle(news.getTitle());
        if(newsWithDuplicateLogin != null){
            return new ResponseEntity<>(new News(), HttpStatus.valueOf(403));
        }
        boolean valid = true;
        int newsTitleLength = news.getTitle().length();
        int newsContentLength = news.getContent().length();
        valid &= newsTitleLength >= 2 && newsTitleLength <= 64;
        valid &= newsContentLength >= 4 && newsContentLength <= 2048;
        if(!valid) {
            return new ResponseEntity<>(new News(), HttpStatus.valueOf(400));
        } else {
            return (ResponseEntity<News>) joinPoint.proceed();
        }
    }

    @Around("execution(* by.bsuir.dc.lab5.controllers.NewsController.update(..))")
    public ResponseEntity<News> filterBeforePutRequest(ProceedingJoinPoint joinPoint) throws Throwable {
        Object[] args = joinPoint.getArgs();
        NewsRequestTo news = (NewsRequestTo) args[0];
        News newsWithDuplicateLogin = service.getByTitle(news.getTitle());
        if(newsWithDuplicateLogin != null){
            return new ResponseEntity<>(new News(), HttpStatus.valueOf(403));
        }
        boolean valid = true;
        int newsTitleLength = news.getTitle().length();
        int newsContentLength = news.getContent().length();
        valid &= newsTitleLength >= 2 && newsTitleLength <= 64;
        valid &= newsContentLength >= 4 && newsContentLength <= 2048;
        if(!valid) {
            return new ResponseEntity<>(new News(), HttpStatus.valueOf(400));
        } else {
            return (ResponseEntity<News>) joinPoint.proceed();
        }
    }
}
