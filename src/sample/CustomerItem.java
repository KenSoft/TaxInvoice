package sample;

public class CustomerItem {
    public int customerId;
    public String customerName,address,homePhone,officePhone,mobilePhone,email,taxId;

    public CustomerItem(int customerId, String customerName, String address, String homePhone, String officePhone,
                        String mobilePhone, String email, String taxId){
        this.customerId = customerId;
        this.customerName = customerName;
        this.address = address;
        this.homePhone = homePhone;
        this.officePhone = officePhone;
        this.mobilePhone = mobilePhone;
        this.email = email;
        this.taxId = taxId;
    }
    public String getTaxId(){
        return taxId;
    }
    public String getAddress(){
        return address;
    }
    public String getCustomerName(){
        return customerName;
    }
    public String getHomePhone(){
        return homePhone;
    }
    public String getOfficePhone(){
        return officePhone;
    }
    public String getMobilePhone(){
        return mobilePhone;
    }
    public String getEmail(){
        return email;
    }
    public int getCustomerId(){
        return customerId;
    }
}
