package com.example.otodu.Koneksi;

import com.example.otodu.Model.Pengguna;
import com.example.otodu.Utils.DatabaseConnection;
import com.example.otodu.Utils.Hash;
import com.example.otodu.Utils.PenggunaSekarang;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PenggunaKoneksi {

    public static boolean verifLogin(String email, String password) {
        String query = "SELECT * FROM users WHERE email = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String storedHash = rs.getString("password");
                if (Hash.hash(password).equals(storedHash)) {
                    // Simpan ke PenggunaSekarang
                    PenggunaSekarang.tambahPengguna(
                            rs.getInt("id"),
                            rs.getString("email"),
                            rs.getString("nama"),
                            rs.getString("nomor"),
                            rs.getString("kota"),
                            rs.getString("password"),
                            rs.getString("role"),
                            rs.getInt("koin"),
                            rs.getString("materi_terakhir")
                    );
                    return true;
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean isEmailTerdaftar(String email) {
        String sql = "SELECT COUNT(*) FROM users WHERE email = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int count = rs.getInt(1);
                return count > 0; // Jika ada minimal 1 data dengan email tsb
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static List<Pengguna> getAllPengguna() {
        List<Pengguna> list = new ArrayList<>();
        String sql = "SELECT * FROM users";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Pengguna p = new Pengguna(
                        rs.getInt("id"),
                        rs.getString("email"),
                        rs.getString("nama"),
                        rs.getString("nomor"),
                        rs.getString("kota"),
                        rs.getString("password"),
                        rs.getString("role"),
                        rs.getInt("koin"),
                        rs.getString("materi_terakhir")
                );
                list.add(p);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static boolean tambahPengguna(Pengguna p) {
        String sql = "INSERT INTO users (id, email, nama, nomor, kota, password, role, koin, materi_terakhir) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, p.getId()); // ID dikirim manual (pastikan sudah unik)
            ps.setString(2, p.getEmail());
            ps.setString(3, p.getNama());
            ps.setString(4, p.getNomor());
            ps.setString(5, p.getKota());
            ps.setString(6, p.getPassword());
            ps.setString(7, p.getRole());
            ps.setInt(8, p.getKoin());
            ps.setString(9, p.getMateriTerakhir());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static int tambahDanKembalikanID(Pengguna p) {
        String sql = "INSERT INTO users (id, email, nama, nomor, kota, password, role, koin, materi_terakhir) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, p.getId()); // ID dikirim manual
            ps.setString(2, p.getEmail());
            ps.setString(3, p.getNama());
            ps.setString(4, p.getNomor());
            ps.setString(5, p.getKota());
            ps.setString(6, p.getPassword());
            ps.setString(7, p.getRole());
            ps.setInt(8, p.getKoin());
            ps.setString(9, p.getMateriTerakhir());

            int affected = ps.executeUpdate();
            return (affected > 0) ? p.getId() : -1;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }


    public static boolean updatePengguna(Pengguna p) {
        String sql = "UPDATE users SET email=?, nama=?, nomor=?, kota=?, password=?, role=?, koin=?, materi_terakhir=? WHERE id=?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, p.getEmail());
            ps.setString(2, p.getNama());
            ps.setString(3, p.getNomor());
            ps.setString(4, p.getKota());
            ps.setString(5, p.getPassword());
            ps.setString(6, p.getRole());
            ps.setInt(7, p.getKoin());
            ps.setString(8, p.getMateriTerakhir());
            ps.setInt(9, p.getId());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean hapusPengguna(int id) {
        String sql = "DELETE FROM users WHERE id=?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static int hitungPenggunaDenganRole(String role) {
        String sql = "SELECT COUNT(*) FROM users WHERE LOWER(role) = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, role.toLowerCase());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static List<Pengguna> getTopMuridDenganPoin() {
        String sql = """
            SELECT u.*, p.total_poin
            FROM users u
            JOIN poin p ON u.id = p.id_user
            WHERE LOWER(u.role) = 'siswa'
            ORDER BY p.total_poin DESC
            LIMIT 10
        """;
        List<Pengguna> list = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Pengguna p = new Pengguna(
                        rs.getInt("id"),
                        rs.getString("email"),
                        rs.getString("nama"),
                        rs.getString("nomor"),
                        rs.getString("kota"),
                        rs.getString("password"),
                        rs.getString("role"),
                        rs.getInt("koin"),
                        rs.getString("materi_terakhir")
                );
                p.setTotalPoin(rs.getInt("total_poin")); // harus ada setter di class Pengguna
                list.add(p);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static void updateKoin(int userId, int koinBaru) {
        String sql = "UPDATE users SET koin = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, koinBaru);
            ps.setInt(2, userId);

            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
