package com.example.otodu.Koneksi;

import com.example.otodu.Model.Statistik;
import com.example.otodu.Utils.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class StatistikKoneksi {

    public static boolean tambahStatistikAwal(int idUser) {
        String sql = "INSERT INTO statistik (id_user) VALUES (?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idUser);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public static Statistik getStatistikByUserId(int idUser) {
        String sql = "SELECT * FROM statistik WHERE id_user = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idUser);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Statistik(
                        rs.getInt("id_user"),
                        rs.getInt("latihan"),
                        rs.getInt("materi")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean updateStatistik(Statistik s) {
        String sql = "UPDATE statistik SET latihan = ?, materi = ? WHERE id_user = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, s.getLatihan());
            ps.setInt(2, s.getMateri());
            ps.setInt(3, s.getIdUser());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean hapusStatistik(int idUser) {
        String sql = "DELETE FROM statistik WHERE id_user = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idUser);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
