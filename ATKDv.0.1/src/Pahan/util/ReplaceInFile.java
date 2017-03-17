package Pahan.util;
import java.io.IOException;
import java.nio.charset.*;
import java.nio.file.*;
/**
 * Created by Pahan on 10.09.2016.
 */
public class ReplaceInFile {
    public static void Replace(String fileName,String find,String replace) throws IOException{
        Charset charset= StandardCharsets.UTF_8;
        Path path= Paths.get(fileName);
        Files.write(path,new String(Files.readAllBytes(path),charset).replace(find,replace).getBytes(charset));
    }
    public static void main(String [] args) throws IOException{
        Replace("a.txt","4","666");
    }
}
