package de.shop.temp;

import java.io.Serializable;
import javax.persistence.*;
import java.math.BigInteger;
import java.sql.Timestamp;


/**
 * The persistent class for the versand database table.
 * 
 */
@Entity
public class Versand implements Serializable {
	private static final long serialVersionUID = 1L;

	private Timestamp aktualisiert;

	private Timestamp erzeugt;

	@Column(name="v_id")
	private BigInteger vId;

	private String versandart;

	private double versandkosten;

	public Versand() {
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

	public BigInteger getVId() {
		return this.vId;
	}

	public void setVId(BigInteger vId) {
		this.vId = vId;
	}

	public String getVersandart() {
		return this.versandart;
	}

	public void setVersandart(String versandart) {
		this.versandart = versandart;
	}

	public double getVersandkosten() {
		return this.versandkosten;
	}

	public void setVersandkosten(double versandkosten) {
		this.versandkosten = versandkosten;
	}

}