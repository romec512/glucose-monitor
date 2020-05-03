package Classes;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class ArffReader {
    public static String readFile(String path){
        FileInputStream fin = null;
        byte[] bytes = null;
        try {
            fin = new FileInputStream(new File(path));
            bytes = new byte[fin.available()];
            fin.read(bytes);
        }catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if(fin != null) {
                    fin.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return new String(bytes);
    }
}
