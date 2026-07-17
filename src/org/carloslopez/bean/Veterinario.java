package org.carloslopez.bean;

import java.util.Date;

public class Veterinario {
    private int codigoVeterinario;
    private String nombreVeterinario;
    private String apellidoVeterinario;
    private String especialidad;
    private String telefonoVeterinario;
    private String Estado;
    private Date fechaIngreso;

    public Veterinario() {
    }
    public Veterinario(int codigoVeterinario, String nombreVeterinario, String apellidoVeterinario, String especialidad, String telefonoVeterinario, String Estado, Date fechaIngreso) {
        this.codigoVeterinario = codigoVeterinario;
        this.nombreVeterinario = nombreVeterinario;
        this.apellidoVeterinario = apellidoVeterinario;
        this.especialidad = especialidad;
        this.telefonoVeterinario = telefonoVeterinario;
        this.Estado = Estado;
        this.fechaIngreso = fechaIngreso;
    }

    public int getCodigoVeterinario() {
        return codigoVeterinario;
    }
    public void setCodigoVeterinario(int codigoVeterinario) {
        this.codigoVeterinario = codigoVeterinario;
    }

    public String getNombreVeterinario() {
        return nombreVeterinario;
    }
    public void setNombreVeterinario(String nombreVeterinario) {
        this.nombreVeterinario = nombreVeterinario;
    }

    public String getApellidoVeterinario() {
        return apellidoVeterinario;
    }
    public void setApellidoVeterinario(String apellidoVeterinario) {
        this.apellidoVeterinario = apellidoVeterinario;
    }

    public String getEspecialidad() {
        return especialidad;
    }
    public void setEspecialidad(String especialidad) {
        this.especialidad = especialidad;
    }

    public String getTelefonoVeterinario() {
        return telefonoVeterinario;
    }
    public void setTelefonoVeterinario(String telefonoVeterinario) {
        this.telefonoVeterinario = telefonoVeterinario;
    }

    public String getEstado() {
        return Estado;
    }
    public void setEstado(String Estado) {
        this.Estado = Estado;
    }

    public Date getFechaIngreso() {
        return fechaIngreso;
    }
    public void setFechaIngreso(Date fechaIngreso) {
        this.fechaIngreso = fechaIngreso;
    }
}