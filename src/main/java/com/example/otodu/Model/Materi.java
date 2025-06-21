package com.example.otodu.Model;

public class Materi {
    private int id;
    private String namaMateri;
    private String jenjang;
    private String kelas;
    private String isiMateri;
    private String gambarPath;
    private String videoPath;
    private boolean selected = false;

    // Constructor lengkap
    public Materi(int id, String namaMateri, String jenjang, String kelas, String isiMateri, String gambarPath, String videoPath) {
        this.id = id;
        this.namaMateri = namaMateri;
        this.jenjang = jenjang;
        this.kelas = kelas;
        this.isiMateri = isiMateri;
        this.gambarPath = gambarPath;
        this.videoPath = videoPath;
    }

    // Constructor tanpa isi materi (untuk daftar)
    public Materi(int id, String namaMateri, String jenjang, String kelas) {
        this.id = id;
        this.namaMateri = namaMateri;
        this.jenjang = jenjang;
        this.kelas = kelas;
    }

    // Getter dan Setter
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNamaMateri() {
        return namaMateri;
    }

    public void setNamaMateri(String namaMateri) {
        this.namaMateri = namaMateri;
    }

    public String getJenjang() {
        return jenjang;
    }

    public void setJenjang(String jenjang) {
        this.jenjang = jenjang;
    }

    public String getKelas() {
        return kelas;
    }

    public void setKelas(String kelas) {
        this.kelas = kelas;
    }

    public String getIsiMateri() {
        return isiMateri;
    }

    public void setIsiMateri(String isiMateri) {
        this.isiMateri = isiMateri;
    }

    public String getGambarPath() {
        return gambarPath;
    }

    public void setGambarPath(String gambarPath) {
        this.gambarPath = gambarPath;
    }

    public String getVideoPath() {
        return videoPath;
    }

    public void setVideoPath(String videoPath) {
        this.videoPath = videoPath;
    }

    public boolean isSelected() {
        return this.selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
