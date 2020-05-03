package Classes;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class ArffHelper {
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

    public static void Append(String filepath, String data) {
        String oldFileContent =  null;
        File file = null;
        file = new File(filepath);
        try {
            if(!file.exists()) {
                file.createNewFile();
            } else {
                oldFileContent = ArffHelper.readFile(filepath);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        FileOutputStream fOut = null;
        try {
            fOut = new FileOutputStream(filepath);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
        try {
            if (oldFileContent == null) {
                oldFileContent = "@relation 'glucose-data'\n" +
                        "\n" +
                        "@attribute date date\n" +
                        "@attribute glucose numeric\n" +
                        "\n" +
                        "@data\n";
            }
            myOutWriter.append(oldFileContent + data);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            myOutWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            fOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
