package de.shop.kundenverwaltung.domain;


import static de.shop.util.Constants.KEINE_ID;
import static javax.persistence.TemporalType.TIMESTAMP;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Temporal;

/**
 * The persistent class for the adresse database table.
 * 
 */
@Entity
public class Adresse implements Serializable {
	private static final long serialVersionUID = 1L;
	
	public static final int PLZ_LENGTH_MAX = 5;

	@Id
	@GeneratedValue()
	@Column(name="ad_id", unique=true, nullable=false, updatable=false)
	private Long id=KEINE_ID;

	@Column(nullable=false)
	@Temporal(TIMESTAMP)
	private Date aktualisiert;

	@Column(nullable=false)
	@Temporal(TIMESTAMP)
	private Date erzeugt;

	@Column(nullable=false)
	private String hausnummer;

	@OneToOne
	@JoinColumn(name="kunde_fk", nullable=false)
	private Kunde kunde;

	@Column(nullable=false)
	private String ort;

	@Column(length=PLZ_LENGTH_MAX, nullable=false)
	private String plz;

	@Column(nullable=false)
	private String strasse;

	public Adresse() {
		super();
	}
	
	@PrePersist
	private void prePersist() {
		erzeugt = new Date();
		aktualisiert = new Date();
	}
	
	@PreUpdate
	private void preUpdate() {
		aktualisiert = new Date();
	}

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
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

	public String getHausnummer() {
		return this.hausnummer;
	}

	public void setHausnummer(String hausnummer) {
		this.hausnummer = hausnummer;
	}

	public Kunde getKunde() {
		return this.kunde;
	}

	public void setKunde(Kunde kunde) {
		this.kunde = kunde;
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
	
	@Override
	public String toString() {
		return "Adresse [id=" + id + ", plz=" + plz + ", ort=" + ort + ", strasse=" + strasse + ", hausnummer=" + hausnummer
		       + ", erzeugt=" + erzeugt + ", aktualisiert=" + aktualisiert + ']';
	}

}