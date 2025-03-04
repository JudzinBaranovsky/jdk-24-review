package org.bananalaba.jdk24;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class StructuredConcurrencyMotivation {

    @Mock
    private OrderService orderService;
    @Mock
    private CustomerService customerService;
    @Mock
    private InvoiceTemplateService invoiceTemplateService;
    @Mock
    private InvoiceRenderer invoiceRenderer;

    private ExecutorService executorService = Executors.newSingleThreadExecutor();

    @Test
    void shouldGenerateInvoiceWithExecutor() {
        var service = new ExecutorBasedInvoiceService(
            orderService,
            customerService,
            invoiceTemplateService,
            invoiceRenderer,
            executorService
        );

        shouldGenerateInvoice(service);
    }

    @Test
    void shouldGenerateInvoiceWithStructuredConcurrency() {
        var service = new StructuredConcurrencyInvoiceService(
            orderService,
            customerService,
            invoiceTemplateService,
            invoiceRenderer
        );

        shouldGenerateInvoice(service);
    }

    void shouldGenerateInvoice(InvoiceService invoiceService) {
        var order = new Order(1, List.of(new LineItem(1, 1)));
        when(orderService.getOrder(1)).thenReturn(order);

        var customer = new Customer(1, "test customer");
        when(customerService.getCustomer(1)).thenReturn(customer);

        var invoiceTemplate = new InvoiceTemplate("{}", "json");
        when(invoiceTemplateService.getInvoiceTemplate("en")).thenReturn(invoiceTemplate);

        var expected = "invoice in json";
        when(invoiceRenderer.render(invoiceTemplate, customer, order)).thenReturn(expected);

        var actual = invoiceService.generateInvoice(1, 1, "en");

        assertThat(actual).isEqualTo(expected);
    }

}
