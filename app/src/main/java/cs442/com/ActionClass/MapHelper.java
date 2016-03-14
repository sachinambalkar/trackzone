package cs442.com.ActionClass;

import com.google.android.gms.maps.model.Marker;

/**
 * Created by Sachin on 08-08-2015.
 */
public class MapHelper
{
   static private Marker marker;
 /*  static private double latitudeVal;
   static private double longitudeVal;
   static private String name;


    public void setLatitudeVal(double lat){
        latitudeVal=lat;
    }
    public double getLatitudeVal(){
        return latitudeVal;
    }

    public void setLongitudeVal(double longitude){
       longitudeVal=longitude;
    }
    public double getLongitudeVal(){
        return longitudeVal;
        marker.
    }

*/
   public void setMarker(Marker marker){
       this.marker=marker;
   }
    public Marker getMarker(){
        return marker;
    }

    public void reset(){
        marker=null;
/*
        latitudeVal=0;
        longitudeVal=0;
        name=null;
*/
    }

    public boolean getStatus(){
        if(marker!=null)
               return true;
        else
            return false;
    }
}
