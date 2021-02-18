import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

@WebServlet(name = "PurchaseServlet", value = "/PurchaseServlet")
public class PurchaseServlet extends HttpServlet {

    PurchaseDao purchaseDao;

    public void init(){
        try{
            purchaseDao = new PurchaseDao();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("PurchaseDao creation failed!!");
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

        if (!isUrlValid(urlParts)) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        } else {
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

    private boolean isUrlValid(String[] urlPath) {
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
        StringBuilder buffer = new StringBuilder();
        BufferedReader reader = request.getReader();
        String line;
        int numberOfItems = 1;
        while ((line = reader.readLine()) != null) {
//            if (line.contains("ItemID")){
//                System.out.println(line);
//                String[] tmp = line.split(":");
//                itemID = tmp[1].substring(1, tmp[1].length()-1);
//                System.out.println("read itemID: " + itemID);
//            }
//            if (line.contains("numberOfItems")){
//                System.out.println(line);
//                String[] tmp = line.split(":");
//                numberOfItems = Integer.parseInt(tmp[1].trim());
//                System.out.println("read numberOfItems: " + numberOfItems);
//            }
//            System.out.println(line);
            buffer.append(line);
            buffer.append(System.lineSeparator());
        }
        String data = buffer.toString();
//        System.out.println(data);
        int startIdx = data.indexOf('[');
        int endIdx = data.indexOf(']');
        data = data.substring(startIdx+1, endIdx);

        List<int[]> itemList = new ArrayList<>();
        String[] itemsString = data.split("[{}]", -1);
        for (String itemStr: itemsString){
            if (itemStr.contains("ItemID")){
                String[] itemInfo = itemStr.split(",");
                int itemID = Integer.parseInt((itemInfo[0].substring(10, itemInfo[0].length()-1)));
                int itemNum = 1;
                if (itemInfo[1].contains("numberOfItems")){
                    String[] tmp = itemInfo[1].split(":");
                    itemNum = Integer.parseInt(tmp[2].trim());
                }
                itemList.add(new int[]{itemID, itemNum});
            }
        }

//        for (int[] item: itemList){
//            System.out.println("ItemId: " + item[0] + ", number: " + item[1]);
//        }

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

        if (!isUrlValid(urlParts)) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        } else {
            response.setStatus(HttpServletResponse.SC_CREATED);
            // do any sophisticated processing with urlParts which contains all the url params
            // TODO: process url params in `urlParts`
            String storeID = urlParts[1].trim();
            String custID = urlParts[3].trim();
            String date = urlParts[5];

            purchaseDao.createPurchase(new Purchase(10, 3, 3, 5, 500, 20));

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
                out.println("<p>Body: " + data + "</p>");
                out.println("</body>");
                out.println("</html>");
            } finally {
                out.close();  // Always close the output writer
            }
        }

    }
}
