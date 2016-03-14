package cs442.com.database;

public class Alert {

	private long alertID;
	private Address alertAddress;
	private String alertTitle;
	private String alertDescription;
	private double alertRadius;
    private int alertStatus;

	public long getAlertID(){return alertID;}
	public Address getAlertAddress(){return alertAddress;}
	public String getAlertTitle(){return alertTitle;}
	public String getAlertDescription(){return alertDescription;}
	public double getAlertRadius(){return alertRadius;}
    public int getAlertStatus(){return alertStatus;}

	public void setAlertID(long alertID){this.alertID = alertID;}
	public void setAlertAddress(Address alertAddress){this.alertAddress = alertAddress;}
	public void setAlertTitle(String alertTitle){this.alertTitle = alertTitle;}
	public void setAlertDescription(String alertDescription){this.alertDescription = alertDescription;}
	public void setAlertRadius(double alertRadius){this.alertRadius = alertRadius;}
    public void setAlertStatus(int alertStatus){this.alertStatus = alertStatus;}
	
}