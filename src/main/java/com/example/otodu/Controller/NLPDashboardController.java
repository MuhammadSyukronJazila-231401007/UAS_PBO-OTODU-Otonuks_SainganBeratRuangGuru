package com.example.otodu.Controller;

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
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;


public class NLPDashboardController {
    @FXML private Label namaPenggunaLabel;
    @FXML private Label materiTerakhirLabel;

    // Card Statistik
    @FXML private Label nlpPoinLabel;
    @FXML private Label materiSelesaiLabel;
    @FXML private Label latihanDikuasaiLabel;

    @FXML private Button bukaTerakhirBtn;
    @FXML private Button bukaMateriBtn;

    @FXML private Button logoutBtn;

    @FXML private Label mentorLabel;

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

        int idUser = PenggunaSekarang.getPengguna().getId();
        String nama = PenggunaSekarang.getPengguna().getNama();
        String materiTerakhir = PenggunaSekarang.getPengguna().getMateriTerakhir();

        namaPenggunaLabel.setText("Halo, " + nama);
        materiTerakhirLabel.setText("Terakhir belajar: " + (materiTerakhir != null ? materiTerakhir : "-"));

        new Thread(() -> {
            Poin poin = PoinKoneksi.getPoinByUserId(idUser);
            Statistik statistik = StatistikKoneksi.getStatistikByUserId(idUser);

            Platform.runLater(() -> {
                if (poin != null) {
                    nlpPoinLabel.setText(poin.getTotalPoin() + " Poin");
                } else {
                    nlpPoinLabel.setText("0 Poin");
                }

                if (statistik != null) {
                    materiSelesaiLabel.setText("Materi yang telah diselesaikan: " + statistik.getMateri());
                    latihanDikuasaiLabel.setText("Latihan yang telah dikuasai: " + statistik.getLatihan());
                } else {
                    materiSelesaiLabel.setText("Materi yang telah diselesaikan: 0");
                    latihanDikuasaiLabel.setText("Latihan yang telah dikuasai: 0");
                }

                setLoading(false); // Sembunyikan overlay setelah data dimuat
            });
        }).start();
    }


    private void setLoading(boolean status) {
        overlayPane.setVisible(status);
        overlayPane.setMouseTransparent(!status);
    }

}
