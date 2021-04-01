import com.rabbitmq.client.Channel;
import org.apache.commons.pool2.ObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPool;
import com.rabbitmq.client.MessageProperties;
import org.json.JSONObject;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.TimeoutException;


@WebServlet(name = "PurchaseServlet", value = "/PurchaseServlet")
public class PurchaseServlet extends HttpServlet {

//    static PurchaseDao purchaseDao;
    Random rand = new Random();
    private ObjectPool rabbitChannelPool;
    private static final String EXCHANGE_NAME = "purchaseLogs";
    private RPCClient getRpc;

    public void init() {
        rabbitChannelPool = new GenericObjectPool<Channel>(new RabbitMQChannelFactory());
        try {
            getRpc = new RPCClient();
        } catch (TimeoutException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }




    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Set the response message's MIME type
        response.setContentType("text/html;charset=UTF-8");
        String urlPath = request.getPathInfo();
        System.out.println(urlPath);
        // check we have a URL!
        if (urlPath == null || urlPath.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.getWriter().write("missing paramterers");
            return;
        }
        String[] urlParts = urlPath.split("/");
        // and now validate url path and return the response status code
        // (and maybe also some value if input is valid)

        if (!isGetUrlValid(urlParts)) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        } else {
            if (urlParts[1].equals("store") || urlParts[1].equals("top10")){
                response.setStatus(HttpServletResponse.SC_OK);
                PrintWriter out = response.getWriter();
                String jsonMessage = new JSONObject().
                        put("op", urlParts[1]).
                        put("param", urlParts[2])
                        .toString();
                try  {
                    String jsonResponseMessage = getRpc.call(jsonMessage);

                    out.println(jsonResponseMessage);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    out.close();
                }
            }
            else {
                response.setStatus(HttpServletResponse.SC_OK);

                // do any sophisticated processing with urlParts which contains all the url params
                // TODO: process url params in `urlParts`
                String storeID = urlParts[1].trim();
                String custID = urlParts[3].trim();
                String date = urlParts[5];

                // Allocate a output writer to write the response message into the network socket
                PrintWriter out = response.getWriter();
                try {
                    out.println("<!DOCTYPE html>");
                    out.println("<html><head>");
                    out.println("<meta http-equiv='Content-Type' content='text/html; charset=UTF-8'>");
                    out.println("<title>New Purchase</title></head>");
                    out.println("<body>");
                    out.println("<h1>Purchase Params</h1>");  // says Hello
                    // Echo client's request information
                    out.println("<p>StoreID: " + storeID + "</p>");
                    out.println("<p>CustomerID: " + custID + "</p>");
                    out.println("<p>PurchaseDate: " + date + "</p>");
                    out.println("<p>Remote Address: " + request.getRemoteAddr() + "</p>");
                    out.println("</body>");
                    out.println("</html>");
                } finally {
                    out.close();  // Always close the output writer
                }
            }
        }
    }

    private boolean isPostUrlValid(String[] urlPath) {
        // TODO: validate the request url path according to the API spec
        // urlPath  = "/{storeID}/customer/{custID}}/date/{date}"
        // urlParts = [, {storeID}, customer, {custID}}, date, {date}]
        String storeID = urlPath[1].trim();
        String customerKey = urlPath[2];
        String custID = urlPath[3].trim();
        String dateKey = urlPath[4];
        String date = urlPath[5];
        if (urlPath[0].length() > 0 || !customerKey.equals("customer") || !dateKey.equals("date")) return false;
        if (storeID.length() == 0 || custID.length() == 0) return false;
        if (date.length() != 8 && !allDigits(date)) return false;
        return true;
    }

    private boolean isGetUrlValid(String[] urlPath) {
        // TODO: validate the request url path according to the API spec
        // urlPath  = "/{storeID}/customer/{custID}}/date/{date}"
        // urlParts = [, {storeID}, customer, {custID}}, date, {date}]
        // urlPath  = "/store/{storeID}"
        // urlParts = [, store, {storeID}]
        // urlPath  = "/top10/{itemID}"
        // urlParts = [, top10, {itemID}]

        if (urlPath.length == 3){
            if (urlPath[1].equals("store") && allDigits(urlPath[2])) return true;
            else if (urlPath[1].equals("top10") && allDigits(urlPath[2])) return true;
            else return false;
        } else if (urlPath.length == 6) {
            String storeID = urlPath[1].trim();
            String customerKey = urlPath[2];
            String custID = urlPath[3].trim();
            String dateKey = urlPath[4];
            String date = urlPath[5];
            if (urlPath[0].length() > 0 || !customerKey.equals("customer") || !dateKey.equals("date")) return false;
            if (storeID.length() == 0 || custID.length() == 0) return false;
            if (date.length() != 8 && !allDigits(date)) return false;
            return true;
        }else {
            return false;
        }
    }

    private boolean allDigits(String s){
        if (s == null || s.length() == 0) return false;
        for (char ch: s.toCharArray()){
            if (ch < '0' || ch > '9') return false;
        }
        return true;
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Read from request
        // parse purchase body to string
        StringBuilder buffer = new StringBuilder();
        BufferedReader reader = request.getReader();
        String line;

        while ((line = reader.readLine()) != null) {
            buffer.append(line);
            buffer.append(System.lineSeparator());
        }
        String data = buffer.toString();


        // Set the response message's MIME type
        response.setContentType("text/html;charset=UTF-8");
        String urlPath = request.getPathInfo();
        System.out.println(urlPath);

        // check we have a URL!
        if (urlPath == null || urlPath.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.getWriter().write("missing paramterers");
            return;
        }
        String[] urlParts = urlPath.split("/");
        // and now validate url path and return the response status code
        // (and maybe also some value if input is valid)
        // isUrlValid method will validate purchase parameter: storeID, custID, pruchaseDate

        if (!isPostUrlValid(urlParts)) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        } else {
            // url check passed, try to write to database, give default status code 200
            response.setStatus(HttpServletResponse.SC_OK);
            // do any sophisticated processing with urlParts which contains all the url params
            String storeID = urlParts[1].trim();
            String custID = urlParts[3].trim();
            String date = urlParts[5];

            // Allocate a output writer to write the response message into the network socket
            PrintWriter out = response.getWriter();
            Channel channel = null;
            try {

                channel = (Channel) rabbitChannelPool.borrowObject();
                channel.exchangeDeclare(EXCHANGE_NAME, "fanout");
                String jsonMessage = new JSONObject()
                        .put("purchaseID", UUID.randomUUID().toString())
                        .put("storeID", storeID)
                        .put("custID", custID)
                        .put("date",date)
                        .put("purchaseBody", data)
                        .toString();

                // channel.basicPublish(EXCHANGE_NAME, "", null, jsonMessage.getBytes(StandardCharsets.UTF_8));
                channel.basicPublish(EXCHANGE_NAME, "", MessageProperties.PERSISTENT_TEXT_PLAIN, jsonMessage.getBytes(StandardCharsets.UTF_8));

//                purchaseDao.createPurchase(UUID.randomUUID().toString(), storeID, custID, date, data);
                // write to database success, change status code 201
                response.setStatus(HttpServletResponse.SC_CREATED);
                out.println("<!DOCTYPE html>");
                out.println("<html><head>");
                out.println("<meta http-equiv='Content-Type' content='text/html; charset=UTF-8'>");
                out.println("<title>New Purchase</title></head>");
                out.println("<body>");
                out.println("<h1>Purchase Params</h1>");  // says Hello
                // Echo client's request information
                out.println("<p>StoreID: " + storeID + "</p>");
                out.println("<p>CustomerID: " + custID + "</p>");
                out.println("<p>PurchaseDate: " + date + "</p>");
                out.println("<p>Remote Address: " + request.getRemoteAddr() + "</p>");
                out.println("<p>Body: " + data + "</p>");
                out.println("</body>");
                out.println("</html>");
            }  catch (Exception e) {
                e.printStackTrace();
            } finally {
                out.close();  // Always close the output writer
                try {
                    rabbitChannelPool.returnObject(channel);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }


}
