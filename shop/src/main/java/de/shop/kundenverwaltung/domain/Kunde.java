package de.shop.kundenverwaltung.domain;


import static de.shop.util.Constants.KEINE_ID;
import static de.shop.util.Constants.MIN_ID;
import static javax.persistence.CascadeType.PERSIST;
import static javax.persistence.CascadeType.REMOVE;
import static javax.persistence.TemporalType.TIMESTAMP;
import static de.shop.util.Constants.ERSTE_VERSION;
import static javax.persistence.FetchType.EAGER;
import static javax.persistence.FetchType.LAZY;

import java.io.Serializable;
import java.lang.invoke.MethodHandles;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.persistence.UniqueConstraint;
import javax.persistence.Basic;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
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
import javax.persistence.PostUpdate;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.Transient;
import javax.persistence.Version;
import javax.validation.Valid;
import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlTransient;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonTypeInfo;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.ScriptAssert;
import org.jboss.logging.Logger;

import de.shop.auth.service.jboss.AuthService.RolleType;
import de.shop.bestellverwaltung.domain.Bestellung;
import de.shop.util.File;
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
            	       		+ " WHERE  UPPER(k.nachname) = UPPER(:" + Kunde.PARAM_KUNDE_NACHNAME + ")"),
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
        		            + " WHERE  UPPER(k.nachname) = UPPER(:" + Kunde.PARAM_KUNDE_NACHNAME + ")"),
        @NamedQuery(name  = Kunde.FIND_KUNDE_BY_ID_FETCH_BESTELLUNGEN,
        		    query = "SELECT DISTINCT k"
        			         + " FROM   Kunde k LEFT JOIN FETCH k.bestellungen"
        			         + " WHERE  k.id = :" + Kunde.PARAM_KUNDE_ID),	            
        @NamedQuery(name  = Kunde.FIND_KUNDEN_BY_PLZ,
                    query = "SELECT k"
                    		+ " FROM  Kunde k"
                    		+ " WHERE k.adresse.plz = :" + Kunde.PARAM_KUNDE_ADRESSE_PLZ),
		@NamedQuery(name  = Kunde.FIND_USERNAME_BY_USERNAME_PREFIX,
          query = "SELECT   CONCAT('', k.id)"
			        + " FROM  Kunde k"
           		+ " WHERE CONCAT('', k.id) LIKE :" + Kunde.PARAM_USERNAME_PREFIX),
	})
                    		
@ScriptAssert(lang = "javascript",
			script = "(_this.password == null && _this.passwordWdh == null)"
						+ "|| (_this.password != null && _this.password.equals(_this.passwordWdh))",
			message = "{kundenverwaltung.kunde.password.notEqual}",
			groups = PasswordGroup.class)

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
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
	public static final String FIND_USERNAME_BY_USERNAME_PREFIX = PREFIX + "findKundeByUsernamePrefix";
	
	public static final String PARAM_KUNDE_ID = "kundeId";
	public static final String PARAM_KUNDE_NACHNAME = "name";
	public static final String PARAM_KUNDE_ADRESSE_PLZ = "plz";
	public static final String PARAM_KUNDE_EMAIL = "email";
	public static final String PARAM_USERNAME_PREFIX = "usernamePrefix";

	@Id
	@GeneratedValue()
	@Column(name = "k_id", unique = true, nullable = false, updatable = false)
	@Min(value = MIN_ID, message = "{kundenverwaltung.kunde.id.min}", groups = IdGroup.class)
	private Long id = KEINE_ID;
	
	@Version
	@Basic(optional = false)
	private int version = ERSTE_VERSION;

	@Column(nullable = false)
	@Temporal(TIMESTAMP)
	@XmlTransient
	private Date aktualisiert;

	@Column(unique = true, nullable = false)
	@Email(message = "{kundenverwaltung.kunde.email.pattern}")
	//@JsonIgnore
	private String email = "";

	@Column(nullable = false)
	@Temporal(TIMESTAMP)
	@JsonIgnore
	private Date erzeugt;

	@Column(length = 1)
	private String geschlecht;

	@Column(nullable = false)
	@NotNull(message = "{kundenverwaltung.kunde.nachname.notNull}")
	@Size(min = NACHNAME_LENGTH_MIN, max = NACHNAME_LENGTH_MAX,
	      message = "{kundenverwaltung.kunde.nachname.length}")
	@Pattern(regexp = NAME_PATTERN, message = "{kundenverwaltung.kunde.nachname.pattern}")
	private String nachname = "";

	private boolean newsletter = false;

	@Column(length = PASSWORD_LENGTH_MAX, nullable = false)
	private String passwort;
	
	@Transient
	@JsonIgnore
	private String passwortWdh;

	@Column(nullable = false)
	@Size(max = VORNAME_LENGTH_MAX, message = "{kundenverwaltung.kunde.vorname.length}")
	private String vorname = "";
	
	@OneToMany
	@JoinColumn(name = "kunde_fk", nullable = false)
	@JsonIgnore
	private List<Bestellung> bestellungen;
	
	@Transient
	private URI bestellungenUri;
	
	@OneToOne(cascade = { PERSIST, REMOVE }, mappedBy = "kunde")
	@Valid
	@NotNull(message = "{kundenverwaltung.kunde.adresse.notNull}")
	private Adresse adresse;
	
	@Transient
	@AssertTrue(message = "{kundenverwaltung.kunde.agb}")
	private boolean agbAkzeptiert;
	
	@ElementCollection(fetch = EAGER)
	@CollectionTable(name = "kunde_rolle",
	                 joinColumns = @JoinColumn(name = "kunde_fk", nullable = false),
	                 uniqueConstraints =  @UniqueConstraint(columnNames = { "kunde_fk", "rolle_fk" }))
	@Column(table = "kunde_rolle", name = "rolle_fk", nullable = false)
	private Set<RolleType> rollen;
	
	@OneToOne(fetch = LAZY, cascade = { PERSIST, REMOVE })
	@JoinColumn(name = "file_fk")
	@JsonIgnore
	private File file;
	
	@Transient
	private URI fileUri;
	
	@PrePersist
	protected void prePersist() {
		erzeugt = new Date();
		aktualisiert = new Date();
	}
	
	@PostPersist
	protected void postPersist() {
		LOGGER.debugf("Neuer Kunde mit ID=%d", id);
	}

	@PreUpdate
	protected void preUpdate() {
		aktualisiert = new Date();
	}
	
	@PostUpdate
	protected void postUpdate() {
		LOGGER.debugf("Kunde mit ID=%d aktualisiert: version=%d", id, version);
	}
	
	@PostLoad
	protected void postLoad() {
		passwortWdh = passwort;
		agbAkzeptiert = true;
	}
	

	public Kunde() {
		super();
	}

	public Long getId() {
		return this.id;
	}
	
	public void setValues(Kunde k) {
		nachname = k.nachname;
		vorname = k.vorname;
		email = k.email;
		passwort = k.passwort;
		passwortWdh = k.passwort;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	public Set<RolleType> getRollen() {
		return rollen;
	}

	public void setRollen(Set<RolleType> rollen) {
		this.rollen = rollen;
	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}

	public URI getFileUri() {
		return fileUri;
	}

	public void setFileUri(URI fileUri) {
		this.fileUri = fileUri;
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

	public String getNachname() {
		return this.nachname;
	}

	public void setNachname(String name) {
		this.nachname = name;
	}

	public boolean isNewsletter() {
		return this.newsletter;
	}

	public void setNewsletter(boolean newsletter) {
		this.newsletter = newsletter;
	}
	
	public void setAgbAkzeptiert(boolean agbAkzeptiert) {
		this.agbAkzeptiert = agbAkzeptiert;
	}

	public boolean isAgbAkzeptiert() {
		return agbAkzeptiert;
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
		return "Kunde [id=" + id + ", version=" + version + ", aktualisiert="
				+ aktualisiert + ", email=" + email + ", erzeugt=" + erzeugt
				+ ", geschlecht=" + geschlecht + ", nachname=" + nachname
				+ ", newsletter=" + newsletter + ", passwort=" + passwort
				+ ", passwortWdh=" + passwortWdh + ", vorname=" + vorname
				+ ", bestellungen=" + bestellungen + ", bestellungenUri="
				+ bestellungenUri + ", adresse=" + adresse + ", agbAkzeptiert="
				+ agbAkzeptiert + "]";
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((adresse == null) ? 0 : adresse.hashCode());
		result = prime * result + (agbAkzeptiert ? 1231 : 1237);
		result = prime * result
				+ ((aktualisiert == null) ? 0 : aktualisiert.hashCode());
		result = prime * result
				+ ((bestellungen == null) ? 0 : bestellungen.hashCode());
		result = prime * result
				+ ((bestellungenUri == null) ? 0 : bestellungenUri.hashCode());
		result = prime * result + ((email == null) ? 0 : email.hashCode());
		result = prime * result + ((erzeugt == null) ? 0 : erzeugt.hashCode());
		result = prime * result
				+ ((geschlecht == null) ? 0 : geschlecht.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result
				+ ((nachname == null) ? 0 : nachname.hashCode());
		result = prime * result + (newsletter ? 1231 : 1237);
		result = prime * result
				+ ((passwort == null) ? 0 : passwort.hashCode());
		result = prime * result
				+ ((passwortWdh == null) ? 0 : passwortWdh.hashCode());
		result = prime * result + version;
		result = prime * result + ((vorname == null) ? 0 : vorname.hashCode());
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
		Kunde other = (Kunde) obj;
		if (adresse == null) {
			if (other.adresse != null)
				return false;
		} else if (!adresse.equals(other.adresse))
			return false;
		if (agbAkzeptiert != other.agbAkzeptiert)
			return false;
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
		if (bestellungenUri == null) {
			if (other.bestellungenUri != null)
				return false;
		} else if (!bestellungenUri.equals(other.bestellungenUri))
			return false;
		if (email == null) {
			if (other.email != null)
				return false;
		} else if (!email.equals(other.email))
			return false;
		if (erzeugt == null) {
			if (other.erzeugt != null)
				return false;
		} else if (!erzeugt.equals(other.erzeugt))
			return false;
		if (geschlecht == null) {
			if (other.geschlecht != null)
				return false;
		} else if (!geschlecht.equals(other.geschlecht))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (nachname == null) {
			if (other.nachname != null)
				return false;
		} else if (!nachname.equals(other.nachname))
			return false;
		if (newsletter != other.newsletter)
			return false;
		if (passwort == null) {
			if (other.passwort != null)
				return false;
		} else if (!passwort.equals(other.passwort))
			return false;
		if (passwortWdh == null) {
			if (other.passwortWdh != null)
				return false;
		} else if (!passwortWdh.equals(other.passwortWdh))
			return false;
		if (version != other.version)
			return false;
		if (vorname == null) {
			if (other.vorname != null)
				return false;
		} else if (!vorname.equals(other.vorname))
			return false;
		return true;
	}

}