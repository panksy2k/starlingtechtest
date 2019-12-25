package com.starlingbank.tech.domain;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Getter
public class ErrorResponse implements Serializable {
    private static final long serialVersionUID = 81199931060897247L;

    Integer errorCode;
    String errorMessage;
}
