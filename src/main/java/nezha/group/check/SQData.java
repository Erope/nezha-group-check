package nezha.group.check;

import java.sql.*;

public class SQData {
    private Connection conn;

    public void connect() {
        if (conn != null) {
            return;
        }
        try {
            // db parameters
            String url = "jdbc:sqlite:D:\\project\\nezha-group-check\\data.db";
            // create a connection to the database
            conn = DriverManager.getConnection(url);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public SQData() {
        this.connect();
    }

    public void close() {
        try {
            if (conn != null) {
                conn.close();
                conn = null;
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public Boolean isExist(String host) {
        this.connect();
        try {
            String query = "SELECT EXISTS (SELECT 1 FROM groupcheck WHERE HOST = ?)";
            PreparedStatement pst = conn.prepareStatement(query);
            pst.setString(1, host);

            try (ResultSet rs = pst.executeQuery()) {
                // Only expecting a single result
                Boolean result = rs.getBoolean(1);
                this.close();
                return result;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        this.close();
        return false;
    }

    public void InSert(long QQ, String host) {
        this.connect();
        try {
            String query = "INSERT INTO groupcheck(QQ, HOST) VALUES(?,?)";
            PreparedStatement pst = conn.prepareStatement(query);
            pst.setLong(1, QQ);
            pst.setString(2, host);
            pst.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        this.close();
    }

}
