package de.shop.data;

import static de.shop.ShopApp.jsonBuilderFactory;

import java.io.Serializable;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

public class Kunde implements JsonMappable, Serializable {
	private static final long serialVersionUID = -7505776004556360014L;

	public Long id;
	public int version;
	public String name;
	public String vorname;
	public String email;
	public String geschlecht;
	public Adresse adresse;
	public boolean newsletter;
	public boolean agbAkzeptiert = true;
	public String bestellungenUri;

	public Kunde() {
		super();
	}
	
	public Kunde(Long id, String name) {
		super();
		this.id = id;
		this.name = name;
	}
	
	protected JsonObjectBuilder getJsonObjectBuilder() {
		return jsonBuilderFactory.createObjectBuilder()
				                 .add("id", id)
			                     .add("version", version)
			                     .add("name", name)
			                     .add("vorname", vorname)
			                     .add("email", email)
			                     .add("geschlecht", geschlecht)
			                     .add("adresse", adresse.getJsonBuilderFactory())
			                     .add("newsletter", newsletter)
			                     .add("agbAkzeptiert", agbAkzeptiert)
			                     .add("bestellungenUri", bestellungenUri);
	}
	
	@Override
	public JsonObject toJsonObject() {
		return getJsonObjectBuilder().build();
	}

	public void fromJsonObject(JsonObject jsonObject) {
		id = Long.valueOf(jsonObject.getJsonNumber("id").longValue());
	    version = jsonObject.getInt("version");
		name = jsonObject.getString("name");
		vorname = jsonObject.getString("vorname");
		email = jsonObject.getString("email");
		geschlecht = jsonObject.getString("geschlecht");
		adresse = new Adresse();
		adresse.fromJsonObject(jsonObject.getJsonObject("adresse"));
		newsletter = jsonObject.getBoolean("newsletter");
		agbAkzeptiert = jsonObject.getBoolean("agbAkzeptiert");
		bestellungenUri = jsonObject.getString("bestellungenUri");
	}
	
	@Override
	public void updateVersion() {
		version++;
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
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Kunde other = (Kunde) obj;
		if (email == null) {
			if (other.email != null)
				return false;
		} else if (!email.equals(other.email))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "AbstractKunde [id=" + id + ", name=" + name + ", vorname="
				+ vorname + ", email=" + email + ", adresse=" + adresse
				+ ", newsletter=" + newsletter + ", geschlecht=" + geschlecht
				+ ", bestellungenUri=" + bestellungenUri + "]";
	}
}
