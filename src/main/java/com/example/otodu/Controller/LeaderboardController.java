// File: LeaderboardController.java
package com.example.otodu.Controller;

import com.example.otodu.Koneksi.PenggunaKoneksi;
import com.example.otodu.Model.Pengguna;
import com.example.otodu.Utils.UbahHalaman;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import java.util.List;

public class LeaderboardController {

    @FXML private TableView<Pengguna> leaderboardTable;
    @FXML private TableColumn<Pengguna, String> rankColumn;
    @FXML private TableColumn<Pengguna, String> namaColumn;
    @FXML private TableColumn<Pengguna, String> emailColumn;
    @FXML private TableColumn<Pengguna, Integer> poinColumn;

    @FXML private Label nlpLabel;
    @FXML private Label mentorLabel;
    @FXML private Button logoutBtn;

    @FXML private AnchorPane overlayPane;

    @FXML
    public void initialize() {
        mentorLabel.setOnMouseClicked(e -> UbahHalaman.switchScene(e, "CariMentor.fxml"));
        nlpLabel.setOnMouseClicked(e -> UbahHalaman.switchScene(e, "NLPDashboard.fxml"));
        logoutBtn.setOnAction(e -> UbahHalaman.konfirmasiLogout(e));

        namaColumn.setCellValueFactory(new PropertyValueFactory<>("nama"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        poinColumn.setCellValueFactory(new PropertyValueFactory<>("totalPoin"));

        // Rank kolom akan diisi manual di loop
        rankColumn.setCellValueFactory(param -> {
            int index = leaderboardTable.getItems().indexOf(param.getValue());
            String rankIcon = switch (index) {
                case 0 -> "\uD83E\uDD47 "; // 🥇
                case 1 -> "\uD83E\uDD48 "; // 🥈
                case 2 -> "\uD83E\uDD49 "; // 🥉
                default -> (index + 1) + ". ";
            };
            return new ReadOnlyStringWrapper(rankIcon);
        });

        setLoading(true);
        new Thread(() -> {
            List<Pengguna> topMurid = PenggunaKoneksi.getTopMuridDenganPoin();
            Platform.runLater(() -> {
                leaderboardTable.getItems().setAll(topMurid);
                setLoading(false);
            });
        }).start();
    }

    private void setLoading(boolean status) {
        overlayPane.setVisible(status);
        logoutBtn.setDisable(status);
        mentorLabel.setDisable(status);
        logoutBtn.setDisable(status);
    }
}
