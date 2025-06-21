package com.example.otodu.Model;

public class Mentor extends Pengguna {
    private String materi;
    private String jenjang;
    private String online;
    private String offline;
    private String riwayatStudi;

    public Mentor(int id, String email, String nama, String nomor, String kota,
                  String password, String role, int koin, String materiTerakhir,
                  String materi, String jenjang, String online, String offline, String riwayatStudi) {
        super(id, email, nama, nomor, kota, password, role, koin, materiTerakhir);
        this.materi = materi;
        this.jenjang = jenjang;
        this.online = online;
        this.offline = offline;
        this.riwayatStudi = riwayatStudi;
    }

    // Getter dan Setter untuk field tambahan
    public String getMateri() {
        return materi;
    }

    public void setMateri(String materi) {
        this.materi = materi;
    }

    public String getJenjang() {
        return jenjang;
    }

    public void setJenjang(String jenjang) {
        this.jenjang = jenjang;
    }

    public String getOnline() {
        return online;
    }

    public void setOnline(String online) {
        this.online = online;
    }

    public String getOffline() {
        return offline;
    }

    public void setOffline(String offline) {
        this.offline = offline;
    }

    public String getRiwayatStudi() {
        return riwayatStudi;
    }

    public void setRiwayatStudi(String riwayatStudi) {
        this.riwayatStudi = riwayatStudi;
    }
}
