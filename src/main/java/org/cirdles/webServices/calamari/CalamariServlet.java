/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cirdles.webServices.calamari;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.nio.file.Paths;
import java.util.Date;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.web.bind.ServletRequestUtils;

/**
 *
 * @author ty
 */
@MultipartConfig
public class CalamariServlet extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
      response.setContentType("text/html;charset=UTF-8");
      // Allocate a output writer to write the response message into the network socket
      PrintWriter out = response.getWriter();
        HttpSession session = request.getSession();
        Integer accessCount;
        synchronized(session) {
            accessCount = (Integer)session.getAttribute("accessCount");
            if (accessCount == null) {
                accessCount = 0;   // autobox int to Integer
            } else {
                accessCount = accessCount + 1;
            }
            session.setAttribute("accessCount", accessCount);
        }
        try {
         out.println("<!DOCTYPE html>");
         out.println("<html>");
         out.println("<head><meta http-equiv='Content-Type' content='text/html; charset=UTF-8'>");
         out.println("<title>Session Test Servlet</title></head><body>");
         out.println("<p>You have access this site " + accessCount + " times in this session.</p>");
         out.println("<p>(Session ID is " + session.getId() + ")</p>");
 
         out.println("<p><a  href='" + request.getRequestURI() +  "'>Refresh</a>");
         out.println("<p><a  href='" + response.encodeURL(request.getRequestURI())  +
                     "'>Refresh with  URL rewriting</a>");
         out.println("</body></html>");
      } finally {
         out.close();  // Always close the output writer
      }
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/zip");
        response.setHeader("Content-Disposition", "attachment; filename=calamari-reports.zip");

        boolean useSBM = ServletRequestUtils.getBooleanParameter(request, "useSBM", true);
        boolean useLinFits = ServletRequestUtils.getBooleanParameter(request, "userLinFits", false);
        String firstLetterRM = ServletRequestUtils.getStringParameter(request, "firstLetterRM", "T");
        Part filePart = request.getPart("prawnFile");

        String fileName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString();
        InputStream fileStream = filePart.getInputStream();
        File myFile = new File(fileName);
        
        PrawnFileHandlerService handler = new PrawnFileHandlerService();
        String fileExt = FilenameUtils.getExtension(fileName);
        
        try {
            File report = null;
            if(fileExt.equals("zip"))
            {
                report = handler.generateReportsZip(fileName, fileStream, useSBM, useLinFits, firstLetterRM).toFile();
            }
            else if(fileExt.equals("xml"))
            {
                report = handler.generateReports(fileName, fileStream, useSBM, useLinFits, firstLetterRM).toFile();
            }

            response.setContentLengthLong(report.length());
            IOUtils.copy(new FileInputStream(report), response.getOutputStream());
            
        } catch (Exception e) {
            System.err.println(e);
        }

    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
