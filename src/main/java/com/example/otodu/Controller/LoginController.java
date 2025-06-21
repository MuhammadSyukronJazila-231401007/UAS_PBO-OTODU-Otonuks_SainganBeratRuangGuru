package com.example.otodu.Controller;

import com.example.otodu.Koneksi.PenggunaKoneksi;
import com.example.otodu.Utils.PenggunaSekarang;
import com.example.otodu.Utils.UbahHalaman;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class LoginController {

    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private Button loginBtn;
    @FXML private Button registerBtn;
    @FXML private Label loginMessage;

    @FXML private AnchorPane overlayPane;
    @FXML private ProgressIndicator loadingIndicator;

    @FXML
    public void initialize(){
        loginBtn.setOnAction(e -> {
            loginBtnOnAction(e);
        });

        registerBtn.setOnAction(e -> {
            UbahHalaman.switchScene(e, "Registrasi.fxml");
        });
    }

    public void loginBtnOnAction(ActionEvent event) {
        String username = emailField.getText().trim();
        String password = passwordField.getText().trim();

//        String username = "tes@gmail.com";
//        String password = "tes";

        if (username.isEmpty() || password.isEmpty()) {
            loginMessage.setText("Username dan Password tidak boleh kosong!");
            return;
        }

        loginMessage.setText("");
        setLoading(true); // Mulai loading

        new Thread(() -> {
            boolean sukses = PenggunaKoneksi.verifLogin(username, password);

            javafx.application.Platform.runLater(() -> {
                setLoading(false); // Selesai loading

                if (sukses) {
                    String role = PenggunaSekarang.getPengguna().getRole().toLowerCase();

                    if (role.equals("siswa")) {
                        UbahHalaman.switchScene(event, "NLPDashboard.fxml");
                    } else if (role.equals("admin")) {
                        UbahHalaman.switchScene(event, "Dashboard.fxml");
                    }
                } else {
                    loginMessage.setText("Username atau Password salah");
                }
            });
        }).start();
    }


    private void setLoading(boolean status) {
        overlayPane.setVisible(status);
        overlayPane.setMouseTransparent(!status); // agar tidak bisa klik saat loading
    }

}
