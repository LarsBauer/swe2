package de.shop.temp;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the bestellung_versand database table.
 * 
 */
@Entity
@Table(name="bestellung_versand")
public class BestellungVersand implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private BestellungVersandPK id;

	public BestellungVersand() {
	}

	public BestellungVersandPK getId() {
		return this.id;
	}

	public void setId(BestellungVersandPK id) {
		this.id = id;
	}

}