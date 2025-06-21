package com.example.otodu.Controller;

import com.example.otodu.Model.Materi;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MateriController {
    @FXML
    private TilePane materiTilePane;
    @FXML private TextField searchField;
    @FXML private Button tambahButton;

    private List<Materi> semuaMateri = new ArrayList<>();

    public void initialize() {
        semuaMateri.add(new Materi("Matematika Kelas IX", "SMA", "11"));
        semuaMateri.add(new Materi("Fisika Kelas X", "SMA", "11"));
        semuaMateri.add(new Materi("Kimia Kelas XI", "SMA", "11"));
        semuaMateri.add(new Materi("Bahasa Indonesia Kelas X" , "SMA", "11"));

        tampilkanMateri(semuaMateri);

        searchField.textProperty().addListener((obs, oldVal, newVal) -> {
            List<Materi> hasil = semuaMateri.stream()
                    .filter(m -> m.getNamaMateri().toLowerCase().contains(newVal.toLowerCase()))
                    .collect(Collectors.toList());
            tampilkanMateri(hasil);
        });
    }

    private void tampilkanMateri(List<Materi> daftar) {
        materiTilePane.getChildren().clear();
        for (Materi m : daftar) {
            materiTilePane.getChildren().add(buatCard(m));
        }
    }

    private VBox buatCard(Materi materi) {
        VBox card = new VBox(5);
        card.setPadding(new Insets(18, 24, 18, 24));
        card.setStyle("-fx-background-color: white; -fx-border-color: #E1F0FF; -fx-border-width: 2;");
        card.setPrefSize(400, 85);

        CheckBox checkBox = new CheckBox();
        checkBox.setSelected(materi.isSelected());
        checkBox.selectedProperty().addListener((obs, oldVal, newVal) -> materi.setSelected(newVal));

        Label nama = new Label(materi.getNamaMateri());
        nama.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #375679;");

        Label badge = new Label(materi.getJenjang());
        badge.setStyle("-fx-background-color: #E1F0FF; -fx-border-color: #007BFF; -fx-font-weight: bold;");
        badge.setPadding(new Insets(2, 10, 2, 10));

        Button hapus = new Button("Hapus");
        hapus.setOnAction(e -> {
            semuaMateri.remove(materi);
            tampilkanMateri(semuaMateri);
        });

        HBox top = new HBox(10, checkBox, nama, hapus);
        HBox bottom = new HBox(badge);

        card.getChildren().addAll(top, bottom);
        return card;
    }

}
