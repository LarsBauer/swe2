package de.shop.bestellverwaltung.domain;

import java.io.Serializable;
import javax.persistence.*;
import java.math.BigInteger;
import java.sql.Timestamp;


/**
 * The persistent class for the bestellung database table.
 * 
 */
@Entity
public class Bestellung implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="b_id")
	private String bId;

	private Timestamp aktualisiert;

	private Timestamp erzeugt;

	@Column(name="kunde_fk")
	private BigInteger kundeFk;

	private String status;

	public Bestellung() {
	}

	public String getBId() {
		return this.bId;
	}

	public void setBId(String bId) {
		this.bId = bId;
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

	public BigInteger getKundeFk() {
		return this.kundeFk;
	}

	public void setKundeFk(BigInteger kundeFk) {
		this.kundeFk = kundeFk;
	}

	public String getStatus() {
		return this.status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

}