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

public class Bestellung implements JsonMappable, Serializable {
	private static final long serialVersionUID = -3227854872557641281L;
	private static final String DATE_FORMAT = "yyyy-MM-dd";
	
	public Long id;
	public Date datum;

	public Bestellung() {
		super();
	}

	public Bestellung(Long id, Date datum) {
		super();
		this.id = id;
		this.datum = datum;
	}
	
	protected JsonObjectBuilder getJsonObjectBuilder() {
		return jsonBuilderFactory.createObjectBuilder()
				                 .add("id", id)
			                     .add("datum", new SimpleDateFormat(DATE_FORMAT, Locale.getDefault()).format(datum));
	}
	
	@Override
	public JsonObject toJsonObject() {
		return getJsonObjectBuilder().build();
	}

	public void fromJsonObject(JsonObject jsonObject) {
		id = Long.valueOf(jsonObject.getJsonNumber("id").longValue());
		try {
			datum = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault()).parse(jsonObject.getString("datum"));
		}
		catch (ParseException e) {
			throw new InternalShopError(e.getMessage(), e);
		};
	}
	
	@Override
	public void updateVersion() { }

	@Override
	public String toString() {
		return "Bestellung [id=" + id + ", datum=" + datum + "]";
	}
}
