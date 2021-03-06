package de.shop.bestellverwaltung.domain;

import static de.shop.util.Constants.KEINE_ID;
import static javax.persistence.CascadeType.PERSIST;
import static javax.persistence.TemporalType.TIMESTAMP;
import static de.shop.util.Constants.ERSTE_VERSION;

import java.io.Serializable;
import java.lang.invoke.MethodHandles;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OrderBy;
import javax.persistence.PostPersist;
import javax.persistence.PostUpdate;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.Transient;
import javax.persistence.Version;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.hibernate.validator.constraints.NotEmpty;
import org.jboss.logging.Logger;


/**
 * The persistent class for the lieferung database table.
 * 
 */
@Entity
@Table(name = "lieferung")
@NamedQueries({
	@NamedQuery(name  = Lieferung.FIND_LIEFERUNGEN_BY_LIEFERNR_FETCH_BESTELLUNGEN,
                query = "SELECT l"
                	    + " FROM Lieferung l LEFT JOIN FETCH l.bestellungen"
			            + " WHERE l.liefernr LIKE :" + Lieferung.PARAM_LIEFER_NR)
})
public class Lieferung implements Serializable {
	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = Logger.getLogger(MethodHandles.lookup().lookupClass());
	
	private static final int LIEFERNR_LENGTH_MAX = 12;
	 
	private static final String PREFIX = "Lieferung.";
	public static final String FIND_LIEFERUNGEN_BY_LIEFERNR_FETCH_BESTELLUNGEN =
		                       PREFIX + "findLieferungenByLiefernrFetchBestellungen";
	public static final String PARAM_LIEFER_NR = "liefernr";

	@Id
	@GeneratedValue
	@Column(name = "id", nullable = false, updatable = false)
	private Long id = KEINE_ID;
	
	@Version
	@Basic(optional = false)
	private int version = ERSTE_VERSION;
	
	@Column(name = "liefernr", length = LIEFERNR_LENGTH_MAX, nullable = false, unique = true)
	@NotNull(message = "{bestellverwaltung.lieferung.lieferNr.notNull}")
	@Size(max = LIEFERNR_LENGTH_MAX, message = "{bestellverwaltung.lieferung.lieferNr.length}")
	private String liefernr;

	@Column(nullable = false)
	private String versandart;

	private double versandkosten;

	@ManyToMany(mappedBy = "lieferungen", cascade = PERSIST)
	@OrderBy("id ASC")
	@NotEmpty(message = "{bestellverwaltung.lieferung.bestellungen.notEmpty}")
	@Valid
	@JsonIgnore
	private Set<Bestellung> bestellungen;
	
	@Transient
	private List<URI> bestellungenUris;

	@Column(nullable = false)
	@Temporal(TIMESTAMP)
	@JsonIgnore
	private Date erzeugt;
	
	@Column(nullable = false)
	@Temporal(TIMESTAMP)
	@JsonIgnore
	private Date aktualisiert;

	
	public Lieferung() {
		super();
	}
	
	public Lieferung(Set<Bestellung> bestellungen) {
		super();
		this.bestellungen = bestellungen;
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
	
	@PostPersist
	private void postPersist() {
		LOGGER.debugf("Neue Lieferung mit ID=%d", id);
	}
	
	@PostUpdate
	private void postUpdate() {
		LOGGER.debugf("Lieferung mit ID=%d aktualisiert: version=%d", id, version);
	}

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	public int getVersion() {
		return version;
	}
	
	public void setVersion(int version) {
		this.version = version;
	}
	
	public String getLieferNr() {
		return liefernr;
	}
	public void setLieferNr(String liefernr) {
		this.liefernr = liefernr;
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
	
	public Set<Bestellung> getBestellungen() {
		return bestellungen == null ? null : Collections.unmodifiableSet(bestellungen);
	}
	
	public void setBestellungen(Set<Bestellung> bestellungen) {
		if (this.bestellungen == null) {
			this.bestellungen = bestellungen;
			return;
		}
		
		// Wiederverwendung der vorhandenen Collection
		this.bestellungen.clear();
		if (bestellungen != null) {
			this.bestellungen.addAll(bestellungen);
		}
	}
	
	public void addBestellung(Bestellung bestellung) {
		if (bestellungen == null) {
			bestellungen = new HashSet<>();
		}
		bestellungen.add(bestellung);
	}
	
	public List<Bestellung> getBestellungenAsList() {
		return bestellungen == null ? null : new ArrayList<>(bestellungen);
	}
	
	public void setBestellungenAsList(List<Bestellung> bestellungen) {
		this.bestellungen = bestellungen == null ? null : new HashSet<>(bestellungen);
	}
	
	public List<URI> getBestellungenUris() {
		return bestellungenUris;
	}
	public void setBestellungenUris(List<URI> bestellungenUris) {
		this.bestellungenUris = bestellungenUris;
	}

	public Date getErzeugt() {
		return erzeugt == null ? null : (Date) erzeugt.clone();
	}

	public void setErzeugt(Date erzeugt) {
		this.erzeugt = erzeugt == null ? null : (Date) erzeugt.clone();
	}

	public Date getAktualisiert() {
		return aktualisiert == null ? null : (Date) aktualisiert.clone();
	}

	public void setAktualisiert(Date aktualisiert) {
		this.aktualisiert = aktualisiert == null ? null : (Date) aktualisiert.clone();
	}


	@Override
	public String toString() {
		return "Lieferung [id=" + id + ", version=" + version 
				+ ", liefernr=" + liefernr + ", versandart=" + versandart
				+ ", versandkosten=" + versandkosten 
				+ ", aktualisiert=" + aktualisiert + ", erzeugt=" + erzeugt + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((liefernr == null) ? 0 : liefernr.hashCode());
		result = prime * result + version;
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
		final Lieferung other = (Lieferung) obj;
		if (liefernr == null) {
			if (other.liefernr != null) {
				return false;
			}
		}
		else if (!liefernr.equals(other.liefernr)) {
			return false;
		}
		if (version != other.version) {
			return false;
		}
		return true;
	}

}
