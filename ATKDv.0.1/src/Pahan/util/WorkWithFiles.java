package Pahan.util;

import java.io.*;
import java.util.logging.*;
import static Pahan.util.Print.print;

/**
 * Created by Lab22 on 14.09.2016.
 */
public class WorkWithFiles {
    private static Logger log=Logger.getLogger(WorkWithFiles.class.getName());
    /**Method, that writes a String in the certain file.
     *
     * @param fileName is name of file in which you want to put data.
     * @param text is the text that will be put in file.
     */
    public static void writeTextToFile(String fileName,String text){
        File file=new File(fileName);
        log.fine("Writing to file: "+fileName);
        try { if (!file.exists()){
            file.createNewFile();
        }
            PrintWriter out=new PrintWriter(file.getAbsoluteFile());
            try {
                out.print(text);
            }finally {
                out.close();
            }
        }catch (IOException e){
            throw new RuntimeException(e);
        }
    }
    /**
     *
     * @param fileName is name of file in which you want to put portion of data string by string without rewriting.
     * @param text is the text that will be put in file.
     */
    public static void writeTextToFileWithoutRewrite(String fileName,String text){
        File file=new File(fileName);
        log.fine("writing iteration to file: "+fileName);
        try { if(!file.exists()) throw new FileNotFoundException();
            FileWriter outfile=new FileWriter(file.getAbsoluteFile(),true);
            try{
                outfile.write(text+"\r\n");
            }finally {
                outfile.close();
            }
        }catch (IOException e){
            throw  new RuntimeException(e);
        }
    }
    /**Method, that read only one String from certain file.
     *
     * @param fileName is the name of file from which one string will be read.
     * @return String, read from file
     * @throws FileNotFoundException
     */
    public static String readOneString(String fileName)throws FileNotFoundException{
        StringBuilder sb=new StringBuilder();
        fileExists(fileName);
        log.fine("Reading one string from file: "+fileName);
        try {
            BufferedReader in=new BufferedReader(new FileReader(fileName));
            try {
                String s;
                s=in.readLine();
                if(s==null){
                    return null;
                }
                sb.append(s);
            }finally {
                in.close();
            }
        }catch (IOException e){
            throw new RuntimeException(e);
        }
        return sb.toString();
    }
    /**Method that can read all Strings from file and assine a number of read Strings
     * to variable readStringCount
     *
     * @param fileName is the name of file from which all strings will be read.
     * @return String, that contains all Strings, read from file
     * @throws FileNotFoundException
     */
    public static String readAllStrings(String fileName) throws FileNotFoundException{
        StringBuilder sb=new StringBuilder();
        fileExists(fileName);
        log.fine("Reading of all strings from file: "+fileName);
        try {
            BufferedReader in=new BufferedReader(new FileReader(fileName));
            try {
                String s;
                while ((s=in.readLine())!=null){
                    sb.append(s);
                    sb.append("\n");
                }
            }finally {
                in.close();
            }
        }catch (IOException e){
            throw new RuntimeException(e);
        }
        return sb.toString();
    }
    /**
     * Method, that checks existing of certain file
     * @param fileName is the name of checked file
     * @throws FileNotFoundException
     */
    public static void fileExists(String fileName)throws FileNotFoundException{
        File file=new File(fileName);
        if(!file.exists()){
            throw new FileNotFoundException(file.getName());
        }
    }
}
