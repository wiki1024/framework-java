package com.wiki.framework.mybatis.simple;

import com.wiki.framework.mybatis.mybatis.listener.OrderedListener;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Comparator;

public class TestOrder {

	@Test
	public void testOrder() {
		ArrayList<OrderedListener> list = new ArrayList<>();
		list.add(new OrderThree());
		list.add(new OrderOne());
		list.add(new OrderTwo());

		list.sort(Comparator.comparing(OrderedListener::getOrder, Comparator.nullsLast(Comparator.naturalOrder())));

		System.out.println("lll");
	}


	class OrderOne implements OrderedListener {
		@Override
		public int getOrder() {
			return 1;
		}
	}

	class OrderTwo implements OrderedListener {
		@Override
		public int getOrder() {
			return 2;
		}
	}

	class OrderThree implements OrderedListener {
		@Override
		public int getOrder() {
			return 3;
		}
	}
}
