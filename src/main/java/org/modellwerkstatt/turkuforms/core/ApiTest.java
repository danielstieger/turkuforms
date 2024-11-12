package org.modellwerkstatt.turkuforms.core;

import com.vaadin.flow.server.VaadinServlet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/* @WebServlet(urlPatterns = "/apitest", name = "DynamicContentServlet")
public class ApiTest extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html; charset=utf-8");
        String name = req.getParameter("name");
        if (name == null) {
            name = "????";
        }

        try {
            name += VaadinServlet.getCurrent();

        } catch (Exception e) {
            name += "" + e;
        }

        String svg = "<?xml version='1.0' encoding='UTF-8' standalone='no'?>"
                + "<svg xmlns='http://www.w3.org/2000/svg' "
                + "xmlns:xlink='http://www.w3.org/1999/xlink'>"
                + "<rect x='10' y='10' height='100' width='100' "
                + "style=' fill: #90C3D4'/><text x='30' y='30' fill='red'>"
                + name + "</text>" + "</svg>";

        resp.getWriter().write(svg);
    }
} */

public class ApiTest {

}
