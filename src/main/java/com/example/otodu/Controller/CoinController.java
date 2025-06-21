package com.example.otodu.Controller;

import com.example.otodu.Model.Pengguna;
import com.example.otodu.Utils.DatabaseConnection;
import com.example.otodu.Utils.PenggunaSekarang;
import com.example.otodu.Utils.UbahHalaman;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.control.Alert.AlertType;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Optional;

public class CoinController {
    @FXML private Pane panePaket1, panePaket2, panePaket3;
    @FXML private Button beliPerKoinBtn;
    @FXML private TextField inputJumlahKoin;
    @FXML private Label nlpLabel;
    @FXML private Label mentorLabel;
    @FXML private Button logoutBtn;

    @FXML
    public void initialize() {
        logoutBtn.setOnAction(e ->{
            UbahHalaman.konfirmasiLogout(e);
        });
        mentorLabel.setOnMouseClicked(e -> {
            UbahHalaman.switchScene(e, "CariMentor.fxml");
        });
        nlpLabel.setOnMouseClicked(e -> {
            UbahHalaman.switchScene(e, "NLPDashboard.fxml");
        });

        // Tombol pembelian
        panePaket1.setOnMouseClicked(e -> konfirmasiPembelian("OTODU SEHARI", 10));
        panePaket2.setOnMouseClicked(e -> konfirmasiPembelian("OTODU 5 HARI", 50));
        panePaket3.setOnMouseClicked(e -> konfirmasiPembelian("OTODU 10 HARI", 100));

        // Tombol beli per koin
        beliPerKoinBtn.setOnAction(e -> {
            try {
                int jumlah = Integer.parseInt(inputJumlahKoin.getText());
                if (jumlah > 0) {
                    konfirmasiPembelian("Beli per koin", jumlah);
                } else {
                    tampilkanAlert("Jumlah koin harus lebih dari 0.");
                }
            } catch (NumberFormatException ex) {
                tampilkanAlert("Masukkan jumlah koin yang valid.");
            }
        });
    }

    private void konfirmasiPembelian(String namaPaket, int jumlahKoin) {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Konfirmasi Pembelian");
        alert.setHeaderText("Apakah Anda yakin ingin membeli paket:");
        alert.setContentText(namaPaket + " (" + jumlahKoin + " koin)");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            tambahKoinPengguna(jumlahKoin);
        }
    }

    private void tambahKoinPengguna(int tambahanKoin) {
        Pengguna pengguna = PenggunaSekarang.getPengguna();
        if (pengguna == null) {
            tampilkanAlert("Pengguna belum login.");
            return;
        }

        int id = pengguna.getId();
        int koinBaru = pengguna.getKoin() + tambahanKoin;

        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "UPDATE users SET koin = ? WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, koinBaru);
            stmt.setInt(2, id);
            int rows = stmt.executeUpdate();

            if (rows > 0) {
                pengguna.setKoin(koinBaru); // update objek lokal
                tampilkanAlert("Pembelian berhasil! Total koin Anda sekarang: " + koinBaru);
            } else {
                tampilkanAlert("Gagal memperbarui data koin.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            tampilkanAlert("Terjadi kesalahan saat mengupdate database.");
        }
    }

    private void tampilkanAlert(String pesan) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Informasi");
        alert.setHeaderText(null);
        alert.setContentText(pesan);
        alert.showAndWait();
    }

}
