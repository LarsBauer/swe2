package de.shop.bestellverwaltung.domain;

import java.io.Serializable;
import javax.persistence.*;

import de.shop.artikelverwaltung.domain.Artikel;

import java.util.Date;


/**
 * The persistent class for the bestellposition database table.
 * 
 */
@Entity
public class Bestellposition implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="bp_id", unique=true, nullable=false, updatable=false)
	private Long id;

	@Column(nullable=false)
	private Date aktualisiert;

	@Column(nullable=false)
	private short anzahl;

	@ManyToOne(optional=false)
	@JoinColumn(name="artikel_fk", nullable=false)
	private Artikel artikel;

	@OneToOne(optional=false)
	@JoinColumn(name="bestellung_fk", updatable=false, insertable=false)
	private Bestellung bestellung;

	@Column(nullable=false)
	private Date erzeugt;

	public Bestellposition() {
		super();
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

	public short getAnzahl() {
		return this.anzahl;
	}

	public void setAnzahl(short anzahl) {
		this.anzahl = anzahl;
	}

	public Artikel getArtikel() {
		return this.artikel;
	}

	public void setArtikel(Artikel artikel) {
		this.artikel = artikel;
	}

	public Bestellung getBestellung() {
		return this.bestellung;
	}

	public void setBestellung(Bestellung bestellung) {
		this.bestellung = bestellung;
	}

	public Date getErzeugt() {
		return (Date)this.erzeugt.clone();
	}

	public void setErzeugt(Date erzeugt) {
		this.erzeugt = (Date)erzeugt.clone();
	}

}