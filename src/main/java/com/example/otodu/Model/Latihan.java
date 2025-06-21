package com.example.otodu.Model;

public class Latihan {
    private String namaLatihan;
    private String jenisLatihan; // "Pilihan Ganda" atau "Isian Singkat"
    private boolean selected = false;

    public Latihan(String namaLatihan, String jenisLatihan) {
        this.namaLatihan = namaLatihan;
        this.jenisLatihan = jenisLatihan;
    }

    public String getNamaLatihan() {
        return namaLatihan;
    }

    public void setNamaLatihan(String namaLatihan) {
        this.namaLatihan = namaLatihan;
    }

    public String getJenisLatihan() {
        return jenisLatihan;
    }

    public void setJenisLatihan(String jenisLatihan) {
        this.jenisLatihan = jenisLatihan;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
