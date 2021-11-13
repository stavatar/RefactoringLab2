import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.CookieStore;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Scanner;

public class Client {
    private final String COMMAND_QUIT = "Q";
    private final String URL;
    private Command operation;
    private Double prevNumber;
    private Integer step;

    CloseableHttpClient httpClient = HttpClients.createDefault();
    CookieStore cookieStore = new BasicCookieStore();
    HttpClientContext httpClientContext = HttpClientContext.create();

    public Client(String URL) {
        this.URL = URL;
        this.prevNumber = Double.NaN;
        this.step = 0;
    }

    private void doOperand(String nextline,DecimalFormat df)  throws NumberFormatException,IOException {
        double num = 0;
        try {
            num = Double.parseDouble(nextline);
        } catch (NumberFormatException e) {
            System.out.println("Please, enter the number in format [#.###]");
            throw new NumberFormatException();
        }

        if (Double.isNaN(prevNumber)) {
            prevNumber = num;
        } else {
            String URI = String.format("%s/%s?arg1=%s&arg2=%s", URL, operation.toString().toLowerCase(), df.format(prevNumber), df.format(num));
            String response = sendGetRequest(new HttpGet(URI));

            try {
                prevNumber = Double.parseDouble(response);
            } catch (NumberFormatException e) {
                System.out.println(response);
                throw new NumberFormatException();
            }
        }

        System.out.printf("[#%d]=%s\n", ++step, df.format(prevNumber));
    }


    private void doOpration( String nextline ,DecimalFormat df) throws NumberFormatException,IOException {
        if (nextline.equals(COMMAND_QUIT)) {
            quit();
            return;
        }  else if (nextline.startsWith("#"))
            move(nextline,df);
         else initCommand( nextline);
    }


    public void startClient() throws IOException {
        boolean isOperand = true;
        DecimalFormat df = new DecimalFormat("#.####");
        DecimalFormatSymbols sym = DecimalFormatSymbols.getInstance();
        sym.setDecimalSeparator('.');
        df.setDecimalFormatSymbols(sym);
        httpClientContext.setAttribute(HttpClientContext.COOKIE_STORE, cookieStore);
        httpClient.execute(new HttpGet(URL), httpClientContext);
        getHelp();
        System.out.print("> ");
        while (true) {
            try {
                    String nextline = new Scanner(System.in).nextLine().toUpperCase();
                    if (isOperand) {
                        doOperand(nextline, df);
                        System.out.print("@: ");
                        isOperand = false;
                    } else {
                        doOpration(nextline, df);
                        System.out.print("> ");
                        isOperand = true;
                    }
            }catch (NumberFormatException e) {
                    continue;
                }
             catch (Exception e ){
                    System.out.println("Unknown error");
                }
            }

        }


    private String sendGetRequest(HttpGet httpGet) throws IOException {
        try (CloseableHttpResponse response = httpClient.execute(httpGet, httpClientContext)) {
            HttpEntity entity = response.getEntity();
            if (entity != null)
                return EntityUtils.toString(entity);
        }
        return "";
    }

    private void  getHelp() throws IOException {
        String URI = String.format("%s/%s", URL, "help");
        System.out.println(sendGetRequest(new HttpGet(URI)));
    }
    private void quit() throws IOException {
        System.out.println("That's all");
        String URI  = String.format("%s?operation=%s", URL, COMMAND_QUIT);
        sendGetRequest(new HttpGet(URI));
    }
    private void move(String nextline,DecimalFormat df) throws IOException {
        String URI;
        double num;
        int stepInput = Integer.parseInt(nextline.substring(1));
        URI = String.format("%s/%s?step=%d", URL, "step", stepInput);
        String response = sendGetRequest(new HttpGet(URI));

        try {
            num = Double.parseDouble(response);
        } catch (NumberFormatException e) {
            System.out.println(response);
            throw new NumberFormatException();
        }

        System.out.printf("[#%d]=%s\n", ++step, df.format(num));
    }
    private void initCommand(String nextline)  {
        switch (nextline) {
            case "+":
                operation = Command.ADD;
                break;
            case "-":
                operation = Command.SUB;
                break;
            case "*":
                operation = Command.MUL;
                break;
            case "/":
                operation = Command.DIV;
                break;
            default:
                System.out.println("Operation not found");
                throw new NumberFormatException();
        }
    }

}
