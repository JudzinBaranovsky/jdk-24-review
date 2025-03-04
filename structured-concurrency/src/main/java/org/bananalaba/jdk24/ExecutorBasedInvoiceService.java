package org.bananalaba.jdk24;

import java.util.concurrent.ExecutorService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ExecutorBasedInvoiceService implements InvoiceService {

    @NonNull
    private final OrderService orderService;
    @NonNull
    private final CustomerService customerService;
    @NonNull
    private final InvoiceTemplateService invoiceTemplateService;
    @NonNull
    private final InvoiceRenderer renderer;

    @NonNull
    private final ExecutorService executor;

    @Override
    public String generateInvoice(final int customerId, final int orderId, final String language) {
        var customerTask = executor.submit(() -> customerService.getCustomer(customerId));
        var orderTask = executor.submit(() -> orderService.getOrder(orderId));
        var invoiceTemplateTask = executor.submit(() -> invoiceTemplateService.getInvoiceTemplate(language));

        Customer customer;
        try {
            customer = customerTask.get();
        } catch (Exception e) {
            orderTask.cancel(false);
            invoiceTemplateTask.cancel(false);

            throw new InternalExecutionException("customer task failed", e);
        }

        Order order;
        try {
            order = orderTask.get();
        } catch (Exception e) {
            invoiceTemplateTask.cancel(false);

            throw new InternalExecutionException("order task failed", e);
        }

        InvoiceTemplate invoiceTemplate;
        try {
            invoiceTemplate = invoiceTemplateTask.get();
        } catch (Exception e) {
            throw new InternalExecutionException("invoice template task failed", e);
        }

        return renderer.render(invoiceTemplate, customer, order);
    }

}
