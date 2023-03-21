package file;


import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * @Class: FileTest
 * @author: decucin
 * @date: 2023 年 03 月 20 日 20:17
 * @version: 1.0
 */
public class FileTest {

    public static void main(String[] args) {

        try(BufferedInputStream reader = new BufferedInputStream(new FileInputStream("/Users/decucin/IdeaProjects/interview/README.md"))){
            byte[] text = new byte[1000];
            reader.read(text);
            System.out.println(new String(text, StandardCharsets.UTF_8));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

}
