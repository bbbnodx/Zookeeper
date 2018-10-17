import java.util.*;

public class Database {
	private String name = new String();
	// テーブル集合
	private LinkedHashMap<String, Table> tables = new LinkedHashMap<String, Table>();
	
	public Database(){}
	public Database(String name){
		this.name = name;
	}
	
	public void setName(String name){
		this.name = name;
	}
	
	public String getName(){
		return name;
	}
	
	public void setTables(LinkedHashMap<String, Table> tables){
		this.tables = tables;
	}
	
	public LinkedHashMap<String, Table> getTables(){
		return tables;
	}
	
	public void addTable(Table table){
		tables.put(table.getName(), table);
	}
	
	public void removeTable(String tableName){
		tables.remove(tables.get(tableName));
	}
	
	// テーブルの名前を変更する
	public void changeTableName(String oldName, String newName){
		Table table = tables.get(oldName);
		table.setName(newName);
		tables.put(newName, table);
		tables.remove(oldName);
	}
	
	// ADD文を実行する
	public Table addRecord(Table table, ArrayList<String> values){
		if(values.size() != table.getDegree()){
			return table;
		}
		LinkedHashMap<String, String> record = new LinkedHashMap<String, String>();
		for(Iterator<String> iterR=values.iterator(), iterF=table.getFields().iterator();
		iterR.hasNext(); ){
			record.put(iterF.next(), iterR.next());
		}
		table.addRecord(record);
		return table;
	}
	
	// SELECT文を実行する
	// 引数のテーブルから指定したフィールドの指定値が一致するレコード集合をもつテーブルを返す
	public Table selectTable(
			Table table,						// SELECT文を実行するテーブル
			ArrayList<String> fields,			// 条件文に出現するフィールド集合
			ArrayList<Object> values,			// 条件文で指定される値
			Operator logop){
		// fieldsが空か，fieldsとvaluesのサイズが異なる場合，元のテーブルを返す
		if(fields.isEmpty() || fields.size() != values.size()){
			//table.printTable();
			return table;
		}
		Table selected = new Table("selected", table.getFields());
		if(fields.size() == 1){
			for(Iterator<LinkedHashMap<String, String>> iter=table.getRecords().iterator(); iter.hasNext(); ){
				LinkedHashMap<String, String> record = iter.next();
				if(record.get(fields.get(0)).equals(values.get(0))){
					selected.addRecord(record);
				}
			}
		}else if(logop == Operator.AND){
			for(Iterator<LinkedHashMap<String, String>> iter=table.getRecords().iterator(); iter.hasNext(); ){
				LinkedHashMap<String, String> record = iter.next();
				int j;
				// 指定されたすべてのフィールドの値が対応する指定値と一致したらそのレコードは選択される
				for(j=0; j<fields.size() && record.get(fields.get(j)).equals(values.get(j)); j++);
				if(j == fields.size()){
					selected.addRecord(record);
				}
			}
		}else if(logop == Operator.OR){
			for(Iterator<LinkedHashMap<String, String>> iter=table.getRecords().iterator(); iter.hasNext(); ){
				LinkedHashMap<String, String> record = iter.next();
				int j;
				// 指定されたいずれかのフィールドの値が対応する指定値と一致したらそのレコードは選択される
				for(j=0; j<fields.size() && !record.get(fields.get(j)).equals(values.get(j)); j++);
				if(j < fields.size()){
					selected.addRecord(record);
				}
			}
		}else{
			return new Table();
		}
		
		if(selected.getRecords().isEmpty()){
			System.out.println("Caution : No record selected from " + table.getName() + ".");
		}
		
		//selected.printTable();
		return selected;
	}
	
	// PROJECT文を実行する
	// 引数のレコード集合とフィールド集合との射影をもつテーブルを返す
	public Table projectTable(
			Table table,
			ArrayList<String> fields){
		// fieldsが空のとき，または引数のテーブルに存在しないフィールドを含んでいた場合，元のテーブルを返す
		if(fields.isEmpty() /*|| !table.getFields().containsAll(fields)*/){
			//System.out.println("Invalid fields.\nReturn this table.");
			table.printTable();
			return table;
		}

		Table projected = new Table("projected", fields);
		for(Iterator<LinkedHashMap<String, String>> iter=table.getRecords().iterator(); iter.hasNext(); ){
			LinkedHashMap<String, String> record = iter.next();
			LinkedHashMap<String, String> newrecord = new LinkedHashMap<String, String>();
			for(int j=0; j<fields.size(); j++){
				newrecord.put(fields.get(j), record.get(fields.get(j)));
			}
			projected.addRecord(newrecord);
		}
		//projected.printTable();
		return projected;
	}
	
	// JOIN文を実行する
	// 2つのテーブルのフィールドを統合し，条件に基づいて統合されたレコード集合をもつテーブルを返す
	public Table joinTable(
			Table table1,
			Table table2,
			ArrayList<String> fields1,
			ArrayList<String> fields2){
		// fieldsのどちらかが空か，あるいは両者のサイズが異なる場合，空のテーブルを返す
		if(fields1.isEmpty() || fields1.size() != fields2.size()){
			return new Table();
		}
		// 戻り値とするテーブルjoinedを初期化する
		ArrayList<String> joinedFields = new ArrayList<String>(table1.getFields());
		ArrayList<String> oldFields = new ArrayList<String>();
		ArrayList<String> newFields1 = new ArrayList<String>();
		ArrayList<String> newFields2 = new ArrayList<String>();
		
		for(Iterator<String> iter=table2.getFields().iterator(); iter.hasNext(); ){
			String field = iter.next();
			if(joinedFields.contains(field)){
				oldFields.add(field);
				newFields1.add(field + "." + table1.getName());
				newFields2.add(field + "." + table2.getName());
				joinedFields.set(joinedFields.indexOf(field),
						field + "." + table1.getName());
				joinedFields.add(field + "." + table2.getName());
			}else{
				joinedFields.add(field);
			}
		}
		
		Table joined = new Table("joined", joinedFields);
		
		// それぞれのレコードについて，指定したフィールドの要素が一致するならレコードを統合する
		for(Iterator<LinkedHashMap<String, String>> iter1=table1.getRecords().iterator(); iter1.hasNext(); ){
			LinkedHashMap<String, String> record1 = iter1.next();
			for(Iterator<LinkedHashMap<String, String>> iter2=table2.getRecords().iterator(); iter2.hasNext(); ){
				LinkedHashMap<String, String> record2 = iter2.next();
				int p;
				// 指定したすべてのフィールドについて一致しているか調べる
				for(p=0; p<fields1.size() && record1.get(fields1.get(p)).equals(
						record2.get(fields2.get(p))); p++);
				if(p == fields1.size()){
					LinkedHashMap<String, String> record = new LinkedHashMap<String, String>(record1);
					// フィールド名の変更に対する処理
					for(int q=0; q<oldFields.size(); q++){
						record.put(newFields1.get(q), record.get(oldFields.get(q)));
						record.remove(oldFields.get(q));
					}
					record.putAll(record2);
					// フィールド名の変更に対する処理
					for(int q=0; q<oldFields.size(); q++){
						record.put(newFields2.get(q), record.get(oldFields.get(q)));
						record.remove(oldFields.get(q));
					}
					joined.addRecord(record);
				}
			}
		}
		//joined.printTable();
		return joined;
	}
	
	// テーブルの名前を列挙する
	public void printTableNames(){
		for(Iterator<String> iter=tables.keySet().iterator(); iter.hasNext(); ){
			System.out.print(iter.next() + "  ");
		}
		System.out.println();
	}
	
	// テーブル集合に含まれるすべてのテーブルを出力する
	public void printTables(){
		for(Iterator<Table> iter=tables.values().iterator(); iter.hasNext(); ){
			iter.next().printTable();
		}
		System.out.println();
	}
	
	// テーブルをCSVファイルから読み込む
	public Table readTableFromCSV(String tableName){
		ArrayList<ArrayList<String>> list = IOcsv.readCSV(tableName);
		
		if(list.size() < 1)
			return new Table();
		
		Table table = new Table(tableName, list.get(0));
		
		for(int i=1; i<list.size(); i++){
			LinkedHashMap<String, String> record = new LinkedHashMap<String, String>();
			for(int j=0; j<table.getFields().size(); j++){
				record.put(list.get(0).get(j), list.get(i).get(j));				
			}
			table.addRecord(record);
		}
		tables.put(tableName.toLowerCase(), table);
		//System.out.println("Load table \"" + table.getName() + "\" from " + table.getName() + ".csv");
		
		return table;
	}
	
	// テーブルをCSVファイルに書き込む
	public void writeTableToCSV(Table table, String name){
		if(!table.isEmpty()){
			ArrayList<ArrayList<String>> list = new ArrayList<ArrayList<String>>();
			ArrayList<String> starr = new ArrayList<String>();
			starr.addAll(table.getFields());
			list.add(starr);
			for(Iterator<LinkedHashMap<String, String>> iterR=table.getRecords().iterator(); iterR.hasNext(); ){
				LinkedHashMap<String, String> record = iterR.next();
				starr.clear();
				for(Iterator<String> iterF=table.getFields().iterator(); iterF.hasNext(); ){
					starr.add(record.get(iterF.next()));
				}
				list.add(starr);
			}
			IOcsv.writeCSV(name, list);
			System.out.println("Save table \"" + table.getName() + "\" to " + name + ".csv");
		}
	}
}
