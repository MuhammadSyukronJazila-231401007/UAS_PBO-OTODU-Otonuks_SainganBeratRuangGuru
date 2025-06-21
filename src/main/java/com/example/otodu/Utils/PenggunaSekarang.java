package com.example.otodu.Utils;

import com.example.otodu.Model.Pengguna;

public class PenggunaSekarang {
    private static Pengguna penggunaAktif;

    public static void tambahPengguna(int id, String email, String nama, String nomor,
                                      String kota, String password,
                                      String role, int koin, String materiTerakhir) {
        penggunaAktif = new Pengguna(id, email, nama, nomor, kota, password, role, koin, materiTerakhir);
    }

    public static Pengguna getPengguna() {
        return penggunaAktif;
    }


    public static void hapusPengguna() {
        penggunaAktif = null;
    }

    public static void logout() {
        penggunaAktif = null;
    }

    public static boolean isLogin() {
        return penggunaAktif != null;
    }
}
