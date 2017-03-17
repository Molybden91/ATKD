/**
 * Created by Lab22 on 30.08.2016.
 */
import static Pahan.util.MathOperations.*;
import static Pahan.util.Error.*;
import static Pahan.util.ReplaceInFile.Replace;
import static Pahan.util.WorkWithFiles.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**Class for ATKD, calculated with ordinary method: with 9 parameters(Temperature,3 harmonics of pressure,
 * amplitude of pressure, 3 harmonics of temperature responce, temperature responce), without accurate
 * temperature measurements(shift is not taken into account).
 */
public class ATKDOrdinaryData extends ATKD{
    private static Logger log = Logger.getLogger(ATKDOrdinaryData.class.getName());
    ATKDOrdinaryData(int numberOfParameters){
        this.numberOfParameters=numberOfParameters;}
    public ATKDOrdinaryData(){new ATKDOrdinaryData(9);}
    /**
     * Method that creates file with Temperature, Temperature Response amplitude, Pressure amplitude.
     * @param filename is name of file, contains amplitudes and harmonic data,usually it is "ATKD.txt"
     * @param filtration - median filtration if true( or without filtration if false)
     */
    public void calculateAmplitudeParameters(String filename,boolean filtration){
        try {
            log.info("Calculation of Amplitude Parameters started");
            if (!amplitudeFilesExist()) makeAuxiliaryFiles(filename);
            ArrayList<Double> responce = getData("8.txt");
            ArrayList<Double> pressure = getData("4.txt");
            ArrayList<Double> temperature = getData("0.txt");
            if(filtration) {
                 responce = medFilter(responce,5);
                 pressure = medFilter(pressure,5);
                 temperature = medFilter(temperature,5);
            }
            ArrayList<Double> ATKD = new ArrayList<Double>();
            if (temperature.size() == 0 || pressure.size() == 0 || responce.size() == 0)
                throw new DataException("Temperature,response or pressure array have no valuable data");
            writeTextToFile("Amplitudes.txt", Double.toString(mean(temperature)) + " " +
                    Double.toString(standartDeviation(temperature)) + " " +
                    relativeError(standartDeviation(temperature), mean(temperature)) + "%"+"\r\n");
            writeTextToFileWithoutRewrite("Amplitudes.txt", Double.toString(mean(pressure)) + " " +
                    Double.toString(standartDeviation(pressure)) + " "
                    + relativeError(standartDeviation(pressure), mean(pressure)) + "%");
            writeTextToFileWithoutRewrite("Amplitudes.txt", Double.toString(mean(responce)) + " " +
                    Double.toString(standartDeviation(responce)) + " " +
                    relativeError(standartDeviation(responce), mean(responce)) + "%");
            for (int i = 0; i < temperature.size(); i++) {
                ATKD.add(responce.get(i) / temperature.get(i) / pressure.get(i));
            }
            writeTextToFileWithoutRewrite("Amplitudes.txt", Double.toString(mean(ATKD)) + " " +
                    Double.toString(standartDeviation(ATKD)) + " " +
                    relativeError(standartDeviation(ATKD), mean(ATKD)) + "%");
            log.info("Amplitude Parameters calculated");
            calculateResponceToPressure(filename,filtration);
        }catch (DataException e){
            log.log(Level.SEVERE,"DataException: ",e);
        }
    }
    /**
     * Method that creates file with Temperature, Temperature Response harmonics, Pressure harmonics.
     * @param filename is name of file, contains amplitudes and harmonic data,usually it is "ATKD.txt"
     * @param filtration - median filtration if true( or without filtration if false)
     */
    public void calculateHarmonics(String filename,boolean filtration){
        try {
            log.info("Calculation of harmonics parameters started");
            if(!allHarmonicFilesExist()) makeAuxiliaryFiles(filename);
            try {
                allharmonicFilesContainsValuableData();
                writeTextToFile("HarmonicsOfPressure.txt", readOneString("0.txt")+"\r\n");
                writeTextToFile("HarmonicsOfTemperature.txt", readOneString("0.txt")+"\r\n");
                for (int i = 1; i < numberOfParameters; i++) {
                    if (i > 0 && i < 4) {
                        ArrayList<Double> Harm = getData(Integer.toString(i)+".txt");
                        if(filtration){
                            Harm=medFilter(Harm,5);
                        }
                        writeTextToFileWithoutRewrite("HarmonicsOfPressure.txt", Double.toString(mean(Harm)) + " " +
                                Double.toString(standartDeviation(Harm)) + " " +
                                relativeError(standartDeviation(Harm), mean(Harm)) + "%");
                    }
                    if (i > 4 && i < 8) {
                        ArrayList<Double> Harm = getData(Integer.toString(i)+".txt");
                        if(filtration){
                            Harm=medFilter(Harm,5);
                        }
                        writeTextToFileWithoutRewrite("HarmonicsOfTemperature.txt", Double.toString(mean(Harm)) + " " +
                                Double.toString(standartDeviation(Harm)) + " " +
                                relativeError(standartDeviation(Harm), mean(Harm)) + "%");
                    }
                }
                log.info("Harmonic parameters calculated");
            }catch (DataException e){
                log.log(Level.SEVERE,"DataException: ",e);
            }
            } catch (IOException e){
            log.log(Level.SEVERE,"IOException: ",e.toString());
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    /**
     *Method that helps us to calculate dT/dP ratio, and standard deviation for it, and also
     * to write result to the same file as for method calculateAmplitudeParameters.
     * @param fileName- the Name of File from which the data of temperature response(dT) and pressure(dP)
     *                will be extract. usually it is "ATKD.txt".
     * @param filtration - median filtration if true( or without filtration if false)
     */
    public void calculateResponceToPressure(String fileName,boolean filtration){
        log.info("Calculation of dT/dP started");
        ArrayList<Double> responce = getData("8.txt");
        ArrayList<Double> pressure = getData("4.txt");
        if(filtration){
            responce=medFilter(responce,5);
        }
        ArrayList<Double> relation=new ArrayList<>();
        File file=new File("Amplitudes.txt");
        if(!file.exists()) writeTextToFile("Amplitudes.txt","");
        for(int i=0;i<responce.size();i++){
            relation.add(responce.get(i) / pressure.get(i));
        }
        log.info("dT/dP calculated");
        writeTextToFileWithoutRewrite("Amplitudes.txt",Double.toString(mean(relation)) + " " +
                Double.toString(standartDeviation(relation)) + " " +
                relativeError(standartDeviation(relation), mean(relation)) + "%");
    }
    /**
     * Method that allow us to delete all files except the initial input files.
     */
    public void deleteAllFiles(){
        super.deleteAllFiles();
        File fileA=new File("Amplitudes.txt");
        if(fileA.isFile()&&fileA.exists()) fileA.delete();
        File fileHP=new File("HarmonicsOfPressure.txt");
        if(fileHP.isFile()&&fileHP.exists()) fileHP.delete();
        File fileHR=new File("HarmonicsOfTemperature.txt");
        if(fileHR.isFile()&&fileHR.exists()) fileHR.delete();
    }
    public String toString(){
        if(9==numberOfParameters) {
            return "numberOfParameters: " + this.numberOfParameters + ", data contains: T,3harm dT,dT,3harm dP,dP ,type: ordinary ATKD.";
        }else return "numberOfParameters: "+ this.numberOfParameters +" type:ordinary ATKD";
    }
    public static void main(String []args)throws  IOException{
        ATKDOrdinaryData ordinaryData=new ATKDOrdinaryData();
        //ordinaryData.calculateAmplitudeParameters("ATKD.txt",true);
        //ordinaryData.calculateHarmonics("ATKD.txt",true);
        ordinaryData.deleteAllFiles();
    }
}
