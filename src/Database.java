import java.util.*;

public class Database {
	private String name = new String();
	// �e�[�u���W��
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
	
	// �e�[�u���̖��O��ύX����
	public void changeTableName(String oldName, String newName){
		Table table = tables.get(oldName);
		table.setName(newName);
		tables.put(newName, table);
		tables.remove(oldName);
	}
	
	// ADD�������s����
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
	
	// SELECT�������s����
	// �����̃e�[�u������w�肵���t�B�[���h�̎w��l����v���郌�R�[�h�W�������e�[�u����Ԃ�
	public Table selectTable(
			Table table,						// SELECT�������s����e�[�u��
			ArrayList<String> fields,			// �������ɏo������t�B�[���h�W��
			ArrayList<Object> values,			// �������Ŏw�肳���l
			Operator logop){
		// fields���󂩁Cfields��values�̃T�C�Y���قȂ�ꍇ�C���̃e�[�u����Ԃ�
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
				// �w�肳�ꂽ���ׂẴt�B�[���h�̒l���Ή�����w��l�ƈ�v�����炻�̃��R�[�h�͑I�������
				for(j=0; j<fields.size() && record.get(fields.get(j)).equals(values.get(j)); j++);
				if(j == fields.size()){
					selected.addRecord(record);
				}
			}
		}else if(logop == Operator.OR){
			for(Iterator<LinkedHashMap<String, String>> iter=table.getRecords().iterator(); iter.hasNext(); ){
				LinkedHashMap<String, String> record = iter.next();
				int j;
				// �w�肳�ꂽ�����ꂩ�̃t�B�[���h�̒l���Ή�����w��l�ƈ�v�����炻�̃��R�[�h�͑I�������
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
	
	// PROJECT�������s����
	// �����̃��R�[�h�W���ƃt�B�[���h�W���Ƃ̎ˉe�����e�[�u����Ԃ�
	public Table projectTable(
			Table table,
			ArrayList<String> fields){
		// fields����̂Ƃ��C�܂��͈����̃e�[�u���ɑ��݂��Ȃ��t�B�[���h���܂�ł����ꍇ�C���̃e�[�u����Ԃ�
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
	
	// JOIN�������s����
	// 2�̃e�[�u���̃t�B�[���h�𓝍����C�����Ɋ�Â��ē������ꂽ���R�[�h�W�������e�[�u����Ԃ�
	public Table joinTable(
			Table table1,
			Table table2,
			ArrayList<String> fields1,
			ArrayList<String> fields2){
		// fields�̂ǂ��炩���󂩁C���邢�͗��҂̃T�C�Y���قȂ�ꍇ�C��̃e�[�u����Ԃ�
		if(fields1.isEmpty() || fields1.size() != fields2.size()){
			return new Table();
		}
		// �߂�l�Ƃ���e�[�u��joined������������
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
		
		// ���ꂼ��̃��R�[�h�ɂ��āC�w�肵���t�B�[���h�̗v�f����v����Ȃ烌�R�[�h�𓝍�����
		for(Iterator<LinkedHashMap<String, String>> iter1=table1.getRecords().iterator(); iter1.hasNext(); ){
			LinkedHashMap<String, String> record1 = iter1.next();
			for(Iterator<LinkedHashMap<String, String>> iter2=table2.getRecords().iterator(); iter2.hasNext(); ){
				LinkedHashMap<String, String> record2 = iter2.next();
				int p;
				// �w�肵�����ׂẴt�B�[���h�ɂ��Ĉ�v���Ă��邩���ׂ�
				for(p=0; p<fields1.size() && record1.get(fields1.get(p)).equals(
						record2.get(fields2.get(p))); p++);
				if(p == fields1.size()){
					LinkedHashMap<String, String> record = new LinkedHashMap<String, String>(record1);
					// �t�B�[���h���̕ύX�ɑ΂��鏈��
					for(int q=0; q<oldFields.size(); q++){
						record.put(newFields1.get(q), record.get(oldFields.get(q)));
						record.remove(oldFields.get(q));
					}
					record.putAll(record2);
					// �t�B�[���h���̕ύX�ɑ΂��鏈��
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
	
	// �e�[�u���̖��O��񋓂���
	public void printTableNames(){
		for(Iterator<String> iter=tables.keySet().iterator(); iter.hasNext(); ){
			System.out.print(iter.next() + "  ");
		}
		System.out.println();
	}
	
	// �e�[�u���W���Ɋ܂܂�邷�ׂẴe�[�u�����o�͂���
	public void printTables(){
		for(Iterator<Table> iter=tables.values().iterator(); iter.hasNext(); ){
			iter.next().printTable();
		}
		System.out.println();
	}
	
	// �e�[�u����CSV�t�@�C������ǂݍ���
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
	
	// �e�[�u����CSV�t�@�C���ɏ�������
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
