import java.io.*;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ReZipping {
    public static void main(String[] args) {
        Set<String> phones = new TreeSet<>();
        Set<String> emails = new TreeSet<>();
        unzip("contacts-2.zip", phones, emails);
        phones.forEach(System.out::println);
        emails.forEach(System.out::println);
    }

    private static void zip(File path) {
        System.out.println("ZIP");
    }

    private static void gz(File path) {
        System.out.println("GZ");
    }

    private static void unzip(String path, Set<String> phones, Set<String> emails) {
        File out = new File("unzipData");
        out.mkdir();
        try(ZipInputStream zip = new ZipInputStream(new FileInputStream(path))) {
            ZipEntry entry;
            FileOutputStream fos;
            while ((entry = zip.getNextEntry()) != null) {
                if (!entry.isDirectory()) {
                    String[] strs = entry.getName().split("/");
                    File zipFile = new File(out + File.separator + strs[strs.length - 1]);
                    byte[] bytes = new byte[1024];
                    fos = new FileOutputStream(zipFile.getPath());
                    while (zip.read(bytes) != -1) {
                        fos.write(bytes);
                    }
                    fos.close();
                    for (File file : out.listFiles()) {
                        if (file.getName().contains(".zip")) {
                            zip(file);
                        } else if (file.getName().contains(".gz")) {
                            gz(file);
                        } else {
                            parseFile(file, phones, emails);
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void parseFile(File unzipFile, Set<String> phones, Set<String> emails) {
        try(FileReader fr = new FileReader(unzipFile);
            Scanner scanner = new Scanner(fr)) {
            String line;
            while (scanner.hasNext()) {
                line = scanner.nextLine();
                System.out.println(line);
                if (line.contains("@") && line.contains("(") && line.contains(")")) {
                    phones.add(getNumber(line));
                    emails.addAll(getEmails(line));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static int getSpaceIndex(String line) {
        int i = line.indexOf("@");
        int spaceNumIndex = 0;
        for (int j = i; j > 0 ; j--) {
            if (line.charAt(j - 1) == ' ') {
                spaceNumIndex = j;
                break;
            }
        }
        return spaceNumIndex;
    }

    private static String getNumber(String line) {
        if (line.substring(line.indexOf(")"),line.indexOf("@")).equals(")")) {
            return "";
        }
        StringBuilder number = new StringBuilder(line.substring(0, getSpaceIndex(line)).trim());
        if (number.charAt(number.length() - 1) == ')') {
            return "";
        }
        int spaceBeforeCityCode = number.indexOf("(");
        if (number.charAt(spaceBeforeCityCode - 1) != ' ') {
                number.insert(spaceBeforeCityCode, ' ');
        }
        int spaceAfterCityCode = number.indexOf(")");
        if (number.charAt(spaceAfterCityCode + 1) != ' ') {
            number.insert(spaceAfterCityCode + 1, ' ');
        }
        int startNumberIndex = spaceAfterCityCode + 2;
        for (int i = startNumberIndex; i < number.length(); i++) {
            if (!Character.isDigit(number.charAt(i))) {
                number.deleteCharAt(i);
            }
        }
        int openBracket = number.indexOf("(");
        int closedBracket = number.indexOf(")") + 1;
        switch (number.substring(openBracket, closedBracket)) {
            case "(101)": number.replace(openBracket, closedBracket, "(401)"); break;
            case "(202)": number.replace(openBracket, closedBracket, "(802)"); break;
            case "(301)": number.replace(openBracket, closedBracket, "(321)"); break;
        }
        return number.toString();
    }

    private static List<String> getEmails(String line) {
        List<String> vaildEmails = new ArrayList<>();
        String emailString = line.substring(getSpaceIndex(line));
        emailString = emailString.replaceAll("\t", " ");
        emailString = emailString.replaceAll(",", " ");
        emailString = emailString.replaceAll(";", " ");
        String[] emails = emailString.split(" ");
        int domainIndex;
        for (String email : emails) {
            domainIndex = email.indexOf("@");
            if (domainIndex == -1) {
                continue;
            }
            if (!email.equals("") && email.substring(domainIndex).contains(".org")) {
                vaildEmails.add(email);
            }
        }
        return vaildEmails;
    }
}
