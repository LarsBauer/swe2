package de.shop.temp;

import java.io.Serializable;
import javax.persistence.*;

import java.math.BigInteger;
import java.sql.Timestamp;


/**
 * The persistent class for the adresse database table.
 * 
 */
@Entity
public class Adresse implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="ad_id")
	private BigInteger adId;

	private Timestamp aktualisiert;

	private Timestamp erzeugt;

	private String hausnummer;

	@Column(name="kunde_fk")
	private BigInteger kundeFk;

	private String ort;

	private String plz;

	private String strasse;

	public Adresse() {
	}

	public BigInteger getAdId() {
		return this.adId;
	}

	public void setAdId(BigInteger adId) {
		this.adId = adId;
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

	public String getHausnummer() {
		return this.hausnummer;
	}

	public void setHausnummer(String hausnummer) {
		this.hausnummer = hausnummer;
	}

	public BigInteger getKundeFk() {
		return this.kundeFk;
	}

	public void setKundeFk(BigInteger kundeFk) {
		this.kundeFk = kundeFk;
	}

	public String getOrt() {
		return this.ort;
	}

	public void setOrt(String ort) {
		this.ort = ort;
	}

	public String getPlz() {
		return this.plz;
	}

	public void setPlz(String plz) {
		this.plz = plz;
	}

	public String getStrasse() {
		return this.strasse;
	}

	public void setStrasse(String strasse) {
		this.strasse = strasse;
	}

}