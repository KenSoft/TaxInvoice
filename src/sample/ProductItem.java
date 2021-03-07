package sample;

public class ProductItem {
    public int productId;
    public double price;
    public String produtName, unit, description;

    public ProductItem(int productId, String produtName, String unit, String description, double price){
        this.productId = productId;
        this.produtName = produtName;
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

    public String getProdutName() {
        return produtName;
    }

    public String getUnit() {
        return unit;
    }
}
