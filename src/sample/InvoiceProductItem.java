package sample;

public class InvoiceProductItem {
    public int productId,number,quantity;
    public double price, totalPrice;
    public String productName,unit;

    public InvoiceProductItem(int number, int productId, String productName, String unit, int quantity, double price, double totalPrice){
        this.number = number;
        this.productId = productId;
        this.productName = productName;
        this.unit = unit;
        this.price = price;
        this.totalPrice = totalPrice;
        this.quantity = quantity;
    }

    public double getPrice() {
        return price;
    }

    public int getProductId() {
        return productId;
    }

    public int getNumber() {
        return number;
    }

    public String getProductName() {
        return productName;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public String getUnit() {
        return unit;
    }

    public int getQuantity() {
        return quantity;
    }
}
