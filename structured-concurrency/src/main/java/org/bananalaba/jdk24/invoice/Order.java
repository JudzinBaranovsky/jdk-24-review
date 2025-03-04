package org.bananalaba.jdk24.invoice;

import java.util.List;
import lombok.NonNull;

public record Order(int id, @NonNull List<LineItem> lineItems) {
}
