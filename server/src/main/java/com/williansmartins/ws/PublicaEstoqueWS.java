package com.williansmartins.ws;

import java.util.LinkedList;
import java.util.List;

import javax.xml.ws.Binding;
import javax.xml.ws.Endpoint;
import javax.xml.ws.handler.Handler;

public class PublicaEstoqueWS {

	public static void main(String[] args) {
		
//		EstoqueWS implementacaoWS = new EstoqueWS();
		String URL = "http://localhost:8080/estoquews";
//
//		System.out.println("EstoqueWS rodando: " + URL);
//		
//		// associando URL com a implementacao
//		Endpoint.publish(URL, implementacaoWS);
		
		Endpoint endpoint = Endpoint.create(new PublicaEstoqueWS());
        Binding binding = endpoint.getBinding();      
        List<Handler> handlerChain = new LinkedList<Handler>();
        handlerChain.add(new SecurityHandler("server"));
        binding.setHandlerChain(handlerChain);
        endpoint.publish(URL);
	}
}