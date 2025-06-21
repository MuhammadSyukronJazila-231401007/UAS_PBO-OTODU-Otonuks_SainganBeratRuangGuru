package com.example.otodu.Utils;

import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;

import java.io.IOException;

public class UbahHalaman {

    public static void switchScene(Event event, String fxmlFileName) {
        try {
            // Cari root node dari event, apapun jenis Event-nya
            Object source = event.getSource();

            if (source instanceof Node) {
                Stage stage = (Stage) ((Node) source).getScene().getWindow();

                FXMLLoader loader = new FXMLLoader(UbahHalaman.class.getResource("/com/example/otodu/" + fxmlFileName));
                Parent root = loader.load();

                Scene scene = new Scene(root, 1280, 720);
                stage.setTitle("OTODU");
                stage.setMaximized(true);
                stage.setResizable(false);
                stage.setScene(scene);
                stage.show();
            } else {
                System.err.println("Source event bukan Node, tidak bisa ganti halaman.");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void konfirmasiLogout(ActionEvent event) {
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
}

