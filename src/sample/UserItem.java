package sample;

public class UserItem {
    public int userId;
    public String username, fullname;

    public UserItem(int userId, String username, String fullname){
        this.userId = userId;
        this.username = username;
        this.fullname = fullname;
    }
    public int getUserId(){
        return userId;
    }
    public String getUsername(){
        return username;
    }
    public String getFullname(){
        return fullname;
    }
}
