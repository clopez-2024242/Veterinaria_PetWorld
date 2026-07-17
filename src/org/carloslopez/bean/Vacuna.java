package org.carloslopez.bean;

public class Vacuna {
    private int codigoVacuna;
    private String nombreVacuna;
    private String descripcion;
    private String dosis;
    private int frecuenciaMeses;

    public Vacuna() {}
    public Vacuna(int codigoVacuna, String nombreVacuna, String descipcion, String dosis, int frecuenciaMeses) {
        this.codigoVacuna = codigoVacuna;
        this.nombreVacuna = nombreVacuna;
        this.descripcion = descipcion;
        this.dosis = dosis;
        this.frecuenciaMeses = frecuenciaMeses;
    }

    public int getCodigoVacuna() {
        return codigoVacuna;
    }
    public void setCodigoVacuna(int codigoVacuna) {
        this.codigoVacuna = codigoVacuna;
    }

    public String getNombreVacuna() {
        return nombreVacuna;
    }
    public void setNombreVacuna(String nombreVacuna) {
        this.nombreVacuna = nombreVacuna;
    }

    public String getDescripcion() {
        return descripcion;
    }
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDosis() {
        return dosis;
    }
    public void setDosis(String dosis) {
        this.dosis = dosis;
    }

    public int getFrecuenciaMeses() {
        return frecuenciaMeses;
    }
    public void setFrecuenciaMeses(int frecuenciaMeses) {
        this.frecuenciaMeses = frecuenciaMeses;
    }
}
