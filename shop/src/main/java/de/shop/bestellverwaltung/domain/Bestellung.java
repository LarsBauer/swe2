package de.shop.bestellverwaltung.domain;


import static de.shop.util.Constants.KEINE_ID;
import static de.shop.util.Constants.MIN_ID;
import static java.util.logging.Level.FINER;
import static javax.persistence.CascadeType.PERSIST;
import static javax.persistence.CascadeType.REMOVE;
import static javax.persistence.FetchType.EAGER;
import static javax.persistence.TemporalType.TIMESTAMP;

import java.io.Serializable;
import java.lang.invoke.MethodHandles;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.PostPersist;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.Transient;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.hibernate.validator.constraints.NotEmpty;

import de.shop.kundenverwaltung.domain.Kunde;
import de.shop.util.IdGroup;
import de.shop.util.PreExistingGroup;

/**
 * The persistent class for the bestellung database table.
 * 
 */
@Entity
	@Table(name = "bestellung")
	@NamedQueries({
		@NamedQuery(name  = Bestellung.FIND_BESTELLUNGEN_BY_KUNDE,
					query = "SELECT b"
			            + " FROM   Bestellung b"
						+ " WHERE  b.kunde.id = :" + Bestellung.PARAM_KUNDEID),
   		@NamedQuery(name  = Bestellung.FIND_KUNDE_BY_ID,
 			    	query = "SELECT b.kunde"
                        + " FROM   Bestellung b"
  			            + " WHERE  b.id = :" + Bestellung.PARAM_ID),
	   	@NamedQuery(name  = Bestellung.FIND_BESTELLUNG_BY_ID_FETCH_LIEFERUNGEN,
	   				query = "SELECT DISTINCT b"
	   					+ " FROM   Bestellung b LEFT JOIN FETCH b.versand"
	   					+ " WHERE  b.id = :" + Bestellung.PARAM_ID),
})
@XmlRootElement
public class Bestellung implements Serializable {
	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());
	
	private static final String PREFIX = "Bestellung.";
	public static final String FIND_BESTELLUNGEN_BY_KUNDE = PREFIX + "findBestellungenByKunde";
	public static final String FIND_BESTELLUNG_BY_ID_FETCH_LIEFERUNGEN =
            PREFIX + "findBestellungenByIdFetchLieferungen";
	public static final String FIND_KUNDE_BY_ID = PREFIX + "findBestellungKundeById";
	
	public static final String PARAM_KUNDEID = "kundeId";
	public static final String PARAM_ID = "id";

	
	@Id
	@GeneratedValue()
	@Column(name = "b_id", unique = true, nullable = false, updatable = false)
	@Min(value = MIN_ID, message = "{bestellverwaltung.bestellung.id.min}", groups = IdGroup.class)
	@XmlAttribute
	private Long id = KEINE_ID;

	@Column(nullable = false)
	@Temporal(TIMESTAMP)
	@XmlTransient
	private Date aktualisiert;

	@Column(nullable = false)
	@Temporal(TIMESTAMP)
	private Date erzeugt;
	
	@Transient
	@XmlElement(name = "kunde", required = true)
	private URI kundeUri;

	@ManyToOne(optional = false)
	@JoinColumn(name = "kunde_fk", nullable = false, insertable = false, updatable = false)
	@NotNull(message = "{bestellverwaltung.bestellung.kunde.notNull}", groups = PreExistingGroup.class)
	@XmlTransient
	private Kunde kunde;
	
	@OneToMany(fetch = EAGER, cascade = { PERSIST, REMOVE })
	@JoinColumn(name = "bestellung_fk", nullable = false)
	@NotEmpty(message = "{bestellverwaltung.bestellung.bestellpositionen.notEmpty}")
	@Valid
	@XmlElementWrapper(name = "bestellposition", required = true)
	@XmlElement(name = "bestellposition", required = true)
	private List<Bestellposition> bestellpositionen;
	
	@ManyToMany
	@JoinTable(name = "bestellung_versand",
			   joinColumns = @JoinColumn(name = "bestellung_fk"),
			                 inverseJoinColumns = @JoinColumn(name = "versand_fk"))
	@XmlTransient
	private List<Versand> versand;
	
	@Transient
	@XmlElement(name = "versand")
	private URI versandUri;

	@Column(nullable = false)
	private String status;

	public Bestellung() {
		super();
	}
	
	public Bestellung(List<Bestellposition> bestellpositionen) {
		super();
		this.bestellpositionen = bestellpositionen;
	}
	
	@PrePersist
	private void prePersist() {
		erzeugt = new Date();
		aktualisiert = new Date();
	}
	
	@PostPersist
	private void postPersist() {
		LOGGER.log(FINER, "Neue Bestellung mit ID={0}", id);
	}
	
	@PreUpdate
	private void preUpdate() {
		aktualisiert = new Date();
	}

	public URI getKundeUri() {
		return kundeUri;
	}
	
	public void setKundeUri(URI kundeUri) {
		this.kundeUri = kundeUri;
	}
	
	public URI getVersandUri() {
		return versandUri;
	}
	public void setVersandUri(URI versandUri) {
		this.versandUri = versandUri;
	}
	
	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	public List<Bestellposition> getBestellpositionen() {
		return Collections.unmodifiableList(bestellpositionen);
	}
	
	public void setBestellpositionen(List<Bestellposition> bestellpositionen) {
		if (this.bestellpositionen == null) {
			this.bestellpositionen = bestellpositionen;
			return;
		}
		
		// Wiederverwendung der vorhandenen Collection
		this.bestellpositionen.clear();
		if (bestellpositionen != null) {
			this.bestellpositionen.addAll(bestellpositionen);
		}
	}
	
	public Bestellung addBestellposition(Bestellposition bestellposition) {
		if (bestellpositionen == null) {
			bestellpositionen = new ArrayList<>();
		}
		bestellpositionen.add(bestellposition);
		return this;
	}
	
	public List<Versand> getVersand() {
		return Collections.unmodifiableList(versand);
	}
	public void setVersand(List<Versand> versand) {
		if (this.versand == null) {
			this.versand = versand;
			return;
		}
		
		// Wiederverwendung der vorhandenen Collection
		this.versand.clear();
		if (versand != null) {
			this.versand.addAll(versand);
		}
	}
	
	public void addVersand(Versand versandneu) {
		versand.add(versandneu);
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

	public Kunde getKunde() {
		return this.kunde;
	}

	public void setKunde(Kunde kunde) {
		this.kunde = kunde;
	}

	public String getStatus() {
		return this.status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	
	@Override
	public String toString() {
		final Long kundeId = kunde == null ? null : kunde.getId();
		return "Bestellung [id=" + id + ", kundeId=" + kundeId
		       + ", erzeugt=" + erzeugt
		       + ", aktualisiert=" + aktualisiert + ']';
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((kunde == null) ? 0 : kunde.hashCode());
		result = prime * result + ((erzeugt == null) ? 0 : erzeugt.hashCode());
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
		final Bestellung other = (Bestellung) obj;
		
		if (kunde == null) {
			if (other.kunde != null) {
				return false;
			}
		}
		else if (!kunde.equals(other.kunde)) {
			return false;
		}
		
		if (erzeugt == null) {
			if (other.erzeugt != null) {
				return false;
			}
		}
		else if (!erzeugt.equals(other.erzeugt)) {
			return false;
		}
		
		return true;
	}
}