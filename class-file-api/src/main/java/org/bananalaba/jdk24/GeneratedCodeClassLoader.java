package org.bananalaba.jdk24;

import java.security.SecureClassLoader;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import lombok.NonNull;

public class GeneratedCodeClassLoader extends SecureClassLoader {

    private final Set<String> generatedClassNames = new ConcurrentSkipListSet<>();
    private final Lock lock = new ReentrantLock();

    public GeneratedCodeClassLoader(final ClassLoader parent) {
        super(parent);
    }

    public Class<?> define(@NonNull final String fullName, @NonNull final byte[] code) {
        lock.lock();

        try {
            return defineIfMissing(fullName, code);
        } finally {
            lock.unlock();
        }
    }

    private Class<?> defineIfMissing(final String fullName, final byte[] code) {
        if (generatedClassNames.contains(fullName)) {
            throw new IllegalStateException("class " + fullName + " already defined");
        }

        generatedClassNames.add(fullName);
        return defineClass(fullName, code, 0, code.length);
    }

}
