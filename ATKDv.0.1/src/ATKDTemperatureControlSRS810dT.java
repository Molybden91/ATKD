import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Logger;
import static Pahan.util.WorkWithFiles.*;
/**
 * Created by Pahan on 07.10.2016.
 */
public class ATKDTemperatureControlSRS810dT extends ATKDTemperatureControl {
    ATKDOrdinarySRS810dT ordinarySRS810dT;
    private static Logger log = Logger.getLogger(ATKDTemperatureControlSRS810dT.class.getName());
    ATKDTemperatureControlSRS810dT(double maxTemperatureRange,double deltaT, int numberOfPoints){
        this.maxTemperatureRange=maxTemperatureRange;
        this.deltaT=deltaT;
        this.ordinarySRS810dT=new ATKDOrdinarySRS810dT(numberOfPoints);
    }
    ATKDTemperatureControlSRS810dT(int numberOfPoints){
        this.ordinarySRS810dT=new ATKDOrdinarySRS810dT(numberOfPoints);
    }
    ATKDTemperatureControlSRS810dT(){
        new ATKDTemperatureControlSRS810dT(1);
    }

    /**
     * Method that creates file with Temperature, Temperature Response amplitude, Pressure amplitude.
     * @param fileName-Name of File with data, usually it is "ATKD.txt"
     * @param cut-is parameter that is responsible for data cutting(if true). Without cutting only preparation of
     *            response will be made. Method without such parameter is method of superclass.
     * @param filtration- median filtration if true ( or without filtration if false)
     */
    public void calculateAmplitudeParameters(String fileName,boolean cut,boolean filtration){
        if(true==cut){
            ordinarySRS810dT.cutData(fileName,filtration);
        }else {
            ordinarySRS810dT.prepareResponce(fileName,filtration);
        }
        super.calculateAmplitudeParameters(fileName,filtration);
    }

    /**
     * Method that creates file with Temperature and Pressure harmonics (harmonics of temperature for constant
     * signal can not be calculated).
     * @param filtration - median filtration if true ( or without filtration if false)
     */
    public void calculateHarmonics(boolean filtration){
        calculateParameters("pressureHarm",1,4,filtration);
    }
    public String toString(){
        return "numberOfParameters:" + Integer.toString(ordinarySRS810dT.REALNUMBEROFPARAMETERS) + ", used DeltaT:" +
                Double.toString(this.deltaT) +", Maximum temperature range: " + Double.toString(this.maxTemperatureRange) +
                ", Samples Per Period(SPP):"+Integer.toString(this.ordinarySRS810dT.numberOfPoints)+
                ",type: with temperature shift control and dT obtained from SRS810.";
    }
    public static void main(String [] args)throws IOException{
        ATKDTemperatureControlSRS810dT temperatureControlSRS810dT=new ATKDTemperatureControlSRS810dT(0.1,1,1);
        //comma2point("ATKD.txt");
        //comma2point("T.txt");
        System.out.println(temperatureControlSRS810dT.toString());
        //temperatureControlSRS810dT.calculateAmplitudeParameters("ATKDPrepared.txt",true,true);
        //temperatureControlSRS810dT.prepareDataExternalT("ATKD.txt",true);
        //temperatureControlSRS810dT.calculateHarmonics();
    }
}
