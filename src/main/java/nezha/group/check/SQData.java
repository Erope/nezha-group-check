package nezha.group.check;

import java.sql.*;

public class SQData {
    private Connection conn = null;

    public void connect() {
        if (this.conn != null) {
            return;
        }
        try {
            // db parameters
            try {
                Class.forName("org.sqlite.JDBC");
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            String url = "jdbc:sqlite:/opt/mcl/nezhacheck/data.db";
            // create a connection to the database
            this.conn = DriverManager.getConnection(url);
            if (conn == null)
                throw new SQLException("SQLITE连接未建立");
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
