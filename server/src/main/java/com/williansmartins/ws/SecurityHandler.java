package com.williansmartins.ws;

import java.util.Set;
import java.util.HashSet;
import javax.xml.namespace.QName;
import javax.xml.soap.SOAPMessage;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;
import java.io.FileInputStream;
import com.sun.xml.wss.ProcessingContext;
import com.sun.xml.wss.SubjectAccessor;
import com.sun.xml.wss.XWSSProcessorFactory;
import com.sun.xml.wss.XWSSProcessor;
import com.sun.xml.wss.XWSSecurityException;

public class SecurityHandler implements SOAPHandler<SOAPMessageContext> {
   
    XWSSProcessor sprocessor = null;
    String clientOrServer = null;
    XWSSProcessor cprocessor = null;
    /** Creates a new instance of SecurityHandler */
    public SecurityHandler(String cOrs) {
        FileInputStream serverConfig = null;
        FileInputStream clientConfig = null;
        this.clientOrServer = cOrs;               
        try {
            if ("client".equals(this.clientOrServer)) {
                //read client side security config
                clientConfig = new java.io.FileInputStream(
                        new java.io.File("META-INF/user-pass-authenticate-client.xml"));
                //Create a XWSSProcessFactory.
                XWSSProcessorFactory factory = XWSSProcessorFactory.newInstance();
                cprocessor = factory.createProcessorForSecurityConfiguration(
                        clientConfig, new SecurityEnvironmentHandler("client"));
                clientConfig.close();
            } else {
                //read server side security configuration
                serverConfig = new java.io.FileInputStream(
                        new java.io.File("META-INF/user-pass-authenticate-server.xml"));
                //Create a XWSSProcessFactory.
                XWSSProcessorFactory factory = XWSSProcessorFactory.newInstance();
                sprocessor = factory.createProcessorForSecurityConfiguration(
                        serverConfig, new SecurityEnvironmentHandler("server"));
                serverConfig.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

    }
   
    public Set<QName> getHeaders() {
         QName securityHeader = new QName("http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd", "Security", "wsse");
         HashSet<QName> headers = new HashSet<QName>();
         headers.add(securityHeader);
         return headers;
    } 

    public boolean handleFault(SOAPMessageContext messageContext) {
        return true;
    }

     public boolean handleMessage(SOAPMessageContext messageContext) {
         if ("client".equals(this.clientOrServer)) {
             secureClient(messageContext);
         } else {
             secureServer(messageContext);
         }
        return true;
    }
    public void close(MessageContext messageContext) {}
   
    private void secureServer(SOAPMessageContext messageContext)
    {
        Boolean outMessageIndicator = (Boolean)       
        messageContext.get (MessageContext.MESSAGE_OUTBOUND_PROPERTY);
        SOAPMessage message = messageContext.getMessage();
       
        if (outMessageIndicator.booleanValue()) {
            System.out.println("\nOutbound SOAP:");
            // do nothing....
            return;
        } else {
            System.out.println("\nInbound SOAP:");
             //verify the secured message.
            try{
                ProcessingContext context =  sprocessor.createProcessingContext(message);
                context.setSOAPMessage(message);
                SOAPMessage verifiedMsg= null;
                verifiedMsg= sprocessor.verifyInboundMessage(context);
                System.out.println("\nRequester Subject " + SubjectAccessor.getRequesterSubject(context));
                messageContext.setMessage(verifiedMsg);
            } catch (XWSSecurityException ex) {
                //create a Message with a Fault in it
                //messageContext.setMessage(createFaultResponse(ex));
                ex.printStackTrace();
                throw new WebServiceException(ex);
            } catch(Exception ex){
                ex.printStackTrace();
                throw new WebServiceException(ex);
            }
        }
    }   

    private SOAPMessage createFaultResponse(XWSSecurityException ex) {
        // TODO: add code here
        return null;
    }
    private void secureClient(SOAPMessageContext messageContext) {
        Boolean outMessageIndicator = (Boolean)       
        messageContext.get (MessageContext.MESSAGE_OUTBOUND_PROPERTY);
        SOAPMessage message = messageContext.getMessage();
        if (outMessageIndicator.booleanValue()) {
            System.out.println("\nOutbound SOAP:");
            ProcessingContext context;
            try {
                context = cprocessor.createProcessingContext(message);
                context.setSOAPMessage(message);
                SOAPMessage secureMsg = cprocessor.secureOutboundMessage(context);
                secureMsg.writeTo(System.out);
                messageContext.setMessage(secureMsg);
            } catch (XWSSecurityException ex) {
                ex.printStackTrace();
                throw new RuntimeException(ex);
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            } 
            return;
        } else {
            System.out.println("\nInbound SOAP:");
            //do nothing
            return;
        }     
    }
}