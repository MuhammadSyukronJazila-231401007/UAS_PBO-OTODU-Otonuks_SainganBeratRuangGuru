package com.example.otodu.Model;

public class Statistik {
    private int idUser;
    private int latihan;
    private int materi;

    public Statistik(int idUser, int latihan, int materi) {
        this.idUser = idUser;
        this.latihan = latihan;
        this.materi = materi;
    }

    public int getIdUser() {
        return idUser;
    }

    public void setIdUser(int idUser) {
        this.idUser = idUser;
    }

    public int getLatihan() {
        return latihan;
    }

    public void setLatihan(int latihan) {
        this.latihan = latihan;
    }

    public int getMateri() {
        return materi;
    }

    public void setMateri(int materi) {
        this.materi = materi;
    }
}
