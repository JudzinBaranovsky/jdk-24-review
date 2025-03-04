package org.bananalaba.jdk24.invoice;

import lombok.NonNull;

public record Customer(int id, @NonNull String fullName) {
}
