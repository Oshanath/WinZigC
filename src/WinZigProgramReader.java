import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class WinZigProgramReader {
    public String readProgram(String path) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(path));
        String line;
        StringBuilder stringBuilder = new StringBuilder();
        String ls = System.getProperty("line.separator");
        while ((line = reader.readLine()) != null) {
            stringBuilder.append(line);
            stringBuilder.append(ls);
        }
        return stringBuilder + "   ";
    }
}
