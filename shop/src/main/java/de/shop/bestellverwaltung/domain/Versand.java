package de.shop.bestellverwaltung.domain;

import java.io.Serializable;
import javax.persistence.*;

import java.util.Date;


/**
 * The persistent class for the versand database table.
 * 
 */
@Entity
public class Versand implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="v_id", unique=true, nullable=false, updatable=false)
	private Long id;
	
	@Column(nullable=false)
	private Date aktualisiert;

	@Column(nullable=false)
	private Date erzeugt;

	@Column(nullable=false)
	private String versandart;

	private double versandkosten;

	public Versand() {
		super();
	}

	public Date getAktualisiert() {
		return (Date)this.aktualisiert.clone();
	}

	public void setAktualisiert(Date aktualisiert) {
		this.aktualisiert = (Date)aktualisiert.clone();
	}

	public Date getErzeugt() {
		return (Date)this.erzeugt.clone();
	}

	public void setErzeugt(Date erzeugt) {
		this.erzeugt = (Date)erzeugt.clone();
	}

	public Long getId() {
		return this.id;
	}

	public void setVId(Long id) {
		this.id = id;
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
	
	@Override
	public String toString() {
		return "Lieferung [id=" + id + ", versandart=" + versandart + ", versandkosten=" + versandkosten
				+ ", erzeugt=" + erzeugt 
				+ ", aktualisiert=" + aktualisiert + ']';
	}

}