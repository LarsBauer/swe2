package de.shop.data;

import static de.shop.ShopApp.jsonBuilderFactory;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

import de.shop.util.InternalShopError;

public class Kunde implements JsonMappable, Serializable {
	private static final long serialVersionUID = -7505776004556360014L;
	private static final String DATE_FORMAT = "yyyy-MM-dd";

	public Long id;
	public int version;
	public String name;
	public String vorname;
	public short kategorie;
	public String email;
	public Adresse adresse;
	public boolean newsletter;
	public boolean agbAkzeptiert = true;
	public Date seit;
	public String bestellungenUri;
	public String type;

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
			                     .add("kategorie", kategorie)
			                     .add("email", email)
			                     .add("adresse", adresse.getJsonBuilderFactory())
			                     .add("newsletter", newsletter)
			                     .add("agbAkzeptiert", agbAkzeptiert)
			                     .add("seit", new SimpleDateFormat(DATE_FORMAT, Locale.getDefault()).format(seit))
			                     .add("bestellungenUri", bestellungenUri)
			                     .add("type", type);
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
	    kategorie = (short) jsonObject.getInt("kategorie");
		email = jsonObject.getString("email");
		adresse = new Adresse();
		adresse.fromJsonObject(jsonObject.getJsonObject("adresse"));
		newsletter = jsonObject.getBoolean("newsletter");
		agbAkzeptiert = jsonObject.getBoolean("agbAkzeptiert");
		try {
			seit = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault()).parse(jsonObject.getString("seit"));
		}
		catch (ParseException e) {
			throw new InternalShopError(e.getMessage(), e);
		};
		bestellungenUri = jsonObject.getString("bestellungenUri");
		type = jsonObject.getString("type");
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
				+ ", newsletter=" + newsletter  + ", seit=" + seit
				+ ", bestellungenUri=" + bestellungenUri + "]";
	}
}
