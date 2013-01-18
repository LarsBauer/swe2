package de.shop.bestellverwaltung.domain;


import static de.shop.util.Constants.KEINE_ID;
import static java.util.logging.Level.FINER;

import java.io.Serializable;
import java.lang.invoke.MethodHandles;
import java.net.URI;
import java.util.logging.Logger;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PostPersist;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import de.shop.artikelverwaltung.domain.Artikel;


/**
 * The persistent class for the bestellposition database table.
 * 
 */
@Entity
@Table(name = "bestellposition")
@XmlRootElement
public class Bestellposition implements Serializable {
	private static final long serialVersionUID = 1L;
	private static final int ANZAHL_MIN = 1;
	private static final Logger LOGGER = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());

	@Id
	@GeneratedValue()
	@Column(name = "bp_id", unique = true, nullable = false, updatable = false)
	@XmlAttribute 
	private Long id = KEINE_ID;

	@Column(name = "anzahl", nullable = false)
	@Min(value = ANZAHL_MIN, message = "{bestellverwaltung.bestellposition.anzahl.min}")
	@XmlElement
	private short anzahl;

	@ManyToOne(optional = false)
	@JoinColumn(name = "artikel_fk", nullable = false)
	@NotNull(message = "{bestellverwaltung.bestellposition.artikel.notNull}")
	@XmlTransient
	private Artikel artikel;
	
	@Transient
	@XmlElement(name = "artikel", required = true)
	private URI artikelUri;


	public Bestellposition() {
		super();
	}
	
	public Bestellposition(Artikel artikel) {
		super();
		this.artikel = artikel;
		this.anzahl = 1;
	}
	
	public Bestellposition(Artikel artikel, short anzahl) {
		super();
		this.artikel = artikel;
		this.anzahl = anzahl;
	}
	
	@PostPersist
	private void postPersist() {
		LOGGER.log(FINER, "Neue Bestellposition mit ID={0}", id);
	}

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
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
	
	public URI getArtikelUri() {
		return artikelUri;
	}
	
	public void setArtikelUri(URI artikelUri) {
		this.artikelUri = artikelUri;
	}
	
	@Override
	public String toString() {
		final Long artikelId = artikel == null ? null : artikel.getId();
		return "Bestellposition [id=" + id + ", artikel=" + artikelId
			   + ", anzahl=" + anzahl + "]";
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