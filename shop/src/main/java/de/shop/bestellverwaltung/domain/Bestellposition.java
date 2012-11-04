//package de.shop.temp;
package de.shop.bestellverwaltung.domain;


import java.io.Serializable;
import javax.persistence.*;
import java.math.BigInteger;


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

	private short anzahl;

	@Column(name="artikel_fk")
	private BigInteger artikelFk;

	@Column(name="bestellung_fk")
	private BigInteger bestellungFk;

	public Bestellposition() {
	}

	public String getBpId() {
		return this.bpId;
	}

	public void setBpId(String bpId) {
		this.bpId = bpId;
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

}