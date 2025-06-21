package com.example.otodu.Koneksi;

import com.example.otodu.Model.Poin;
import com.example.otodu.Utils.DatabaseConnection;

import java.sql.*;

public class PoinKoneksi {

    // Tambahkan entri awal
    public static boolean tambahPoinAwal(int idUser) {
        String sql = "INSERT INTO poin (id_user) VALUES (?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idUser);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Ambil data poin
    public static Poin getPoinByUserId(int idUser) {
        String sql = "SELECT * FROM poin WHERE id_user = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idUser);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Poin(
                        rs.getInt("id_user"),
                        rs.getInt("materi"),
                        rs.getInt("latihan"),
                        rs.getInt("total_poin")
                );
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Update total poin
    public static boolean updatePoin(Poin p) {
        p.updateTotal(); // Hitung ulang total
        String sql = "UPDATE poin SET materi=?, latihan=?, total_poin=? WHERE id_user=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, p.getMateri());
            ps.setInt(2, p.getLatihan());
            ps.setInt(3, p.getTotalPoin());
            ps.setInt(4, p.getIdUser());
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Tambahkan poin ke materi atau latihan
    public static boolean tambahPoin(String tipe, int idUser, int jumlah) {
        Poin poin = getPoinByUserId(idUser);
        if (poin == null) return false;

        if (tipe.equalsIgnoreCase("materi")) {
            poin.setMateri(poin.getMateri() + jumlah);
        } else if (tipe.equalsIgnoreCase("latihan")) {
            poin.setLatihan(poin.getLatihan() + jumlah);
        }

        return updatePoin(poin);
    }

    public static boolean hapusPoin(int idUser) {
        String sql = "DELETE FROM poin WHERE id_user=?";
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
