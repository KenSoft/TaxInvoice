package sample;

public class InvoiceItem {
    public int invoiceId;
    public double totalPrice;
    public String customerName;

    public InvoiceItem(int invoiceId, double totalPrice, String customerName){
        this.invoiceId = invoiceId;
        this.totalPrice = totalPrice;
        this.customerName = customerName;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public int getInvoiceId() {
        return invoiceId;
    }

    public String getCustomerName() {
        return customerName;
    }
}
