package org.bananalaba.jdk24;

import java.util.concurrent.StructuredTaskScope;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class StructuredConcurrencyInvoiceService implements InvoiceService {

    @NonNull
    private final OrderService orderService;
    @NonNull
    private final CustomerService customerService;
    @NonNull
    private final InvoiceTemplateService invoiceTemplateService;
    @NonNull
    private final InvoiceRenderer renderer;

    @Override
    public String generateInvoice(final int customerId, final int orderId, final String language) {
        try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
            var customerTask = scope.fork(() -> customerService.getCustomer(customerId));
            var orderTask = scope.fork(() -> orderService.getOrder(orderId));
            var invoiceTemplateTask = scope.fork(() -> invoiceTemplateService.getInvoiceTemplate(language));

            scope.join();
            scope.throwIfFailed();

            var customer = customerTask.get();
            var order = orderTask.get();
            var invoiceTemplate = invoiceTemplateTask.get();

            return renderer.render(invoiceTemplate, customer, order);
        } catch (Exception e) {
            throw new InternalExecutionException("invoice generation failed", e);
        }
    }

}
