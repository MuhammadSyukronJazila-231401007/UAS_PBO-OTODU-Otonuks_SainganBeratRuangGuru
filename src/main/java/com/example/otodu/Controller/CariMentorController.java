package com.example.otodu.Controller;

import com.example.otodu.Koneksi.MentorKoneksi;
import com.example.otodu.Model.Mentor;
import com.example.otodu.Utils.UbahHalaman;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CariMentorController {

    @FXML private VBox mentorCardContainer;
    @FXML private Label nlpLabel;

    @FXML private CheckBox matematikaCheckbox, bingCheckbox, dasproCheckbox, utbkCheckbox;
    @FXML private ComboBox<String> matematikaJenjang, bingJenjang, dasproJenjang;
    @FXML private TextField kotaField;

    @FXML private TextField onlineMulaiField, onlineSampaiField;
    @FXML private TextField offlineMulaiField, offlineSampaiField;

    @FXML private Button cariMentorBtn;
    @FXML private AnchorPane overlayPane;
    @FXML private ProgressIndicator loadingIndicator;
    @FXML private Button logoutBtn;

    private List<Mentor> semuaMentor;

    @FXML
    public void initialize() {
        setLoading(true); // Tampilkan overlay dulu

        logoutBtn.setOnAction(e -> {
            UbahHalaman.konfirmasiLogout(e);
        });

        nlpLabel.setOnMouseClicked(e -> UbahHalaman.switchScene(e, "NLPDashboard.fxml"));
        cariMentorBtn.setOnAction(e -> filterMentor());

        new Thread(() -> {
            semuaMentor = MentorKoneksi.getAllMentor();

            // Update UI di JavaFX thread
            javafx.application.Platform.runLater(() -> {
                tampilkanDaftarMentor(semuaMentor);
                setLoading(false); // Sembunyikan overlay setelah selesai
            });
        }).start();
    }

    private void tampilkanDaftarMentor(List<Mentor> mentorList) {
        mentorCardContainer.getChildren().clear();
        for (Mentor mentor : mentorList) {
            VBox card = buatCardMentor(mentor);
            mentorCardContainer.getChildren().add(card);
        }
    }

    private void filterMentor() {
        List<Mentor> hasil = new ArrayList<>(semuaMentor);

        // === Ambil input dari form ===
        List<String> materiFilter = new ArrayList<>();
        if (matematikaCheckbox.isSelected()) materiFilter.add("Matematika");
        if (bingCheckbox.isSelected()) materiFilter.add("Bahasa Inggris");
        if (dasproCheckbox.isSelected()) materiFilter.add("Dasar Pemrograman");
        if (utbkCheckbox.isSelected()) materiFilter.add("UTBK");

        String jenjangMat = matematikaJenjang.getValue();
        String jenjangBing = bingJenjang.getValue();
        String jenjangDaspro = dasproJenjang.getValue();

        String kotaInput = kotaField.getText().trim().toLowerCase();

        String onlineMulai = onlineMulaiField.getText().trim();
        String onlineSampai = onlineSampaiField.getText().trim();
        String offlineMulai = offlineMulaiField.getText().trim();
        String offlineSampai = offlineSampaiField.getText().trim();

        hasil = hasil.stream().filter(mentor -> {
            // === Materi: HARUS mengandung SEMUA materi yang dicentang ===
            boolean cocokMateri = materiFilter.isEmpty() ||
                    materiFilter.stream().allMatch(m -> mentor.getMateri().contains(m));

            // === Jenjang: Jika materi dicentang dan jenjang dipilih, harus cocok ===
            boolean cocokJenjang = true;
            if (matematikaCheckbox.isSelected() && jenjangMat != null) {
                cocokJenjang &= mentor.getJenjang().contains("Matematika-" + jenjangMat);
            }
            if (bingCheckbox.isSelected() && jenjangBing != null) {
                cocokJenjang &= mentor.getJenjang().contains("Bahasa Inggris-" + jenjangBing);
            }
            if (dasproCheckbox.isSelected() && jenjangDaspro != null) {
                cocokJenjang &= mentor.getJenjang().contains("Dasar Pemrograman-" + jenjangDaspro);
            }

            // === Kota ===
            boolean cocokKota = kotaInput.isEmpty() ||
                    mentor.getKota().toLowerCase().contains(kotaInput);

            // === Waktu Online: Jika user isi waktu, mentor harus mengandung semuanya ===
            boolean cocokOnline = waktuTermasuk(mentor.getOnline(), onlineMulai, onlineSampai);

            // === Waktu Offline ===
            boolean cocokOffline = waktuTermasuk(mentor.getOffline(), offlineMulai, offlineSampai);

            return cocokMateri && cocokJenjang && cocokKota && cocokOnline && cocokOffline;

        }).collect(Collectors.toList());

        tampilkanDaftarMentor(hasil);
    }

    private boolean waktuTermasuk(String rentangMentor, String userMulai, String userSampai) {
        if (userMulai.isEmpty() || userSampai.isEmpty()) return true;

        try {
            LocalTime mulaiUser = LocalTime.parse(userMulai);
            LocalTime sampaiUser = LocalTime.parse(userSampai);

            String[] rentangList = rentangMentor.split(";");
            for (String rentang : rentangList) {
                String[] jam = rentang.trim().split(" - ");
                if (jam.length == 2) {
                    LocalTime mulaiMentor = LocalTime.parse(jam[0].trim());
                    LocalTime sampaiMentor = LocalTime.parse(jam[1].trim());

                    // Cek apakah seluruh rentang user berada di dalam rentang mentor
                    if (!mulaiUser.isBefore(mulaiMentor) && !sampaiUser.isAfter(sampaiMentor)) {
                        return true;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace(); // Jika input salah format
        }
        return false;
    }

    private VBox buatCardMentor(Mentor mentor) {
        VBox cardWrapper = new VBox();
        cardWrapper.setPadding(new Insets(15));
        cardWrapper.setSpacing(10);
        cardWrapper.setStyle("""
        -fx-background-color: white;
        -fx-border-color: #E0E0E0;
        -fx-border-radius: 10;
        -fx-background-radius: 10;
        """);

        // === Nama Mentor ===
        Label nama = new Label(mentor.getNama());
        nama.setStyle("-fx-font-size: 18px; -fx-font-family: 'Poppins'; -fx-font-weight: bold; -fx-text-fill: #495DA3;");

        // === Kotak Info ===
        HBox infoRow = new HBox(15);
        infoRow.setAlignment(Pos.CENTER_LEFT);

        infoRow.getChildren().addAll(
                createInfoBox("Email", mentor.getEmail(), "#F0F4FF"),
                createInfoBox("No HP", mentor.getNomor(), "#F9F9F9")
        );

        HBox infoRow2 = new HBox(15);
        infoRow2.setAlignment(Pos.CENTER_LEFT);

        infoRow2.getChildren().addAll(
                createInfoBox("Kota", mentor.getKota(), "#E1F5FE"),
                createInfoBox("Materi", mentor.getMateri().replace(";", ", "), "#F8F3FF")
        );

        // === Riwayat Studi Full Width ===
        Label riwayatLabel = new Label("Riwayat Studi:\n" + mentor.getRiwayatStudi().replace(";", "\n"));
        riwayatLabel.setStyle("""
        -fx-font-size: 13px;
        -fx-font-family: 'Poppins';
        -fx-background-color: #F5F5F5;
        -fx-padding: 8;
        -fx-border-radius: 5;
        -fx-background-radius: 5;
        """);

        // === Tombol Pesan ===
        Button pesanBtn = new Button("Pesan Mentor");
        pesanBtn.setStyle("""
        -fx-background-color: #495DA3;
        -fx-text-fill: white;
        -fx-font-weight: bold;
        -fx-font-size: 14px;
        -fx-font-family: 'Poppins';
        """);
        pesanBtn.setPrefHeight(36);
        pesanBtn.setPrefWidth(200);

        HBox tombolBox = new HBox(pesanBtn);
        tombolBox.setAlignment(Pos.CENTER_RIGHT);

        cardWrapper.getChildren().addAll(nama, infoRow, infoRow2, riwayatLabel, tombolBox);
        return cardWrapper;
    }

    // Util: Membuat kotak kecil info
    private VBox createInfoBox(String label, String content, String bgColor) {
        VBox box = new VBox(3);
        box.setPadding(new Insets(10));
        box.setSpacing(3);
        box.setStyle(String.format("""
        -fx-background-color: %s;
        -fx-border-color: #D0D0D0;
        -fx-border-radius: 6;
        -fx-background-radius: 6;
        """, bgColor));
        box.setPrefWidth(250);

        Label title = new Label(label);
        title.setStyle("-fx-font-size: 11px; -fx-text-fill: gray; -fx-font-family: 'Poppins';");

        Label value = new Label(content);
        value.setStyle("-fx-font-size: 13px; -fx-font-family: 'Poppins'; -fx-font-weight: bold;");

        box.getChildren().addAll(title, value);
        return box;
    }

    private void setLoading(boolean status) {
        overlayPane.setVisible(status);
        overlayPane.setMouseTransparent(!status); // Agar tidak bisa klik saat loading
    }


}
