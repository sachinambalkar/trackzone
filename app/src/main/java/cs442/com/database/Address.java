package cs442.com.database;

public class Address {

	private long addressID;
	private String addressStreet;
	private String addressCity;
	private String addressState;
	private int addressZipCode;
	private double addressLatitude;
	private double addressLongitude;

	public long getAddressID(){return addressID;}
	public String getAddressStreet(){return addressStreet;}
	public String getAddressCity(){return addressCity;}
	public String getAddressState(){return addressState;}
	public int getAddressZipCode(){return addressZipCode;}
	public double getAddressLatitude(){return addressLatitude;}
	public double getAddressLongitude(){return addressLongitude;}

	public void setAddressID(long addressID){this.addressID = addressID;}
	public void setAddressStreet(String addressStreet){this.addressStreet = addressStreet;}
	public void setAddressCity(String addressCity){this.addressCity = addressCity;}
	public void setAddressState(String addressState){this.addressState = addressState;}
	public void setAddressZipCode(int addressZipCode){this.addressZipCode = addressZipCode;}
	public void setAddressLatitude(double addressLatitude){this.addressLatitude = addressLatitude;}
	public void setAddressLongitude(double addressLongitude){this.addressLongitude = addressLongitude;}

}