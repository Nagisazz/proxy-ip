import java.io.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MergeFiles {

    public static void handle(List<File> files, String name) {
        Set<String> ipList = removeDup(files);
        RecordValidProxy.write(ipList, name);
    }

    private static Set<String> removeDup(List<File> files) {
        BufferedReader reader = null;
        Set<String> ipList = new HashSet<String>();
        try {
            for (File file : files) {
                reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
                String ip = null;
                while ((ip = reader.readLine()) != null) {
                    if (!ip.trim().equals("")) {
                        ipList.add(ip);
                    }
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return ipList;
    }
}
