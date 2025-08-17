package com.petverse.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)//bu anotasyonla firlatilacak exceptionlari kontrol altina alirken hangi status code kullanilacagini da belirliyoruz
//kullanilmazsa frameworkun yonetimine kalir ve muhtemelen 500 lu kodlar firlatir.
public class ConflictException extends RuntimeException{

    public ConflictException(String message){
        super(message);
    }
}