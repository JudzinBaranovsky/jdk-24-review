package org.bananalaba.jdk24;

import lombok.NonNull;

public record Customer(int id, @NonNull String fullName) {
}
