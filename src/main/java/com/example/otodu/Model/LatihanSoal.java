package com.example.otodu.Model;

import java.util.List;

public class LatihanSoal {
    private String pertanyaan;
    private List<String> opsi;  // untuk pilihan ganda
    private String jawabanBenar;

    public LatihanSoal(String pertanyaan, List<String> opsi, String jawabanBenar) {
        this.pertanyaan = pertanyaan;
        this.opsi = opsi;
        this.jawabanBenar = jawabanBenar;
    }

    public LatihanSoal(String pertanyaan, String jawabanBenar) {
        this.pertanyaan = pertanyaan;
        this.jawabanBenar = jawabanBenar;
        this.opsi = null;
    }

    public String getPertanyaan() { return pertanyaan; }
    public List<String> getOpsi() { return opsi; }
    public String getJawabanBenar() { return jawabanBenar; }
}
