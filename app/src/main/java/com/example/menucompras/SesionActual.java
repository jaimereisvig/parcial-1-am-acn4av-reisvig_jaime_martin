package com.example.menucompras;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public final class SesionActual {

    private SesionActual() {}

    // UID del usuario autenticado, o null si no hay sesion.
    public static String uid() {
        FirebaseUser u = FirebaseAuth.getInstance().getCurrentUser();
        return (u != null) ? u.getUid() : null;
    }
}