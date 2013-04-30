package de.shop.artikelverwaltung.domain;

import static de.shop.util.Constants.KEINE_ID;
import static de.shop.util.Constants.ERSTE_VERSION;
import static javax.persistence.TemporalType.TIMESTAMP;

import java.io.Serializable;
import java.lang.invoke.MethodHandles;
import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.PostPersist;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.PostUpdate;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.jboss.logging.Logger;

@Entity
@Table(name = "artikel")
@NamedQueries({
	@NamedQuery(name  = Artikel.FIND_VERFUEGBARE_ARTIKEL,
        	query = "SELECT      a"
        	        + " FROM     Artikel a"
                    + " ORDER BY a.id ASC"),
                    
	@NamedQuery(name = Artikel.FIND_ARTIKEL_BY_BEZ,
			query = "SELECT      a"
					+ " FROM     Artikel a"
					+ " WHERE    a.bezeichnung LIKE :" + Artikel.PARAM_BEZEICHNUNG
					+ " ORDER BY a.id ASC"),
	
	@NamedQuery(name = Artikel.FIND_ARTIKEL_MAX_PREIS,
		query = "SELECT		 a"
				+ " FROM 	Artikel a"
				+ " WHERE	a.preis < :" + Artikel.PARAM_PREIS_MAX
				+ " ORDER BY a.id ASC")	
})
@Cacheable
public class Artikel implements Serializable {
	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = Logger.getLogger(MethodHandles.lookup().lookupClass());
	
	private static final int BEZEICHNUNG_LENGTH_MAX = 32;
	private static final int GROESSE_LENGTH_MAX = 3;
	
	private static final String PREFIX = "Artikel.";
	public static final String FIND_VERFUEGBARE_ARTIKEL = PREFIX + "findVerfuegbareArtikel";
	public static final String FIND_ARTIKEL_BY_BEZ = PREFIX + "findArtikelByBez";
	public static final String FIND_ARTIKEL_MAX_PREIS = PREFIX + "findArtikelByMaxPreis";

	public static final String PARAM_BEZEICHNUNG = "bezeichnung";
	public static final String PARAM_PREIS_MAX = "preis";
	

	@Id
	@GeneratedValue
	@Column(name = "id", nullable = false, updatable = false)
	private Long id = KEINE_ID;
	
	@Version
	@Basic(optional = false)
	private int version = ERSTE_VERSION;

	@Column(length = BEZEICHNUNG_LENGTH_MAX, nullable = false)
	@NotNull(message = "{artikelverwaltung.artikel.bezeichnung.notNull}")
	@Size(max = BEZEICHNUNG_LENGTH_MAX, message = "{artikelverwaltung.artikel.bezeichnung.length}")
	private String bezeichnung = "";

	@Column(name = "groesse", length = GROESSE_LENGTH_MAX, nullable = false)
	private String groesse;

	private double preis;

	@Column(nullable = false)
	@Temporal(TIMESTAMP)
	@JsonIgnore
	private Date erzeugt;

	@Column(nullable = false)
	@Temporal(TIMESTAMP)
	@JsonIgnore
	private Date aktualisiert;


	@PrePersist
	private void prePersist() {
		erzeugt = new Date();
		aktualisiert = new Date();
	}
	
	@PostPersist
	private void postPersist() {
		LOGGER.debugf("Neuer Artikel mit ID=%d", id);
	}
	
	@PostUpdate
	private void postUpdate() {
		LOGGER.debugf("Artikel mit ID=%s aktualisiert: version=%d", id, version);
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
	
	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public String getBezeichnung() {
		return this.bezeichnung;
	}

	public void setBezeichnung(String bez) {
		this.bezeichnung = bez;
	}

	public String getGroesse() {
		return this.groesse;
	}

	public void setGroesse(String groesse) {
		this.groesse = groesse;
	}

	public double getPreis() {
		return this.preis;
	}

	public void setPreis(double preis) {
		this.preis = preis;
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
		return "Artikel [id=" + id + ", version=" + version
				+ ", bezeichnung=" + bezeichnung + ", groesse="+ groesse 
				+ ", preis=" + preis + "]";
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((bezeichnung == null) ? 0 : bezeichnung.hashCode());
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
		final Artikel other = (Artikel) obj;
		if (bezeichnung == null) {
			if (other.bezeichnung != null) {
				return false;
			}
		}
		else if (!bezeichnung.equals(other.bezeichnung)) {
			return false;
		}
		return true;
	}

}
