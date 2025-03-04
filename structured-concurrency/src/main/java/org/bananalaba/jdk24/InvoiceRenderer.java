package org.bananalaba.jdk24;

public interface InvoiceRenderer {

    String render(InvoiceTemplate template, Customer customer, Order order);

}
