package de.shop.bestellverwaltung.domain;

import static de.shop.util.Constants.KEINE_ID;
import static javax.persistence.CascadeType.MERGE;
import static javax.persistence.CascadeType.PERSIST;
import static javax.persistence.TemporalType.TIMESTAMP;
import static de.shop.util.Constants.ERSTE_VERSION;

import java.io.Serializable;
import java.lang.invoke.MethodHandles;
import java.net.URI;
import java.util.Collections;
import java.util.Date;
import java.util.List;







import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
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

import de.shop.util.PreExistingGroup;


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
	@GeneratedValue()
	@Column(name = "l_id", unique = true, nullable = false, updatable = false)
	private Long id = KEINE_ID;
	
	@Version
	@Basic(optional = false)
	private int version = ERSTE_VERSION;
	
	@Column(name = "liefernr", length = LIEFERNR_LENGTH_MAX, nullable = false, unique = true)
	@NotNull(message = "{bestellverwaltung.lieferung.lieferNr.notNull}")
	@Size(max = LIEFERNR_LENGTH_MAX, message = "{bestellverwaltung.lieferung.lieferNr.length}")
	private String liefernr;
	
	@Column(nullable = false)
	@Temporal(TIMESTAMP)
	@JsonIgnore
	private Date aktualisiert;

	@Column(nullable = false)
	@Temporal(TIMESTAMP)
	@JsonIgnore
	private Date erzeugt;

	@Column(nullable = false)
	private String versandart;

	private double versandkosten;
	
	@ManyToMany(mappedBy = "lieferungen", cascade = { PERSIST, MERGE })
	@NotEmpty(message = "{bestellverwaltung.lieferung.bestellungen.notEmpty}", groups = PreExistingGroup.class)
	@Valid
	@JsonIgnore
	private List<Bestellung> bestellungen;
	
	@Transient
	private List<URI> bestellungenUris;

	public Lieferung() {
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
	
	@PostPersist
	private void postPersist() {
		LOGGER.debugf("Neue Lieferung mit ID=%d", id);
	}
	
	@PostUpdate
	private void postUpdate() {
		LOGGER.debugf("Lieferung mit ID=%d aktualisiert: version=%d", id, version);
	}

	public Date getAktualisiert() {
		return (Date) this.aktualisiert.clone();
	}

	public void setAktualisiert(Date aktualisiert) {
		this.aktualisiert = (Date) aktualisiert.clone();
	}

	public Date getErzeugt() {
		return (Date) this.erzeugt.clone();
	}

	public void setErzeugt(Date erzeugt) {
		this.erzeugt = (Date) erzeugt.clone();
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
	
	public List<Bestellung> getBestellungen() {
		return bestellungen == null ? null : Collections.unmodifiableList(bestellungen);
	}
	
	public void setBestellungen(List<Bestellung> bestellungen) {
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
		bestellungen.add(bestellung);
	}
	
	@Override
	public String toString() {
		return "Lieferung [id=" + id + ", version=" + version + ", liefernr="
				+ liefernr + ", aktualisiert=" + aktualisiert + ", erzeugt="
				+ erzeugt + ", versandart=" + versandart + ", versandkosten="
				+ versandkosten + ", bestellungen=" + bestellungen
				+ ", bestellungenUris=" + bestellungenUris + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((aktualisiert == null) ? 0 : aktualisiert.hashCode());
		result = prime * result
				+ ((bestellungen == null) ? 0 : bestellungen.hashCode());
		result = prime
				* result
				+ ((bestellungenUris == null) ? 0 : bestellungenUris.hashCode());
		result = prime * result + ((erzeugt == null) ? 0 : erzeugt.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result
				+ ((liefernr == null) ? 0 : liefernr.hashCode());
		result = prime * result
				+ ((versandart == null) ? 0 : versandart.hashCode());
		long temp;
		temp = Double.doubleToLongBits(versandkosten);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + version;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Lieferung other = (Lieferung) obj;
		if (aktualisiert == null) {
			if (other.aktualisiert != null)
				return false;
		} else if (!aktualisiert.equals(other.aktualisiert))
			return false;
		if (bestellungen == null) {
			if (other.bestellungen != null)
				return false;
		} else if (!bestellungen.equals(other.bestellungen))
			return false;
		if (bestellungenUris == null) {
			if (other.bestellungenUris != null)
				return false;
		} else if (!bestellungenUris.equals(other.bestellungenUris))
			return false;
		if (erzeugt == null) {
			if (other.erzeugt != null)
				return false;
		} else if (!erzeugt.equals(other.erzeugt))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (liefernr == null) {
			if (other.liefernr != null)
				return false;
		} else if (!liefernr.equals(other.liefernr))
			return false;
		if (versandart == null) {
			if (other.versandart != null)
				return false;
		} else if (!versandart.equals(other.versandart))
			return false;
		if (Double.doubleToLongBits(versandkosten) != Double
				.doubleToLongBits(other.versandkosten))
			return false;
		if (version != other.version)
			return false;
		return true;
	}

}