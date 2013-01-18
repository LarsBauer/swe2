package de.shop.artikelverwaltung.domain;

import static de.shop.util.Constants.KEINE_ID;
import static de.shop.util.Constants.MIN_ID;
import static java.util.logging.Level.FINER;
import static javax.persistence.TemporalType.TIMESTAMP;

import java.io.Serializable;
import java.lang.invoke.MethodHandles;
import java.util.Date;
import java.util.logging.Logger;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.PostPersist;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import de.shop.util.IdGroup;
/**
 * The persistent class for the artikel database table.
 * 
 */
@Entity
@Table(name = "artikel")
@NamedQueries({
	@NamedQuery(name  = Artikel.FIND_VERFUEGBARE_ARTIKEL,
        	query = "SELECT      a"
        	        + " FROM     Artikel a"
                    + " ORDER BY a.id ASC"),
                    
	@NamedQuery(name = Artikel.FIND_ARTIKEL_BY_NAME,
			query = "SELECT      a"
					+ " FROM     Artikel a"
					+ " WHERE    a.name LIKE :" + Artikel.PARAM_NAME
					+ " ORDER BY a.id ASC"),
	
	@NamedQuery(name = Artikel.FIND_ARTIKEL_MAX_PREIS,
		query = "SELECT		 a"
				+ " FROM 	Artikel a"
				+ " WHERE	a.preis < :" + Artikel.PARAM_PREIS_MAX
				+ " ORDER BY a.id ASC"),	
})
@XmlRootElement
public class Artikel implements Serializable {
	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());
	
	private static final int NAME_LENGTH_MAX = 32;
	private static final int GROESSE_LENGTH_MAX = 3;
	
	private static final String PREFIX = "Artikel.";
	public static final String FIND_VERFUEGBARE_ARTIKEL = PREFIX + "findVerfuegbareArtikel";
	public static final String FIND_ARTIKEL_BY_NAME = PREFIX + "findArtikelByName";
	public static final String FIND_ARTIKEL_MAX_PREIS = PREFIX + "findArtikelByMaxPreis";

	public static final String PARAM_NAME = "name";
	public static final String PARAM_PREIS_MAX = "preis";
	

	@Id
	@GeneratedValue()
	@Column(name = "a_id", unique = true, nullable = false, updatable = false)
	@Min(value = MIN_ID, message = "{artikelverwaltung.artikel.id.min}", groups = IdGroup.class)
	@XmlAttribute
	private Long id = KEINE_ID;

	@Column(nullable = false)
	@Temporal(TIMESTAMP)
	@XmlTransient
	private Date aktualisiert;

	@Column(nullable = false)
	@Temporal(TIMESTAMP)
	@XmlTransient
	private Date erzeugt;

	@Column(name = "groesse", length = GROESSE_LENGTH_MAX, nullable = false)
	@XmlElement(required = true)
	private String groesse;

	@Column(name = "name", length = NAME_LENGTH_MAX, nullable = false)
	@Size(max = NAME_LENGTH_MAX, message = "{artikelverwaltung.artikel.bezeichnung.length}")
	@XmlElement(required = true)
	private String name;

	@XmlElement
	private double preis;

	
	
	public Artikel() {
		super();
	}

	@PrePersist
	private void prePersist() {
		erzeugt = new Date();
		aktualisiert = new Date();
	}
	
	@PostPersist
	private void postPersist() {
		LOGGER.log(FINER, "Neuer Artikel mit ID={0}", id);
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

	public String getGroesse() {
		return this.groesse;
	}

	public void setGroesse(String groesse) {
		this.groesse = groesse;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public double getPreis() {
		return this.preis;
	}

	public void setPreis(double preis) {
		this.preis = preis;
	}
	
	@Override
	public String toString() {
		return "Artikel [id=" + id + ", name=" + name
		       + ", preis=" + preis + ", groesse=" + groesse
		       + ", erzeugt=" + erzeugt
			   + ", aktualisiert=" + aktualisiert + "]";
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((name == null) ? 0 : name.hashCode());
		long temp;
		temp = Double.doubleToLongBits(preis);
		result = prime * result + (int) (temp ^ (temp >>> 32));
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
		Artikel other = (Artikel) obj;
		
		
		if (name == null) {
			if (other.name != null) {
				return false;
			}
		}
		else if (!name.equals(other.name)) {
			return false;
		}
		
		if (Double.doubleToLongBits(preis) != Double.doubleToLongBits(other.preis)) {
			return false;
		}
		return true;
	}

}