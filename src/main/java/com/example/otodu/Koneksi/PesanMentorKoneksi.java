package com.example.otodu.Koneksi;

import com.example.otodu.Model.PesanMentor;
import com.example.otodu.Utils.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PesanMentorKoneksi {

    public static boolean tambahPesanan(String namaSiswa, String namaMentor, String emailMentor, String nomorMentor) {
        String sql = "INSERT INTO pesan_mentor (nama_siswa, nama_mentor, email, nomor) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, namaSiswa);
            ps.setString(2, namaMentor);
            ps.setString(3, emailMentor);
            ps.setString(4, nomorMentor);

            ps.executeUpdate();
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static List<PesanMentor> getSemuaPesanan() {
        List<PesanMentor> list = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT * FROM pesan_mentor ORDER BY tanggal DESC";
            ResultSet rs = conn.createStatement().executeQuery(sql);

            while (rs.next()) {
                list.add(new PesanMentor(
                        rs.getInt("id"),
                        rs.getString("nama_siswa"),
                        rs.getString("nama_mentor"),
                        rs.getString("email"),
                        rs.getString("nomor"),
                        rs.getDate("tanggal"),
                        rs.getBoolean("selesai")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static void tandaiSelesai(int id) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "UPDATE pesan_mentor SET selesai = TRUE WHERE id = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
