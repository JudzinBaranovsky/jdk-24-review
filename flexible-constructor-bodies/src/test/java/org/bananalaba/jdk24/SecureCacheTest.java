package org.bananalaba.jdk24;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.Test;

public class SecureCacheTest {

    @Test
    void shouldNotAllowWeakEncryptionAlgorithm() {
        assertThatThrownBy(() -> new SecureCache("DES", 10)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void shouldNotAllowCacheOverload() {
        var cache = new SecureCache("SHA-256", 2);

        cache.put("a", "1");
        cache.put("b", "2");
        assertThatThrownBy(() -> cache.put("c", "3")).isInstanceOf(IllegalStateException.class);
    }

}
