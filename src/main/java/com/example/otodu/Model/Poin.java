package com.example.otodu.Model;

public class Poin {
    private int idUser;
    private int materi;
    private int latihan;
    private int totalPoin;

    public Poin(int idUser, int materi, int latihan, int totalPoin) {
        this.idUser = idUser;
        this.materi = materi;
        this.latihan = latihan;
        this.totalPoin = totalPoin;
    }

    public int getIdUser() {
        return idUser;
    }

    public int getMateri() {
        return materi;
    }

    public int getLatihan() {
        return latihan;
    }

    public int getTotalPoin() {
        return totalPoin;
    }

    public void setMateri(int materi) {
        this.materi = materi;
    }

    public void setLatihan(int latihan) {
        this.latihan = latihan;
    }

    public void setTotalPoin(int totalPoin) {
        this.totalPoin = totalPoin;
    }

    // Optional: auto update total
    public void updateTotal() {
        this.totalPoin = this.materi + this.latihan;
    }
}
