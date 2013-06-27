package de.shop.data;

import static de.shop.ShopApp.jsonBuilderFactory;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.json.JsonArray;
import javax.json.JsonObject;

import de.shop.data.Bestellposition;

public class Bestellung implements JsonMappable, Serializable {
	private static final long serialVersionUID = -3227854872557641281L;
	
	public Long id;
	public int version;
	public List<Bestellposition> bestellpositionen;

	public Bestellung() {
		super();
	}

	public Bestellung(long id, Date datum) {
		super();
		this.id = id;
	}

	@Override
	public JsonObject toJsonObject() {
		return jsonBuilderFactory.createObjectBuilder()
		                         .add("id", id)
		                         .add("version", version)
		                         .build();
	}
	
	@Override
	public void fromJsonObject(JsonObject jsonObject) {
		id = Long.valueOf(jsonObject.getJsonNumber("id").longValue());
		version = jsonObject.getInt("version");
		
		JsonArray jsonArray = jsonObject.getJsonArray("bestellpositionen");
		bestellpositionen = new ArrayList<Bestellposition>();
		
		for(int i = 0; i < jsonArray.size(); ++i) {
			Bestellposition bp = new Bestellposition();
			bp.fromJsonObject(jsonArray.getJsonObject(i));
			bestellpositionen.add(bp);
		}
	}
	
	@Override
	public void updateVersion() {
		version++;
	}

	@Override
	public String toString() {
		return "Bestellung [id=" + id + "]";
	}
}
