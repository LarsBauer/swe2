package de.shop.bestellverwaltung.domain;

import static java.util.logging.Level.FINER;
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
import java.util.logging.Logger;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.PostPersist;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.Transient;
import javax.persistence.Version;
import javax.validation.Valid;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.hibernate.validator.constraints.NotEmpty;

import de.shop.util.PreExistingGroup;


/**
 * The persistent class for the versand database table.
 * 
 */
@Entity
@Table(name = "versand")
@NamedQueries({
	@NamedQuery(name  = Versand.FIND_VERSAND_BY_ID_FETCH_BESTELLUNGEN,
                query = "SELECT v"
                	    + " FROM Versand v LEFT JOIN FETCH v.bestellungen"
			            + " WHERE v.id LIKE :" + Versand.PARAM_VERSAND_ID)
})
public class Versand implements Serializable {
	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());
	
	private static final String PREFIX = "Versand.";
	public static final String FIND_VERSAND_BY_ID_FETCH_BESTELLUNGEN =
		                       PREFIX + "findVersandByIdFetchBestellungen";
	public static final String PARAM_VERSAND_ID = "id";

	@Id
	@GeneratedValue()
	@Column(name = "v_id", unique = true, nullable = false, updatable = false)
	private Long id = KEINE_ID;
	
	@Version
	@Basic(optional = false)
	private int version = ERSTE_VERSION;
	
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
	
	@ManyToMany(mappedBy = "versand", cascade = { PERSIST, MERGE })
	@NotEmpty(message = "{bestellverwaltung.lieferung.bestellungen.notEmpty}", groups = PreExistingGroup.class)
	@Valid
	@JsonIgnore
	private List<Bestellung> bestellungen;
	
	@Transient
	private List<URI> bestellungenUris;

	public Versand() {
		super();
	}
	
	@PrePersist
	protected void prePersist() {
		erzeugt = new Date();
		aktualisiert = new Date();
	}
	
	@PostPersist
	private void postPersist() {
		LOGGER.log(FINER, "Neuer Versand mit ID={0}", id);
	}
	
	@PreUpdate
	protected void preUpdate() {
		aktualisiert = new Date();
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
		return "Versand [id=" + id + ", version=" + version + ", aktualisiert="
				+ aktualisiert + ", erzeugt=" + erzeugt + ", versandart="
				+ versandart + ", versandkosten=" + versandkosten
				+ ", bestellungen=" + bestellungen + ", bestellungenUris="
				+ bestellungenUris + "]";
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
		Versand other = (Versand) obj;
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