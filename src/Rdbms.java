import java.util.*;

public class Rdbms {
	HashMap<String, Database> dbs = new HashMap<String, Database>();
	Database curdb;
	
	// �O������f�[�^�x�[�X�ɐڑ�����
	public void connect(String dbName){
		if(dbs.containsKey(dbName)){
			//System.out.println("connect \"" + dbName + "\"");
			curdb = dbs.get(dbName);
		}else{
			System.out.println("\"" + dbName + "\" doesn't exist.");
		}
	}

	public Database getCurrentDB(){
		return curdb;
	}
	
	public Set<String> getDBnames(){
		return dbs.keySet();
	}
		
	public Table getTable(String tableName){
		return curdb.getTables().get(tableName);
	}
	
	public void createDatabase(String dbName){
		if(!dbs.containsKey(dbName)){
			//System.out.println("create \"" + dbName + "\"");
			dbs.put(dbName, new Database(dbName));
			curdb = dbs.get(dbName);
		}else{
			System.out.println("\"" + dbName + "\" exists.");
		}
	}
	
	public void setCurDatabase(String dbName){
		curdb = dbs.get(dbName);
	}
	
	public void addTable(Table table){
		curdb.addTable(table);
	}
	
	public void addTableAs(Table table, String name){
		table.setName(name);
		curdb.addTable(table);
	}
	
	public void removeTable(String tableName){
		curdb.removeTable(tableName);
	}
	
	public void updateTable(Table table){
		if(containsTable(table.getName())){
			removeTable(table.getName());
		}
		addTable(table);
	}
	
	public void changeTableName(String oldname, String newname){
		curdb.changeTableName(oldname, newname);
	}
	
	public void readTableFromCSV(String filename){
		curdb.readTableFromCSV(filename);
	}
	
	public void writeTableToCSV(Table table, String filename){
		curdb.writeTableToCSV(table, filename);
	}
	
	public boolean containsTable(String tableName){
		if(curdb.getTables().containsKey(tableName)){
			return true;
		}else{
			return false;
		}
	}
	
	// �����̃t�B�[���h�ɂ�����l���ŏ��Ɉ�v���郌�R�[�h��index��Ԃ����\�b�h
	public int indexOfRecord(Table table, String field, String value){
		int i=0;
		for(Iterator<LinkedHashMap<String, String>> iter=table.getRecords().iterator();
		iter.hasNext() && !iter.next().get(field).equals(value); i++);
		if(i < table.getCardinality()){
			return i;
		}else{
			return -1;
		}
	}
	
	// �����̃t�B�[���h�ɂ�����l���Ō�Ɉ�v���郌�R�[�h��index��Ԃ����\�b�h
	public int lastIndexOfRecord(Table table, String field, String value){
		int i=0;
		int idx=-1;
		for(Iterator<LinkedHashMap<String, String>> iter=table.getRecords().iterator();
		iter.hasNext(); i++){
			if(iter.next().get(field).equals(value)){
				idx = i;
			}
		}
		return idx;
	}
	
	public int indexOfField(Table table, String field){
		int i=0;
		for(Iterator<String> iter = table.getFields().iterator();
		iter.hasNext() && !iter.next().equals(field); i++);
		if(i < table.getDegree()){
			return i;
		}else{
			return -1;
		}
	}
	
	// �f�[�^�x�[�X�Ɋւ��閽�߂����s����
	public Table execInstruction(String query){
		Instruction inst = new Instruction(query);
		return inst.execInstruction(curdb, inst.getInst());
	}
	
	// �w�肵���e�[�u���̎w�肵���t�B�[���h�̒l����v���郌�R�[�h��ArrayList�Ƃ��ĕԂ����\�b�h(�s���o)
	public ArrayList<String> fetchRecord(Table table, String field, String value){
		return fetchRecord(table, indexOfRecord(table, field, value));
	}
	
	// �w�肵���e�[�u����idx�Ԗڂ̃��R�[�h��ArrayList�Ƃ��ĕԂ����\�b�h(�s���o)
	public ArrayList<String> fetchRecord(Table table, int idx){
		if(idx > table.getCardinality()){
			System.out.println("The number of records is less than " + idx + "\nReturn a empty ArrayList");
			return new ArrayList<String>();
		}
		LinkedHashMap<String, String> record = new LinkedHashMap<String, String>();
		Iterator<LinkedHashMap<String, String>> iterR = table.getRecords().iterator();
		// idx�Ԗڂ�record�����o��
		for(int i=0; i<=idx; i++){
			record = iterR.next();
		}
		// record�̑S�v�f��ArrayList�Ɋi�[����
		ArrayList<String> fetched = new ArrayList<String>();
		for(Iterator<String> iter=record.values().iterator(); iter.hasNext(); ){
			fetched.add(iter.next());
		}
		return fetched;	
	}
	
	// �w�肵���e�[�u���̎w�肵���t�B�[���h�̑S���R�[�h��ArrayList�Ƃ��ĕԂ����\�b�h(�񒊏o)
	public ArrayList<String> fetchColumn(Table table, String field){
		LinkedHashSet<String> fetched = new LinkedHashSet<String>();
		for(Iterator<LinkedHashMap<String, String>> iter = table.getRecords().iterator();
		iter.hasNext(); ){
			fetched.add(iter.next().get(field));
		}
		return new ArrayList<String>(fetched);
	}
	
	// �w�肵���e�[�u����idx�Ԗڂ̃t�B�[���h�̑S���R�[�h��ArrayList�Ƃ��ĕԂ����\�b�h(�񒊏o)
	public ArrayList<String> fetchColumn(Table table, int idx){
		if(idx > table.getDegree()){
			System.out.println("The number of fields is less than " + idx + "\nReturn a empty ArrayList");
			return new ArrayList<String>();
		}
		String field = new String();
		Iterator<String> iterF = table.getFields().iterator();
		// idx�Ԗڂ̃t�B�[���h�����o��
		for(int i=0; i<idx; i++){
			field = iterF.next();
		}
		
		// �S���R�[�h����field�̗v�f�����o��
		LinkedHashSet<String> fetched = new LinkedHashSet<String>();
		for(Iterator<LinkedHashMap<String, String>> iter = table.getRecords().iterator();
		iter.hasNext(); ){
			fetched.add(iter.next().get(field));
		}
		return new ArrayList<String>(fetched);
	}
	
	// �R�}���h���C������f�[�^�x�[�X�𒼐ڑ��삷��
	public void exec(){
		Instruction.commandHelp();
		// �C�x���g�쓮�����Ŗ��߂���́E���s����
		while(true){
			Instruction inst = new Instruction();
			System.out.print("> ");
			inst.getInstruction();
			if(inst.getInst().get(0).toLowerCase().equals("quit")){
				System.out.println("quit");
				break;
			}else {
				inst.execInstruction(curdb, inst.getInst());
			}
		}
	}
}
