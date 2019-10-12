/*
 * Created on Jan 1, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.wiki.framework.mybatis.database;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.TreeMap;

/**
 * @author thomason
 * @version 1.0
 * @since 2017/10/9 上午9:15
 */
public class ForeignKeys {

	private final Table pkTable;

	private final TreeMap<String, List<ForeignKey>> keys;

	public ForeignKeys(Table pkTable) {
		this.pkTable = pkTable;
		this.keys = new TreeMap<>();
	}

	public void addForeignKey(ForeignKey foreignKey) {
		List<ForeignKey> foreignKeyList = keys.putIfAbsent(foreignKey.getFkTable(), new ArrayList<>());
		foreignKeyList.add(foreignKey);
		foreignKeyList.sort(Comparator.comparingInt(ForeignKey::getSeq));
	}

	public Table getPkTable() {
		return pkTable;
	}

	public TreeMap<String, List<ForeignKey>> getKeys() {
		return keys;
	}
}
