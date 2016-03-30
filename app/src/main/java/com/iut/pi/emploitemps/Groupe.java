package com.iut.pi.emploitemps;

/**
 * Created by asus on 29/03/2016.
 */
public class Groupe {
    private String codeGroupe;
    private String alias;
    private String couleur;
    private String nom;
    private String identifiant;

    public Groupe(){

    }

    public String getCodeGroupe(){
        return codeGroupe;
    }

    public void setCodeGroupe(String id){
        this.codeGroupe = id;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getCouleur() {
        return couleur;
    }

    public void setCouleur(String couleur) {
        this.couleur = couleur;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getIdentifiant() {
        return identifiant;
    }

    public void setIdentifiant(String identifiant) {
        this.identifiant = identifiant;
    }
}
