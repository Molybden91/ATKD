import java.util.ArrayList;
import static Pahan.util.WorkWithFiles.*;
import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import static  Pahan.util.MathOperations.*;
/**
 * Created by Pahan on 04.10.2016.
 */
public class ATKDOrdinarySRS810dT extends ATKDOrdinaryData{
    private static Logger log = Logger.getLogger(ATKDOrdinarySRS810dT.class.getName());
    protected final int REALNUMBEROFPARAMETERS=6;//They are:T,3 harmonics of dP,dP, dT
    int numberOfPoints=1;//Number of points of temperature response(dT) per one point of T,harmonics of dP and dP.
    //You can set number of points aka samples pre period in the corresponding Labview program for measurements with SRS810.
    ATKDOrdinarySRS810dT(){}
    ATKDOrdinarySRS810dT(int numberOfPoints){
        this.numberOfPoints=numberOfPoints;
        this.numberOfParameters=REALNUMBEROFPARAMETERS+numberOfPoints-1;
        }

    /**
     *Method that help to prepare response before calculation. It makes one file with averaged response. Number of obtained
     * values is equal to number of values of pressure. Each value is obtains from number of points(aka samples per period)
     * specified int he corresponding Labview program for measurements with SRS810. This value must be specified for
     * instance of this class.
     * @param fileName- Name of file with data, usually it is "ATKD.txt"
     * @param filtration - median filtration if true( or without filtration if false)
     */
    public void prepareResponce(String fileName,boolean filtration){
        log.info("Preparation of Response from file: "+fileName+" started");
        makeAuxiliaryFiles(fileName);
        for(int p=5;p<numberOfParameters;p++){
            String auxName= Integer.toString(p)+".txt";
            File file=new File(auxName);
            if(file.exists()&&file.isFile()) {
                file.deleteOnExit();
            }
        }
        ArrayList <Double> data=getData(fileName);
        double [] auxData=new double[numberOfPoints];
        writeTextToFile("8.txt","");
        writeTextToFile("PreparedResponse.txt","");
        int count=REALNUMBEROFPARAMETERS-1;
        ArrayList <Double> preparedData=new ArrayList<>();
        ArrayList <Double> preparedStd=new ArrayList<>();
        for(int i=0;i<data.size();i=i+numberOfParameters){
            int k=0;
            for(int j=count;j<count+numberOfPoints;j++){
                auxData[k]=data.get(j);
                k++;
            }
            if(filtration) {
                auxData = medFilter(auxData, 5);
            }
            for(int s=0;s<auxData.length;s++){
                writeTextToFileWithoutRewrite("PreparedResponse.txt",Double.toString(auxData[s]));
            }
            preparedData.add(mean(auxData));
            preparedStd.add(standartDeviation(auxData));
            count=count+numberOfParameters;
        }
        log.info("Response from file: "+fileName+" was prepared");
        if(filtration) {
            preparedData = medFilter(preparedData, 5);
        }
        if(1==numberOfPoints) {
            for (int l = 0; l < preparedData.size(); l++) {
                writeTextToFileWithoutRewrite("8.txt", Double.toString(preparedData.get(l)));
            }
        }else {
            writeTextToFileWithoutRewrite("PreparedResponse.txt","\r\n\r\nAveraged Values: ");
            for (int l = 0; l < preparedData.size(); l++) {
                writeTextToFileWithoutRewrite("8.txt", Double.toString(preparedData.get(l)));
                writeTextToFileWithoutRewrite("PreparedResponse.txt", Double.toString(preparedData.get(l))+" "+
                        Double.toString(preparedStd.get(l))+" "+Double.toString(relativeError(preparedStd.get(l),preparedData.get(l))));
            }
        }
    }
    /**
     * Method, that can prepare and average data from two files: T.txt with temperature, and fileName with harmonics of pressure and pressure.
     * @param fileName is name of file with all parameters except temperature
     * @param resultFileName is name of result file where prepared data file will be recorded
     */
    public void prepareDataExternalT(String fileName,String TFile, String resultFileName){
        log.info("Preparation data from 2 initial file with T and with harmonics dP,dP started");
        makeAuxiliaryFiles(fileName);
        for(int p=5;p<numberOfParameters;p++){
            String auxName= Integer.toString(p)+".txt";
            File file=new File(auxName);
            if(file.exists()&&file.isFile()) {
                file.deleteOnExit();
            }
        }
        ArrayList <Double> data=getData(fileName);
        double [] auxData=new double[numberOfPoints];
        writeTextToFile("PreparedResponse.txt","");
        int count=REALNUMBEROFPARAMETERS-1;
        ArrayList <Double> preparedData=new ArrayList<>();
        ArrayList <Double> preparedStd=new ArrayList<>();
        for(int i=0;i<data.size();i=i+numberOfParameters){
            int k=0;
            for(int j=count;j<count+numberOfPoints;j++){
                auxData[k]=data.get(j);
                k++;
            }
            for(int s=0;s<auxData.length;s++){
                writeTextToFileWithoutRewrite("PreparedResponse.txt",Double.toString(auxData[s]));
            }
            preparedData.add(mean(auxData));
            preparedStd.add(standartDeviation(auxData));
            count=count+numberOfParameters;
        }
        ArrayList <Double> temperature=getData(TFile);
        ArrayList <Double> firstHarm=getData("1.txt");
        ArrayList <Double> secondHarm=getData("2.txt");
        ArrayList <Double> thirdHarm=getData("3.txt");
        ArrayList <Double> pressure=getData("4.txt");
        ArrayList <Double> response=getData("PreparedResponse.txt");
        writeTextToFile(resultFileName,"");
        writeTextToFile("0.txt","");
        int responseCount=0;
        for (int i=0;i<temperature.size();i++){
            writeTextToFileWithoutRewrite("0.txt",Double.toString(temperature.get(i)));
            if(i<pressure.size()) {
                writeTextToFileWithoutRewrite(resultFileName, Double.toString(temperature.get(i)) + "\r\n" +
                        Double.toString(firstHarm.get(i)) + "\r\n" + Double.toString(secondHarm.get(i)) + "\r\n" +
                        Double.toString(thirdHarm.get(i)) + "\r\n" + Double.toString(pressure.get(i)));
                for(int j=responseCount;j<responseCount+numberOfPoints;j++){
                    writeTextToFileWithoutRewrite(resultFileName,Double.toString(response.get(j)));
                }
                responseCount=responseCount+numberOfPoints;
            }
        }
    }
    /**
     * Method than cuts some of the first points in data file of such type. It is useful because some of the first point
     * have no valuable information for such data.
     * @param fileName- Name of File with data, usually it is "ATKD.txt"
     * @param filtration - median filtration if true( or without filtration if false)
     */
    public void cutData(String fileName,boolean filtration ){
        log.info("Data cut started");
        prepareResponce(fileName,filtration);
        for(int i=0;i<9;i++){
            String auxFileName=Integer.toString(i)+".txt";
            if(i<5||8==i){
                ArrayList <Double> forData=getData(auxFileName);
                writeTextToFile(auxFileName,"");
                for(int j=11;j<forData.size()-1;j++){
                    writeTextToFileWithoutRewrite(auxFileName,Double.toString(forData.get(j)));
                }
            }
        }
    }
    /**
     * Method that creates file with Temperature, Temperature Response amplitude, Pressure amplitude.
     * @param fileName- Name of File with data, usually it is "ATKD.txt"
     * @param cut is parameter that is responsible for data cutting(if true). Without cutting only preparation of
     *            response will be made. Method without such parameter is method of superclass.
     * @param filtration - median filtration if true( or without filtration if false)
     */
    public void calculateAmplitudeParameters(String fileName,boolean cut,boolean filtration){
        log.info("Calculation of amplitude parameters from file: "+fileName+" started");
        if (true==cut){
            cutData(fileName,filtration);
        }else  {
            prepareResponce(fileName,filtration);
        }
        super.calculateAmplitudeParameters(fileName,filtration);
    }

    /**
     * Method that creates file with Temperature and Pressure harmonics (harmonics of temperature for constant
     * signal can not be calculated).
     * @param filtration - median filtration if true( or without filtration if false)
     */
    public void calculateHarmonics(boolean filtration){
        try {
            log.info("Calculation of harmonics parameters started");
            writeTextToFile("HarmonicsOfPressure.txt", readOneString("0.txt") + "\r\n");
            for (int i = 1; i < numberOfParameters; i++) {
                if (i > 0 && i < 4) {
                    ArrayList<Double> Harm = getData(Integer.toString(i)+".txt");
                    if (filtration){
                        Harm=medFilter(Harm,5);
                    }
                    writeTextToFileWithoutRewrite("HarmonicsOfPressure.txt", Double.toString(mean(Harm)) + " " +
                            Double.toString(standartDeviation(Harm)) + " " +
                            Double.toString(relativeError(standartDeviation(Harm), mean(Harm))) + "%");
                }
            }
            log.info("Harmonic parameters was calculated");
        }catch (IOException e){
            log.log(Level.SEVERE,"IOException: ",e);
            e.printStackTrace();
            throw  new RuntimeException(e);
        }
    }
    public String toString(){
        return "numberOfParameters: "+REALNUMBEROFPARAMETERS+" ,Samples Per Period(SPP): "+this.numberOfPoints+", " +
                "type: ordianry ATKD with Temperature Response, obtained from SRS810.";
    }
    /**
     * Method that allow us to delete all files except the initial input files.
     */
    public void deleteAllFiles(){
        super.deleteAllFiles();
        File file=new File("PreparedResponse.txt");
        if (file.exists()&&file.isFile()) file.delete();
        for(int p=5;p<numberOfParameters;p++){
            String auxName= Integer.toString(p)+".txt";
            File fileAux=new File(auxName);
            if(file.exists()&&file.isFile()) {
                fileAux.delete();
                if(!fileAux.delete()){
                    System.gc();
                    fileAux.delete();
                }
            }
        }
    }
    public static void main(String [] args) throws IOException {
        ATKDOrdinarySRS810dT ordinarySRS810dT=new ATKDOrdinarySRS810dT(20);
        comma2point("ATKD.txt");
        comma2point("T.txt");
        ordinarySRS810dT.prepareDataExternalT("ATKD.txt","T.txt","result.txt");
        //ordinarySRS810dT.calculateHarmonics();
        //ordinarySRS810dT.deleteAllFiles();
    }
}
