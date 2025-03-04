package org.bananalaba.jdk24.invoice;

public interface InvoiceService {

    String generateInvoice(final int customerId, final int orderId, final String language);

}
