package com.example.otodu.Controller;

import com.example.otodu.Model.Pengguna;
import com.example.otodu.Utils.UbahHalaman;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import com.example.otodu.Koneksi.PoinKoneksi;
import com.example.otodu.Koneksi.StatistikKoneksi;
import com.example.otodu.Model.Poin;
import com.example.otodu.Model.Statistik;
import com.example.otodu.Utils.PenggunaSekarang;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.example.otodu.Utils.DatabaseConnection;


public class NLPDashboardController {
    @FXML private Label namaPenggunaLabel;
    @FXML private Label materiTerakhirLabel;

    // Card Statistik
    @FXML private Label nlpPoinLabel;
    @FXML private Label mentorLabel;
    @FXML private Label koinLabel;
    @FXML private Label materiSelesaiLabel;
    @FXML private Label latihanDikuasaiLabel;

    @FXML private HBox leaderboard;

    @FXML private Button bukaLatihanBtn;
    @FXML private Button bukaMateriBtn;

    @FXML private Button logoutBtn;
    @FXML private HBox boxCoin;

    @FXML private AnchorPane overlayPane;
    @FXML private ProgressIndicator loadingIndicator;
    @FXML private StackPane nlpStack; // jika perlu kontrol stack atau alignment


    @FXML
    public void initialize() {
        setLoading(true); // Tampilkan overlay

        mentorLabel.setOnMouseClicked(e -> {
            UbahHalaman.switchScene(e, "CariMentor.fxml");
        });

        logoutBtn.setOnAction(e ->{
            UbahHalaman.konfirmasiLogout(e);
        });

        bukaMateriBtn.setOnMouseClicked(e -> {
            UbahHalaman.switchScene(e, "MateriPengguna.fxml");
        });

        bukaLatihanBtn.setOnMouseClicked(e -> {
            UbahHalaman.switchScene(e, "LatihanPengguna.fxml");
        });

        boxCoin.setOnMouseClicked(e -> {
            UbahHalaman.switchScene(e, "Coin.fxml");
        });

        leaderboard.setOnMouseClicked(e -> {
            UbahHalaman.switchScene(e, "Leaderboard.fxml");
        });

        koinLabel.setText(Integer.toString(PenggunaSekarang.getPengguna().getKoin()));

        int idUser = PenggunaSekarang.getPengguna().getId();
        String nama = PenggunaSekarang.getPengguna().getNama();
        String materiTerakhir = getMateriTerakhir(idUser);
        materiTerakhirLabel.setText("Terakhir belajar: " + (materiTerakhir != null ? materiTerakhir : "-"));

        namaPenggunaLabel.setText("Halo, " + nama);
        materiTerakhirLabel.setText("Terakhir belajar: " + (materiTerakhir != null ? materiTerakhir : "-"));

        new Thread(() -> {
            Poin poin = PoinKoneksi.getPoinByUserId(idUser);
            Statistik statistik = StatistikKoneksi.getStatistikByUserId(idUser);

            // Tambahan: hitung jumlah materi dan latihan dari database
            int jumlahMateri = getJumlahMateriDibeli(idUser);
            int jumlahLatihan = getJumlahLatihanDibeli(idUser);

            Platform.runLater(() -> {
                if (poin != null) {
                    nlpPoinLabel.setText(poin.getTotalPoin() + " Poin");
                } else {
                    nlpPoinLabel.setText("0 Poin");
                }

                Pengguna pengguna = PenggunaSekarang.getPengguna();
                int koin = DatabaseConnection.getKoinPengguna(pengguna.getId());
                koinLabel.setText(String.valueOf(koin));

                // Ganti label dengan jumlah riil dari database
                materiSelesaiLabel.setText("Materi yang telah diselesaikan: " + jumlahMateri);
                latihanDikuasaiLabel.setText("Latihan yang telah dikuasai: " + jumlahLatihan);

                setLoading(false); // Sembunyikan overlay setelah data dimuat
            });
        }).start();
    }

    public static int getJumlahMateriDibeli(int idUser) {
        String query = "SELECT COUNT(*) FROM beli_subtopik WHERE id_user = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, idUser);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static int getJumlahLatihanDibeli(int idUser) {
        String query = "SELECT COUNT(*) FROM beli_latihan WHERE id_user = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, idUser);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
    private String getMateriTerakhir(int idUser) {
        String query = "SELECT materi_terakhir FROM users WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, idUser);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("materi_terakhir");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }


    private void setLoading(boolean status) {
        overlayPane.setVisible(status);
        overlayPane.setMouseTransparent(!status);
    }

}
