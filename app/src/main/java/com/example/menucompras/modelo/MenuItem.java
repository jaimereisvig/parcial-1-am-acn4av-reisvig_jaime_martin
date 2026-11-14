package com.example.menucompras.modelo;

public class MenuItem {
    private int id;
    private int comidaId;
    private Dia dia;
    private Franja franja;
    private double factor;

    public MenuItem(int id, int comidaId, Dia dia, Franja franja, double factor) {
        this.id = id;
        this.comidaId = comidaId;
        this.dia = dia;
        this.franja = franja;
        this.factor = factor;
    }

    public int getId() { return id; }
    public int getComidaId() { return comidaId; }
    public Dia getDia() { return dia; }
    public Franja getFranja() { return franja; }
    public double getFactor() { return factor; }
}