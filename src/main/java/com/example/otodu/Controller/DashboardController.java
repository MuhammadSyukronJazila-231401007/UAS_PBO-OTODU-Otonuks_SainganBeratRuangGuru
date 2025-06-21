// DashboardController.java
package com.example.otodu.Controller;

import com.example.otodu.Koneksi.MateriKoneksi;
import com.example.otodu.Koneksi.PenggunaKoneksi;
import com.example.otodu.Koneksi.PoinKoneksi;
import com.example.otodu.Model.Pengguna;
import com.example.otodu.Model.Poin;
import com.example.otodu.Utils.PenggunaSekarang;
import com.example.otodu.Utils.UbahHalaman;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;

import java.util.List;

public class DashboardController {

    @FXML private Label namaLabel;
    @FXML private Label jumlahMuridLabel;
    @FXML private Label jumlahMentorLabel;
    @FXML private Label jumlahMateriLabel;

    @FXML private TableView<Pengguna> muridTable;
    @FXML private TableColumn<Pengguna, String> namaColumn;
    @FXML private TableColumn<Pengguna, Integer> poinColumn;
    @FXML private TableColumn<Pengguna, String> emailColumn;
    @FXML private TableColumn<Pengguna, String> nomorColumn;
    @FXML private TableColumn<Pengguna, Integer> koinColumn;
    @FXML private ProgressIndicator loadingIndicator;

    @FXML private Button mentorBtn;
    @FXML private Button materiBtn;
    @FXML private Button logoutBtn;

    @FXML private AnchorPane overlayPane;
    @FXML private StackPane dashboardStack;

    @FXML
    public void initialize() {
        mentorBtn.setOnAction(e -> {
            UbahHalaman.switchScene(e,"Mentor.fxml");
        });

        materiBtn.setOnAction(e -> {
//            konfirmasiLogout(e);
        });

        logoutBtn.setOnAction(e -> {
            UbahHalaman.konfirmasiLogout(e);
        });

        namaLabel.setText("Welcome " + PenggunaSekarang.getPengguna().getNama() + "!");
        setLoading(true);

        // Set kolom table
        namaColumn.setCellValueFactory(new PropertyValueFactory<>("nama"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        nomorColumn.setCellValueFactory(new PropertyValueFactory<>("nomor"));
        koinColumn.setCellValueFactory(new PropertyValueFactory<>("koin"));
        poinColumn.setCellValueFactory(new PropertyValueFactory<>("totalPoin"));

        new Thread(() -> {
            int jumlahMurid = PenggunaKoneksi.hitungPenggunaDenganRole("siswa");
            int jumlahMentor = PenggunaKoneksi.hitungPenggunaDenganRole("mentor");
            int jumlahMateri = MateriKoneksi.hitungJumlahMateri();
            List<Pengguna> topMurid = PenggunaKoneksi.getTopMuridDenganPoin();

            Platform.runLater(() -> {
                jumlahMuridLabel.setText(String.valueOf(jumlahMurid));
                jumlahMentorLabel.setText(String.valueOf(jumlahMentor));
                jumlahMateriLabel.setText(String.valueOf(jumlahMateri));
                muridTable.getItems().setAll(topMurid);
                setLoading(false); // tampilkan konten, hilangkan loader
            });
        }).start();
    }

    private void konfirmasiLogout(ActionEvent event) {
        Alert konfirmasi = new Alert(Alert.AlertType.CONFIRMATION);
        konfirmasi.setTitle("Konfirmasi Logout");
        konfirmasi.setHeaderText(null);
        konfirmasi.setContentText("Apakah Anda yakin ingin keluar dari akun ini?");

        konfirmasi.showAndWait().ifPresent(respon -> {
            if (respon == ButtonType.OK) {
                PenggunaSekarang.hapusPengguna();
                UbahHalaman.switchScene(event, "Login.fxml");
            }
        });
    }

    private void setLoading(boolean status) {
        overlayPane.setVisible(status);
        mentorBtn.setDisable(status);
        materiBtn.setDisable(status);
        logoutBtn.setDisable(status);
        // tambahkan tombol lainnya juga jika ada
    }
}