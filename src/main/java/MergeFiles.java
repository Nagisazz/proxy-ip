import java.io.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MergeFiles {

    public static void handle(List<File> files, String name) {
        Set<String> ipList = removeDup(files);
        removePrefix(name);
        RecordValidProxy.write(ipList, Constant.filePath + "/" + name);
    }

    private static void removePrefix(String name) {
        File fileDir = new File(Constant.filePath);
        File[] subFiles = fileDir.listFiles();
        for (File file : subFiles) {
            if (file.getName().startsWith(name)) {
                file.delete();
            }
        }
    }

    private static Set<String> removeDup(List<File> files) {
        Set<String> ipList = new HashSet<String>();
        for (File file : files) {
            BufferedReader reader = null;
            try {
                reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
                String ip = null;
                while ((ip = reader.readLine()) != null) {
                    if (!ip.trim().equals("")) {
                        ipList.add(ip);
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
        }
        return ipList;
    }
}
