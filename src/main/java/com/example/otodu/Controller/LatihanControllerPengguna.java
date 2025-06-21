// File: LatihanControllerPengguna.java
package com.example.otodu.Controller;

import com.example.otodu.Model.Latihan;
import com.example.otodu.Model.LatihanSoal;
import com.example.otodu.Model.Pengguna;
import com.example.otodu.Utils.DatabaseConnection;
import com.example.otodu.Utils.PenggunaSekarang;
import com.example.otodu.Utils.UbahHalaman;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LatihanControllerPengguna {
    @FXML
    private TilePane latihanTilePane;
    @FXML private Label nlpLabel;
    @FXML private Label mentorLabel;
    @FXML private Button logoutBtn;
    @FXML private Label koinLabel;
    @FXML private VBox loadingOverlay;
    private List<Latihan> semuaLatihan = new ArrayList<>();
    private Map<Latihan, List<LatihanSoal>> bankSoal = new HashMap<>();

    public void initialize() {
        logoutBtn.setOnAction(e -> {
            UbahHalaman.konfirmasiLogout(e);
        });

        mentorLabel.setOnMouseClicked(e -> UbahHalaman.switchScene(e, "CariMentor.fxml"));
        nlpLabel.setOnMouseClicked(e -> UbahHalaman.switchScene(e, "NLPDashboard.fxml"));

        showLoading(true); // Tampilkan overlay loading

        new Thread(() -> {
            try {
                // Simulasi delay, bisa dihapus jika tidak perlu
                Thread.sleep(2000);

                Platform.runLater(() -> {
                    loadLatihanDariDatabase();

                    Pengguna pengguna = PenggunaSekarang.getPengguna();
                    int koin = DatabaseConnection.getKoinPengguna(pengguna.getId());
                    koinLabel.setText(String.valueOf(koin));

                    showLoading(false); // Sembunyikan overlay loading setelah selesai
                });

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }


    private void tampilkanLatihan(List<Latihan> daftar) {
        latihanTilePane.getChildren().clear();

        Pengguna pengguna = PenggunaSekarang.getPengguna();
        int idUser = pengguna.getId();

        for (Latihan latihan : daftar) {
            VBox card = new VBox(5);
            card.setPadding(new Insets(18, 24, 18, 24));
            card.setStyle("-fx-background-color: white; -fx-border-color: #E1F0FF; -fx-border-width: 2;");
            card.setPrefSize(400, 100);

            Label nama = new Label(latihan.getNamaLatihan());
            nama.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #375679;");

            Label badge = new Label(latihan.getJenisLatihan());
            badge.setStyle("-fx-background-color: #E1F0FF; -fx-border-color: #007BFF; -fx-font-weight: bold;");
            badge.setPadding(new Insets(2, 10, 2, 10));

            // ➕ Tambahkan status pembelian
            boolean sudahBeli = sudahBeliLatihan(idUser, latihan.getId());
            Label statusLabel = new Label(sudahBeli ? "✅ Sudah dibeli" : "❌ Belum dibeli");
            statusLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: " + (sudahBeli ? "green;" : "red;"));

            Button mulaiButton = new Button("Mulai");
            mulaiButton.setOnAction(e -> {
                if (!sudahBeli) {
                    Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                            "Latihan ini belum Anda beli. Ingin membeli?",
                            ButtonType.YES, ButtonType.NO);
                    confirm.setTitle("Konfirmasi Pembelian");
                    confirm.setHeaderText(null);
                    confirm.showAndWait().ifPresent(response -> {
                        if (response == ButtonType.YES) {
                            catatPembelianLatihan(idUser, latihan.getId());
                            tampilkanLatihan(semuaLatihan); // Refresh tampilan secara real-time
                            tampilkanPopupLatihan(latihan, bankSoal.get(latihan));
                        }
                    });
                } else {
                    tampilkanPopupLatihan(latihan, bankSoal.get(latihan));
                }
            });

            HBox top = new HBox(10, nama, mulaiButton);
            HBox bottom = new HBox(10, badge, statusLabel);

            card.getChildren().addAll(top, bottom);
            latihanTilePane.getChildren().add(card);
        }
    }


    private void loadLatihanDariDatabase() {
        semuaLatihan.clear();
        bankSoal.clear();
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sqlLatihan = "SELECT * FROM latihan";
            Statement st = conn.createStatement();
            ResultSet rsLatihan = st.executeQuery(sqlLatihan);

            while (rsLatihan.next()) {
                int id = rsLatihan.getInt("id");
                String nama = rsLatihan.getString("nama_latihan");
                String jenis = rsLatihan.getString("jenis_latihan");

                // ✅ Gunakan konstruktor dengan id (jika sudah ditambahkan di Latihan.java)
                Latihan latihan = new Latihan(id, nama, jenis);
                semuaLatihan.add(latihan);

                List<LatihanSoal> soalList = new ArrayList<>();
                PreparedStatement ps = conn.prepareStatement("SELECT * FROM soal WHERE latihan_id = ?");
                ps.setInt(1, id);
                ResultSet rsSoal = ps.executeQuery();

                while (rsSoal.next()) {
                    String pertanyaan = rsSoal.getString("pertanyaan");
                    String jawaban = rsSoal.getString("jawaban_benar");
                    if (jenis.equals("Pilihan Ganda")) {
                        List<String> opsi = List.of(
                                rsSoal.getString("opsi_a"),
                                rsSoal.getString("opsi_b"),
                                rsSoal.getString("opsi_c")
                        );
                        soalList.add(new LatihanSoal(pertanyaan, opsi, jawaban));
                    } else {
                        soalList.add(new LatihanSoal(pertanyaan, jawaban));
                    }
                }

                bankSoal.put(latihan, soalList);
            }

            tampilkanLatihan(semuaLatihan);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    private void tampilkanPopupLatihan(Latihan latihan, List<LatihanSoal> soalList) {
        Stage popupStage = new Stage();
        VBox layout = new VBox(20);
        layout.setPadding(new Insets(20));

        List<Object> inputJawabanList = new ArrayList<>();

        for (LatihanSoal soal : soalList) {
            VBox soalBox = new VBox(10);
            soalBox.getChildren().add(new Label(soal.getPertanyaan()));

            if (latihan.getJenisLatihan().equals("Pilihan Ganda")) {
                ToggleGroup group = new ToggleGroup();
                for (String opsi : soal.getOpsi()) {
                    RadioButton rb = new RadioButton(opsi);
                    rb.setToggleGroup(group);
                    soalBox.getChildren().add(rb);
                }
                inputJawabanList.add(group);
            } else {
                TextField tf = new TextField();
                tf.setPromptText("Jawaban Anda");
                soalBox.getChildren().add(tf);
                inputJawabanList.add(tf);
            }

            layout.getChildren().add(soalBox);
        }

        Button cekButton = new Button("Cek Jawaban");
        Label hasilLabel = new Label();

        cekButton.setOnAction(e -> {
            int benar = 0;
            for (int i = 0; i < soalList.size(); i++) {
                LatihanSoal soal = soalList.get(i);
                if (latihan.getJenisLatihan().equals("Pilihan Ganda")) {
                    ToggleGroup group = (ToggleGroup) inputJawabanList.get(i);
                    RadioButton selected = (RadioButton) group.getSelectedToggle();
                    if (selected != null && selected.getText().equalsIgnoreCase(soal.getJawabanBenar())) {
                        benar++;
                    }
                } else {
                    TextField tf = (TextField) inputJawabanList.get(i);
                    if (tf.getText().trim().equalsIgnoreCase(soal.getJawabanBenar())) {
                        benar++;
                    }
                }
            }
            hasilLabel.setText("Jawaban Benar: " + benar + " dari " + soalList.size());
            Pengguna pengguna = PenggunaSekarang.getPengguna();
            int idUser = pengguna.getId(); // atau ambil dari session/login
            tambahPoinKeDatabase(idUser, benar);
        });



        layout.getChildren().addAll(cekButton, hasilLabel);
        popupStage.setScene(new Scene(new ScrollPane(layout), 500, 600));
        popupStage.setTitle("Latihan: " + latihan.getNamaLatihan());
        popupStage.show();
    }

    private void tambahPoinKeDatabase(int idUser, int jumlahBenar) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn =DatabaseConnection.getConnection(); // Buat method koneksi sendiri sesuai proyekmu

            // Cek apakah user sudah punya data poin
            String cekQuery = "SELECT total_poin FROM poin WHERE id_user = ?";
            pstmt = conn.prepareStatement(cekQuery);
            pstmt.setInt(1, idUser);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                // Sudah ada, update poin
                int totalSaatIni = rs.getInt("total_poin");
                int totalBaru = totalSaatIni + jumlahBenar;

                String updateQuery = "UPDATE poin SET total_poin = ? WHERE id_user = ?";
                pstmt = conn.prepareStatement(updateQuery);
                pstmt.setInt(1, totalBaru);
                pstmt.setInt(2, idUser);
                pstmt.executeUpdate();

            } else {
                // Belum ada, insert baru
                String insertQuery = "INSERT INTO poin (id_user, total_poin) VALUES (?, ?)";
                pstmt = conn.prepareStatement(insertQuery);
                pstmt.setInt(1, idUser);
                pstmt.setInt(2, jumlahBenar);
                pstmt.executeUpdate();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // Tutup resource
            try { if (rs != null) rs.close(); } catch (SQLException ignored) {}
            try { if (pstmt != null) pstmt.close(); } catch (SQLException ignored) {}
            try { if (conn != null) conn.close(); } catch (SQLException ignored) {}
        }
    }

    private boolean sudahBeliLatihan(int idUser, int idLatihan) {
        String query = "SELECT * FROM beli_latihan WHERE id_user = ? AND id_latihan = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, idUser);
            stmt.setInt(2, idLatihan);
            ResultSet rs = stmt.executeQuery();

            return rs.next(); // Jika ada hasil, berarti sudah beli
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void catatPembelianLatihan(int idUser, int idLatihan) {
        final int HARGA_LATIHAN = 10;

        try (Connection conn = DatabaseConnection.getConnection()) {
            // 1. Ambil koin pengguna
            String cekKoinQuery = "SELECT koin FROM users WHERE id = ?";
            PreparedStatement cekStmt = conn.prepareStatement(cekKoinQuery);
            cekStmt.setInt(1, idUser);
            ResultSet rs = cekStmt.executeQuery();

            if (rs.next()) {
                int koinSaatIni = rs.getInt("koin");
                if (koinSaatIni < HARGA_LATIHAN) {
                    Alert alert = new Alert(Alert.AlertType.WARNING, "Koin Anda tidak cukup untuk membeli latihan ini.");
                    alert.setHeaderText(null);
                    alert.setTitle("Koin Tidak Cukup");
                    alert.show();
                    return;
                }

                // 2. Insert ke tabel beli_latihan
                String insertQuery = "INSERT INTO beli_latihan (id_user, id_latihan) VALUES (?, ?)";
                PreparedStatement insertStmt = conn.prepareStatement(insertQuery);
                insertStmt.setInt(1, idUser);
                insertStmt.setInt(2, idLatihan);
                insertStmt.executeUpdate();

                // 3. Kurangi koin pengguna
                int koinBaru = koinSaatIni - HARGA_LATIHAN;
                String updateKoinQuery = "UPDATE users SET koin = ? WHERE id = ?";
                PreparedStatement updateStmt = conn.prepareStatement(updateKoinQuery);
                updateStmt.setInt(1, koinBaru);
                updateStmt.setInt(2, idUser);
                updateStmt.executeUpdate();

                // ✅ Tambahkan: update nilai label jumlahKoin di UI
                Platform.runLater(() -> koinLabel.setText(String.valueOf(koinBaru)));

            } else {
                System.err.println("User tidak ditemukan saat pengecekan koin.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void showLoading(boolean show) {
        if (loadingOverlay != null) {
            loadingOverlay.setVisible(show);
        }
    }



}
