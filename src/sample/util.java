package sample;
import java.sql.*;
import org.json.*;

public class util {
    static String SQLUser = "application";
    static String SQLPassword = "nIADePiLqD7YvPrK";
    public static JSONObject loginSQL(String username, String password){
        JSONObject obj = new JSONObject();
        obj.put("role","Invalid");
        try{
            Class.forName("com.mysql.jdbc.Driver");
            Connection con=DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/tax_receipt_system",SQLUser, SQLPassword);
            //here sonoo is database name, root is username and password
            Statement stmt=con.createStatement();
            ResultSet rs=stmt.executeQuery("select * from users where username='"+username+"' AND password=password('"+password+"')");
            while(rs.next()) {
                //System.out.println(rs.getString(1));
                JSONObject log = new JSONObject();
                log.put("withData",1);
                log.put("action","User logged in successfully");
                log.put("userId",rs.getInt(1));
                writeLog(log);
                obj.put("userId",rs.getInt(1));
                obj.put("username",rs.getString(2));
                obj.put("fullname",rs.getString(3));
                obj.put("role",rs.getString(5));
            }
            con.close();
        }catch(Exception e){ System.out.println(e);}

        return obj;
    }
    public static void deleteUser(int userId, JSONObject superuserInfo){
        try{

            Class.forName("com.mysql.jdbc.Driver");
            Connection con=DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/tax_receipt_system",SQLUser, SQLPassword);
            //here sonoo is database name, root is username and password
            Statement stmt=con.createStatement();
            stmt.execute("DELETE FROM users WHERE userId="+userId);

            JSONObject log = new JSONObject();
            log.put("withData",1);
            log.put("target",userId);
            log.put("action","User deleted successfully");
            log.put("userId",superuserInfo.getInt("userId"));
            writeLog(log);
            con.close();
        }catch(Exception e){ System.out.println(e);}
    }
    public static JSONObject superuserLoginSQL(String username, String password){
        JSONObject obj = new JSONObject();
        obj.put("role","Invalid");
        try{
            Class.forName("com.mysql.jdbc.Driver");
            Connection con=DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/tax_receipt_system",SQLUser, SQLPassword);
            //here sonoo is database name, root is username and password
            Statement stmt=con.createStatement();
            ResultSet rs=stmt.executeQuery("select * from users where username='"+username+"' AND password=password('"+password+"') AND role='Admin'");
            while(rs.next()) {
                //System.out.println(rs.getString(1));
                JSONObject log = new JSONObject();
                log.put("withData",1);
                log.put("action","Superuser logged in successfully");
                log.put("userId",rs.getInt(1));
                writeLog(log);
                obj.put("userId",rs.getInt(1));
                obj.put("username",rs.getString(2));
                obj.put("fullname",rs.getString(3));
                obj.put("role",rs.getString(5));
            }
            con.close();
        }catch(Exception e){ System.out.println(e);}

        return obj;
    }
    public static JSONArray getUsers(JSONObject superuserInfo){
        JSONArray users = new JSONArray();

        try{

            Class.forName("com.mysql.jdbc.Driver");
            Connection con=DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/tax_receipt_system",SQLUser, SQLPassword);
            //here sonoo is database name, root is username and password
            Statement stmt=con.createStatement();
            ResultSet rs=stmt.executeQuery("select * from users where role='User'");
            while(rs.next()) {
                JSONObject obj = new JSONObject();
                obj.put("userId",rs.getInt(1));
                obj.put("username",rs.getString(2));
                obj.put("fullname",rs.getString(3));
                users.put(obj);

            }
            JSONObject log = new JSONObject();
            log.put("withData",1);
            log.put("action","User list fetched successfully");
            log.put("userId",superuserInfo.getInt("userId"));
            writeLog(log);
            con.close();
        }catch(Exception e){ System.out.println(e);}
        return users;
    }
    public static JSONObject getUser(int userId,JSONObject superuserInfo){
        JSONObject user = new JSONObject();
        try{

            Class.forName("com.mysql.jdbc.Driver");
            Connection con=DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/tax_receipt_system",SQLUser, SQLPassword);
            //here sonoo is database name, root is username and password
            Statement stmt=con.createStatement();
            ResultSet rs=stmt.executeQuery("select * from users where userId="+userId);
            while(rs.next()) {

                user.put("userId",rs.getInt(1));
                user.put("username",rs.getString(2));
                user.put("fullname",rs.getString(3));
                user.put("role",rs.getString(5));
                JSONObject log = new JSONObject();
                log.put("withData",2);
                log.put("target",user.toString());
                log.put("action","User information read successfully");
                log.put("userId",superuserInfo.getInt("userId"));
                writeLog(log);
            }
            con.close();
        }catch(Exception e){ System.out.println(e);}
        return user;
    }
    public static int getUserByUsername(String username,JSONObject superuserInfo){
        JSONObject user = new JSONObject();
        int count = 0;
        try{

            Class.forName("com.mysql.jdbc.Driver");
            Connection con=DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/tax_receipt_system",SQLUser, SQLPassword);

            Statement stmt=con.createStatement();
            ResultSet rs=stmt.executeQuery("select * from users where role='User' AND username='"+username+"'");
            JSONObject log = new JSONObject();
            log.put("withData",2);
            log.put("target",username);
            log.put("action","User information read successfully");
            log.put("userId",superuserInfo.getInt("userId"));
            writeLog(log);
            while(rs.next()) {

                count++;

            }
            //System.out.println(count);
            con.close();
        }catch(Exception e){ System.out.println(e);}
        return count;
    }

    public static void modifyUser(JSONObject newInfo, JSONObject superuserInfo){
        JSONObject oldInfo = getUser(newInfo.getInt("userId"),superuserInfo);
        try{

            Class.forName("com.mysql.jdbc.Driver");
            Connection con=DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/tax_receipt_system",SQLUser, SQLPassword);
            //here sonoo is database name, root is username and password
            Statement stmt=con.createStatement();
            stmt.execute("UPDATE users SET username='"+newInfo.getString("username")+"', password=password('"+newInfo.getString("password")+"'),"+
                    "fullname='"+newInfo.getString("fullname")+"',role='"+newInfo.getString("role")+"' WHERE userId="+newInfo.getInt("userId"));
            JSONObject log = new JSONObject();
            log.put("withData",3);
            log.put("target",oldInfo.toString());
            newInfo.remove("password");
            newInfo.remove("confirmPassword");
            log.put("newValue", newInfo.toString());
            log.put("action","User information edited successfully");
            log.put("userId",superuserInfo.getInt("userId"));
            writeLog(log);
            con.close();
        }catch(Exception e){ System.out.println(e);}
    }
    public static void newUser(JSONObject newInfo, JSONObject superuserInfo){
        try{
            Class.forName("com.mysql.jdbc.Driver");
            Connection con=DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/tax_receipt_system",SQLUser, SQLPassword);
            //here sonoo is database name, root is username and password
            Statement stmt=con.createStatement();
            stmt.execute("INSERT INTO users (username,fullname,password,role) VALUES ('"+newInfo.getString("username")+"','"
                    +newInfo.getString("fullname")+"',password('"+newInfo.getString("password")+"'),'"+newInfo.getString("role")+"')");
            JSONObject log = new JSONObject();
            log.put("withData",2);
            newInfo.remove("password");
            newInfo.remove("confirmPassword");
            log.put("target", newInfo.toString());
            log.put("action","User created successfully");
            log.put("userId",superuserInfo.getInt("userId"));
            writeLog(log);
            con.close();
        }catch(Exception e){ System.out.println(e);}
    }
    public static void writeLog(JSONObject log){
        int withData = log.getInt("withData");
        String action = log.getString("action");

        try{

            Class.forName("com.mysql.jdbc.Driver");
            Connection con=DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/tax_receipt_system",SQLUser, SQLPassword);
            //here sonoo is database name, root is username and password
            Statement stmt=con.createStatement();
            if(withData==3){
                String target = log.getString("target");
                String newValue = log.getString("newValue");
                int userId = log.getInt("userId");
                stmt.execute("INSERT INTO logs (userId,action,target,newValue) VALUES ("+userId+",'"
                        +action+"','"+target+"','"+newValue+"')");
            } else if (withData==2){
                String target = log.getString("target");
                int userId = log.getInt("userId");
                stmt.execute("INSERT INTO logs (userId,action,target) VALUES ("+userId+",'"
                    +action+"','"+target+"')");
            } else if (withData==1){
                int userId = log.getInt("userId");
                stmt.execute("INSERT INTO logs (userId,action) VALUES ("+userId+",'"
                        +action+"')");
            } else {
                stmt.execute("INSERT INTO logs (action) VALUES ('"+action+"')");
            }

            con.close();
        }catch(Exception e){ System.out.println(e);}
    }
    public static JSONArray getCustomers(JSONObject userInfo){
        JSONArray customers = new JSONArray();

        try{

            Class.forName("com.mysql.jdbc.Driver");
            Connection con=DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/tax_receipt_system",SQLUser, SQLPassword);
            //here sonoo is database name, root is username and password
            Statement stmt=con.createStatement();
            ResultSet rs=stmt.executeQuery("select * from clients");
            while(rs.next()) {
                JSONObject customer = new JSONObject();
                customer.put("customerId",rs.getInt(1));
                customer.put("customerName",rs.getString(2));
                customer.put("address",rs.getString(3));
                customer.put("homePhone",rs.getString(4));
                customer.put("officePhone",rs.getString(5));
                customer.put("mobilePhone",rs.getString(6));
                customer.put("email",rs.getString(7));
                customer.put("taxId",rs.getString(8));
                customers.put(customer);

            }
            JSONObject log = new JSONObject();
            log.put("withData",1);
            log.put("action","Customer list fetched successfully");
            log.put("userId",userInfo.getInt("userId"));
            writeLog(log);
            con.close();
        }catch(Exception e){ System.out.println(e);}
        return customers;
    }
    public static int getCustomerByTaxId(String taxId,JSONObject superuserInfo){
        JSONObject user = new JSONObject();
        int count = 0;
        try{

            Class.forName("com.mysql.jdbc.Driver");
            Connection con=DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/tax_receipt_system",SQLUser, SQLPassword);

            Statement stmt=con.createStatement();
            ResultSet rs=stmt.executeQuery("select * from clients where taxId='"+taxId+"'");
            JSONObject log = new JSONObject();
            log.put("withData",2);
            log.put("target",taxId);
            log.put("action","Customer information read successfully");
            log.put("userId",superuserInfo.getInt("userId"));
            writeLog(log);
            while(rs.next()) {

                count++;

            }
            //System.out.println(count);
            con.close();
        }catch(Exception e){ System.out.println(e);}
        return count;
    }
    public static JSONObject getCustomer(int customerId,JSONObject superuserInfo){
        JSONObject customer = new JSONObject();

        try{

            Class.forName("com.mysql.jdbc.Driver");
            Connection con=DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/tax_receipt_system",SQLUser, SQLPassword);

            Statement stmt=con.createStatement();
            ResultSet rs=stmt.executeQuery("select * from clients where customerId="+customerId);
            JSONObject log = new JSONObject();

            while(rs.next()) {

                customer.put("customerId",rs.getInt(1));
                customer.put("customerName",rs.getString(2));
                customer.put("address",rs.getString(3));
                customer.put("homePhone",rs.getString(4));
                customer.put("officePhone",rs.getString(5));
                customer.put("mobilePhone",rs.getString(6));
                customer.put("email",rs.getString(7));
                customer.put("taxId",rs.getString(8));

            }
            log.put("withData",2);
            log.put("target",customer.toString());
            log.put("action","Customer information read successfully");
            log.put("userId",superuserInfo.getInt("userId"));
            writeLog(log);
            //System.out.println(count);
            con.close();
        }catch(Exception e){ System.out.println(e);}
        return customer;
    }
    public static void newCustomer(JSONObject newCustomer, JSONObject userInfo){
        try{
            Class.forName("com.mysql.jdbc.Driver");
            Connection con=DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/tax_receipt_system",SQLUser, SQLPassword);
            //here sonoo is database name, root is username and password
            Statement stmt=con.createStatement();
            stmt.execute("INSERT INTO clients (customerName, address, homePhone, officePhone, mobilePhone, email, taxId) VALUES ('"+
                    newCustomer.getString("customerName")+"','"+newCustomer.getString("address")+"','"
                    +newCustomer.getString("homePhone")+"','"+newCustomer.getString("officePhone")+"','"
                    +newCustomer.getString("mobilePhone")+"','"+newCustomer.getString("email")+"','"
                    +newCustomer.getString("taxId")+"')");

            JSONObject log = new JSONObject();
            log.put("withData",2);
            log.put("target", newCustomer.toString());
            log.put("action","Customer added successfully");
            log.put("userId",userInfo.getInt("userId"));
            writeLog(log);
            con.close();
        }catch(Exception e){ System.out.println(e);}
    }
    public static void modifyCustomer(JSONObject newInfo, JSONObject superuserInfo){
        JSONObject oldInfo = getCustomer(newInfo.getInt("customerId"),superuserInfo);
        try{

            Class.forName("com.mysql.jdbc.Driver");
            Connection con=DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/tax_receipt_system",SQLUser, SQLPassword);
            //here sonoo is database name, root is username and password
            Statement stmt=con.createStatement();
            stmt.execute("UPDATE clients SET customerName='"+newInfo.getString("customerName")
                    +"', address='"+newInfo.getString("address")
                    +"',homePhone='"+newInfo.getString("homePhone")
                    +"',officePhone='"+newInfo.getString("officePhone")
                    +"',mobilePhone='"+newInfo.getString("mobilePhone")
                    +"',email='"+newInfo.getString("email")
                    +"',taxId='"+newInfo.getString("taxId")
                    +"' WHERE customerId="+newInfo.getInt("customerId"));

            JSONObject log = new JSONObject();
            log.put("withData",3);
            log.put("target",oldInfo.toString());
            log.put("newValue", newInfo.toString());
            log.put("action","Customer information edited successfully");
            log.put("userId",superuserInfo.getInt("userId"));
            writeLog(log);
            con.close();
        }catch(Exception e){ System.out.println(e);}
    }
    public static void deleteCustomer(int customerId, JSONObject superuserInfo){
        try{

            Class.forName("com.mysql.jdbc.Driver");
            Connection con=DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/tax_receipt_system",SQLUser, SQLPassword);
            //here sonoo is database name, root is username and password
            Statement stmt=con.createStatement();
            stmt.execute("DELETE FROM clients WHERE customerId="+customerId);

            JSONObject log = new JSONObject();
            log.put("withData",1);
            log.put("target",customerId);
            log.put("action","Customer deleted successfully");
            log.put("userId",superuserInfo.getInt("userId"));
            writeLog(log);
            con.close();
        }catch(Exception e){ System.out.println(e);}
    }
    public static JSONArray getProducts(JSONObject userInfo){
        JSONArray products = new JSONArray();

        try{

            Class.forName("com.mysql.jdbc.Driver");
            Connection con=DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/tax_receipt_system",SQLUser, SQLPassword);
            //here sonoo is database name, root is username and password
            Statement stmt=con.createStatement();
            ResultSet rs=stmt.executeQuery("select * from products");
            while(rs.next()) {
                JSONObject product = new JSONObject();
                product.put("productId",rs.getInt(1));
                product.put("productName",rs.getString(2));
                product.put("unit",rs.getString(3));
                product.put("description",rs.getString(4));
                product.put("price",rs.getDouble(5));
                products.put(product);

            }
            JSONObject log = new JSONObject();
            log.put("withData",1);
            log.put("action","Product list fetched successfully");
            log.put("userId",userInfo.getInt("userId"));
            writeLog(log);
            con.close();
        }catch(Exception e){ System.out.println(e);}
        return products;
    }
    public static void newProduct(JSONObject newProduct, JSONObject userInfo){
        try{
            Class.forName("com.mysql.jdbc.Driver");
            Connection con=DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/tax_receipt_system",SQLUser, SQLPassword);
            //here sonoo is database name, root is username and password
            Statement stmt=con.createStatement();
            stmt.execute("INSERT INTO products (productName, unit, description, price) VALUES ('"+
                    newProduct.getString("productName")+"','"+newProduct.getString("unit")+"','"
                    +newProduct.getString("description")+"','"+newProduct.getDouble("price")+"')");

            JSONObject log = new JSONObject();
            log.put("withData",2);
            log.put("target", newProduct.toString());
            log.put("action","Product added successfully");
            log.put("userId",userInfo.getInt("userId"));
            writeLog(log);
            con.close();
        }catch(Exception e){ System.out.println(e);}
    }
    public static JSONObject getProduct(int productId,JSONObject userInfo){
        JSONObject product = new JSONObject();

        try{

            Class.forName("com.mysql.jdbc.Driver");
            Connection con=DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/tax_receipt_system",SQLUser, SQLPassword);

            Statement stmt=con.createStatement();
            ResultSet rs=stmt.executeQuery("select * from products where productId="+productId);
            JSONObject log = new JSONObject();

            while(rs.next()) {


                product.put("productId",rs.getInt(1));
                product.put("productName",rs.getString(2));
                product.put("unit",rs.getString(3));
                product.put("description",rs.getString(4));
                product.put("price",rs.getDouble(5));


            }
            log.put("withData",2);
            log.put("target",product.toString());
            log.put("action","Product information read successfully");
            log.put("userId",userInfo.getInt("userId"));
            writeLog(log);
            //System.out.println(count);
            con.close();
        }catch(Exception e){ System.out.println(e);}
        return product;
    }
    public static void modifyProduct(JSONObject newInfo, JSONObject userInfo){
        JSONObject oldInfo = getProduct(newInfo.getInt("productId"),userInfo);
        try{

            Class.forName("com.mysql.jdbc.Driver");
            Connection con=DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/tax_receipt_system",SQLUser, SQLPassword);
            //here sonoo is database name, root is username and password
            Statement stmt=con.createStatement();
            stmt.execute("UPDATE products SET productName='"+newInfo.getString("productName")
                    +"',description='"+newInfo.getString("description")
                    +"',unit='"+newInfo.getString("unit")
                    +"',price='"+newInfo.getDouble("price")
                    +"' WHERE productId="+newInfo.getInt("productId"));

            JSONObject log = new JSONObject();
            log.put("withData",3);
            log.put("target",oldInfo.toString());
            log.put("newValue", newInfo.toString());
            log.put("action","Product information edited successfully");
            log.put("userId",userInfo.getInt("userId"));
            writeLog(log);
            con.close();
        }catch(Exception e){ System.out.println(e);}
    }
    public static void deleteProduct(int productId, JSONObject userInfo){
        try{

            Class.forName("com.mysql.jdbc.Driver");
            Connection con=DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/tax_receipt_system",SQLUser, SQLPassword);
            //here sonoo is database name, root is username and password
            Statement stmt=con.createStatement();
            stmt.execute("DELETE FROM products WHERE productId="+productId);

            JSONObject log = new JSONObject();
            log.put("withData",1);
            log.put("target",productId);
            log.put("action","Product deleted successfully");
            log.put("userId",userInfo.getInt("userId"));
            writeLog(log);
            con.close();
        }catch(Exception e){ System.out.println(e);}
    }
    public static int newInvoice(JSONObject userInfo){
        int invoiceId = 0;
        try{
            Class.forName("com.mysql.jdbc.Driver");
            Connection con=DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/tax_receipt_system",SQLUser, SQLPassword);
            //here sonoo is database name, root is username and password
            Statement stmt=con.createStatement();
            stmt.execute("INSERT INTO invoices (JSON) VALUES ('{}')");
            ResultSet rs = stmt.executeQuery("SELECT MAX(invoiceId) FROM `invoices`");
            while(rs.next()){
                invoiceId = rs.getInt(1);
            }
            JSONObject log = new JSONObject();
            log.put("withData",2);
            log.put("target", String.valueOf(invoiceId));
            log.put("action","New invoice created successfully");
            log.put("userId",userInfo.getInt("userId"));
            writeLog(log);
            con.close();
            return invoiceId;
        }catch(Exception e){ System.out.println(e);}
        return 0;
    }
    public static JSONObject getCompanyInfo(JSONObject userInfo){
        JSONObject product = new JSONObject();

        try{

            Class.forName("com.mysql.jdbc.Driver");
            Connection con=DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/tax_receipt_system",SQLUser, SQLPassword);

            Statement stmt=con.createStatement();
            ResultSet rs=stmt.executeQuery("select * from companyInfo where id=1");
            JSONObject log = new JSONObject();

            while(rs.next()) {


                product.put("companyName",rs.getString(2));
                product.put("taxId",rs.getString(3));
                product.put("address",rs.getString(4));
                product.put("officePhone",rs.getString(5));
                product.put("email",rs.getString(6));
                product.put("website",rs.getString(7));


            }
            log.put("withData",2);
            log.put("target",product.toString());
            log.put("action","Company information read successfully");
            log.put("userId",userInfo.getInt("userId"));
            writeLog(log);
            //System.out.println(count);
            con.close();
        }catch(Exception e){ System.out.println(e);}
        return product;
    }
    public static JSONObject getInvoice(int invoiceId,JSONObject userInfo){
        JSONObject invoice = new JSONObject();

        try{

            Class.forName("com.mysql.jdbc.Driver");
            Connection con=DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/tax_receipt_system",SQLUser, SQLPassword);

            Statement stmt=con.createStatement();
            ResultSet rs=stmt.executeQuery("select * from invoices where invoiceId="+invoiceId);
            JSONObject log = new JSONObject();

            while(rs.next()) {


                invoice.put("invoiceId",rs.getInt(1));
                invoice.put("JSON",rs.getString(2));


            }
            log.put("withData",2);
            log.put("target",invoice.toString());
            log.put("action","Invoice information read successfully");
            log.put("userId",userInfo.getInt("userId"));
            writeLog(log);
            //System.out.println(count);
            con.close();
        }catch(Exception e){ System.out.println(e);}
        return invoice;
    }
    public static void updateInvoice(JSONObject newInfo, int invoiceId, JSONObject userInfo){
        JSONObject oldInfo = getInvoice(newInfo.getInt("invoiceId"),userInfo);
        try{

            Class.forName("com.mysql.jdbc.Driver");
            Connection con=DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/tax_receipt_system",SQLUser, SQLPassword);
            //here sonoo is database name, root is username and password
            Statement stmt=con.createStatement();
            stmt.execute("UPDATE invoices SET JSON='"+newInfo.getString("JSON")
                    +"' WHERE invoiceId="+invoiceId);

            JSONObject log = new JSONObject();
            log.put("withData",3);
            log.put("target",oldInfo.toString());
            log.put("newValue", newInfo.toString());
            log.put("action","Invoice edited successfully");
            log.put("userId",userInfo.getInt("userId"));
            writeLog(log);
            con.close();
        }catch(Exception e){ System.out.println(e);}
    }
    public static JSONArray getInvoices(JSONObject userInfo){
        JSONArray invoices = new JSONArray();

        try{

            Class.forName("com.mysql.jdbc.Driver");
            Connection con=DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/tax_receipt_system",SQLUser, SQLPassword);
            //here sonoo is database name, root is username and password
            Statement stmt=con.createStatement();
            ResultSet rs=stmt.executeQuery("select * from invoices");
            while(rs.next()) {
                JSONObject invoice = new JSONObject();
                invoice.put("invoiceId",rs.getInt(1));
                invoice.put("JSON",rs.getString(2));
                invoices.put(invoice);

            }
            JSONObject log = new JSONObject();
            log.put("withData",1);
            log.put("action","Invoice list fetched successfully");
            log.put("userId",userInfo.getInt("userId"));
            writeLog(log);
            con.close();
        }catch(Exception e){ System.out.println(e);}
        return invoices;
    }
}

