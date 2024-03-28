/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p/>
 * Copyright (c) Piotr Polak 2008-2017
 **************************************************/

package admin;

import androidx.annotation.NonNull;

import org.apache.commons.io.FileUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

import admin.logic.HTMLDocument;
import ro.polak.http.Headers;
import ro.polak.http.configuration.ServerConfig;
import ro.polak.http.configuration.impl.ServerConfigImpl;
import ro.polak.http.exception.ServletException;
import ro.polak.http.servlet.HttpServlet;
import ro.polak.http.servlet.HttpServletRequest;
import ro.polak.http.servlet.HttpServletResponse;
import ro.polak.http.utilities.IOUtilities;
import ro.polak.http.utilities.StringUtilities;
import util.SdcardUtil;

/**
 * 使用浏览器渲染txt文本内容
 */
public class OpenTxtFileServlet extends HttpServlet {

    private static final String ATTR_ADMIN_DRIVE_ACCESS_ENABLED = "admin.driveAccess.enabled";

    File externalStorageDirectory;

    @Override
    public void init() {
        externalStorageDirectory = SdcardUtil.getSDPath();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void service(final HttpServletRequest request, final HttpServletResponse response) throws ServletException {
        ServerConfig serverConfig = (ServerConfig) getServletContext().getAttribute(ServerConfig.class.getName());

        if (!serverConfig.getAttribute(ATTR_ADMIN_DRIVE_ACCESS_ENABLED).equals(ServerConfigImpl.TRUE)) {
            response.getWriter().println("Option disabled in configuration.");
            return;
        }

        boolean fileExists = false;

        if (!request.getQueryString().equals("")) {
            File f = new File(getPathname(request));
            if (f.exists() && f.isFile()) {
                fileExists = true;
                try {
                    printFile(f, response);
                } catch (IOException e) {
                    throw new ServletException(e);
                }
            }
        }

        if (!fileExists) {
            response.setStatus(HttpServletResponse.STATUS_NOT_FOUND);
            response.getWriter().print("File does not exist.");
        }
    }

    @NonNull
    private String getPathname(final HttpServletRequest request) {
        return externalStorageDirectory + StringUtilities.urlDecode(request.getQueryString());
    }

    private void printFile(final File file, final HttpServletResponse response) throws IOException {
        response.setHeader("content-type","text/html;charset=UTF-8");
        HTMLDocument doc = new HTMLDocument(file.getName());
        doc.setOwnerClass(getClass().getSimpleName());
        BufferedReader bf = new BufferedReader(new FileReader(file));
        String line = null;
        while ((line = bf.readLine()) != null) {
            doc.writeln(line);
            doc.write("</br>");
        }
        response.getWriter().print(doc);
    }
}
