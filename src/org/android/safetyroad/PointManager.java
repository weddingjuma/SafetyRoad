package org.android.safetyroad;

public class PointManager {
	
	private double pLat, pLon;
	private String addressName;
	
	public PointManager() {
		pLat = 0;
		pLon = 0;
		addressName = "";
	}
	
	public PointManager(double tmpLat, double tmpLon, String address) {
		pLat = tmpLat;
		pLon = tmpLon;
		addressName = address;
	}
	
	public double getLat() {
		return pLat;
	}
	
	public double getLon() {
		return pLon;
	}
	
	public String getAddress() {
		return addressName;
	}
	
	public void setLat(double tmp) {
		pLat = tmp;
	}
	
	public void setLon(double tmp) {
		pLon = tmp;
	}
	
	public void setAddress(String address) {
		addressName = address;
	}
}
