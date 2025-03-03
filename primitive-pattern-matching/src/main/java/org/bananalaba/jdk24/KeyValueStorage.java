package org.bananalaba.jdk24;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class KeyValueStorage {

    @NonNull
    private final KeyValueEngine engine;

    public void save(@NonNull final Parameter parameter) {
        switch (parameter) {
            case Parameter(String key, byte value) -> engine.putByte(key, value);
            case Parameter(String key, double value) -> engine.putDouble(key, value);
        }
    }

}
