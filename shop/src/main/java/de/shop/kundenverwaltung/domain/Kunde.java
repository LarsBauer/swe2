package de.shop.kundenverwaltung.domain;


import static de.shop.util.Constants.KEINE_ID;
import static de.shop.util.Constants.MIN_ID;
import static java.util.logging.Level.FINER;
import static javax.persistence.CascadeType.PERSIST;
import static javax.persistence.CascadeType.REMOVE;
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
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.PostLoad;
import javax.persistence.PostPersist;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.Transient;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.ScriptAssert;

import de.shop.bestellverwaltung.domain.Bestellung;
import de.shop.util.IdGroup;


/**
 * The persistent class for the kunde database table.
 * 
 */
@Entity
@Table(name = "kunde")
	@NamedQueries({
		@NamedQuery(name  = Kunde.FIND_KUNDEN,
                	query = "SELECT k"
                			+ " FROM   Kunde k"),
        @NamedQuery(name  = Kunde.FIND_KUNDEN_ORDER_BY_ID,
            		query = "SELECT   k"
            		        + " FROM  Kunde k"
            		        + " ORDER BY k.id"),
        @NamedQuery(name  = Kunde.FIND_KUNDEN_BY_NACHNAME,
                    query = "SELECT k"
                    		+ " FROM   Kunde k"
            	       		+ " WHERE  UPPER(k.name) = UPPER(:" + Kunde.PARAM_KUNDE_NACHNAME + ")"),
        @NamedQuery(name  = Kunde.FIND_KUNDE_BY_EMAIL,
                    query = "SELECT DISTINCT k"
        		            + " FROM   Kunde k"
        		            + " WHERE  k.email = :" + Kunde.PARAM_KUNDE_EMAIL),
        @NamedQuery(name  = Kunde.FIND_KUNDEN_FETCH_BESTELLUNGEN,
        			query = "SELECT  DISTINCT k"
        					+ " FROM Kunde k LEFT JOIN FETCH k.bestellungen"),
        @NamedQuery(name  = Kunde.FIND_KUNDEN_BY_NACHNAME_FETCH_BESTELLUNGEN,
                    query = "SELECT DISTINCT k"
        		            + " FROM   Kunde k LEFT JOIN FETCH k.bestellungen"
        		            + " WHERE  UPPER(k.name) = UPPER(:" + Kunde.PARAM_KUNDE_NACHNAME + ")"),
        @NamedQuery(name  = Kunde.FIND_KUNDE_BY_ID_FETCH_BESTELLUNGEN,
        		    query = "SELECT DISTINCT k"
        			         + " FROM   Kunde k LEFT JOIN FETCH k.bestellungen"
        			         + " WHERE  k.id = :" + Kunde.PARAM_KUNDE_ID),	            
        @NamedQuery(name  = Kunde.FIND_KUNDEN_BY_PLZ,
                    query = "SELECT k"
                    		+ " FROM  Kunde k"
                    		+ " WHERE k.adresse.plz = :" + Kunde.PARAM_KUNDE_ADRESSE_PLZ)
	})
                    		
@ScriptAssert(lang = "javascript",
			script = "(_this.password == null && _this.passwordWdh == null)"
						+ "|| (_this.password != null && _this.password.equals(_this.passwordWdh))",
			message = "{kundenverwaltung.kunde.password.notEqual}",
			groups = PasswordGroup.class)

@XmlRootElement
public class Kunde implements Serializable {
	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());
	
	private static final String NAME_PATTERN = "[A-Z\u00C4\u00D6\u00DC][a-z\u00E4\u00F6\u00FC\u00DF]+";
	public static final int NACHNAME_LENGTH_MIN = 2;
	public static final int NACHNAME_LENGTH_MAX = 32;
	public static final int VORNAME_LENGTH_MAX = 32;
	public static final int EMAIL_LENGTH_MAX = 128;
	public static final int DETAILS_LENGTH_MAX = 128 * 1024;
	public static final int PASSWORD_LENGTH_MAX = 256;
	
	private static final String PREFIX = "Kunde.";
	public static final String FIND_KUNDEN = PREFIX + "findKunden";
	public static final String FIND_KUNDEN_ORDER_BY_ID = PREFIX + "findKundenOrderById";
	public static final String FIND_KUNDEN_FETCH_BESTELLUNGEN = PREFIX + "findKundenFetchBestellungen";
	public static final String FIND_KUNDEN_BY_NACHNAME_FETCH_BESTELLUNGEN =
            PREFIX + "findKundenByNachnameFetchBestellungen";
	public static final String FIND_KUNDE_BY_ID_FETCH_BESTELLUNGEN =
            PREFIX + "findKundeByIdFetchBestellungen";
	public static final String FIND_KUNDEN_BY_NACHNAME = PREFIX + "findKundenByNachname";
	public static final String FIND_KUNDE_BY_EMAIL = PREFIX + "findKundeByEmail";
	public static final String FIND_KUNDEN_BY_PLZ = PREFIX + "findKundenByPlz";
	
	public static final String PARAM_KUNDE_ID = "kundeId";
	public static final String PARAM_KUNDE_NACHNAME = "name";
	public static final String PARAM_KUNDE_ADRESSE_PLZ = "plz";
	public static final String PARAM_KUNDE_EMAIL = "email";

	@Id
	@GeneratedValue()
	@Column(name = "k_id", unique = true, nullable = false, updatable = false)
	@Min(value = MIN_ID, message = "{kundenverwaltung.kunde.id.min}", groups = IdGroup.class)
	@XmlAttribute
	private Long id = KEINE_ID;

	@Column(nullable = false)
	@Temporal(TIMESTAMP)
	@XmlTransient
	private Date aktualisiert;

	@Column(unique = true, nullable = false)
	@Email(message = "{kundenverwaltung.kunde.email.pattern}")
	//@XmlTransient
	private String email;

	@Column(nullable = false)
	@Temporal(TIMESTAMP)
	@XmlTransient
	private Date erzeugt;

	@Column(length = 1)
	@XmlElement
	private String geschlecht;

	@Column(nullable = false)
	@NotNull(message = "{kundenverwaltung.kunde.nachname.notNull}")
	@Size(min = NACHNAME_LENGTH_MIN, max = NACHNAME_LENGTH_MAX,
	      message = "{kundenverwaltung.kunde.nachname.length}")
	@Pattern(regexp = NAME_PATTERN, message = "{kundenverwaltung.kunde.nachname.pattern}")
	@XmlElement(required = true)
	private String name;

	private boolean newsletter;

	@Column(length = PASSWORD_LENGTH_MAX, nullable = false)
	private String passwort;
	
	@Transient
	private String passwortWdh;

	@Column(nullable = false)
	@Size(max = VORNAME_LENGTH_MAX, message = "{kundenverwaltung.kunde.vorname.length}")
	private String vorname;
	
	@OneToMany
	@JoinColumn(name = "kunde_fk", nullable = false)
	@XmlTransient
	private List<Bestellung> bestellungen;
	
	@Transient
	@XmlElement(name = "bestellungen")
	private URI bestellungenUri;
	
	@OneToOne(cascade = { PERSIST, REMOVE }, mappedBy = "kunde")
	@Valid
	@NotNull(message = "{kundenverwaltung.kunde.adresse.notNull}")
	@XmlElement(required = true)
	private Adresse adresse;
	
	@PrePersist
	protected void prePersist() {
		erzeugt = new Date();
		aktualisiert = new Date();
	}
	
	@PostPersist
	protected void postPersist() {
		LOGGER.log(FINER, "Neuer Kunde mit ID={0}", id);
	}
	
	@PreUpdate
	protected void preUpdate() {
		aktualisiert = new Date();
	}
	
	@PostLoad
	protected void postLoad() {
		passwortWdh = passwort;
	}
	

	public Kunde() {
		super();
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

	public String getEmail() {
		return this.email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Date getErzeugt() {
		return (Date) this.erzeugt.clone();
	}

	public void setErzeugt(Date erzeugt) {
		this.erzeugt = (Date) erzeugt.clone();
	}

	public String getGeschlecht() {
		return this.geschlecht;
	}

	public void setGeschlecht(String geschlecht) {
		this.geschlecht = geschlecht;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean getNewsletter() {
		return this.newsletter;
	}

	public void setNewsletter(boolean newsletter) {
		this.newsletter = newsletter;
	}

	public String getPasswort() {
		return this.passwort;
	}

	public void setPasswort(String passwort) {
		this.passwort = passwort;
	}
	
	public String getPasswortWdh() {
		return passwortWdh;
	}

	public void setPasswortWdh(String passwortWdh) {
		this.passwortWdh = passwortWdh;
	}


	public String getVorname() {
		return this.vorname;
	}

	public void setVorname(String vorname) {
		this.vorname = vorname;
	}
	
	public Adresse getAdresse() {
		return adresse;
	}
	public void setAdresse(Adresse adresse) {
		this.adresse = adresse;
	}
	
	public List<Bestellung> getBestellungen() {
		if (bestellungen == null) {
			return null;
		}
		
		return Collections.unmodifiableList(bestellungen);
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
	
	public Kunde addBestellung(Bestellung bestellung) {
		if (bestellungen == null) {
			bestellungen = new ArrayList<>();
		}
		bestellungen.add(bestellung);
		return this;
	}
	
	public URI getBestellungenUri() {
		return bestellungenUri;
	}
	public void setBestellungenUri(URI bestellungenUri) {
		this.bestellungenUri = bestellungenUri;
	}
	
	@Override
	public String toString() {
		return "Kunde [id=" + id
			   + ", nachname=" + name + ", vorname=" + vorname
			   + ", geschlecht=" + geschlecht + ", email=" + email 
			   + ", newsletter=" + newsletter + ", passwort=" + passwort + ", passwortwdh =" + passwortWdh
			   + ", erzeugt=" + erzeugt + ", aktualisiert=" + aktualisiert + "]";
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((email == null) ? 0 : email.hashCode());
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
		final Kunde other = (Kunde) obj;
		
		if (email == null) {
			if (other.email != null) {
				return false;
			}
		}
		else if (!email.equals(other.email)) {
			return false;
		}
		
		return true;
	}

}