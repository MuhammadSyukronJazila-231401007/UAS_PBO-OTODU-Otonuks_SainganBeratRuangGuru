package com.example.otodu.Model;

public class Materi {
    private int kodeMateri;
    private String namaMateri;
    private String jenjang;
    private String kelas;
    private boolean selected = false;

    public Materi(int kodeMateri, String namaMateri, String jenjang, String kelas) {
        this.kodeMateri = kodeMateri;
        this.namaMateri = namaMateri;
        this.jenjang = jenjang;
        this.kelas = kelas;
    }

    public Materi(String namaMateri, String jenjang, String kelas) {
        this.namaMateri = namaMateri;
        this.jenjang = jenjang;
        this.kelas = kelas;
    }

    public Materi(String namaMateri) {
        this.namaMateri = namaMateri;
    }

    public int getKodeMateri() {
        return kodeMateri;
    }

    public void setKodeMateri(int kodeMateri) {
        this.kodeMateri = kodeMateri;
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

    public boolean isSelected(){
        return this.selected;
    }

    public void setSelected(boolean newSelect){
         this.selected = newSelect;
    }
}
