package com.example.otodu.Koneksi;


import com.example.otodu.Model.Materi;
import com.example.otodu.Utils.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MateriKoneksi {

    public static int hitungJumlahMateri() {
        String sql = "SELECT COUNT(*) FROM materi";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static List<Materi> getAllMateri() {
        List<Materi> list = new ArrayList<>();
        String query = "SELECT * FROM materi";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Materi m = new Materi(
                        rs.getInt("kode_materi"),
                        rs.getString("nama_materi"),
                        rs.getString("jenjang"),
                        rs.getString("kelas")
                );
                list.add(m);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    public static boolean insertMateri(Materi materi) {
        String query = "INSERT INTO materi (nama_materi, jenjang, kelas) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, materi.getNamaMateri());
            stmt.setString(2, materi.getJenjang());
            stmt.setString(3, materi.getKelas());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public static boolean updateMateri(Materi materi) {
        String query = "UPDATE materi SET nama_materi = ?, jenjang = ?, kelas = ? WHERE kode_materi = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, materi.getNamaMateri());
            stmt.setString(2, materi.getJenjang());
            stmt.setString(3, materi.getKelas());
            stmt.setInt(4, materi.getKodeMateri());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public static boolean deleteMateri(int kodeMateri) {
        String query = "DELETE FROM materi WHERE kode_materi = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, kodeMateri);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }
}

