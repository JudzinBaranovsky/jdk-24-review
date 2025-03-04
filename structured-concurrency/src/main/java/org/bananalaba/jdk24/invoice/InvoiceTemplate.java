package org.bananalaba.jdk24.invoice;

import lombok.NonNull;

public record InvoiceTemplate(@NonNull String contentBody, @NonNull String contentType) {
}
