package com.williansmartins.ws;

import java.io.IOException;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;
import com.sun.xml.wss.impl.callback.PasswordCallback;
import com.sun.xml.wss.impl.callback.PasswordValidationCallback;
import com.sun.xml.wss.impl.callback.UsernameCallback;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

public class SecurityEnvironmentHandler implements CallbackHandler {
   
    private static final UnsupportedCallbackException unsupported =
    new UnsupportedCallbackException(null, "Unsupported Callback Type Encountered");
    
    /** Creates a new instance of SecurityEnvironmentHandler */
    public SecurityEnvironmentHandler(String arg) {
    }
    
    private String readLine() throws IOException {
        return new BufferedReader
            (new InputStreamReader(System.in)).readLine();
    }

    public void handle(Callback[] callbacks) throws IOException, UnsupportedCallbackException {
        for (int i=0; i < callbacks.length; i++) {
            if (callbacks[i] instanceof PasswordValidationCallback) {
                PasswordValidationCallback cb = (PasswordValidationCallback) callbacks[i];
                if (cb.getRequest() instanceof PasswordValidationCallback.PlainTextPasswordRequest) {
                    cb.setValidator(new PlainTextPasswordValidator());
                    
                } else if (cb.getRequest() instanceof PasswordValidationCallback.DigestPasswordRequest) {
                    PasswordValidationCallback.DigestPasswordRequest request =
                            (PasswordValidationCallback.DigestPasswordRequest) cb.getRequest();
                    String username = request.getUsername();
                    if ("Ron".equals(username)) {
                        request.setPassword("noR");
                        cb.setValidator(new PasswordValidationCallback.DigestPasswordValidator());
                    }
                }
            } else if (callbacks[i] instanceof UsernameCallback) {
                UsernameCallback cb = (UsernameCallback)callbacks[i];
                System.out.println("Username: ");
                String username= readLine();
                if (username != null) {
                    cb.setUsername(username);
                }
                
            } else if (callbacks[i] instanceof PasswordCallback) {
                PasswordCallback cb = (PasswordCallback)callbacks[i];
                System.out.println("Password: ");
                String password = readLine();
                if (password != null) {
                    cb.setPassword(password);
                }
            } else {
                throw unsupported;
            }
        }
    }
    
     private class PlainTextPasswordValidator implements PasswordValidationCallback.PasswordValidator { 
        public boolean validate(PasswordValidationCallback.Request request)
        throws PasswordValidationCallback.PasswordValidationException {
            
            PasswordValidationCallback.PlainTextPasswordRequest plainTextRequest =
            (PasswordValidationCallback.PlainTextPasswordRequest) request;
            if ("Ron".equals(plainTextRequest.getUsername()) &&
            "noR".equals(plainTextRequest.getPassword())) {
                return true;
            }
            return false;
        }
    }
}
