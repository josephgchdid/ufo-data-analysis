import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Parser {

    final String TARGET_URL = "http://www.nuforc.org/webreports/ndxloc.html";

    final String BASE_URL = "http://www.nuforc.org/webreports";

    private List<String> pageList;

    private List<String> dataList;

    public Parser(){
        pageList = new ArrayList<>();
        dataList = new ArrayList<>();
    }


    public void parseTargetUrl() throws IOException {

        System.out.println("Fetching " + TARGET_URL);

        Document doc = Jsoup.connect(TARGET_URL).get();

        Elements pages = doc.select("table");

        for(Element row : pages.select("tr")){
            for(Element td : row.select("td")){
                Elements links = td.select("a[href]");

                for(Element link : links){
                   pageList.add(String.format("%s/%s", BASE_URL, link.attr("href")));
                }
            }
        }

        System.out.println("found " + pageList.size() + " pages in " + TARGET_URL);
    }

    public  void parseRows() throws IOException{
        for(String url : pageList){
            System.out.println("Fetching " + url);

            Connection conn = Jsoup.connect(url);

            conn.timeout(100 * 1000);

            Document doc = conn.get();

            Elements pages = doc.select("table");

            Elements rows = pages.select("tr");

            System.out.println("found " +  ( rows.size()  - 1 ) + " rows");
            //start from 1 to skip header
            for(int i = 1; i < rows.size(); i++){

                Element row = rows.get(i);

                String elem = "";

                for(Element td : row.select("td")){
                    elem += td.text().replace(",", ".") + ",";
                }

                dataList.add(elem.substring(0, elem.lastIndexOf(",")));
            }
          //  System.out.println("Total rows -> " + dataList.size());
        }
    }

    public void writeCsv() throws IOException {
        String[] headers = {
                "date Time" ,
                "location",
                "city/country",
                "shape",
                "duration",
                "summary",
                "posted"
        };

        File file = new File("C:\\Users\\joe\\Desktop\\obj\\ufo.csv");
        
        if(file.createNewFile()){
            System.out.println("Created new file");
        }
        else {
            System.out.println("File already exists");
        }

        System.out.println("Saving data to file");

        String header = "";

        for(String column : headers){
            header += column + ",";
        }

        header = header.substring(0, header.lastIndexOf(","));

        FileWriter writer = new FileWriter(file.getAbsolutePath());
        writer.write(header);
        writer.close();

        writer = new FileWriter(file.getAbsolutePath(), true);
        BufferedWriter bw = new BufferedWriter(writer);

        for(String row : dataList){
            bw.write("\n" + row);
        }
        bw.close();
    }
}
