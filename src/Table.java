import java.util.*;

public class Table {
	private String name = new String();		// テーブルの名前(小文字)
	// レコード集合
	private LinkedHashSet<LinkedHashMap<String, String>> records = new LinkedHashSet<LinkedHashMap<String, String>>();
	
	// フィールド集合(すべて小文字の文字列とする)
	private LinkedHashSet<String> fields;
	
	public Table(){	
		fields = new LinkedHashSet<String>();
	}
	
	public Table(String name, Collection<String> fields){
		this.name = name.toLowerCase();
		this.fields = new LinkedHashSet<String>();
		for(Iterator<String> iter=fields.iterator(); iter.hasNext(); ){
			this.fields.add(iter.next().toLowerCase());
		}
	}
	/*
	public Table(String name, ArrayList<String> fields){
		this.name = name.toLowerCase();
		this.fields = new LinkedHashSet<String>();
		for(Iterator<String> iter=fields.iterator(); iter.hasNext(); ){
			this.fields.add(iter.next().toLowerCase());
		}
	}*/
	
	public void setName(String name){
		this.name = name.toLowerCase();
	}
	
	public String getName(){
		return name;
	}
	
	public void setRecords(LinkedHashSet<LinkedHashMap<String, String>> records){
		this.records = records;
	}
	
	public LinkedHashSet<LinkedHashMap<String, String>> getRecords(){
		return records;
	}
	
	public void addRecord(LinkedHashMap<String, String> record){
		records.add(record);
	}
	
	public void setFields(Collection<String> fields){
		if(records.isEmpty()){
			for(Iterator<String> iter=fields.iterator(); iter.hasNext(); ){
				this.fields.add(iter.next().toLowerCase());
			}
		// レコードが存在するなら，setFieldsによる変更は許可しない	
		}else{
			System.out.println("Table \"" + name + "\" has records.\nPlease use the \"changeField\"method");
		}	
	}
	/*
	public void setFields(ArrayList<String> fields){
		if(records.isEmpty()){
			for(Iterator<String> iter=fields.iterator(); iter.hasNext(); ){
				this.fields.add(iter.next().toLowerCase());
			}
		// レコードが存在するなら，setFieldsによる変更は許可しない	
		}else{
			System.out.println("Table \"" + name + "\" has records.\nPlease use the \"changeField\"method");
		}	
	}*/
	
	public void changeField(String oldField, String newField){
		String newFieldLow = newField.toLowerCase();
		// テーブルがoldFieldをもっていて，かつnewFieldをもっていないときに限る
		if(fields.contains(oldField) && !fields.contains(newFieldLow)){
			fields.remove(oldField);
			fields.add(newFieldLow);
			for(Iterator<LinkedHashMap<String, String>> iter=records.iterator(); iter.hasNext(); ){
				LinkedHashMap<String, String> record = iter.next();
				record.put(newField.toLowerCase(), record.get(oldField));
				record.remove(oldField);
			}
		}
	}
	
	public LinkedHashSet<String> getFields(){
		return fields;
	}
	
	// レコードの数を返す
	public int getCardinality(){
		return records.size();
	}
	
	// フィールドの数を返す
	public int getDegree(){
		return fields.size();
	}
	
	public boolean isEmpty(){
		if(fields.isEmpty())
			return true;
		else
			return false;
	}
	
	public boolean hasRecords(){
		return !records.isEmpty();
	}
	
	// テーブルの出力
	public void printTable(){
		System.out.println("Print table \"" + name + "\"");
		for(Iterator<String> iter=fields.iterator(); iter.hasNext(); ){
			System.out.printf("%21s |", iter.next());
		}
		System.out.println();
		for(int i=0; i<fields.size()*23; i++){
			System.out.print("-");
		}
		System.out.println();
		for(Iterator<LinkedHashMap<String, String>> iterR=records.iterator(); iterR.hasNext(); ){
			LinkedHashMap<String, String> record = iterR.next();
			for(Iterator<String> iterF=fields.iterator(); iterF.hasNext(); ){
				System.out.printf("%21s |", record.get(iterF.next()));
			}
			System.out.println();
		}
		System.out.println();
	}
}
