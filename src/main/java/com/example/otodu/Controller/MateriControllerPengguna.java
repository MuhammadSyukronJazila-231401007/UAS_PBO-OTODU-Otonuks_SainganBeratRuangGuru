package com.example.otodu.Controller;

import com.example.otodu.Model.Materi;
import com.example.otodu.Model.Pengguna;
import com.example.otodu.Utils.DatabaseConnection;
import com.example.otodu.Utils.PenggunaSekarang;
import com.example.otodu.Utils.UbahHalaman;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MateriControllerPengguna {
    @FXML private Label nlpLabel;
    @FXML private Label mentorLabel;
    @FXML private Label koinLabel;
    @FXML private TilePane latihanTilePane;
    @FXML private VBox loadingOverlay;
    @FXML private Button logoutBtn;

    private final List<Materi> semuaMateri = new ArrayList<>();

    public void initialize() {
        logoutBtn.setOnAction(e -> {
            UbahHalaman.konfirmasiLogout(e);
        });
        mentorLabel.setOnMouseClicked(e -> UbahHalaman.switchScene(e, "CariMentor.fxml"));
        nlpLabel.setOnMouseClicked(e -> UbahHalaman.switchScene(e, "NLPDashboard.fxml"));

        showLoading(true);

        new Thread(() -> {
            try {
                Thread.sleep(2000); // Simulasi loading
                Platform.runLater(() -> {
                    loadMateriDariDatabase();
                    Pengguna pengguna = PenggunaSekarang.getPengguna();
                    int koin = DatabaseConnection.getKoinPengguna(pengguna.getId());
                    koinLabel.setText(String.valueOf(koin));
                    showLoading(false);
                });
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void loadMateriDariDatabase() {
        semuaMateri.clear();
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT * FROM materi";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                semuaMateri.add(new Materi(
                        rs.getInt("id"),
                        rs.getString("nama_materi"),
                        rs.getString("jenjang"),
                        rs.getString("kelas"),
                        rs.getString("isi_materi"),
                        rs.getString("gambar_path"),
                        rs.getString("video_path")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        tampilkanMateri(semuaMateri);
    }

    private void tampilkanMateri(List<Materi> daftar) {
        latihanTilePane.getChildren().clear();
        Pengguna pengguna = PenggunaSekarang.getPengguna();

        for (Materi materi : daftar) {
            VBox card = new VBox(10);
            card.setPadding(new Insets(15));
            card.setPrefSize(300, 100);
            card.setStyle("-fx-cursor: hand; -fx-background-color: white; -fx-border-color: #DDD; -fx-border-radius: 5;");

            Label nama = new Label(materi.getNamaMateri());
            nama.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
            Label jenjang = new Label("Jenjang: " + materi.getJenjang());
            Label kelas = new Label("Kelas: " + materi.getKelas());
            Label statusLabel = new Label();

            boolean sudah = sudahDibeli(pengguna.getId(), materi.getId());
            if (sudah) {
                statusLabel.setText("✅ Sudah Dibeli");
                statusLabel.setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
            } else {
                statusLabel.setText("❌ Belum Dibeli");
                statusLabel.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
            }

            card.getChildren().addAll(nama, jenjang, kelas, statusLabel);

            card.setOnMouseClicked(e -> {
                showLoading(true); // Tampilkan loading saat card diklik

                new Thread(() -> {
                    boolean updateStatus = sudahDibeli(pengguna.getId(), materi.getId());

                    Platform.runLater(() -> {
                        if (updateStatus) {
                            updateMateriTerakhir(pengguna.getId(), materi.getNamaMateri());
                            bukaPopupDetailMateri(materi);
                            showLoading(false); // Tutup loading setelah popup tampil
                        } else {
                            showLoading(false); // Pastikan loading hilang sebelum konfirmasi
                            Alert konfirmasi = new Alert(Alert.AlertType.CONFIRMATION);
                            konfirmasi.setTitle("Konfirmasi Pembelian");
                            konfirmasi.setHeaderText("Materi ini belum dibeli");
                            konfirmasi.setContentText("Apakah Anda yakin ingin membeli materi ini dengan 10 koin?");
                            konfirmasi.showAndWait().ifPresent(response -> {
                                if (response == ButtonType.OK) {
                                    if (DatabaseConnection.getKoinPengguna(pengguna.getId()) >= 10) {
                                        showLoading(true); // Loading saat proses beli

                                        new Thread(() -> {
                                            kurangiKoin(pengguna.getId(), 10);
                                            catatPembelian(pengguna.getId(), materi.getId());
                                            updateMateriTerakhir(pengguna.getId(), materi.getNamaMateri());

                                            Platform.runLater(() -> {
                                                koinLabel.setText(String.valueOf(DatabaseConnection.getKoinPengguna(pengguna.getId())));
                                                tampilkanMateri(semuaMateri); // Refresh
                                                bukaPopupDetailMateri(materi);
                                                showLoading(false); // Selesai loading
                                            });
                                        }).start();
                                    } else {
                                        Alert gagal = new Alert(Alert.AlertType.ERROR);
                                        gagal.setTitle("Gagal");
                                        gagal.setHeaderText("Koin tidak cukup");
                                        gagal.setContentText("Anda tidak memiliki cukup koin untuk membeli materi ini.");
                                        gagal.showAndWait();
                                    }
                                }
                            });
                        }
                    });
                }).start();
            });


            latihanTilePane.getChildren().add(card);
        }
    }

    private void bukaPopupDetailMateri(Materi materi) {
        Stage popup = new Stage();
        VBox layout = new VBox(15);
        layout.setPadding(new Insets(20));
        layout.setStyle("-fx-background-color: #FAFAFA;");

        Label namaLabel = new Label("Nama Materi: " + materi.getNamaMateri());
        namaLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        Label jenjangLabel = new Label("Jenjang: " + materi.getJenjang());
        Label kelasLabel = new Label("Kelas: " + materi.getKelas());

        ImageView imageView = new ImageView();
        if (materi.getGambarPath() != null) {
            File imgFile = new File("src/main/resources/" + materi.getGambarPath());
            if (imgFile.exists()) {
                imageView.setImage(new Image(imgFile.toURI().toString()));
                imageView.setFitWidth(300);
                imageView.setPreserveRatio(true);
            }
        }

        VBox videoBox = new VBox(5);
        javafx.scene.web.WebView webView = null;

        if (materi.getVideoPath() != null && !materi.getVideoPath().isEmpty()) {
            String embedUrl = "https://www.youtube.com/embed/" + materi.getVideoPath() + "?autoplay=0";
            webView = new javafx.scene.web.WebView();
            webView.setPrefSize(480, 270);
            webView.getEngine().load(embedUrl);
            videoBox.getChildren().addAll(new Label("Video Materi:"), webView);
        }

        Label isiLabel = new Label("Isi Materi:");
        isiLabel.setStyle("-fx-font-weight: bold;");
        Label isiText = new Label(materi.getIsiMateri());
        isiText.setWrapText(true);
        isiText.setMaxWidth(460);

        layout.getChildren().addAll(
                namaLabel, jenjangLabel, kelasLabel,
                imageView, videoBox, isiLabel, isiText
        );

        ScrollPane scrollPane = new ScrollPane(layout);
        scrollPane.setFitToWidth(true);
        popup.setTitle("Detail Materi");
        popup.setScene(new Scene(scrollPane, 520, 650));

        // 👉 Tutup video saat popup ditutup
        javafx.scene.web.WebView finalWebView = webView; // final agar bisa dipakai di lambda
        popup.setOnCloseRequest(event -> {
            if (finalWebView != null) {
                finalWebView.getEngine().load(null); // stop video & release resource
            }
        });

        popup.show();
    }


    private void showLoading(boolean show) {
        loadingOverlay.setVisible(show);
    }

    private boolean sudahDibeli(int userId, int materiId) {
        String query = "SELECT 1 FROM beli_subtopik WHERE id_user = " + userId + " AND id_materi = " + materiId;
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private void catatPembelian(int userId, int materiId) {
        String query = "INSERT INTO beli_subtopik (id_user, id_materi) VALUES (" + userId + ", " + materiId + ")";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void kurangiKoin(int userId, int jumlah) {
        String query = "UPDATE users SET koin = koin - " + jumlah + " WHERE id = " + userId;
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void updateMateriTerakhir(int userId, String namaMateri) {
        String query = "UPDATE users SET materi_terakhir = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, namaMateri);
            ps.setInt(2, userId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
