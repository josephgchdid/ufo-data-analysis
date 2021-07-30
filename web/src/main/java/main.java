import java.io.IOException;

public class main {

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        Parser parser = new Parser();
        parser.parseTargetUrl();
        parser.parseRows();
        parser.writeCsv();
    }
}
