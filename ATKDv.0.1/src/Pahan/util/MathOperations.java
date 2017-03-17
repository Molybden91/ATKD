package Pahan.util;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Logger;

import static Pahan.util.Error.*;
import static Pahan.util.Print.*;
import static Pahan.util.WorkWithFiles.writeTextToFile;
import static Pahan.util.WorkWithFiles.writeTextToFileWithoutRewrite;
/**
 * Created by Lab22 on 29.08.2016.
 */

public class MathOperations {
    private static Logger log=Logger.getLogger(MathOperations.class.getName());
    /**
     * Calculates standart deviation of data set in array of doubles.
     * @param data array of doubles
     * @return standart deviation if considered array have data and 0 if not.
     */
    public static double standartDeviation(double [] data){
        try {
            if(data.length<2) {
                warning("Array for standart deviation contains less then 2 values");
                return 0;
            }
            else {
                double mui = 0;
                double meanValue=mean(data);
                for (int i = 0; i < data.length; i++) {
                    mui = mui + Math.pow(meanValue-data[i], 2);
                }
                double deviation = Math.sqrt(mui / (data.length - 1));
                return deviation;
            }
        }catch (ArithmeticException e){
            error("Impossible to divide by zero");
            return 0;
        }
    }

    /**
     * Calculates standart deviation of data set in ArrayList of Doubles.
     * @param data ArrayList of Doubles.
     * @return standart deviation if considered ArrayLisst have data and 0 if not.
     */
    public static double standartDeviation(ArrayList <Double> data){
        try {
            if(data.size()<2){
                warning("Array for standart deviation contains less then 2 values");
                return 0;
            }
            else {
                double mui = 0;
                double meanValue=mean(data);
                for (int i = 0; i < data.size(); i++) {
                    mui = mui + Math.pow(meanValue-data.get(i), 2);
                }
                double deviation = Math.sqrt(mui / (data.size() - 1));
                return deviation;
            }
        }catch (ArithmeticException e){
            error("Impossible to divide by zero");
            return 0;
        }
    }

    /**
     * Calculates mean value of data set in array of doubles
     * @param data array of doubles
     * @return mean if considered array have data and 0 if not
     */
    public static double mean(double [] data){
        try {
            if(data.length<1) return 0;
            else {
                double mean = 0;
                for (int i = 0; i < data.length; i++) {
                    mean = mean + data[i];
                }
                return mean / data.length;
            }
        }catch (ArithmeticException e){
            error("Impossible to divide by zero");
            return 0;
        }
    }

    /**
     * Calculates mean value of data set in ArrayList of Doubles
     * @param doubleArrayList ArrayList of Doubles
     * @return mean if considered Arraylist have data and 0 if not
     */
    public static double mean(ArrayList <Double> doubleArrayList){
        try {
            if (doubleArrayList.size()<1)
                return 0;
            else {
                double mean = 0;
                for (int i = 0; i < doubleArrayList.size(); i++) {
                    mean = mean + doubleArrayList.get(i);
                }
                return mean / doubleArrayList.size();
            }
        }catch (ArithmeticException e){
            error("Impossible to divide by zero");
            return 0;
        }
    }

    /**
     * Calculates relative error
     * @param std standart deviation of data
     * @param mean mean value of data
     * @return relative error
     */
    public static double relativeError(double std,double mean ){
        try {
            if (mean == 0) throw new ArithmeticException();
            else {
                return std / mean * 100;
            }
        }catch (ArithmeticException e){
            error("Impossible to divide by zero");
            return 0;
        }
    }

    /**
     * Method for median filtration of data
     * @param data- initial data in form of Array List of Doubles.
     * @param window- base for median filter(width of window)
     * @return Array List of filtered data or of initial data if size of input Array List< then window.
     * Size of this Array List is equal to size of initial data.
     */
    public static ArrayList <Double> medFilter(ArrayList <Double> data, int window) {
        double [] bufferValues=new double [window];
        ArrayList <Double> resultData=new ArrayList<>();
        ArrayList <Double> auxData=new ArrayList<>();
        if(data.size()>=5) {
            for (int i = 0; i < window / 2; i++) {
                auxData.add(data.get(i));
            }
            for (int j = 0; j < data.size(); j++) {
                auxData.add(data.get(j));
            }
            for (int i = data.size() - 2; i < data.size(); i++) {
                auxData.add(data.get(i));
            }
            for (int k = 0; k < data.size(); k++) {
                for (int i = 0; i < window; i++) {
                    bufferValues[i] = auxData.get(i + k);
                }
                Arrays.sort(bufferValues);
                resultData.add(bufferValues[window / 2]);
            }
            log.fine("Data was filtered");
            return resultData;
        }else {
            log.fine("Not enough data for filtration in the initial data");
            return data;
        }
    }

    /**
     * Method for median filtration of data
     * @param data- initial data in form of array of doubles.
     * @param window- base for median filter(width of window)
     * @return  array of filtered data or of initial data if lenght of input array < then window.
     * Length of this array is equal to size of initial data.
     */
    public static double [] medFilter(double [] data,int window){
        double [] bufferValues=new double[window];
        double [] resultData=new double[data.length];
        ArrayList <Double> auxData=new ArrayList<>();
        if(data.length>=5) {
            for (int i = 0; i < window / 2; i++) {
                auxData.add(data[i]);
            }
            for (int j = 0; j < data.length; j++) {
                auxData.add(data[j]);
            }
            for (int i = data.length - 2; i < data.length; i++) {
                auxData.add(data[i]);
            }
            for (int k = 0; k < data.length; k++) {
                for (int i = 0; i < window; i++) {
                    bufferValues[i] = auxData.get(i + k);
                }
                Arrays.sort(bufferValues);
                resultData[k] = bufferValues[window / 2];
            }
            log.fine("Data was filtered");
            return resultData;
        }else {
            log.fine("Not enough data for filtration in the initial data");
            return data;
        }

    }
    public static void main(String [] args){

        /**
        ArrayList <Double> data=new ArrayList<Double>();
        double [] data1=new double[10];
        for(int i=0;i<10;i++){
            data.add((double)i);
            data1[i]=(double)i;
        }
        print("initial data(ArrayList): "+data.toString()+" \r\n"+"it's standart deviation: "+standartDeviation(data)
        +" \r\n"+"it's mean: "+mean(data)+" \r\n"+"it's relative error: "+relativeError(standartDeviation(data),mean(data)));
        print("initial data(double array): "+data1.toString()+" \r\n"+"it's standart deviation: "+standartDeviation(data1)
                +" \r\n"+"it's mean: "+mean(data1)+" \r\n"+"it's relative error: "+relativeError(standartDeviation(data1),mean(data1)));
         **/
    }
}
