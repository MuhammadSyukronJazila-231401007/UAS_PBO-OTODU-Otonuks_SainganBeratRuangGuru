package com.example.otodu.Model;

import java.sql.Date;

public class PesanMentor {
    private int id;
    private String namaSiswa;
    private String namaMentor;
    private String emailMentor;
    private String nomorMentor;
    private Date tanggal;
    private boolean selesai;

    public PesanMentor(int id, String namaSiswa, String namaMentor, String emailMentor, String nomorMentor, Date tanggal, boolean selesai) {
        this.id = id;
        this.namaSiswa = namaSiswa;
        this.namaMentor = namaMentor;
        this.emailMentor = emailMentor;
        this.nomorMentor = nomorMentor;
        this.tanggal = tanggal;
        this.selesai = selesai;
    }

    public int getId() { return id; }
    public String getNamaSiswa() { return namaSiswa; }
    public String getNamaMentor() { return namaMentor; }
    public String getEmailMentor() { return emailMentor; }
    public String getNomorMentor() { return nomorMentor; }
    public Date getTanggal() { return tanggal; }
    public boolean isSelesai() { return selesai; }
    public void setSelesai(boolean selesai) { this.selesai = selesai; }
}
