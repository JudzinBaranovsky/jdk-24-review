package org.bananalaba.jdk24.invoice;

public interface InvoiceRenderer {

    String render(InvoiceTemplate template, Customer customer, Order order);

}
