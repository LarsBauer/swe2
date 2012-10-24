package de.shop.temp;

import java.io.Serializable;
import javax.persistence.*;
import java.sql.Timestamp;


/**
 * The persistent class for the kunde database table.
 * 
 */
@Entity
public class Kunde implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="k_id")
	private String kId;

	private Timestamp aktualisiert;

	private String email;

	private Timestamp erzeugt;

	private int geschlecht;

	private String name;

	private byte newsletter;

	private String passwort;

	private String vorname;

	public Kunde() {
	}

	public String getKId() {
		return this.kId;
	}

	public void setKId(String kId) {
		this.kId = kId;
	}

	public Timestamp getAktualisiert() {
		return this.aktualisiert;
	}

	public void setAktualisiert(Timestamp aktualisiert) {
		this.aktualisiert = aktualisiert;
	}

	public String getEmail() {
		return this.email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Timestamp getErzeugt() {
		return this.erzeugt;
	}

	public void setErzeugt(Timestamp erzeugt) {
		this.erzeugt = erzeugt;
	}

	public int getGeschlecht() {
		return this.geschlecht;
	}

	public void setGeschlecht(int geschlecht) {
		this.geschlecht = geschlecht;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public byte getNewsletter() {
		return this.newsletter;
	}

	public void setNewsletter(byte newsletter) {
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

}