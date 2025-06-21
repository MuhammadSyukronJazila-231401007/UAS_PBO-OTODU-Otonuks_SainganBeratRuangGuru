package com.example.otodu.Controller;

import com.example.otodu.Koneksi.PesanMentorKoneksi;
import com.example.otodu.Model.PesanMentor;
import com.example.otodu.Utils.UbahHalaman;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.util.List;

public class PesanMentorController {

    @FXML private TilePane pesanTilePane;
    @FXML private AnchorPane overlayPane;
    @FXML private Button dashboardBtn, mentorBtn, materiBtn, latihanBtn, logoutBtn;

    @FXML
    public void initialize() {
        dashboardBtn.setOnAction(e -> UbahHalaman.switchScene(e, "Dashboard.fxml"));
        mentorBtn.setOnAction(e -> UbahHalaman.switchScene(e, "Mentor.fxml"));
        materiBtn.setOnAction(e -> UbahHalaman.switchScene(e, "Materi.fxml"));
        latihanBtn.setOnAction(e -> UbahHalaman.switchScene(e, "Latihan.fxml"));
        logoutBtn.setOnAction(e -> UbahHalaman.konfirmasiLogout(e));

        tampilkanDaftarPesanan();
    }

    private void tampilkanDaftarPesanan() {
        setLoading(true);
        new Thread(() -> {
            List<PesanMentor> semua = PesanMentorKoneksi.getSemuaPesanan();

            // Update UI di thread JavaFX
            Platform.runLater(() -> {
                pesanTilePane.getChildren().clear();
                for (PesanMentor pesan : semua) {
                    pesanTilePane.getChildren().add(buatCard(pesan));
                }
                setLoading(false); // <- Perbaikan: loading disembunyikan setelah isi dimuat
            });
        }).start();
    }

    private VBox buatCard(PesanMentor p) {
        VBox card = new VBox(8);
        card.setPadding(new Insets(15));
        card.setStyle("-fx-background-color: white; -fx-border-color: #E1F0FF; -fx-border-radius: 10; -fx-background-radius: 10;");
        card.setPrefWidth(400);

        Label siswa = new Label("👤 Siswa: " + p.getNamaSiswa());
        Label mentor = new Label("📚 Mentor: " + p.getNamaMentor());
        Label email = new Label("✉️ Email: " + p.getEmailMentor());
        Label nohp = new Label("📞 Nomor: " + p.getNomorMentor());
        Label tanggal = new Label("📅 Tanggal: " + p.getTanggal());

        siswa.setStyle("-fx-font-size: 14px;");
        mentor.setStyle("-fx-font-size: 14px;");
        email.setStyle("-fx-font-size: 14px;");
        nohp.setStyle("-fx-font-size: 14px;");
        tanggal.setStyle("-fx-font-size: 13px; -fx-text-fill: gray;");

        Button selesaiBtn = new Button("Tandai Selesai");
        selesaiBtn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold;");
        selesaiBtn.setOnAction(e -> {
            Alert konfirmasi = new Alert(Alert.AlertType.CONFIRMATION, "Yakin tandai pemesanan ini sebagai selesai?", ButtonType.YES, ButtonType.NO);
            konfirmasi.setHeaderText(null);
            konfirmasi.showAndWait().ifPresent(res -> {
                if (res == ButtonType.YES) {
                    PesanMentorKoneksi.tandaiSelesai(p.getId());
                    tampilkanDaftarPesanan();
                }
            });
        });

        if (p.isSelesai()) {
            selesaiBtn.setDisable(true);
            selesaiBtn.setText("Sudah Selesai");
            selesaiBtn.setStyle("-fx-background-color: gray; -fx-text-fill: white;");
        }

        card.getChildren().addAll(siswa, mentor, email, nohp, tanggal, selesaiBtn);
        return card;
    }

    private void setLoading(boolean status) {
        overlayPane.setVisible(status);
        dashboardBtn.setDisable(status);
        mentorBtn.setDisable(status);
        materiBtn.setDisable(status);
        latihanBtn.setDisable(status);
        logoutBtn.setDisable(status);
        // tambahkan tombol lainnya juga jika ada
    }
}
