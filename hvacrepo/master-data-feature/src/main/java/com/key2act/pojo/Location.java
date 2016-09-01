package com.key2act.pojo;

public class Location {

	private String locationId;
	private String location;
	
	public Location() {}

	public Location(String locationId, String location) {
		super();
		this.locationId = locationId;
		this.location = location;
	}

	public String getLocationId() {
		return locationId;
	}

	public void setLocationId(String locationId) {
		this.locationId = locationId;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

}
