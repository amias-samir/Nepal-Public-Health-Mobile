package np.com.naxa.nphf.model;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

/**
 * Created by Samir on 3/15/2017.
 */
public class StaticListOfCoordinates {
    static ArrayList<LatLng> list = new ArrayList<LatLng>();
    public static void setList(ArrayList<LatLng> listToAdd){
        list = listToAdd ;
    }
    public static ArrayList<LatLng> getList(){
        return list ;
    }
}
