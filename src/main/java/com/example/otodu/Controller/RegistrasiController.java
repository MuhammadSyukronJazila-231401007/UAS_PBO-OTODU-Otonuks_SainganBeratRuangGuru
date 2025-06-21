package com.example.otodu.Controller;

import com.example.otodu.Koneksi.PenggunaKoneksi;
import com.example.otodu.Koneksi.PoinKoneksi;
import com.example.otodu.Model.Pengguna;
import com.example.otodu.Utils.Hash;
import com.example.otodu.Utils.IDGenerator;
import com.example.otodu.Utils.UbahHalaman;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class RegistrasiController {

    @FXML private TextField emailField;
    @FXML private TextField namaField;
    @FXML private TextField noTelponField;
    @FXML private TextField kotaField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;

    @FXML private CheckBox setujuCheckbox;
    @FXML private Button loginBtn;
    @FXML private Button registerBtn;
    @FXML private Button loginText;

    @FXML private AnchorPane overlayPane;
    @FXML private ProgressIndicator loadingIndicator;


    public void initialize() {
        loginBtn.setOnAction(e -> {
            loginBtnOnAction(e);
        });

        registerBtn.setOnAction(e -> {
            registerBtnOnAction(e);
        });

        loginText.setOnAction(e -> {
            loginBtnOnAction(e);
        });
    }

    @FXML
    private void registerBtnOnAction(ActionEvent event) {
        String email = emailField.getText();
        String nama = namaField.getText();
        String nomor = noTelponField.getText();
        String kota = kotaField.getText();
        String password = passwordField.getText();
        String konfirmasi = confirmPasswordField.getText();

        if (email.isBlank() || nama.isBlank() || nomor.isBlank() || kota.isBlank() || password.isBlank() || konfirmasi.isBlank()) {
            showPopupError("Data tidak lengkap", "Harap isi semua field!");
            return;
        }

        if (!setujuCheckbox.isSelected()) {
            showPopupError("Syarat & Ketentuan", "Anda harus menyetujui S&K terlebih dahulu.");
            return;
        }

        if (!password.equals(konfirmasi)) {
            showPopupError("Password Tidak Cocok", "Password dan konfirmasi tidak sama.");
            return;
        }

        setLoading(true); // Tampilkan loader

        new Thread(() -> {
            if (PenggunaKoneksi.isEmailTerdaftar(email)) {
                Platform.runLater(() -> {
                    setLoading(false);
                    showPopupError("Gagal", "Email sudah terdaftar. Gunakan email lain.");
                });
                return;
            }

            int id = IDGenerator.generateIntID();
            Pengguna pengguna = new Pengguna(id, email, nama, nomor, kota, Hash.hash(password), "siswa", 0, null);
            boolean sukses = PenggunaKoneksi.tambahPengguna(pengguna);

            if (sukses) {
                boolean poin = PoinKoneksi.tambahPoinAwal(pengguna.getId());
                Platform.runLater(() -> {
                    setLoading(false);
                    if (poin) {
                        showPopup("Berhasil Registrasi. Silahkan login.");
                        clearField();
                    } else {
                        showPopupError("Gagal", "Gagal menambahkan poin awal.");
                    }
                });
            } else {
                Platform.runLater(() -> {
                    setLoading(false);
                    showPopupError("Gagal Registrasi", "Terjadi kesalahan saat menyimpan data.");
                });
            }
        }).start();
    }


    @FXML
    private void loginBtnOnAction(ActionEvent event) {
        try {
            UbahHalaman.switchScene(event, "Login.fxml");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showPopup(String pesan) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Berhasil");
        alert.setHeaderText("Registrasi Sukses");
        alert.setContentText(pesan);
        alert.showAndWait();
    }

    private void showPopupError(String header, String pesan) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Terjadi Kesalahan");
        alert.setHeaderText(header);
        alert.setContentText(pesan);
        alert.showAndWait();
    }

    private void clearField() {
        emailField.clear();
        namaField.clear();
        noTelponField.clear();
        kotaField.clear();
        passwordField.clear();
        confirmPasswordField.clear();
        setujuCheckbox.setSelected(false);
    }

    private void setLoading(boolean status) {
        overlayPane.setVisible(status);
        overlayPane.setMouseTransparent(!status); // agar tidak bisa klik saat loading
    }
}
