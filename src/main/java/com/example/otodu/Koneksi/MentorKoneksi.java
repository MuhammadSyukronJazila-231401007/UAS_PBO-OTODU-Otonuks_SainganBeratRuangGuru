package com.example.otodu.Koneksi;

import com.example.otodu.Model.Mentor;
import com.example.otodu.Utils.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MentorKoneksi {

    public static List<Mentor> getAllMentor() {
        List<Mentor> mentorList = new ArrayList<>();

        String sql = """
                SELECT u.id, u.email, u.nama, u.nomor, u.kota, u.password,
                       u.role, u.koin, u.materi_terakhir,
                       dm.materi, dm.jenjang, dm.online, dm.offline, dm.riwayat_studi
                FROM users u
                JOIN data_mentor dm ON u.id = dm.id_user
                WHERE u.role = 'Mentor'
                """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Mentor m = new Mentor(
                        rs.getInt("id"),
                        rs.getString("email"),
                        rs.getString("nama"),
                        rs.getString("nomor"),
                        rs.getString("kota"),
                        rs.getString("password"),
                        rs.getString("role"),
                        rs.getInt("koin"),
                        rs.getString("materi_terakhir"),
                        rs.getString("materi"),
                        rs.getString("jenjang"),
                        rs.getString("online"),
                        rs.getString("offline"),
                        rs.getString("riwayat_studi")
                );
                mentorList.add(m);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return mentorList;
    }

    // ✅ Tambah mentor ke DB (users lalu data_mentor)
    public static boolean tambahMentor(Mentor mentor) {
        String userSql = """
            INSERT INTO users (email, nama, nomor, kota, password, role, koin)
            VALUES (?, ?, ?, ?, ?, ?, ?) RETURNING id
        """;

        String mentorSql = """
            INSERT INTO data_mentor (id_user, materi, jenjang, online, offline, riwayat_studi)
            VALUES (?, ?, ?, ?, ?, ?)
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement psUser = conn.prepareStatement(userSql);
        ) {
            conn.setAutoCommit(false);

            // Masukkan ke tabel users
            psUser.setString(1, mentor.getEmail());
            psUser.setString(2, mentor.getNama());
            psUser.setString(3, mentor.getNomor());
            psUser.setString(4, mentor.getKota());
            psUser.setString(5, mentor.getPassword());
            psUser.setString(6, mentor.getRole());
            psUser.setInt(7, mentor.getKoin());

            ResultSet rs = psUser.executeQuery();
            if (rs.next()) {
                int userId = rs.getInt("id");

                try (PreparedStatement psMentor = conn.prepareStatement(mentorSql)) {
                    psMentor.setInt(1, userId);
                    psMentor.setString(2, mentor.getMateri());
                    psMentor.setString(3, mentor.getJenjang());
                    psMentor.setString(4, mentor.getOnline());
                    psMentor.setString(5, mentor.getOffline());
                    psMentor.setString(6, mentor.getRiwayatStudi());

                    psMentor.executeUpdate();
                    conn.commit();
                    return true;
                }
            }

            conn.rollback();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    // ✅ Hapus mentor berdasarkan id_user
    public static boolean hapusMentor(int idUser) {
        String deleteMentorSql = "DELETE FROM data_mentor WHERE id_user = ?";
        String deleteUserSql = "DELETE FROM users WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement psMentor = conn.prepareStatement(deleteMentorSql);
                 PreparedStatement psUser = conn.prepareStatement(deleteUserSql)) {

                psMentor.setInt(1, idUser);
                psMentor.executeUpdate();

                psUser.setInt(1, idUser);
                psUser.executeUpdate();

                conn.commit();
                return true;
            } catch (SQLException e) {
                conn.rollback();
                e.printStackTrace();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public static boolean updateMentor(Mentor mentor) {
        String updateUserSql = """
        UPDATE users SET email = ?, nama = ?, nomor = ?, kota = ?, password = ?, role = ?, koin = ?, materi_terakhir = ?
        WHERE id = ?
    """;

        String updateMentorSql = """
        UPDATE data_mentor SET materi = ?, jenjang = ?, online = ?, offline = ?, riwayat_studi = ?
        WHERE id_user = ?
    """;

        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false); // Mulai transaksi manual

            // Update tabel users
            try (PreparedStatement userStmt = conn.prepareStatement(updateUserSql)) {
                userStmt.setString(1, mentor.getEmail());
                userStmt.setString(2, mentor.getNama());
                userStmt.setString(3, mentor.getNomor());
                userStmt.setString(4, mentor.getKota());
                userStmt.setString(5, mentor.getPassword());
                userStmt.setString(6, mentor.getRole());
                userStmt.setInt(7, mentor.getKoin());
                userStmt.setString(8, mentor.getMateriTerakhir());
                userStmt.setInt(9, mentor.getId());

                userStmt.executeUpdate();
            }

            // Update tabel data_mentor
            try (PreparedStatement mentorStmt = conn.prepareStatement(updateMentorSql)) {
                mentorStmt.setString(1, mentor.getMateri());
                mentorStmt.setString(2, mentor.getJenjang());
                mentorStmt.setString(3, mentor.getOnline());
                mentorStmt.setString(4, mentor.getOffline());
                mentorStmt.setString(5, mentor.getRiwayatStudi());
                mentorStmt.setInt(6, mentor.getId()); // id = id_user

                mentorStmt.executeUpdate();
            }

            conn.commit(); // Commit jika semua berhasil
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

}

