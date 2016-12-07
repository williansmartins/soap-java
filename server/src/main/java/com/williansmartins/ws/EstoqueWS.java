package com.williansmartins.ws;

import java.util.List;

import javax.jws.WebService;
import javax.jws.WebMethod;

import com.williansmartins.modelo.item.Item;
import com.williansmartins.modelo.item.ItemDao;


@WebService
public class EstoqueWS {

	private ItemDao dao = new ItemDao();

	@WebMethod(operationName="todosOsItens")
	public List<Item> getItens() {
		System.out.println("Chamando todosItens()");
		return dao.todosItens();
	}
}