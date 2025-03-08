package org.bananalaba.jdk24;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.jackson.Jacksonized;

@RequiredArgsConstructor
@Jacksonized
@Builder
@Getter
public class Message {

    @NonNull
    private final String key;
    @NonNull
    private final String text;

}
