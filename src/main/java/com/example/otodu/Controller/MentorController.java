package com.example.otodu.Controller;

import com.example.otodu.Koneksi.MentorKoneksi;
import com.example.otodu.Koneksi.PenggunaKoneksi;
import com.example.otodu.Model.Mentor;
import com.example.otodu.Model.Pengguna;
import com.example.otodu.Utils.Hash;
import com.example.otodu.Utils.PenggunaSekarang;
import com.example.otodu.Utils.UbahHalaman;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MentorController {
    @FXML private VBox mentorContainer;
    @FXML private Button tambahMentorBtn;

    @FXML private Button dashboardBtn;
    @FXML private Button materiBtn;

    @FXML private AnchorPane overlayPane;
    @FXML private StackPane dashboardStack;

    @FXML private Button logoutBtn;

    private List<Mentor> semuaMentor = new ArrayList<>();

    @FXML
    public void initialize() {
        setLoading(true);

        dashboardBtn.setOnAction(e -> UbahHalaman.switchScene(e, "Dashboard.fxml"));
        materiBtn.setOnAction(e -> {/* bisa isi navigasi ke halaman materi */});

        logoutBtn.setOnAction(e -> {
            UbahHalaman.konfirmasiLogout(e);
        });

        tambahMentorBtn.setOnAction(e -> showMentorDialog(null, null));

        new Thread(() -> {
            // Ambil data mentor di thread terpisah
            List<Mentor> hasil = MentorKoneksi.getAllMentor();

            // Kembali ke UI thread untuk update UI
            javafx.application.Platform.runLater(() -> {
                semuaMentor.clear();
                semuaMentor.addAll(hasil);
                tampilkanSemuaMentor();
                setLoading(false);
            });
        }).start();
    }


    private void tampilkanSemuaMentor() {
        mentorContainer.getChildren().clear();
        for (Mentor mentor : semuaMentor) {
            mentorContainer.getChildren().add(buatCard(mentor));
        }
    }

    private HBox buatCard(Mentor mentor) {
        HBox card = new HBox(15);
        card.setAlignment(Pos.CENTER_LEFT);
        card.setStyle("-fx-background-color: white; -fx-padding: 15; -fx-border-color: #E0E0E0; -fx-border-width: 0 0 1 0;");

        VBox detailBox = new VBox(4);

        Label namaLabel = new Label(mentor.getNama());
        namaLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #495DA3;");

        Label emailLabel = new Label(mentor.getEmail());
        emailLabel.setStyle("-fx-font-size: 13px;");

        Label nomorLabel = new Label(mentor.getNomor());
        nomorLabel.setStyle("-fx-font-size: 13px;");

        detailBox.getChildren().addAll(namaLabel, emailLabel, nomorLabel);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox actionBox = new HBox(10);

        Label kotaLabel = new Label(mentor.getKota());
        kotaLabel.setStyle("-fx-background-color: #E1F0FF; -fx-border-color: #007BFF; -fx-padding: 4 10 4 10; -fx-font-size: 12px;");

        Button editButton = new Button("Edit");
        editButton.setStyle("-fx-background-color: #495DA3; -fx-text-fill: white;");
        editButton.setOnAction(e -> {
            showMentorDialog(mentor, () -> {
                namaLabel.setText(mentor.getNama());
                emailLabel.setText(mentor.getEmail());
                nomorLabel.setText(mentor.getNomor());
                kotaLabel.setText(mentor.getKota());
            });
        });

        Button deleteButton = new Button("Delete");
        deleteButton.setStyle("-fx-background-color: #E53935; -fx-text-fill: white;");
        deleteButton.setOnAction(e -> {
            Alert konfirmasi = new Alert(Alert.AlertType.CONFIRMATION, "Yakin ingin menghapus mentor ini?", ButtonType.YES, ButtonType.NO);
            konfirmasi.setHeaderText(null);
            konfirmasi.showAndWait().ifPresent(res -> {
                if (res == ButtonType.YES) {
                    semuaMentor.remove(mentor);
                    mentorContainer.getChildren().remove(card);

                    new Thread(() -> {
                        PenggunaKoneksi.hapusPengguna(mentor.getId());
                        MentorKoneksi.hapusMentor(mentor.getId());
                    }).start();
                }
            });
        });

        actionBox.getChildren().addAll(kotaLabel, editButton, deleteButton);
        card.getChildren().addAll(detailBox, spacer, actionBox);

        return card;
    }

    private void showMentorDialog(Mentor mentor, Runnable onUpdateUI) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle(mentor == null ? "Tambah Mentor" : "Edit Mentor");

        GridPane grid = new GridPane();
        grid.setVgap(10);
        grid.setHgap(10);
        grid.setPadding(new Insets(20));

        TextField emailField = new TextField();
        emailField.setPromptText("contoh: mentor@mail.com");

        TextField namaField = new TextField();
        namaField.setPromptText("Nama lengkap mentor");

        TextField nomorField = new TextField();
        nomorField.setPromptText("contoh: 08123456789");

        TextField kotaField = new TextField();
        kotaField.setPromptText("contoh: Jakarta");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Kata sandi (hanya saat tambah)");

        TextArea materiArea = new TextArea();
        materiArea.setPromptText("Pisahkan materi dengan tanda titik koma (;) misal: Matematika;Bahasa Inggris");
        materiArea.setPrefRowCount(2);

        TextArea jenjangArea = new TextArea();
        jenjangArea.setPromptText("Format: Matematika-SMP;Bahasa Inggris-SMA");
        jenjangArea.setPrefRowCount(2);

        TextArea onlineArea = new TextArea();
        onlineArea.setPromptText("Pisahkan dengan titik koma, contoh: 08:00 - 10:00;13:00 - 15:00");
        onlineArea.setPrefRowCount(2);

        TextArea offlineArea = new TextArea();
        offlineArea.setPromptText("Pisahkan dengan titik koma jika lebih dari satu");
        offlineArea.setPrefRowCount(2);

        TextArea riwayatArea = new TextArea();
        riwayatArea.setPromptText("Format: Jurusan - Universitas, pisahkan dengan titik koma");
        riwayatArea.setPrefRowCount(2);

        grid.add(new Label("Email:"), 0, 0); grid.add(emailField, 1, 0);
        grid.add(new Label("Nama:"), 0, 1); grid.add(namaField, 1, 1);
        grid.add(new Label("Nomor HP:"), 0, 2); grid.add(nomorField, 1, 2);
        grid.add(new Label("Kota:"), 0, 3); grid.add(kotaField, 1, 3);
        grid.add(new Label("Password:"), 0, 4); grid.add(passwordField, 1, 4);
        grid.add(new Label("Materi:"), 0, 5); grid.add(materiArea, 1, 5);
        grid.add(new Label("Jenjang:"), 0, 6); grid.add(jenjangArea, 1, 6);
        grid.add(new Label("Online:"), 0, 7); grid.add(onlineArea, 1, 7);
        grid.add(new Label("Offline:"), 0, 8); grid.add(offlineArea, 1, 8);
        grid.add(new Label("Riwayat Studi:"), 0, 9); grid.add(riwayatArea, 1, 9);

        if (mentor != null) {
            emailField.setText(mentor.getEmail());
            namaField.setText(mentor.getNama());
            nomorField.setText(mentor.getNomor());
            kotaField.setText(mentor.getKota());
            materiArea.setText(mentor.getMateri());
            jenjangArea.setText(mentor.getJenjang());
            onlineArea.setText(mentor.getOnline());
            offlineArea.setText(mentor.getOffline());
            riwayatArea.setText(mentor.getRiwayatStudi());
        }

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            if (mentor == null) {
                Pengguna baru = new Pengguna(0, emailField.getText(), namaField.getText(), nomorField.getText(), kotaField.getText(), Hash.hash(passwordField.getText()), "Mentor", 0, null);
                int idUser = PenggunaKoneksi.tambahDanKembalikanID(baru);
                Mentor dataMentor = new Mentor(idUser, baru.getEmail(), baru.getNama(), baru.getNomor(), baru.getKota(), baru.getPassword(), "Mentor", 0,
                        materiArea.getText(), jenjangArea.getText(), onlineArea.getText(), offlineArea.getText(), riwayatArea.getText(), null);
                semuaMentor.add(dataMentor);
                mentorContainer.getChildren().add(buatCard(dataMentor));
                new Thread(() -> MentorKoneksi.tambahMentor(dataMentor)).start();
            } else {
                mentor.setEmail(emailField.getText());
                mentor.setNama(namaField.getText());
                mentor.setNomor(nomorField.getText());
                mentor.setKota(kotaField.getText());
                mentor.setMateri(materiArea.getText());
                mentor.setJenjang(jenjangArea.getText());
                mentor.setOnline(onlineArea.getText());
                mentor.setOffline(offlineArea.getText());
                mentor.setRiwayatStudi(riwayatArea.getText());
                if (onUpdateUI != null) onUpdateUI.run();
                new Thread(() -> {
                    PenggunaKoneksi.updatePengguna(mentor);
                    MentorKoneksi.updateMentor(mentor);
                }).start();
            }
        }
    }

    private void setLoading(boolean status) {
        overlayPane.setVisible(status);
        dashboardBtn.setDisable(status);
        materiBtn.setDisable(status);
        logoutBtn.setDisable(status);
    }


}
