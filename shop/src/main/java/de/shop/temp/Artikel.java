package de.shop.temp;

import java.io.Serializable;
import javax.persistence.*;
import java.sql.Timestamp;


/**
 * The persistent class for the artikel database table.
 * 
 */
@Entity
public class Artikel implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="a_id")
	private String aId;

	private Timestamp aktualisiert;

	private Timestamp erzeugt;

	private String groesse;

	private String name;

	private double preis;

	public Artikel() {
	}

	public String getAId() {
		return this.aId;
	}

	public void setAId(String aId) {
		this.aId = aId;
	}

	public Timestamp getAktualisiert() {
		return this.aktualisiert;
	}

	public void setAktualisiert(Timestamp aktualisiert) {
		this.aktualisiert = aktualisiert;
	}

	public Timestamp getErzeugt() {
		return this.erzeugt;
	}

	public void setErzeugt(Timestamp erzeugt) {
		this.erzeugt = erzeugt;
	}

	public String getGroesse() {
		return this.groesse;
	}

	public void setGroesse(String groesse) {
		this.groesse = groesse;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public double getPreis() {
		return this.preis;
	}

	public void setPreis(double preis) {
		this.preis = preis;
	}

}