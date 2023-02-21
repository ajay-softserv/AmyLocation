package sandbox.xmlworker;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;
import org.jsoup.Jsoup;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.tool.xml.ElementList;
import com.itextpdf.tool.xml.XMLWorkerHelper;


@WebServlet("/ParseHtml")
public class ParseHtml extends HttpServlet {
	private static final long serialVersionUID = 1L;
     public ParseHtml() {
        super();
     }
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}
	public String parserXHtml(String html) {
        org.jsoup.nodes.Document document = Jsoup.parseBodyFragment(html);
        document.outputSettings().syntax(org.jsoup.nodes.Document.OutputSettings.Syntax.xml); //This will ensure the validity
        document.outputSettings().charset("UTF-8");
        return document.toString();
    }
	 public void createPdf(String file,String sb) throws IOException, DocumentException {
        // step 1
        Document document = new Document();
        // step 2
        PdfWriter.getInstance(document, new FileOutputStream(file));
        // step 3
        document.open();
        // step 4
        PdfPTable table = new PdfPTable(1);
        PdfPCell cell = new PdfPCell();
        ElementList list = XMLWorkerHelper.parseToElementList(sb, null);
        for (Element element : list) {
            cell.addElement(element);
        }
        table.addCell(cell);
        document.add(table);
        // step 5
        document.close();
    }
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
			System.out.println("Post pdf");
		    response.setContentType("application/json;charset=UTF-8"); 
		    response.setHeader("Cache-Control", "no-cache"); 
		       PrintWriter out = response.getWriter();
		       JSONObject  Result=new JSONObject();
		       String MapURL= request.getParameter("MapURL");
		       MapURL=MapURL.replaceAll("#", ""); 
		       MapURL=MapURL.replaceAll("\\^", ""); 
		       MapURL=MapURL.replaceAll("`", ""); 
		       MapURL=MapURL.replaceAll("\\{", ""); 
		       MapURL=MapURL.replaceAll("\\}", ""); 
		       MapURL=MapURL.replaceAll("\\|", "");
		       String ContentValues= request.getParameter("ContentValues");
		       String FileName= request.getParameter("FileName");
		       String FullPath=System.getProperty("catalina.base")+"\\webapps\\Location\\Google Map Reports\\"+FileName+".pdf";
		       File file = new File(FullPath);
		        file.getParentFile().mkdirs();
		        StringBuilder sb = new StringBuilder();
		        MapURL= MapURL.replaceAll(" ", "%20");
		        sb.append("<div><img src='"+MapURL+"'/></div>");
		        sb.append(ContentValues);
              String beautifyhtml=parserXHtml(sb.toString());
              beautifyhtml= beautifyhtml.replaceAll("&amp;", "&");          
		        try {
					new ParseHtml().createPdf(FullPath,beautifyhtml);
					  Result.put("Success", "../Location/Google Map Reports/");
		        }catch(Exception e)
		        {
		        	e.printStackTrace();
		        }
		      out.print(Result);
	}

}
