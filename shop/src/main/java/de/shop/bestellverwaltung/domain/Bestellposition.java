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
	
	@Override
	public String toString() {
		final Long artikelId = artikel == null ? null : artikel.getId();
		return "Bestellposition [id=" + id + ", artikel=" + artikelId
			   + ", anzahl=" + anzahl + ", erzeugt=" + erzeugt +
			   ", aktualisiert=" + aktualisiert + "]";
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + anzahl;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((artikel == null) ? 0 : artikel.hashCode());
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		Bestellposition other = (Bestellposition) obj;
		
		// Bei persistenten Bestellpositionen koennen zu verschiedenen Bestellungen gehoeren
		// und deshalb den gleichen Artikel (s.u.) referenzieren, deshalb wird Id hier beruecksichtigt
		if (id == null) {
			if (other.id != null) {
				return false;
			}
		}
		else if (!id.equals(other.id)) {
			return false;
		}

		// Wenn eine neue Bestellung angelegt wird, dann wird jeder zu bestellende Artikel
		// genau 1x referenziert (nicht zu verwechseln mit der "anzahl")
		if (artikel == null) {
			if (other.artikel != null) {
				return false;
			}
		}
		else if (!artikel.equals(other.artikel)) {
			return false;
		}
		
		return true;
	}

}