package com.example.otodu.Utils;

import java.sql.*;

public class DatabaseConnection {
    private static final String URL = "jdbc:postgresql://db.swgjqlomuhobrrqtfmxw.supabase.co:5432/postgres";
    private static final String USER = "postgres";
    private static final String PASSWORD = "otoduvsrg"; // Ganti dengan passwordmu

    private static Connection connection = null;

    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Berhasil terhubung ke database Supabase PostgreSQL.");
        }
        return connection;
    }

    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Koneksi ditutup.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
//        try {
//            Connection conn = DatabaseConnection.getConnection();
//            Statement stmt = conn.createStatement();
//            ResultSet rs = stmt.executeQuery("SELECT * FROM users");
//
//            while (rs.next()) {
//                System.out.println(rs.getString("nama"));
//            }
//
//            DatabaseConnection.closeConnection();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }
}
