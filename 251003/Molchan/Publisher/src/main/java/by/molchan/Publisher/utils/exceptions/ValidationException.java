package by.molchan.Publisher.utils.exceptions;

import org.springframework.validation.BindingResult;

public class ValidationException extends RuntimeException{
    private BindingResult bindingResult;

    public ValidationException(BindingResult bindingResult) {
        this.bindingResult = bindingResult;
    }

    public BindingResult getBindingResult() {
        return bindingResult;
    }
}
