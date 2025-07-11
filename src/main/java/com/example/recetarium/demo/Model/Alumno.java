package com.example.recetarium.demo.Model;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Alumno {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idAlumno;

    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column
    private byte[] dniFrente;
    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column
    private byte[] dniFondo;
    @Column
    private String nroTramite;



    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name="cuentaCorriente",referencedColumnName = "idCuentaCorriente")
    private CuentaCorriente cuentaCorriente;

    @OneToMany(mappedBy ="alumno",cascade = CascadeType.ALL)
    private List<AsistenciaCurso> asistenciaCursos;

    public Long getIdAlumno() {
        return idAlumno;
    }

    public byte[] getDniFrente() {
        return dniFrente;
    }

    public byte[] getDniFondo() {
        return dniFondo;
    }

    public String getNroTramite() {
        return nroTramite;
    }



    public void setCuentaCorriente(CuentaCorriente cuentaCorriente) {
        this.cuentaCorriente = cuentaCorriente;
    }

    public void setAsistenciaCursos(List<AsistenciaCurso> asistenciaCursos) {
        this.asistenciaCursos = asistenciaCursos;
    }

    public CuentaCorriente getCuentaCorriente() {
        return cuentaCorriente;
    }

    public List<AsistenciaCurso> getAsistenciaCursos() {
        return asistenciaCursos;
    }

    public void setIdAlumno(Long idAlumno) {
        this.idAlumno = idAlumno;
    }

    public void setDniFrente(byte[] dniFrente) {
        this.dniFrente = dniFrente;
    }

    public void setDniFondo(byte[] dniFondo) {
        this.dniFondo = dniFondo;
    }

    public void setNroTramite(String nroTramite) {
        this.nroTramite = nroTramite;
    }




}
