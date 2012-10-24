package de.shop.temp;

import java.io.Serializable;
import javax.persistence.*;

/**
 * The primary key class for the bestellung_versand database table.
 * 
 */
@Embeddable
public class BestellungVersandPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	@Column(name="bestellung_fk")
	private String bestellungFk;

	@Column(name="versand_fk")
	private String versandFk;

	public BestellungVersandPK() {
	}
	public String getBestellungFk() {
		return this.bestellungFk;
	}
	public void setBestellungFk(String bestellungFk) {
		this.bestellungFk = bestellungFk;
	}
	public String getVersandFk() {
		return this.versandFk;
	}
	public void setVersandFk(String versandFk) {
		this.versandFk = versandFk;
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof BestellungVersandPK)) {
			return false;
		}
		BestellungVersandPK castOther = (BestellungVersandPK)other;
		return 
			this.bestellungFk.equals(castOther.bestellungFk)
			&& this.versandFk.equals(castOther.versandFk);
	}

	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.bestellungFk.hashCode();
		hash = hash * prime + this.versandFk.hashCode();
		
		return hash;
	}
}