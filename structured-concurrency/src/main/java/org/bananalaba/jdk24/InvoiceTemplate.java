package org.bananalaba.jdk24;

import lombok.NonNull;

public record InvoiceTemplate(@NonNull String contentBody, @NonNull String contentType) {
}
