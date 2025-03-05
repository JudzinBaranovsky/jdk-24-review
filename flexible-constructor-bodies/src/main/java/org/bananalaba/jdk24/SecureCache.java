package org.bananalaba.jdk24;

import java.util.Set;

// assume the ThirdPartyCache class is from a third-party library = not under our control
// yet we need to use exactly ThirdPartyCache or its subclass
public class SecureCache extends ThirdPartyCache {

    private static final Set<String> WEAK_ENCRYPTION_ALGORITHMS = Set.of(
        "DES", "MD-5", "SHA-1", "RC4", "RSA"
    );

    private final int maxSize;

    public SecureCache(final String encryptionAlgorithm, final int maxSize) {
        // opportunity 1 - code that do not access "this" instance
        if (WEAK_ENCRYPTION_ALGORITHMS.contains(encryptionAlgorithm)) {
            throw new IllegalArgumentException("weak encryption algorithm: " + encryptionAlgorithm);
        }

        // opportunity 2 - code that works with the subclass fields, not the superclass'
        if (maxSize < 0) {
            throw new IllegalArgumentException("invalid max size: " + maxSize);
        }
        this.maxSize = maxSize;

        // assume the initialisation logic is expensive
        // and we want to do the validation before it
        super(encryptionAlgorithm);
    }

    public void put(final String key, final String value) {
        if (size() == maxSize) {
            throw new IllegalStateException("cache is full");
        }

        super.put(key, value);
    }

}
