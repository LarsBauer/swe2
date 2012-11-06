package de.shop.bestellverwaltung.domain;

import java.io.Serializable;
import javax.persistence.*;
import java.math.BigInteger;
import java.sql.Timestamp;


/**
 * The persistent class for the bestellposition database table.
 * 
 */
@Entity
public class Bestellposition implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="bp_id")
	private String bpId;

	private Timestamp aktualisiert;

	private short anzahl;

	@Column(name="artikel_fk")
	private BigInteger artikelFk;

	@Column(name="bestellung_fk")
	private BigInteger bestellungFk;

	private Timestamp erzeugt;

	public Bestellposition() {
	}

	public String getBpId() {
		return this.bpId;
	}

	public void setBpId(String bpId) {
		this.bpId = bpId;
	}

	public Timestamp getAktualisiert() {
		return this.aktualisiert;
	}

	public void setAktualisiert(Timestamp aktualisiert) {
		this.aktualisiert = aktualisiert;
	}

	public short getAnzahl() {
		return this.anzahl;
	}

	public void setAnzahl(short anzahl) {
		this.anzahl = anzahl;
	}

	public BigInteger getArtikelFk() {
		return this.artikelFk;
	}

	public void setArtikelFk(BigInteger artikelFk) {
		this.artikelFk = artikelFk;
	}

	public BigInteger getBestellungFk() {
		return this.bestellungFk;
	}

	public void setBestellungFk(BigInteger bestellungFk) {
		this.bestellungFk = bestellungFk;
	}

	public Timestamp getErzeugt() {
		return this.erzeugt;
	}

	public void setErzeugt(Timestamp erzeugt) {
		this.erzeugt = erzeugt;
	}

}