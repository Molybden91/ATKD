/**
 * Created by Lab22 on 30.08.2016.
 */
import static Pahan.util.MathOperations.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import static Pahan.util.Print.*;
import static Pahan.util.WorkWithFiles.*;
/**
 * Class for handling raw temperature or pressure data, and calculation of standart deviation
 * and mean values of that parameters. Input parameters are only Temperature or Pressure data.
 */
public class StdCheck extends ATKD{
    private static Logger log = Logger.getLogger(StdCheck.class.getName());
    private int SamplesPerSecond=100000;
    private double allowedIntervalError;
    StdCheck(double allowedIntervalError){
        this.allowedIntervalError=allowedIntervalError;
        this.numberOfParameters=3;
    }
    StdCheck(double allowedIntervalError,int SamplesPerSecond){
        this.allowedIntervalError=allowedIntervalError;
        this.SamplesPerSecond=SamplesPerSecond;
        this.numberOfParameters=3;
    }
    StdCheck (){
        new StdCheck(7.75);
    }
    /**
     * This method allow us to obtain information from raw data, it selects intervals with allowable
     * standart deviation and calculates mean value and overall standart deviation/
     * @param fileName of file,that contains raw data. It doesn't matter what kind of data it contains.
     * @param timeStep in seconds.
     * @return Arraylist of averaged and handled data.
     */
    public void handleRawData(String fileName,int timeStep,boolean filtration) {
        try {
            log.info("Handling raw data started");
            int samples = timeStep * SamplesPerSecond;
            comma2point(fileName);
            ArrayList<Double> values = getData(fileName);
            if(filtration){
                values=medFilter(values,5);
            }
            int iterationNumber = values.size() / samples;
            int step = 1;
            String resultFileName = "result_from_" + fileName;
            writeTextToFile(resultFileName, "");
            ArrayList<Double> cutValues;
            ArrayList<Double> averagedValues = new ArrayList<>();
            ArrayList<Double> stdErrors = new ArrayList<>();
            for (int j = 0; j < iterationNumber; j++) {
                cutValues = new ArrayList<>();
                for (int i = 0; i < step + samples; i++) {
                    cutValues.add(values.get(i));
                }
                double errorCutValues = standartDeviation(cutValues);
                double meanCutValues = mean(cutValues);
                double relativeErrorCutValues = relativeError(errorCutValues, meanCutValues);
                if (relativeErrorCutValues < allowedIntervalError) {
                    averagedValues.add(meanCutValues);
                    stdErrors.add(errorCutValues);
                    log.fine("handled points: " + step + "-" + (step + samples));
                    log.fine(meanCutValues + errorCutValues + relativeErrorCutValues + "%");
                    writeTextToFileWithoutRewrite(resultFileName, "handled points: " + Integer.toString(step) + "-" + Integer.toString(step + samples) + "\r\n" +
                            Double.toString(meanCutValues) + " " + Double.toString(errorCutValues) + " " + Double.toString(relativeErrorCutValues) + "%" + "\r\n");
                }
                step = samples * (j + 1);
            }
            int p = 0;
            ArrayList<Double> handledAveragedValues = new ArrayList<Double>();
            for (int i = 0; i < averagedValues.size(); i++) {
                if (averagedValues.get(i) != 0) {
                    if (p > iterationNumber) break;
                    handledAveragedValues.add(averagedValues.get(i));
                    p++;
                }
            }
            double overAllMean = mean(handledAveragedValues);
            double overAllStd = standartDeviation(handledAveragedValues);
            double overAllReltive = relativeError(overAllStd, overAllMean);
            log.fine("Obtained data: " + "\r\nOverall mean: " + overAllMean + " Overall deviation: " + overAllStd + " Relative error: " + overAllReltive + "%");
            writeTextToFileWithoutRewrite(resultFileName, "\r\n\r\nResult: " + "\r\nOverall mean: " + overAllMean + " " +
                    " Overall deviation: " + overAllStd + " Relative error: " + overAllReltive + "%");
            log.info("Raw data handled");
            //return handledAveragedValues;
        }catch (IOException e){
            log.log(Level.SEVERE,"IOException: ",e);
            e.printStackTrace();
            throw  new RuntimeException(e);
        }
    }

    /**
     * More complicate version of handleRawData method. It needs not only file with raw data but even file with
     * amplitudes, because of need of pressure. This method allows to analyse ATKD using raw data of
     * temperature responce, amplitudes of pressure and temperature from thermocouple.
     * @param ATKDfileName is the name of file,contains temperature,amplitude of pressure and amplitude of responce.
     *                     Usually it's name is "ATKD.txt".
     * @param rawDataFileName is file,that contains raw data. It must contain temperature responce raw data.
     * @param timeStep timeStep in seconds.
     */
    public void completeAnalysis(String ATKDfileName,String rawDataFileName,int timeStep,boolean filtration){
        log.info("Complete analysis of raw data temperature,ATKD and pressure started");
        makeAuxiliaryFiles(ATKDfileName);
        ArrayList <Double> temperature=getData("0.txt");
        ArrayList<Double> pressure=getData("2.txt");
        if(filtration){
            temperature=medFilter(temperature,5);
            pressure=medFilter(pressure,5);
        }
        int samples=timeStep*SamplesPerSecond;
        ArrayList<Double> values=getData(rawDataFileName);
        if(filtration){
            values=medFilter(values,5);
        }
        int iterationNumber= values.size()/samples;
        int step=1;
        String resultFileName="result_from_"+rawDataFileName;
        writeTextToFile(resultFileName,"");
        ArrayList <Double> cutValues;
        ArrayList <Double> averagedValues= new ArrayList<>();
        ArrayList <Double> stdErrors=new ArrayList<>();
        for(int j=0;j<iterationNumber;j++){
            cutValues=new ArrayList<>();
            for(int i=0;i<step+samples;i++){
                cutValues.add(values.get(i));
            }
            double errorCutValues=standartDeviation(cutValues);
            double meanCutValues=mean(cutValues);
            double relativeErrorCutValues=relativeError(errorCutValues,meanCutValues);
            if(relativeErrorCutValues<allowedIntervalError){
                averagedValues.add(meanCutValues);
                stdErrors.add(errorCutValues);
                int iterationForPrint=step+samples;
                log.fine("handled points: "+step+"-"+iterationForPrint);
                log.fine(meanCutValues+errorCutValues+relativeErrorCutValues+"%");
                writeTextToFileWithoutRewrite(resultFileName,"handled points: "+Integer.toString(step)+"-"+Integer.toString(step+samples)+"\r\n"+
                        Double.toString(meanCutValues)+" "+Double.toString(errorCutValues)+" "+Double.toString(relativeErrorCutValues)+"%"+"\r\n");
            }
            step=samples*(j+1);
        }
        int p=0;
        ArrayList <Double> handledAveragedResponce=new ArrayList<Double>();
        ArrayList <Double> handledPressure=new ArrayList<Double>();
        ArrayList <Double> handledTemperature=new ArrayList<Double>();
        for (int i=0;i<averagedValues.size();i++){
            if(averagedValues.get(i)!=0){
                if (p>iterationNumber) break;
                handledAveragedResponce.add(averagedValues.get(i));
                if(i<pressure.size()&&i<temperature.size()) {
                    handledPressure.add(pressure.get(i));
                    handledTemperature.add(temperature.get(i));
                }else{
                    break;
                }
                p++;
            }
        }
        double overAllMean=mean(handledAveragedResponce);
        double overAllStd=standartDeviation(handledAveragedResponce);
        double overAllRelative=relativeError(overAllStd,overAllMean);
        log.fine("Temperature responce: "+overAllMean+" "+overAllStd+" "+overAllRelative+"%");
        writeTextToFileWithoutRewrite(resultFileName,"Temperature responce:\r\n "+Double.toString(overAllMean)+" "+
                Double.toString(overAllStd)+" "+Double.toString(overAllRelative)+"%");
        ArrayList<Double> ATKD=new ArrayList<Double>();
        for(int j=0;j<handledAveragedResponce.size();j++){
            if(j<pressure.size()&&j<temperature.size()) {
                ATKD.add(handledAveragedResponce.get(j) / handledTemperature.get(j) / handledPressure.get(j));
            }
        }
        writeTextToFileWithoutRewrite(resultFileName,"ATKD:\r\n "+Double.toString(mean(ATKD))+" "+
                Double.toString(standartDeviation(ATKD))+" "+
                Double.toString(relativeError(standartDeviation(ATKD),mean(ATKD)))+"%");
        pressureAnalysis("2.txt",resultFileName,filtration);
        log.info("Complete analyses was performed");
    }

    /**
     * Auxiliary method that allow to decimate raw data. It can be useful if you have to big raw data temperature file
     * and decimation will not lead to data loss.
     * @param fileName is name of file with raw data for reduction.
     * @param valueOfReduction- how many times the number of points will be decreased.
     */
    public static void dataReduction(String fileName,String fileResult,int valueOfReduction){
        ArrayList<Double> data=getData(fileName);
        writeTextToFile(fileResult,"");
        for(int i=0;i<data.size();i=i+10){
            writeTextToFileWithoutRewrite(fileResult,Double.toString(data.get(i)));
        }
        log.info("Raw data from file "+fileName+" was decimated "+valueOfReduction+"times");
    }
    public String toString(){
        return "Allowed Interval error: "+allowedIntervalError+", Samples Per Second: "+SamplesPerSecond+", type: RAW data";
    }
    public void deleteAllFiles(){
        super.deleteAllFiles();
    }
    public static void main(String [] args){
        try {
            StdCheck stdCheck = new StdCheck((double) 1488);
            //StdCheck.dataReduction("Raw_data_temperature.txt",10);
            //stdCheck.handleRawData("Raw_data_temperature.txt", 3,false);
            stdCheck.completeAnalysis("ATKD.txt","Raw_data_temperature.txt",3,false);
        }catch (Exception e){

        }
    }
}
class DataException extends Exception{
    DataException(String message){
        super(message);
    }
}
