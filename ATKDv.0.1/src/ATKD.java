import static Pahan.util.MathOperations.*;
import java.io.*;
import java.util.ArrayList;
import static Pahan.util.Print.*;
import static Pahan.util.Error.*;
import static Pahan.util.ReplaceInFile.*;
import static Pahan.util.WorkWithFiles.*;

import java.util.Arrays;
import java.util.logging.*;
/**
 * Created by Lab22 on 29.08.2016.
 */
/**Super class for different types of input data
 */
public class ATKD {
    protected int numberOfParameters=9;//Number of parameters in the input file. Usually there are 9 parameters: T,3harmonics of dP,3harmonics of dT,dT
    private static Logger log = Logger.getLogger(ATKD.class.getName());
    ATKD(int numberOfParameters){
        this.numberOfParameters=numberOfParameters;
    }
    ATKD(){}

    /**
     * Method that changes comma to point in the chosen file.
     * @param fileName- initial file in which comma will be replaced to a point.
     * @throws IOException
     */
    public static void comma2point(String fileName)throws IOException{
        Replace(fileName,",",".");
        log.info("Comma replaced to point in file: "+fileName);
    }

    /**
     * Method that changes comma to point in the chosen file.
     * @param fileName- initial file in which point will be replaced to a comma.
     * @throws IOException
     */
    public static void point2comma(String fileName) throws IOException{
        Replace(fileName,".",",");
        log.info("Point replaced to comma in file: "+fileName);
    }
    /**Method,that makes files with sequential numbers names, with respect to number of input parameters.
     * /
     * @param fileName is file from which auxiliary files will be created. Usually it is "ATKD.txt".
     */
    public void makeAuxiliaryFiles(String fileName){
        try {
            createEmptyAuxiliaryFiles();
            ArrayList <Double> data=getData(fileName);
            log.info("Making auxiliary files from file: "+fileName);
            int ncount=data.size();
            if (ncount<numberOfParameters) throw new DataException("Not enough data in the input file!");
            int count;
            for (int i = 0; i < numberOfParameters; i++) {
                String fileNumber = Integer.toString(i);
                String auxiliaryFile=fileNumber+".txt";
                writeTextToFile(auxiliaryFile,"");
                count=i;
                BufferedReader brAuxiliary=new BufferedReader(new FileReader(fileName));
                for(int j=0;j<ncount/numberOfParameters;j++){
                    writeTextToFileWithoutRewrite(auxiliaryFile,Double.toString(data.get(count)));
                    count=count+numberOfParameters;
                }
                if(getData(auxiliaryFile).size()!=ncount/numberOfParameters) throw new DataException("Not enough data in the output file!");
            }
            if(data.size()%numberOfParameters==0){
                throw new DataException("Initial data file doesn't contains allowable sets of data");
            }
            if(data.size()==numberOfParameters){
                log.warning("Initial file contains only one data set");
            }
        }catch (IOException e){
            log.log(Level.SEVERE,"IOException : ",e);
            e.printStackTrace();
            throw new RuntimeException();
        }catch (DataException e){

        }
    }
    /**
     *
     * @return private field numberOfParameters
     */
    public int getnumberOfParameters(){
        return numberOfParameters;
    }

    /**
     * Mehod that creates empthy auxiliary files. Number of files is equal to number of parameters.
     * @throws IOException
     */
    public void createEmptyAuxiliaryFiles() throws IOException{
        log.info("Making empty auxiliary files from file: ");
        for(int i=0;i<numberOfParameters;i++) {
            String fileNumber = Integer.toString(i);
            String auxiliaryFile = fileNumber + ".txt";
            File file = new File(auxiliaryFile);
            file.createNewFile();
        }
    }
    /**
     * Here I tried to develop method that must be a version of very useful Matlab "load" function.
     * This method simply get data from file and makes  ArrayList with data.
     * @param fileName is the name of file from which we want to get data.
     * @return
     */
    public static ArrayList<Double> getData (String fileName){
        ArrayList <Double> data=new ArrayList<>();
        log.info("Getting data from file: "+fileName);
        try {
            BufferedReader br=new BufferedReader(new FileReader(fileName));
            String ss;
            while ((ss=br.readLine())!=null){
                data.add(Double.parseDouble(ss));
            }
            return data;
        }catch (IOException e){
            log.log(Level.SEVERE,"IOException: ",e);
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
    /**
     * Method that can check existence of files with harmonics data.
     * @return false if one or more files doesn't exist and true if all exist.
     */
    public  boolean allHarmonicFilesExist(){
        for(int j=0;j<getnumberOfParameters();j++){
            File file=new File(Integer.toString(j)+".txt");
            if((j>0&&j<4)||(j>4&&j<8)) {
                if (!file.exists()) return false;
            }
        }
        return true;
    }

    /**
     * Method that can check existence of files with amplitudes data.
     * @return false if one or more files doesn't exist and true if all exist.
     */
    public boolean amplitudeFilesExist(){
        File file4=new File("4.txt");
        File file8=new File("8.txt");
        File file0=new File("0.txt");
        if(!file4.exists()||!file8.exists()||!file0.exists()) return false;
        else  return true;
    }

    /**
     * Methos that can check presence of valuable data in harmonic files
     * @return true if all files contains data and throws exception if even one doesn't.
     * @throws DataException
     */
    public boolean allharmonicFilesContainsValuableData()throws DataException{
        for(int j=0;j<getnumberOfParameters();j++){
            if((j>0&&j<4)||(j>4&&j<8)) {
                if (getData(Integer.toString(j)+".txt").size()==0) throw new DataException("Any of the harmonic files have no data");
            }
        }
        return true;
    }
    /**
     * Method that allows to analyse pressure data.
     * @param fileName is name of file,contains pressure amplitudes data.
     */
    public static void pressureAnalysis(String fileName,String resultFileName,boolean filtration){
        try {
            log.info("Pressure analysis fo file:"+fileName+ "started");
            ArrayList<Double> pressures;
            int i = 0;
            File file=new File(resultFileName);
            if (!file.exists())writeTextToFile(resultFileName,"");
            pressures=getData(fileName);
            if(filtration){
                pressures=medFilter(pressures,5);
            }
            if(pressures.size()==0) throw new DataException ("There is no data in pressure file");
            double meanPressure = mean(pressures);
            double stdPressure = standartDeviation(pressures);
            double relativeError = relativeError(stdPressure, meanPressure);
            writeTextToFileWithoutRewrite(resultFileName,"Pressure result:\r\n "+Double.toString(meanPressure) +
                    " " + Double.toString(stdPressure) + " " + Double.toString(relativeError)+"%");
            log.info("Pressure from file: "+fileName+" was analysed. Result in file: "+resultFileName);
            log.info("Pressure mean value: " + meanPressure + " Pressure error: " + stdPressure + " in %:" + relativeError);
        }catch (DataException e){
            log.log(Level.SEVERE,"DataException: ",e);
        }
    }

    /**
     * This method can covert temperature from thermocouple (in micro Volts) into absolute temperature.
     * It can be useful if your labview program doesn't contain such method. It replaces initial file
     * with file contains absolute temperature.
     * @param SeebeckCoefficient is the coefficient that responsible for value of voltage per degree
     *                           from thermocouple.
     * @param roomTemperature is roomTemperature in Celsius degree.
     * @param fileName is name of file with initial data
     * @param fileResult is name of result file
     */
    public static void convertTemperaturemkVtoK(double SeebeckCoefficient,double roomTemperature,String fileName,String fileResult){
        ArrayList <Double> temperature=getData(fileName);
        writeTextToFile(fileResult,"");
        for(int i=0;i<temperature.size();i++){
            writeTextToFileWithoutRewrite(fileResult,Double.toString((temperature.get(i)/SeebeckCoefficient)+roomTemperature));
        }
        log.info("Temperature converted for Seebeck Coefficient:"+Double.toString(SeebeckCoefficient)+
                " and room temperature:"+Double.toString(roomTemperature));
    }

    /**
     * This method can covert temperature from absolute temperature into micro Volts.
     * @param SeebeckCoefficient is the coefficient that responsible for value of voltage per degree
     *                           from thermocouple.
     * @param roomTemperature is roomTemperature in Celsius degree.
     * @param fileName is name of file with initial data
     * @param fileResult is name of result file
     */
    public static void convertTemperatureKtomV(double SeebeckCoefficient,double roomTemperature,String fileName,String fileResult){
        ArrayList <Double> temperature=getData(fileName);
        writeTextToFile(fileResult,"");
        for(int i=0;i<temperature.size();i++){
            writeTextToFileWithoutRewrite(fileResult,Double.toString((temperature.get(i)-roomTemperature)*SeebeckCoefficient));
        }
        log.info("Temperature converted for Seebeck Coefficient:"+Double.toString(SeebeckCoefficient)+
                " and room temperature:"+Double.toString(roomTemperature));
    }
    /**
     * Method that allow us to delete all files except the initial input files.
     */
    public void deleteAllFiles(){
        for (int i=0;i<numberOfParameters;i++){
            String auxFileName=Integer.toString(i)+".txt";
            File file=new File(auxFileName);
            if(file.exists()&&file.isFile()) {
                file.delete();
            }
        }

        log.info("All files except initial data file deleted");
    }
    public String toString(){
        return "Number of Parameters:"+Integer.toString(this.numberOfParameters)+" type:ATKD";
    }
    public static void main(String [] args) throws FileNotFoundException,IOException,DataException{
        ArrayList <Double> result=medFilter(ATKD.getData("data.txt"),5);
        writeTextToFile("result.txt","");
        for(int i=0;i<result.size();i++) {
            writeTextToFileWithoutRewrite("result.txt", Double.toString(result.get(i)));
        }
        //ATKD.convertTemperatureKtomV(40,293.15);
        //ATKD.convertTemperaturemkVtoK(40,293.15);
       /* String text = "This new text \r\nThis new text2\r\nThis new text3\r\nThis new text4\r\n";
        String fileName = "test.txt";
        writeTextToFile(fileName,text);
        print(readAllStrings(fileName));
        print(readOneString(fileName));
        fileExists("test.txt");
        fileExists("ATKD.txt");
        point2comma("ATKD.txt");
        comma2point("ATKD.txt");
        ArrayList <Double> data=getData("ATKD.txt");
        print(data.toString());
        */
        //comma2point("ATKD.txt");
        //ATKD atkd=new ATKD();
        //atkd.makeAuxiliaryFiles("ATKD.txt");
        //print("The number of points of input data: "+getData("ATKD.txt").size()+" \r\n"+"in the output files: ");
        //for(int i=0;i<9;i++){
        //    String auxFile=Integer.toString(i)+".txt";
        //    print("1: "+getData(auxFile).size());
        //}
        //pressureAnalysis("4.txt");
        //print("All Harmonic files exist? "+atkd.allHarmonicFilesExist());
        //print("All amplitude files exist? "+atkd.amplitudeFilesExist());
        //print("All Harmonic files contains valuable data? "+atkd.allharmonicFilesContainsValuableData());
    }
}
