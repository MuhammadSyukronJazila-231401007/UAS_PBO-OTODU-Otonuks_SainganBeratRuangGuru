// File: LatihanControllerAdmin.java
package com.example.otodu.Controller;

import com.example.otodu.Model.Latihan;
import com.example.otodu.Model.LatihanSoal;
import com.example.otodu.Utils.DatabaseConnection;
import com.example.otodu.Utils.UbahHalaman;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LatihanController {
    @FXML
    private TilePane latihanTilePane;
    @FXML
    private Button tambahPGButton;
    @FXML
    private Button tambahIsianButton;

    @FXML private Button dashboardBtn;
    @FXML private Button materiBtn;
    @FXML private Button mentorBtn;
    @FXML private Button latihanBtn;
    @FXML private Button pesanBtn;

    @FXML private AnchorPane overlayPane;
    @FXML private Button logoutBtn;

    private List<Latihan> semuaLatihan = new ArrayList<>();
    private Map<Latihan, List<LatihanSoal>> bankSoal = new HashMap<>();

    public void initialize() {
        dashboardBtn.setOnAction(e -> UbahHalaman.switchScene(e, "Dashboard.fxml"));
        materiBtn.setOnAction(e -> UbahHalaman.switchScene(e, "Materi.fxml"));
        mentorBtn.setOnAction(e -> UbahHalaman.switchScene(e, "Mentor.fxml"));
        pesanBtn.setOnAction(e -> UbahHalaman.switchScene(e, "PesanMentor.fxml"));

        logoutBtn.setOnAction(e -> UbahHalaman.konfirmasiLogout(e));

        setLoading(true); // mulai loading
        new Thread(() -> {
            loadLatihanDariDatabase(); // dijalankan di thread background
            javafx.application.Platform.runLater(() -> {
                tampilkanLatihan(semuaLatihan); // update UI di thread JavaFX
                setLoading(false); // loading selesai
            });
        }).start();

        tambahPGButton.setOnAction(e -> bukaPopupTambahLatihan("Pilihan Ganda"));
        tambahIsianButton.setOnAction(e -> bukaPopupTambahLatihan("Isian Singkat"));
    }


    private void tampilkanLatihan(List<Latihan> daftar) {
        latihanTilePane.getChildren().clear();
        for (Latihan latihan : daftar) {
            VBox card = new VBox(5);
            card.setPadding(new Insets(18, 24, 18, 24));
            card.setStyle("-fx-background-color: white; -fx-border-color: #E1F0FF; -fx-border-width: 2;");
            card.setPrefSize(400, 85);

            Label nama = new Label(latihan.getNamaLatihan());
            nama.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #375679;");

            Label badge = new Label(latihan.getJenisLatihan());
            badge.setStyle("-fx-background-color: #E1F0FF; -fx-border-color: #007BFF; -fx-font-weight: bold;");
            badge.setPadding(new Insets(2, 10, 2, 10));

            // Tombol Edit
            Button editButton = new Button("Edit");
            editButton.setOnAction(e -> bukaPopupEditLatihan(latihan));

            // Tombol Hapus
            Button hapusButton = new Button("Hapus");
            hapusButton.setStyle("-fx-background-color: #dc3545; -fx-text-fill: white;");
            hapusButton.setOnAction(e -> {
                Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Yakin ingin menghapus latihan ini?", ButtonType.YES, ButtonType.NO);
                confirm.setTitle("Konfirmasi Hapus");
                confirm.setHeaderText(null);
                confirm.showAndWait().ifPresent(response -> {
                    if (response == ButtonType.YES) {
                        hapusLatihan(latihan);
                    }
                });
            });

            HBox top = new HBox(10, nama, editButton, hapusButton);
            HBox bottom = new HBox(badge);

            card.getChildren().addAll(top, bottom);
            latihanTilePane.getChildren().add(card);

        }
    }

    private void hapusLatihan(Latihan latihan) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            PreparedStatement psId = conn.prepareStatement("SELECT id FROM latihan WHERE nama_latihan = ?");
            psId.setString(1, latihan.getNamaLatihan());
            ResultSet rs = psId.executeQuery();
            if (rs.next()) {
                int latihanId = rs.getInt("id");

                PreparedStatement psDeleteSoal = conn.prepareStatement("DELETE FROM soal WHERE latihan_id = ?");
                psDeleteSoal.setInt(1, latihanId);
                psDeleteSoal.executeUpdate();

                PreparedStatement psDeleteLatihan = conn.prepareStatement("DELETE FROM latihan WHERE id = ?");
                psDeleteLatihan.setInt(1, latihanId);
                psDeleteLatihan.executeUpdate();

                loadLatihanDariDatabase();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }



    private void bukaPopupTambahLatihan(String jenisLatihan) {
        Stage popupStage = new Stage();
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20));

        TextField namaField = new TextField();
        namaField.setPromptText("Nama Latihan");
        layout.getChildren().addAll(new Label("Nama Latihan:"), namaField);

        List<VBox> soalBoxList = new ArrayList<>();

        for (int i = 0; i < 3; i++) {
            VBox soalBox = new VBox(5);
            soalBox.getChildren().add(new Label("Soal " + (i + 1)));
            TextArea soalField = new TextArea();
            soalBox.getChildren().add(soalField);

            if (jenisLatihan.equals("Pilihan Ganda")) {
                for (char opsi = 'A'; opsi <= 'C'; opsi++) {
                    TextField opsiField = new TextField();
                    opsiField.setPromptText("Opsi " + opsi);
                    soalBox.getChildren().add(opsiField);
                }
            }

            TextField jawabanField = new TextField();
            jawabanField.setPromptText("Jawaban benar");
            soalBox.getChildren().add(jawabanField);

            soalBoxList.add(soalBox);
            layout.getChildren().add(soalBox);
        }

        Button simpanButton = new Button("Simpan");
        simpanButton.setOnAction(e -> {
            String namaLatihan = namaField.getText();
            Latihan latihan = new Latihan(namaLatihan, jenisLatihan);
            List<LatihanSoal> soalList = new ArrayList<>();

            for (VBox soalBox : soalBoxList) {
                String pertanyaan = ((TextArea) soalBox.getChildren().get(1)).getText();
                String jawaban = ((TextField) soalBox.getChildren().get(soalBox.getChildren().size() - 1)).getText();
                if (jenisLatihan.equals("Pilihan Ganda")) {
                    List<String> opsi = new ArrayList<>();
                    for (int j = 2; j <= 4; j++) {
                        opsi.add(((TextField) soalBox.getChildren().get(j)).getText());
                    }
                    soalList.add(new LatihanSoal(pertanyaan, opsi, jawaban));
                } else {
                    soalList.add(new LatihanSoal(pertanyaan, jawaban));
                }
            }

            simpanLatihanKeDatabase(latihan, soalList);
            popupStage.close();
        });

        layout.getChildren().add(simpanButton);
        popupStage.setScene(new Scene(new ScrollPane(layout), 600, 600));
        popupStage.setTitle("Tambah " + jenisLatihan);
        popupStage.show();
    }

    private void bukaPopupEditLatihan(Latihan latihan) {
        List<LatihanSoal> soalList = bankSoal.get(latihan);
        Stage popupStage = new Stage();
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20));

        TextField namaField = new TextField(latihan.getNamaLatihan());
        layout.getChildren().addAll(new Label("Edit Nama Latihan:"), namaField);

        List<VBox> soalBoxList = new ArrayList<>();

        for (LatihanSoal soal : soalList) {
            VBox soalBox = new VBox(5);
            soalBox.getChildren().add(new Label("Soal"));
            TextArea soalField = new TextArea(soal.getPertanyaan());
            soalBox.getChildren().add(soalField);

            if (latihan.getJenisLatihan().equals("Pilihan Ganda")) {
                for (int i = 0; i < soal.getOpsi().size(); i++) {
                    TextField opsiField = new TextField(soal.getOpsi().get(i));
                    opsiField.setPromptText("Opsi " + (char) ('A' + i));
                    soalBox.getChildren().add(opsiField);
                }
            }

            TextField jawabanField = new TextField(soal.getJawabanBenar());
            jawabanField.setPromptText("Jawaban Benar");
            soalBox.getChildren().add(jawabanField);

            soalBoxList.add(soalBox);
            layout.getChildren().add(soalBox);
        }

        Button simpanButton = new Button("Simpan Perubahan");
        simpanButton.setOnAction(e -> {
            try (Connection conn = DatabaseConnection.getConnection()) {
                // Update nama latihan
                PreparedStatement psUpdate = conn.prepareStatement("UPDATE latihan SET nama_latihan = ? WHERE nama_latihan = ?");
                psUpdate.setString(1, namaField.getText());
                psUpdate.setString(2, latihan.getNamaLatihan());
                psUpdate.executeUpdate();

                // Dapatkan ID latihan
                PreparedStatement psGetId = conn.prepareStatement("SELECT id FROM latihan WHERE nama_latihan = ?");
                psGetId.setString(1, namaField.getText());
                ResultSet rs = psGetId.executeQuery();
                rs.next();
                int latihanId = rs.getInt("id");

                // Hapus soal lama
                PreparedStatement psHapus = conn.prepareStatement("DELETE FROM soal WHERE latihan_id = ?");
                psHapus.setInt(1, latihanId);
                psHapus.executeUpdate();

                // Tambahkan soal baru (hasil edit)
                for (VBox soalBox : soalBoxList) {
                    String pertanyaan = ((TextArea) soalBox.getChildren().get(1)).getText();
                    String jawaban = ((TextField) soalBox.getChildren().get(soalBox.getChildren().size() - 1)).getText();
                    PreparedStatement psSoal = conn.prepareStatement(
                            "INSERT INTO soal(latihan_id, pertanyaan, opsi_a, opsi_b, opsi_c, jawaban_benar) VALUES (?, ?, ?, ?, ?, ?)"
                    );
                    psSoal.setInt(1, latihanId);
                    psSoal.setString(2, pertanyaan);

                    if (latihan.getJenisLatihan().equals("Pilihan Ganda")) {
                        psSoal.setString(3, ((TextField) soalBox.getChildren().get(2)).getText());
                        psSoal.setString(4, ((TextField) soalBox.getChildren().get(3)).getText());
                        psSoal.setString(5, ((TextField) soalBox.getChildren().get(4)).getText());
                    } else {
                        psSoal.setNull(3, Types.VARCHAR);
                        psSoal.setNull(4, Types.VARCHAR);
                        psSoal.setNull(5, Types.VARCHAR);
                    }

                    psSoal.setString(6, jawaban);
                    psSoal.executeUpdate();
                }

                popupStage.close();
                loadLatihanDariDatabase();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        });

        layout.getChildren().add(simpanButton);
        popupStage.setScene(new Scene(new ScrollPane(layout), 600, 600));
        popupStage.setTitle("Edit Latihan: " + latihan.getNamaLatihan());
        popupStage.show();
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
                Latihan latihan = new Latihan(nama, jenis);
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
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    private void simpanLatihanKeDatabase(Latihan latihan, List<LatihanSoal> soalList) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String insertLatihan = "INSERT INTO latihan(nama_latihan, jenis_latihan) VALUES (?, ?) RETURNING id";
            PreparedStatement psLatihan = conn.prepareStatement(insertLatihan);
            psLatihan.setString(1, latihan.getNamaLatihan());
            psLatihan.setString(2, latihan.getJenisLatihan());
            ResultSet rs = psLatihan.executeQuery();
            rs.next();
            int idLatihan = rs.getInt(1);

            for (LatihanSoal soal : soalList) {
                String insertSoal = "INSERT INTO soal(latihan_id, pertanyaan, opsi_a, opsi_b, opsi_c, jawaban_benar) VALUES (?, ?, ?, ?, ?, ?)";
                PreparedStatement psSoal = conn.prepareStatement(insertSoal);
                psSoal.setInt(1, idLatihan);
                psSoal.setString(2, soal.getPertanyaan());
                if (latihan.getJenisLatihan().equals("Pilihan Ganda")) {
                    psSoal.setString(3, soal.getOpsi().get(0));
                    psSoal.setString(4, soal.getOpsi().get(1));
                    psSoal.setString(5, soal.getOpsi().get(2));
                } else {
                    psSoal.setNull(3, Types.VARCHAR);
                    psSoal.setNull(4, Types.VARCHAR);
                    psSoal.setNull(5, Types.VARCHAR);
                }
                psSoal.setString(6, soal.getJawabanBenar());
                psSoal.executeUpdate();
            }
            loadLatihanDariDatabase();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void setLoading(boolean status) {
        overlayPane.setVisible(status);
        tambahPGButton.setDisable(status);
        tambahIsianButton.setDisable(status);
        dashboardBtn.setDisable(status);
        mentorBtn.setDisable(status);
        materiBtn.setDisable(status);
        logoutBtn.setDisable(status);
        pesanBtn.setDisable(status);
        logoutBtn.setDisable(status);
    }

}
