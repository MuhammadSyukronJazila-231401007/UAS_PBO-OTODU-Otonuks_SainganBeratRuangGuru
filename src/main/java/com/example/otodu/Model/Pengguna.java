package com.example.otodu.Model;

public class Pengguna {
    private int id;
    private String email;
    private String nama;
    private String nomor;
    private String kota;
    private String password;
    private String role;
    private int koin;
    private String materiTerakhir;
    private int totalPoin;

    public Pengguna(String email, String nama, String nomor, String kota, String password, String role, int koin, String materiTerakhir) {
        this.email = email;
        this.nama = nama;
        this.nomor = nomor;
        this.kota = kota;
        this.password = password;
        this.role = role;
        this.koin = koin;
        this.materiTerakhir = materiTerakhir;
    }

    public Pengguna(int id, String email, String nama, String nomor, String kota, String password, String role, int koin, String materiTerakhir) {
        this.id = id;
        this.email = email;
        this.nama = nama;
        this.nomor = nomor;
        this.kota = kota;
        this.password = password;
        this.role = role;
        this.koin = koin;
        this.materiTerakhir = materiTerakhir;
    }

    // Getter & Setter
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getNama() { return nama; }
    public void setNama(String nama) { this.nama = nama; }

    public String getNomor() { return nomor; }
    public void setNomor(String nomor) { this.nomor = nomor; }

    public String getKota() { return kota; }
    public void setKota(String nomor) { this.kota = kota; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public int getKoin() { return koin; }
    public void setKoin(int koin) { this.koin = koin; }

    public String getMateriTerakhir() { return materiTerakhir; }
    public void setMateriTerakhir(String materiTerakhir) { this.materiTerakhir = materiTerakhir; }

    public void setTotalPoin(int p) { this.totalPoin = p; }
    public int getTotalPoin() { return totalPoin; }
}
