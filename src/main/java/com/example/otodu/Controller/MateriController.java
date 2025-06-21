// File: MateriController.java
package com.example.otodu.Controller;

import com.example.otodu.Koneksi.PenggunaKoneksi;
import com.example.otodu.Model.Materi;
import com.example.otodu.Model.Pengguna;
import com.example.otodu.Utils.DatabaseConnection;
import com.example.otodu.Utils.UbahHalaman;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MateriController {
    @FXML
    private TilePane materiTilePane;
    @FXML private TextField searchField;
    @FXML private Button tambahButton;
    @FXML private Button mentorBtn;
    @FXML private Button dashboardBtn;
    @FXML private Button latihanBtn;
    @FXML private Button pesanBtn;

    @FXML private Button logoutBtn;

    @FXML private AnchorPane overlayPane;
    private List<Materi> semuaMateri = new ArrayList<>();

    public void initialize() {
        mentorBtn.setOnAction(e -> UbahHalaman.switchScene(e,"Mentor.fxml"));
        dashboardBtn.setOnAction(e -> UbahHalaman.switchScene(e,"Dashboard.fxml"));
        latihanBtn.setOnAction(e -> UbahHalaman.switchScene(e,"Latihan.fxml"));
        pesanBtn.setOnAction(e -> UbahHalaman.switchScene(e, "PesanMentor.fxml"));

        logoutBtn.setOnAction(e -> {
            UbahHalaman.konfirmasiLogout(e);
        });

        setLoading(true); // tampilkan overlay

        tambahButton.setOnAction(e -> bukaPopupTambahMateri());

        searchField.textProperty().addListener((obs, oldVal, newVal) -> {
            List<Materi> hasil = semuaMateri.stream()
                    .filter(m -> m.getNamaMateri().toLowerCase().contains(newVal.toLowerCase()))
                    .collect(Collectors.toList());
            tampilkanMateri(hasil);
        });

        new Thread(() -> {
            // (Optional) ambil top murid
            List<Pengguna> topMurid = PenggunaKoneksi.getTopMuridDenganPoin();

            // Ambil materi dari database
            semuaMateri.clear();
            try (Connection conn = DatabaseConnection.getConnection()) {
                String query = "SELECT * FROM materi";
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(query);

                System.out.println("Mulai ambil data materi dari DB...");

                int count = 0;
                while (rs.next()) {
                    Materi m = new Materi(
                            rs.getInt("id"),
                            rs.getString("nama_materi"),
                            rs.getString("jenjang"),
                            rs.getString("kelas"),
                            rs.getString("isi_materi"),
                            rs.getString("gambar_path"),
                            rs.getString("video_path")
                    );
                    semuaMateri.add(m);
                    count++;

                    // Debug info per item
                    System.out.println("-> Materi: " + m.getNamaMateri() + " | Jenjang: " + m.getJenjang() + " | Kelas: " + m.getKelas());
                }

                System.out.println("Total materi ditemukan: " + count);

            } catch (SQLException e) {
                e.printStackTrace();
            }

            Platform.runLater(() -> {
                tampilkanMateri(semuaMateri);
                setLoading(false);
            });
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
        materiTilePane.getChildren().clear();
        for (Materi m : daftar) {
            materiTilePane.getChildren().add(buatCard(m));
        }
    }
    private String extractYouTubeId(String url) {
        if (url == null || url.isEmpty()) return null;
        String pattern = "^(?:https?://)?(?:www\\.)?(?:youtube\\.com/(?:watch\\?v=|embed/)|youtu\\.be/)([\\w-]{11}).*$";
        return url.matches(pattern) ? url.replaceAll(pattern, "$1") : null;
    }


    private VBox buatCard(Materi materi) {
        VBox card = new VBox(10);
        card.setPadding(new Insets(18, 24, 18, 24));
        card.setStyle("-fx-background-color: white; -fx-border-color: #E1F0FF; -fx-border-width: 2;");
        card.setPrefSize(400, 100);

        // Label nama materi
        Label nama = new Label(materi.getNamaMateri());
        nama.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #375679;");
        HBox.setHgrow(nama, Priority.ALWAYS);

        // Tombol Edit
        Button editButton = new Button("Edit");
        editButton.setOnAction(e -> bukaPopupEditMateri(materi));

        // Tombol Hapus
        Button hapusButton = new Button("Hapus");
        hapusButton.setStyle("-fx-background-color: #dc3545; -fx-text-fill: white;");
        hapusButton.setOnAction(e -> {
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Yakin ingin menghapus materi ini?", ButtonType.YES, ButtonType.NO);
            confirm.setTitle("Konfirmasi Hapus");
            confirm.setHeaderText(null);
            confirm.showAndWait().ifPresent(response -> {
                if (response == ButtonType.YES) {
                    hapusMateriDariDatabase(materi);
                }
            });
        });

        // Top bar dengan nama + tombol di kanan
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        HBox topBar = new HBox(10, nama, spacer, editButton, hapusButton);

        // Badge Jenjang
        Label badgeJenjang = new Label("Jenjang: " + materi.getJenjang());
        badgeJenjang.setStyle("-fx-background-color: #E1F0FF; -fx-border-color: #007BFF; -fx-font-weight: bold;");
        badgeJenjang.setPadding(new Insets(2, 10, 2, 10));

        // Badge Kelas
        Label badgeKelas = new Label("Kelas: " + materi.getKelas());
        badgeKelas.setStyle("-fx-background-color: #FFF3CD; -fx-border-color: #FFC107; -fx-font-weight: bold;");
        badgeKelas.setPadding(new Insets(2, 10, 2, 10));

        HBox badges = new HBox(10, badgeJenjang, badgeKelas);

        card.getChildren().addAll(topBar, badges);
        return card;
    }




    private void bukaPopupTambahMateri() {
        Stage popup = new Stage();
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20));

        TextField namaField = new TextField();
        TextField jenjangField = new TextField();
        TextField kelasField = new TextField();
        TextArea isiArea = new TextArea();
        isiArea.setPromptText("Isi materi panjang...");
        TextField youtubeLinkField = new TextField();
        youtubeLinkField.setPromptText("Link video YouTube");

        FileChooser fileChooser = new FileChooser();
        Button uploadGambar = new Button("Upload Gambar");
        final File[] selectedImage = new File[1];

        uploadGambar.setOnAction(e -> {
            File chosenFile = fileChooser.showOpenDialog(popup);
            if (chosenFile != null) {
                try {
                    String fileName = chosenFile.getName();
                    File destDir = new File("src/main/resources/Gambar");
                    if (!destDir.exists()) destDir.mkdirs();

                    File destFile = new File(destDir, fileName);
                    java.nio.file.Files.copy(chosenFile.toPath(), destFile.toPath(),
                            java.nio.file.StandardCopyOption.REPLACE_EXISTING);

                    selectedImage[0] = new File("Gambar/" + fileName);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        Button simpan = new Button("Simpan");
        simpan.setOnAction(e -> {
            try (Connection conn = DatabaseConnection.getConnection()) {
                String insert = "INSERT INTO materi (nama_materi, jenjang, kelas, isi_materi, gambar_path, video_path) VALUES (?, ?, ?, ?, ?, ?)";
                PreparedStatement ps = conn.prepareStatement(insert);

                String videoLink = youtubeLinkField.getText();
                String videoId = extractYouTubeId(videoLink); // Fungsi ekstrak ID

                ps.setString(1, namaField.getText());
                ps.setString(2, jenjangField.getText());
                ps.setString(3, kelasField.getText());
                ps.setString(4, isiArea.getText());
                ps.setString(5, selectedImage[0] != null ? selectedImage[0].getPath() : null);
                ps.setString(6, videoId); // hanya ID YouTube

                ps.executeUpdate();
                popup.close();
                loadMateriDariDatabase();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        });

        layout.getChildren().addAll(
                new Label("Nama Materi"), namaField,
                new Label("Jenjang"), jenjangField,
                new Label("Kelas"), kelasField,
                new Label("Isi Materi"), isiArea,
                new Label("Link Video YouTube"), youtubeLinkField,
                uploadGambar, simpan
        );

        popup.setScene(new Scene(layout));
        popup.setTitle("Tambah Materi");
        popup.show();
    }



    private void bukaPopupEditMateri(Materi materi) {
        Stage popup = new Stage();
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20));

        TextField namaField = new TextField(materi.getNamaMateri());
        TextField jenjangField = new TextField(materi.getJenjang());
        TextField kelasField = new TextField(materi.getKelas());
        TextArea isiArea = new TextArea(materi.getIsiMateri());
        TextField youtubeLinkField = new TextField();
        youtubeLinkField.setPromptText("Link video YouTube");
        youtubeLinkField.setText(materi.getVideoPath() != null ? "https://youtu.be/" + materi.getVideoPath() : "");

        FileChooser fileChooser = new FileChooser();
        Button uploadGambar = new Button("Ganti Gambar");
        final File[] selectedImage = new File[1];

        uploadGambar.setOnAction(e -> {
            File chosenFile = fileChooser.showOpenDialog(popup);
            if (chosenFile != null) {
                try {
                    String fileName = chosenFile.getName();
                    File destDir = new File("src/main/resources/Gambar");
                    if (!destDir.exists()) destDir.mkdirs();

                    File destFile = new File(destDir, fileName);
                    java.nio.file.Files.copy(chosenFile.toPath(), destFile.toPath(),
                            java.nio.file.StandardCopyOption.REPLACE_EXISTING);

                    selectedImage[0] = new File("Gambar/" + fileName);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        Button simpan = new Button("Update");
        simpan.setOnAction(e -> {
            try (Connection conn = DatabaseConnection.getConnection()) {
                String update = "UPDATE materi SET nama_materi = ?, jenjang = ?, kelas = ?, isi_materi = ?, gambar_path = ?, video_path = ? WHERE id = ?";
                PreparedStatement ps = conn.prepareStatement(update);

                String videoId = extractYouTubeId(youtubeLinkField.getText());

                ps.setString(1, namaField.getText());
                ps.setString(2, jenjangField.getText());
                ps.setString(3, kelasField.getText());
                ps.setString(4, isiArea.getText());
                ps.setString(5, selectedImage[0] != null ? selectedImage[0].getPath() : materi.getGambarPath());
                ps.setString(6, videoId);
                ps.setInt(7, materi.getId());

                ps.executeUpdate();
                popup.close();
                loadMateriDariDatabase();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        });

        layout.getChildren().addAll(
                new Label("Nama Materi"), namaField,
                new Label("Jenjang"), jenjangField,
                new Label("Kelas"), kelasField,
                new Label("Isi Materi"), isiArea,
                new Label("Link Video YouTube"), youtubeLinkField,
                uploadGambar, simpan
        );

        popup.setScene(new Scene(layout));
        popup.setTitle("Edit Materi");
        popup.show();
    }

    private void hapusMateriDariDatabase(Materi materi) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            // Hapus data dari database
            String delete = "DELETE FROM materi WHERE id = ?";
            PreparedStatement ps = conn.prepareStatement(delete);
            ps.setInt(1, materi.getId());
            ps.executeUpdate();

            // Hapus gambar dari folder resources/Gambar jika ada
            if (materi.getGambarPath() != null) {
                File gambarFile = new File("src/main/resources/" + materi.getGambarPath());
                if (gambarFile.exists()) {
                    boolean success = gambarFile.delete();
                    if (!success) {
                        System.err.println("Gagal menghapus file gambar: " + gambarFile.getPath());
                    }
                }
            }

            loadMateriDariDatabase(); // refresh tampilan
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void setLoading(boolean status) {
        overlayPane.setVisible(status);
        dashboardBtn.setDisable(status);
        mentorBtn.setDisable(status);
        latihanBtn.setDisable(status);
        logoutBtn.setDisable(status);
        pesanBtn.setDisable(status);
    }

}