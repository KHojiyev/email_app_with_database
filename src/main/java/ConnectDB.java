import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectDB {

    public static Connection ConnectionDb(String db_name){
        String url = "jdbc:postgresql://localhost:5432/"+db_name;
        String user_name = "Khojiyev_admin";
        String password = "1705986717lxm";

        try {
            return DriverManager.getConnection(url,user_name,password);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }
}
