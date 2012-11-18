package de.shop.kundenverwaltung.domain;


import static de.shop.util.Constants.KEINE_ID;
import static javax.persistence.CascadeType.PERSIST;
import static javax.persistence.CascadeType.REMOVE;
import static javax.persistence.TemporalType.TIMESTAMP;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.validation.Valid;
import de.shop.bestellverwaltung.domain.Bestellung;


/**
 * The persistent class for the kunde database table.
 * 
 */
@Entity
@Table(name="kunde")
	@NamedQueries({
		@NamedQuery(name  = Kunde.FIND_KUNDEN,
                	query = "SELECT k"
                			+ " FROM   Kunde k"),
        @NamedQuery(name  = Kunde.FIND_KUNDEN_BY_NACHNAME,
                    query = "SELECT k"
                    		+ " FROM   Kunde k"
            	       		+ " WHERE  UPPER(k.name) = UPPER(:" + Kunde.PARAM_KUNDE_NACHNAME + ")"),
        @NamedQuery(name  = Kunde.FIND_KUNDE_BY_EMAIL,
                    query = "SELECT DISTINCT k"
        		            + " FROM   Kunde k"
        		            + " WHERE  k.email = :" + Kunde.PARAM_KUNDE_EMAIL),
        
        /*	            
        @NamedQuery(name  = Kunde.FIND_KUNDEN_BY_PLZ,
                    query = "SELECT k"
                    		+ " FROM  Kunde k"
                    		+ " WHERE k.adresse.plz = :" + Kunde.PARAM_KUNDE_ADRESSE_PLZ)
        */
	})
                    		
public class Kunde implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private static final String PREFIX = "Kunde.";
	public static final String FIND_KUNDEN = PREFIX + "findKunden";
	public static final String FIND_KUNDEN_BY_NACHNAME = PREFIX + "findKundenByNachname";
	public static final String FIND_KUNDE_BY_EMAIL = PREFIX + "findKundeByEmail";
	public static final String FIND_KUNDEN_BY_PLZ = PREFIX + "findKundenByPlz";
	
	public static final String PARAM_KUNDE_ID = "kundeId";
	public static final String PARAM_KUNDE_NACHNAME = "nachname";
	public static final String PARAM_KUNDE_ADRESSE_PLZ = "plz";
	public static final String PARAM_KUNDE_EMAIL = "email";

	@Id
	@GeneratedValue()
	@Column(name="k_id", unique=true, nullable=false, updatable=false)
	private Long id=KEINE_ID;

	@Column(nullable=false)
	@Temporal(TIMESTAMP)
	private Date aktualisiert;

	@Column(unique=true, nullable=false)
	private String email;

	@Column(nullable=false)
	@Temporal(TIMESTAMP)
	private Date erzeugt;

	@Column(length=1)
	private String geschlecht;

	@Column(nullable=false)
	private String name;

	private boolean newsletter;

	@Column(nullable=false)
	private String passwort;

	@Column(nullable=false)
	private String vorname;
	
	@OneToMany
	@JoinColumn(name = "kunde_fk", nullable = false)
	private List<Bestellung> bestellungen;
	
	@OneToOne(cascade = { PERSIST, REMOVE }, mappedBy = "kunde")
	@Valid
	private Adresse adresse;
	
	@PrePersist
	protected void prePersist() {
		erzeugt = new Date();
		aktualisiert = new Date();
	}
	
	@PreUpdate
	protected void preUpdate() {
		aktualisiert = new Date();
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
		return (Date)this.aktualisiert.clone();
	}

	public void setAktualisiert(Date aktualisiert) {
		this.aktualisiert = (Date)aktualisiert.clone();
	}

	public String getEmail() {
		return this.email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Date getErzeugt() {
		return (Date)this.erzeugt.clone();
	}

	public void setErzeugt(Date erzeugt) {
		this.erzeugt = (Date)erzeugt.clone();
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
	
	@Override
	public String toString() {
		return "Kunde [id=" + id
			   + ", nachname=" + name + ", vorname=" + vorname
			   + ", geschlecht=" + geschlecht + ", email=" + email 
			   + ", newsletter=" + newsletter + ", password=" + passwort
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