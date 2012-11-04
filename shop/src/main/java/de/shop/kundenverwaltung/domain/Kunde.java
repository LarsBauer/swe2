package de.shop.kundenverwaltung.domain;

import static de.shop.util.Constants.KEINE_ID;
import static de.shop.util.Constants.LONG_ANZ_ZIFFERN;
import static de.shop.util.Constants.MIN_ID;
import static javax.persistence.CascadeType.PERSIST;
import static javax.persistence.CascadeType.REMOVE;
import static javax.persistence.TemporalType.DATE;
import static javax.persistence.TemporalType.TIMESTAMP;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderColumn;
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
import javax.validation.constraints.Past;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.ScriptAssert;

import de.shop.bestellverwaltung.domain.Bestellung;

@Entity
@Table(name = "kunde")
@NamedQueries({
	@NamedQuery(name  = Kunde.FIND_KUNDEN,
                query = "SELECT k"
				        + " FROM   Kunde k"),
	@NamedQuery(name  = Kunde.FIND_KUNDEN_FETCH_BESTELLUNGEN,
				query = "SELECT  DISTINCT k"
						+ " FROM Kunde k LEFT JOIN FETCH k.bestellungen"),
	@NamedQuery(name  = Kunde.FIND_KUNDEN_ORDER_BY_ID,
		        query = "SELECT   k"
				        + " FROM  Kunde k"
		                + " ORDER BY k.k_id"),
	@NamedQuery(name  = Kunde.FIND_IDS_BY_PREFIX,
		        query = "SELECT   k.k_id"
		                + " FROM  Kunde k"
		                + " WHERE CONCAT('', k.k_id) LIKE :" + Kunde.PARAM_KUNDE_ID_PREFIX
		                + " ORDER BY k.k_id"),
	@NamedQuery(name  = Kunde.FIND_KUNDEN_BY_NACHNAME,
	            query = "SELECT k"
				        + " FROM   Kunde k"
	            		+ " WHERE  UPPER(k.name) = UPPER(:" + Kunde.PARAM_KUNDE_NACHNAME + ")"),
	@NamedQuery(name  = Kunde.FIND_NACHNAMEN_BY_PREFIX,
   	            query = "SELECT   DISTINCT k.name"
				        + " FROM  Kunde k "
	            		+ " WHERE UPPER(k.name) LIKE UPPER(:"
	            		+ Kunde.PARAM_KUNDE_NACHNAME_PREFIX + ")"),
	@NamedQuery(name  = Kunde.FIND_KUNDEN_BY_NACHNAME_FETCH_BESTELLUNGEN,
	            query = "SELECT DISTINCT k"
			            + " FROM   Kunde k LEFT JOIN FETCH k.bestellungen"
			            + " WHERE  UPPER(k.name) = UPPER(:" + Kunde.PARAM_KUNDE_NACHNAME + ")"),
	@NamedQuery(name  = Kunde.FIND_KUNDE_BY_ID_FETCH_BESTELLUNGEN,
	            query = "SELECT DISTINCT k"
			            + " FROM   Kunde k LEFT JOIN FETCH k.bestellungen"
			            + " WHERE  k.k_id = :" + Kunde.PARAM_KUNDE_ID),
   	@NamedQuery(name  = Kunde.FIND_KUNDE_BY_EMAIL,
   	            query = "SELECT DISTINCT k"
   			            + " FROM   Kunde k"
   			            + " WHERE  k.email = :" + Kunde.PARAM_KUNDE_EMAIL),
    @NamedQuery(name  = Kunde.FIND_KUNDEN_BY_PLZ,
	            query = "SELECT k"
				        + " FROM  Kunde k"
			            + " WHERE k.adresse.plz = :" + Kunde.PARAM_KUNDE_ADRESSE_PLZ),
})

/*
@ScriptAssert(lang = "javascript",
	          script = "(_this.password == null && _this.passwordWdh == null)"
	                   + "|| (_this.password != null && _this.password.equals(_this.passwordWdh))",
	          message = "{kundenverwaltung.kunde.password.notEqual}",
	          groups = PasswordGroup.class)
*/

public abstract class Kunde implements java.io.Serializable {
	private static final long serialVersionUID = 2764174240930170015L;
	
	//Pattern mit UTF-8 (statt Latin-1 bzw. ISO-8859-1) Schreibweise fuer Umlaute:
	private static final String NAME_PATTERN = "[A-Z\u00C4\u00D6\u00DC][a-z\u00E4\u00F6\u00FC\u00DF]+";
	//private static final String PREFIX_ADEL = "(o'|von|von der|von und zu|van)?";
	
	public static final String NACHNAME_PATTERN = /*PREFIX_ADEL + */ NAME_PATTERN + "(-" + NAME_PATTERN + ")?";
	public static final int NACHNAME_LENGTH_MIN = 2;
	public static final int NACHNAME_LENGTH_MAX = 32;
	public static final int VORNAME_LENGTH_MAX = 32;
	public static final int EMAIL_LENGTH_MAX = 128;
	public static final int PASSWORD_LENGTH_MAX = 32;
	
	private static final String PREFIX = "Kunde.";
	public static final String FIND_KUNDEN = PREFIX + "findKunden";
	public static final String FIND_KUNDEN_FETCH_BESTELLUNGEN = PREFIX + "findKundenFetchBestellungen";
	public static final String FIND_KUNDEN_ORDER_BY_ID = PREFIX + "findKundenOrderById";
	public static final String FIND_IDS_BY_PREFIX = PREFIX + "findIdsByPrefix";
	public static final String FIND_KUNDEN_BY_NACHNAME = PREFIX + "findKundenByNachname";
	public static final String FIND_KUNDEN_BY_NACHNAME_FETCH_BESTELLUNGEN =
		                       PREFIX + "findKundenByNachnameFetchBestellungen";
	public static final String FIND_NACHNAMEN_BY_PREFIX = PREFIX + "findNachnamenByPrefix";
	public static final String FIND_KUNDE_BY_ID_FETCH_BESTELLUNGEN =
		                       PREFIX + "findKundeByIdFetchBestellungen";
	public static final String FIND_KUNDE_BY_EMAIL = PREFIX + "findKundeByEmail";
	public static final String FIND_KUNDEN_BY_PLZ = PREFIX + "findKundenByPlz";
	
	public static final String PARAM_KUNDE_ID = "kundeId";
	public static final String PARAM_KUNDE_ID_PREFIX = "idPrefix";
	public static final String PARAM_KUNDE_NACHNAME = "name";
	public static final String PARAM_KUNDE_NACHNAME_PREFIX = "nachnamePrefix";
	public static final String PARAM_KUNDE_ADRESSE_PLZ = "plz";
	public static final String PARAM_KUNDE_EMAIL = "email";

	@Id
	@GeneratedValue
	@Column(name = "k_id", unique = true, nullable = false, updatable = false, precision = LONG_ANZ_ZIFFERN)
	//	@Min(value = MIN_ID, message = "{kundenverwaltung.kunde.k_id.min}", groups = IdGroup.class)
	@Min(value = MIN_ID, message = "{kundenverwaltung.kunde.k_id.min}")
	private Long k_id = KEINE_ID;

	@Column(length = NACHNAME_LENGTH_MAX)
	@NotNull(message = "{kundenverwaltung.kunde.name.notNull}")
	@Size(min = NACHNAME_LENGTH_MIN, max = NACHNAME_LENGTH_MAX,
	      message = "{kundenverwaltung.kunde.name.length}")
	@Pattern(regexp = NACHNAME_PATTERN, message = "{kundenverwaltung.kunde.name.pattern}")
	private String name;

	@Column(length = VORNAME_LENGTH_MAX)
	@Size(max = VORNAME_LENGTH_MAX, message = "{kundenverwaltung.kunde.vorname.length}")
	private String vorname;
	
	@Column(length = EMAIL_LENGTH_MAX, nullable = false, unique = true)
	@Email(message = "{kundenverwaltung.kunde.email.pattern}")
	private String email;
	
	private boolean newsletter = false;
	
	@Column(length = PASSWORD_LENGTH_MAX)
	private String password;
	
	@Transient
	private String passwordWdh;
	
//	@AssertTrue(groups = PasswordGroup.class, message = "{kundenverwaltung.kunde.password.notEqual}")
//	public boolean isPasswordEqual() {
//		if (password == null) {
//			return passwordWdh == null;
//		}
//		return password.equals(passwordWdh);
//	}
	
	@OneToOne(cascade = { PERSIST, REMOVE }, mappedBy = "kunde")
	@Valid
	@NotNull(message = "{kundenverwaltung.kunde.adresse.notNull}")
	private Adresse adresse;

	// Default: fetch=LAZY
	@OneToMany
	@JoinColumn(name = "kunde_fk", nullable = false)
	@OrderColumn(name = "b_id", nullable = false)
	private List<Bestellung> bestellungen;
	
	@Column(nullable = false)
	@Temporal(TIMESTAMP)
	private Date erzeugt;

	@Column(nullable = false)
	@Temporal(TIMESTAMP)
	private Date aktualisiert;

	@PrePersist
	protected void prePersist() {
		erzeugt = new Date();
		aktualisiert = new Date();
	}
	
	@PreUpdate
	protected void preUpdate() {
		aktualisiert = new Date();
	}
	
	@PostLoad
	protected void postLoad() {
		passwordWdh = password;
	}
	
	public void setValues(Kunde k) {
		name = k.name;
		vorname = k.vorname;
		email = k.email;
		password = k.password;
		passwordWdh = k.password;
	}
	
	public Long getId() {
		return k_id;
	}
	public void setId(Long k_id) {
		this.k_id = k_id;
	}

	public String getNachname() {
		return name;
	}
	public void setNachname(String name) {
		this.name = name;
	}

	public String getVorname() {
		return vorname;
	}
	public void setVorname(String vorname) {
		this.vorname = vorname;
	}

	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}

	public void setNewsletter(boolean newsletter) {
		this.newsletter = newsletter;
	}

	public boolean isNewsletter() {
		return newsletter;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getPasswordWdh() {
		return passwordWdh;
	}

	public void setPasswordWdh(String passwordWdh) {
		this.passwordWdh = passwordWdh;
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

	public Date getAktualisiert() {
		return aktualisiert == null ? null : (Date) aktualisiert.clone();
	}
	public void setAktualisiert(Date aktualisiert) {
		this.aktualisiert = aktualisiert == null ? null : (Date) aktualisiert.clone();
	}

	public Date getErzeugt() {
		return erzeugt == null ? null : (Date) erzeugt.clone();
	}
	public void setErzeugt(Date erzeugt) {
		this.erzeugt = erzeugt == null ? null : (Date) erzeugt.clone();
	}
	
	@Override
	public String toString() {
		return "Kunde [k_id=" + k_id
			   + ", name=" + name + ", vorname=" + vorname
			   + ", email=" + email
			   + ", password=" + password + ", passwordWdh=" + passwordWdh
			   + ", erzeugt=" + erzeugt
			   + ", aktualisiert=" + aktualisiert + "]";
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
