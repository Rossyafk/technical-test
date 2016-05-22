package com.rizandoelrizo.ij.server.web.view;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * View for a success login page.
 */
public class LoginSuccessView  {

    public void writeTo(OutputStream os) throws IOException {
        try (InputStream is = getClass().getResourceAsStream("/static/login-success.html")) {
            byte[] buffer = new byte[1024];
            int len = is.read(buffer);
            while (len != -1) {
                os.write(buffer, 0, len);
                len = is.read(buffer);
            }
        }
    }

}
