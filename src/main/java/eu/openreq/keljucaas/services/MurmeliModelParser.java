package eu.openreq.keljucaas.services;

import fi.helsinki.ese.murmeli.*;

import com.google.gson.Gson;

public class MurmeliModelParser {

	public ElementModel parseMurmeliModel(String json) {
		
		Gson gson = new Gson();
		
		ElementModel model = gson.fromJson(json, ElementModel.class);
		
		return model;
	}
}
