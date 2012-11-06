package de.shop.kundenverwaltung.domain;

import java.io.Serializable;
import javax.persistence.*;
import java.util.Date;


/**
 * The persistent class for the kreditkarte database table.
 * 
 */
@Entity
public class Kreditkarte implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="kr_id", unique=true, nullable=false, updatable=false)
	private String id;

	@Column(nullable=false)
	private Date aktualisiert;

	@Column(nullable=false)
	private String anbieter;

	@Column(nullable=false)
	private Date erzeugt;

	@Column(nullable=false)
	private String gueltigbis;

	@Column(length=16, nullable=false)
	private String kreditkartennr;

	@ManyToOne
	@JoinColumn(name="kunde_fk", nullable=false)
	private Kunde kunde;

	@Column(length=3, nullable=false)
	private String sicherheitscode;

	public Kreditkarte() {
		super();
	}

	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Date getAktualisiert() {
		return (Date)this.aktualisiert.clone();
	}

	public void setAktualisiert(Date aktualisiert) {
		this.aktualisiert = (Date)aktualisiert.clone();
	}

	public String getAnbieter() {
		return this.anbieter;
	}

	public void setAnbieter(String anbieter) {
		this.anbieter = anbieter;
	}

	public Date getErzeugt() {
		return (Date)this.erzeugt.clone();
	}

	public void setErzeugt(Date erzeugt) {
		this.erzeugt = (Date)erzeugt.clone();
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

	public Kunde getKunde() {
		return this.kunde;
	}

	public void setKunde(Kunde kunde) {
		this.kunde = kunde;
	}

	public String getSicherheitscode() {
		return this.sicherheitscode;
	}

	public void setSicherheitscode(String sicherheitscode) {
		this.sicherheitscode = sicherheitscode;
	}

}