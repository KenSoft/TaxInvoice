package sample;

public class ProductItem {
    public int productId;
    public double price;
    public String productName, unit, description;

    public ProductItem(int productId, String productName, String unit, String description, double price){
        this.productId = productId;
        this.productName = productName;
        this.unit = unit;
        this.description = description;
        this.price = price;
    }

    public double getPrice() {
        return price;
    }

    public int getProductId() {
        return productId;
    }

    public String getDescription() {
        return description;
    }

    public String getProductName() {
        return productName;
    }

    public String getUnit() {
        return unit;
    }

}
