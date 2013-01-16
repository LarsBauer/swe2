package de.shop.bestellverwaltung.domain;

import static java.util.logging.Level.FINER;
import static javax.persistence.CascadeType.MERGE;
import static javax.persistence.CascadeType.PERSIST;
import static javax.persistence.TemporalType.TIMESTAMP;

import java.io.Serializable;
import java.lang.invoke.MethodHandles;
import java.net.URI;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

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
import javax.validation.Valid;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

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
@XmlRootElement
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
	@XmlAttribute
	private Long id;
	
	@Column(nullable = false)
	@Temporal(TIMESTAMP)
	@XmlTransient
	private Date aktualisiert;

	@Column(nullable = false)
	@Temporal(TIMESTAMP)
	@XmlTransient
	private Date erzeugt;

	@Column(nullable = false)
	@XmlElement
	private String versandart;

	private double versandkosten;
	
	@ManyToMany(mappedBy = "versand", cascade = { PERSIST, MERGE })
	@NotEmpty(message = "{bestellverwaltung.lieferung.bestellungen.notEmpty}", groups = PreExistingGroup.class)
	@Valid
	@XmlTransient
	private List<Bestellung> bestellungen;
	
	@Transient
	@XmlElementWrapper(name = "bestellungen", required = true)
	@XmlElement(name = "bestellung", required = true)
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
		return "Lieferung [id=" + id + ", versandart=" + versandart + ", versandkosten=" + versandkosten
				+ ", erzeugt=" + erzeugt 
				+ ", aktualisiert=" + aktualisiert + ']';
	}

}