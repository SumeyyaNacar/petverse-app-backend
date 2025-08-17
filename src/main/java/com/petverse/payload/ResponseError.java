package com.petverse.payload;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
//Exception durumlarında (hatalarda) kullanacağımız standardize edilmiş hata yanıtı.
public class ResponseError {
    private int status;
    private String error;
    private String message;
    private LocalDateTime timestamp;
}
