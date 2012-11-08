package de.shop.kundenverwaltung.domain;

import java.io.Serializable;
import javax.persistence.*;

import de.shop.bestellverwaltung.domain.Bestellung;

import java.util.Collections;
import java.util.Date;
import java.util.List;


/**
 * The persistent class for the kunde database table.
 * 
 */
@Entity
public class Kunde implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="k_id", unique=true, nullable=false, updatable=false)
	private Long id;

	@Column(nullable=false)
	private Date aktualisiert;

	@Column(unique=true, nullable=false)
	private String email;

	@Column(nullable=false)
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
	
	@Override
	public String toString() {
		return "Kunde [id=" + id
			   + ", nachname=" + name + ", vorname=" + vorname
			   + ", geschlecht=" + geschlecht + ", email=" + email 
			   + ", newsletter=" + newsletter + ", password=" + passwort
			   + ", erzeugt=" + erzeugt + ", aktualisiert=" + aktualisiert + "]";
	}

}