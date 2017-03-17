/**
 * Created by Lab22 on 30.08.2016.
 */
import java.io.IOException;
import java.io.*;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import static Pahan.util.Error.*;
import static Pahan.util.MathOperations.*;
import static Pahan.util.WorkWithFiles.*;
import static Pahan.util.Print.*;
/**Class for ATKD, calculated with method,which allows to control temperature shift: with 9 parameters
 * (Temperature,3 harmonics of pressure,amplitude of pressure, 3 harmonics of temperature response,
 * temperature response), with accurate temperature measurements
 * (shift is taken into account and parameters is averaged into certain temperature intervals).
 */
public class ATKDTemperatureControl extends ATKDOrdinaryData{
    private static Logger log= Logger.getLogger(ATKDTemperatureControl.class.getName());
    protected double maxTemperatureRange;
    protected double deltaT;
    public double getMaxTemperatureRange(){
        return maxTemperatureRange;
    }
    public double getDeltaT(){
        return deltaT;
    }
    ATKDTemperatureControl (double maxTemperatureRange,double deltaT){
        this.maxTemperatureRange=maxTemperatureRange;
        this.deltaT=deltaT;
    }
    ATKDTemperatureControl (){
        new ATKDTemperatureControl(1,0.1);
    }

    /**
     * Method that can calculate ATKD and amplitudes of data set with temperature control.
     * @param fileName is name of file, contains amplitudes and harmonic data,usually it is "ATKD.txt"
     * @param filtration - median filtration if true( or without filtration if false)
     */
    public void calculateAmplitudeParameters(String fileName,boolean filtration){
        try {
            log.info("Calculation of Amplitude Parameters started");
            calculateParameters("TemperatureAmplitude",8,9,filtration);
            calculateParameters("PressureAmplitude",4,5,filtration);
            if (!amplitudeFilesExist()) makeAuxiliaryFiles(fileName);
            amplitudeFilesExist();
            ArrayList <Double> temperature=getData("0.txt");
            ArrayList <Double> responce=getData("8.txt");
            ArrayList <Double> pressure=getData("4.txt");
            if(filtration){
                temperature=medFilter(temperature,5);
                responce=medFilter(responce,5);
                pressure=medFilter(pressure,5);
            }
            ArrayList <Double> ATKD=new ArrayList<Double>();
            if(temperature.size()==0||pressure.size()==0||responce.size()==0)
                    throw new DataException("Temperature,response or pressure array have no valuable data");
            for(int i=0;i<temperature.size();i++){
                    ATKD.add(responce.get(i)/temperature.get(i)/pressure.get(i));
            }
            ArrayList <Double> ATKDAveraged=new ArrayList<Double>();
            ArrayList <Double> TemperatureAveraged=new ArrayList<Double>();
            ArrayList <Double> ATKDstd=new ArrayList<Double>();
            ArrayList <Double> Temperaturestd=new ArrayList<Double>();
            int k=1,i=0,j=0;
            while (temperature.get(k)-temperature.get(0)<=maxTemperatureRange&&k<temperature.size()){
                int count=0;
                    while (k<temperature.size()&&Math.abs(temperature.get(k)-temperature.get(j))<=deltaT){
                        k++;
                        count++;
                    }
                        if(count==0){
                            k++;
                            j++;
                        }
                        ArrayList <Double> ATKDsumbuffer=new ArrayList<Double>();
                        ArrayList <Double> tempsumbuffer=new ArrayList<Double>();
                        if(count==0){
                            ATKDsumbuffer.add(0,ATKD.get(k-2));
                            tempsumbuffer.add(0,temperature.get(k-2));
                        }else for(int p=0;p<count;p++){
                            ATKDsumbuffer.add(ATKD.get(k-1-count+p));
                            tempsumbuffer.add(temperature.get(k-1-count+p));
                        }
                        ATKDAveraged.add(i,mean(ATKDsumbuffer));
                        TemperatureAveraged.add(i,mean(tempsumbuffer));
                        if(ATKDsumbuffer.size()==0||tempsumbuffer.size()==0){
                            throw new DataException("ATKD and/or temperature data have no valuable data");
                        }
                        if(ATKDsumbuffer.size()==1&&tempsumbuffer.size()==1){
                            log.warning(i+"interval of Delta T contains only one point");
                            ATKDstd.add(0,(double)0);
                            Temperaturestd.add(0,(double)0);
                        }else {
                            ATKDstd.add(i, standartDeviation(ATKDsumbuffer));
                            Temperaturestd.add(i, standartDeviation(tempsumbuffer));
                        }
                        j++;
                        if(temperature.size()<=k||temperature.size()<=j){
                            break;
                        }
                        i++;
            }
            log.info("Amplitude parameters was calculated");
            writeTextToFile("ATKDresult.txt","");
            for(int l=0;l<ATKDAveraged.size();l++){
                writeTextToFileWithoutRewrite("ATKDresult.txt",Double.toString(TemperatureAveraged.get(l))+" "+Double.toString(Temperaturestd.get(l))+
                        " "+Double.toString(relativeError(Temperaturestd.get(l),TemperatureAveraged.get(l)))+" "+
                        Double.toString(ATKDAveraged.get(l))+" "+Double.toString(ATKDstd.get(l))+" "+
                        Double.toString(relativeError(ATKDstd.get(l),ATKDAveraged.get(l))));
            }
            if (ATKDAveraged.size()==1){
                log.warning("Chosen Temperature Range contains only one delta temperature range,please check your settings");
            }
            if(ATKDAveraged.size()==0){
                log.warning("Chosen Maximum Temperature Range contains no data, please check your settings");
            }
        }catch (DataException e){
            log.log(Level.SEVERE,"DataException: ",e);
        }
    }

    /**
     * Method that creates file with Temperature, Temperature Response harmonics, Pressure harmonics.
     * @param filtration - median filtration if true( or without filtration if false)
     */
    public void calculateHarmonics(boolean filtration){
        log.info("Harmonics calcualtion started");
        calculateParameters("temperatureHarm",5,8,filtration);
        calculateParameters("pressureHarm",1,4,filtration);
        log.info("Harmonic parameters was calculated");
    }

    /**
     * Method that helps to calculate chosen number of parameters in range [leftBorder,rightBorder).
     * Overall number of parameters is 9. For example, for [5,8) this method will calculate only harmonics of
     * temperature response and for [4,5) only value of pressure.
     * @param type- type of data, determines the name of output file
     * @param leftBorder- left border of range for calculation
     * @param rightBorder- right border of range for calculation. It worth noting that calculation is going in borders [leftBorder,rightBorder)
     * @param filtration - median filtration if true( or without filtration if false)
     */
    protected void calculateParameters(String type,int leftBorder,int rightBorder,boolean filtration){
        try {
            writeTextToFile(type+".txt","");
            for (int filenumber = leftBorder; filenumber < rightBorder; filenumber++) {
                String fileName = Integer.toString(filenumber) + ".txt";
                File file0=new File("0.txt");
                File file=new File(fileName);
                if(!file0.exists()||!file.exists()) makeAuxiliaryFiles("ATKD.txt");
                ArrayList <Double> parameter=getData(fileName);
                ArrayList <Double> temperature=getData("0.txt");
                if(filtration){
                    parameter=medFilter(parameter,5);
                    temperature=medFilter(temperature,5);
                }
                ArrayList <Double> ParameterAveraged=new ArrayList<Double>();
                ArrayList <Double> TemperatureAveraged=new ArrayList<Double>();
                ArrayList <Double> Parameterstd=new ArrayList<Double>();
                ArrayList <Double> Temperaturestd=new ArrayList<Double>();
                int k=1,i=0,j=0;
                while (temperature.get(k)-temperature.get(0)<=maxTemperatureRange&&k<temperature.size()){
                    int count=0;
                    while (k<temperature.size()&&Math.abs(temperature.get(k)-temperature.get(j))<=deltaT){
                        k++;
                        count++;
                    }
                    if(count==0){
                        k++;
                        j++;
                    }
                    ArrayList <Double> Parametersumbuffer=new ArrayList<Double>();
                    ArrayList <Double> tempsumbuffer=new ArrayList<Double>();
                    if(count==0){
                        Parametersumbuffer.add(parameter.get(k-2));
                        tempsumbuffer.add(temperature.get(k-2));
                    }else for(int p=0;p<count;p++){
                        Parametersumbuffer.add(parameter.get(k-1-count+p));
                        tempsumbuffer.add(temperature.get(k-1-count+p));
                    }
                    log.fine("Parameters SumBuffer: "+Parametersumbuffer+" \r\n size: "+Parametersumbuffer.size()+" \r\n mean: "+ mean(Parametersumbuffer));
                    log.fine("Temperature buffer: "+tempsumbuffer+" \r\n size: "+tempsumbuffer.size()+" \r\n mean: "+ mean(tempsumbuffer));
                    ParameterAveraged.add(i,mean(Parametersumbuffer));
                    TemperatureAveraged.add(i,mean(tempsumbuffer));
                    if(Parametersumbuffer.size()==0||tempsumbuffer.size()==0){
                        throw new DataException("Parameter and/or temperature data have no valuable data");
                    }
                    if(Parametersumbuffer.size()==1&&tempsumbuffer.size()==1){
                        log.warning(i+" interval of Delta T contains only one point");
                        Parameterstd.add((double)0);
                        Temperaturestd.add((double)0);
                    }else {
                        Parameterstd.add(i, standartDeviation(Parametersumbuffer));
                        Temperaturestd.add(i, standartDeviation(tempsumbuffer));
                    }
                    j++;
                    if(temperature.size()<=k||temperature.size()<=j){
                        break;
                    }
                    i++;
                }
                log.fine("Averaged Parameters array: "+ParameterAveraged.toString()+" \r\n"+"Averaged Temperature array: "+TemperatureAveraged.toString());
                log.fine("Averaged std array: "+Parameterstd.toString()+" \r\n"+"Averaged std array: "+Temperaturestd.toString());
                if (ParameterAveraged.size()==1){
                    log.warning("Chosen Temperature Range contains only one delta temperature range,please check your settings");
                }
                if(ParameterAveraged.size()==0){
                    log.warning("Chosen Maximum Temperature Range contains no data, please check your settings");
                }
                for(int l=0;l<ParameterAveraged.size();l++){
                    writeTextToFileWithoutRewrite(type+".txt",Double.toString(TemperatureAveraged.get(l))+" "+Double.toString(Temperaturestd.get(l))+
                            " "+Double.toString(relativeError(Temperaturestd.get(l),TemperatureAveraged.get(l)))+" "+
                            Double.toString(ParameterAveraged.get(l))+" "+Double.toString(Parameterstd.get(l))+" "+
                            Double.toString(relativeError(Parameterstd.get(l),ParameterAveraged.get(l))));
                }
            }
        }catch (DataException e){
            log.log(Level.SEVERE,"Data Exception:",e);
        }
    }
    public String toString(){
        if (9==this.numberOfParameters) {
            return "numberOfParameters:" + this.numberOfParameters + ", used DeltaT:" + this.deltaT + ", Maximum temperature range: " + this.maxTemperatureRange +
                    ",data contains: T,3harm dT,dT,3harm dP,dP ,type: with temperature shift control.";
        }else{
            return "numberOfParameters:" + this.numberOfParameters + ", used DeltaT:" + this.deltaT + ", Maximum temperature range: " + this.maxTemperatureRange +
                    " ,type: with temperature shift control.";
        }
    }
    /**
     * Method that allow us to delete all files except the initial input files.
     */
    public void deleteAllFiles(){
        File fileTA=new File("TemperatureAmplitude.txt");
        if(fileTA.isFile()&&fileTA.exists()) fileTA.delete();
        File filePA=new File("PressureAmplitude.txt");
        if(filePA.isFile()&&filePA.exists()) filePA.delete();
        File fileHP=new File("pressureHarm.txt");
        if(fileHP.isFile()&&fileHP.exists()) fileHP.delete();
        File fileHR=new File("temperatureHarm.txt");
        if(fileHR.isFile()&&fileHR.exists()) fileHR.delete();
        File fileAT=new File("ATKDresult.txt");
        if(fileAT.isFile()&&fileAT.exists()) fileAT.delete();
        super.deleteAllFiles();
    }
    public static void main(String []args)throws IOException{
        ATKDTemperatureControl temperatureControl=new ATKDTemperatureControl((double)1,0.9);
        temperatureControl.deleteAllFiles();
        temperatureControl.calculateHarmonics(false);
        //temperatureControl.calculateAmplitudeParameters("ATKD.txt",true);
        //temperatureControl.calculateParameters("temperatureHarm",5,8,true);
        //temperatureControl.calculateParameters("pressureHarm",1,4,true);
    }
}
