package de.shop.temp;

import java.io.Serializable;
import javax.persistence.*;
import java.math.BigInteger;
import java.sql.Timestamp;


/**
 * The persistent class for the kreditkarte database table.
 * 
 */
@Entity
public class Kreditkarte implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="kr_id")
	private String krId;

	private Timestamp aktualisiert;

	private String anbieter;

	private Timestamp erzeugt;

	private String gueltigbis;

	private String kreditkartennr;

	@Column(name="kunde_fk")
	private BigInteger kundeFk;

	private String sicherheitscode;

	public Kreditkarte() {
	}

	public String getKrId() {
		return this.krId;
	}

	public void setKrId(String krId) {
		this.krId = krId;
	}

	public Timestamp getAktualisiert() {
		return this.aktualisiert;
	}

	public void setAktualisiert(Timestamp aktualisiert) {
		this.aktualisiert = aktualisiert;
	}

	public String getAnbieter() {
		return this.anbieter;
	}

	public void setAnbieter(String anbieter) {
		this.anbieter = anbieter;
	}

	public Timestamp getErzeugt() {
		return this.erzeugt;
	}

	public void setErzeugt(Timestamp erzeugt) {
		this.erzeugt = erzeugt;
	}

	public String getGueltigbis() {
		return this.gueltigbis;
	}

	public void setGueltigbis(String gueltigbis) {
		this.gueltigbis = gueltigbis;
	}

	public String getKreditkartennr() {
		return this.kreditkartennr;
	}

	public void setKreditkartennr(String kreditkartennr) {
		this.kreditkartennr = kreditkartennr;
	}

	public BigInteger getKundeFk() {
		return this.kundeFk;
	}

	public void setKundeFk(BigInteger kundeFk) {
		this.kundeFk = kundeFk;
	}

	public String getSicherheitscode() {
		return this.sicherheitscode;
	}

	public void setSicherheitscode(String sicherheitscode) {
		this.sicherheitscode = sicherheitscode;
	}

}