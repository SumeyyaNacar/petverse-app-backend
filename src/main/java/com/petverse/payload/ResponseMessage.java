package com.petverse.payload;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import org.springframework.http.HttpStatus;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseMessage<E> {//farkli data typelerle calismasi icin generic turde olustur

    private E object;
    private String message;
    private HttpStatus httpStatus;
}
