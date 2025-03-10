package org.bananalaba.jdk24;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.jackson.Jacksonized;

@Jacksonized
@Builder
@Getter
@RequiredArgsConstructor
public class ErrorDto {

    private final String message;

}
