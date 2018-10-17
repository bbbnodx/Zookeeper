import java.io.*;
import java.util.*;

public class Instruction {
	// ���߂����P�ʂɋ�؂�C������z��Ɋi�[����
	private ArrayList<String> inst = new ArrayList<String>();
	// ����q���������݂���Ƃ��Csubinst�Ɋi�[����
	private ArrayList<Instruction> subinst = new ArrayList<Instruction>();
	
	public Instruction(){}
	public Instruction(String st){
		getInstruction(st);
	}
	
	public ArrayList<String> getInst(){
		return inst;
	}
	
	public void setInst(ArrayList<String> inst){
		this.inst = inst;
	}
	
	public ArrayList<Instruction> getSubinst(){
		return subinst;
	}
	
	public void setSubinst(ArrayList<Instruction> subinst){
		this.subinst = subinst;
	}
	
	// �W�����͂���擾����������𕪊����ĕԂ����\�b�h
	public ArrayList<String> getInstruction(){
		try{
			BufferedReader buf = new BufferedReader(new InputStreamReader(System.in));
			String st = buf.readLine();
			inst = splitInstruction(st);
		} catch (IOException e) {
			// BufferedReader�I�u�W�F�N�g�̃N���[�Y���̗�O�ߑ�
			e.printStackTrace();
		}
		return inst;
	}
	
	// �W�����͂���擾����������𕪊����ĕԂ����\�b�h
	public ArrayList<String> getInstruction(String st){
		inst = splitInstruction(st);
		return inst;
	}
	
	// ���߂Ƃ��ė^����ꂽ����������P�ʂɕ�������
	// []�̓����̖��߂��珇�Ɋi�[�����
	// ���̖��߂�[]������"TABLE"�ɒu�������
	// �߂�l�͌��ɕ������ꂽ������z��̔z��ƂȂ�
	public ArrayList<String> splitInstruction(String st){
		st = st.trim();
		// depth�͓���q�̐[����\���Cbegin��end�͂��ꂼ��O����'['�C']'��index��ۑ�����
		int depth = 0;
		ArrayList<Integer> begin = new ArrayList<Integer>();
		ArrayList<Integer> end = new ArrayList<Integer>();
		// []�����݂���ꍇ�C[]�̊O�̖��߂𕪊����C���̖��߂�[]��TABLE�ɒu������
		for(int i=0; i<st.length(); i++){
			if(st.charAt(i) == '['){
				if(depth++ == 0)
					begin.add(i+1);
			}else if(st.charAt(i) == ']'){
				if(--depth == 0)
					end.add(i);
			}
		}
		if(!begin.isEmpty() && depth == 0){
			for(int i=0; i<begin.size(); i++){
				subinst.add(new Instruction());
			}
			// �u���ɂ���ĕ����񂪕ϓ�����̂ŁC�������u������
			for(int i=begin.size()-1; i>=0; i--){
				subinst.get(i).splitInstruction(st.substring(begin.get(i), end.get(i)));
				st = st.replace(st.subSequence(begin.get(i)-1, end.get(i)+1), "TABLE");
			}
		}
		// �󔒂���؂蕶���Ƃ��ĕ�������
		String[] starr = st.split(" +");
		for(int i=0; i<starr.length; i++){
			inst.add(starr[i]);
		}
		return inst;
	}
	
	// ���߂����s���郁�\�b�h
	public Table execInstruction(Database database, ArrayList<String> inst){
		//System.out.println(inst);
		if(!inst.isEmpty()){
			// load���߂̎��s
			if(inst.get(0).toLowerCase().equals("load")){
				database.readTableFromCSV(inst.get(1));
			// save���߂̎��s
			}else if(inst.get(0).toLowerCase().equals("save")){
				Table table = new Table();
				// �e�[�u��������q[]�̕��ŗ^����ꂽ�ꍇ�Csubinst�����s���ĕԂ����e�[�u����ݒ肷��
				if(inst.get(1).equals("TABLE")){
					table = subinst.get(0).execInstruction(database, subinst.get(0).getInst());
					if(table.isEmpty()){
						System.out.println("Table \"" + inst.get(1) + "\" doesn't exist.");
						clear();
						return table;
					}
				// �e�[�u�����e�[�u�����ŗ^����ꂽ�ꍇ�C�e�[�u���W������e�[�u�����擾����
				}else{
					if(database.getTables().containsKey(inst.get(1).toLowerCase())){
						table = database.getTables().get(inst.get(1).toLowerCase());
					}else{
						System.out.println("Table \"" + inst.get(1) + "\" doesn't exist.");
						clear();
						return table;
					}
				}
				// �ʂ̃e�[�u�����ŕۑ�����ꍇ�Ctables�ɂ��ǉ�����
				if(inst.contains("as")){
					String tableName = inst.get(inst.indexOf("as")+1);
					database.writeTableToCSV(table, tableName);
					database.readTableFromCSV(tableName);
				}else{
					database.writeTableToCSV(table, table.getName());
				}
				return table;
			// ADD���̎��s
			}else if(inst.get(0).toLowerCase().equals("add")){
				Table table = new Table();
				int to = inst.indexOf("to");
				// �e�[�u��������q[]�̕��ŗ^����ꂽ�ꍇ�Csubinst�����s���ĕԂ����e�[�u����ݒ肷��
				if(inst.get(to+1).equals("TABLE")){
					table = subinst.get(0).execInstruction(database, subinst.get(0).getInst());
					if(table.isEmpty()){
						System.out.println("Table \"" + inst.get(to+1) + "\" doesn't exist.");
						clear();
						return table;
					}
				// �e�[�u�����e�[�u�����ŗ^����ꂽ�ꍇ�C�e�[�u���W������e�[�u�����擾����
				}else{
					if(database.getTables().containsKey(inst.get(to+1).toLowerCase())){
						table = database.getTables().get(inst.get(to+1).toLowerCase());
					}else{
						System.out.println("Table \"" + inst.get(to+1) + "\" doesn't exist.");
						clear();
						return table;
					}
				}
				ArrayList<String> values = new ArrayList<String>();
				for(int i=1; i<to; i++){
					values.add(inst.get(i));
				}
				clear();
				return database.addRecord(table, values);
				
			// SELECT���̎��s
			}else if(inst.get(0).toLowerCase().equals("select")){
				Table table = new Table();
				// �e�[�u��������q[]�̕��ŗ^����ꂽ�ꍇ�Csubinst�����s���ĕԂ����e�[�u����ݒ肷��
				if(inst.get(1).equals("TABLE")){
					table = subinst.get(0).execInstruction(database, subinst.get(0).getInst());
					if(table.isEmpty()){
						System.out.println("Table \"" + inst.get(1) + "\" doesn't exist.");
						clear();
						return table;
					}
				// �e�[�u�����e�[�u�����ŗ^����ꂽ�ꍇ�C�e�[�u���W������e�[�u�����擾����
				}else{
					if(database.getTables().containsKey(inst.get(1).toLowerCase())){
						table = database.getTables().get(inst.get(1).toLowerCase());
					}else{
						System.out.println("Table \"" + inst.get(1) + "\" doesn't exist.");
						clear();
						return table;
					}
				}
				ArrayList<String> fields = new ArrayList<String>();
				ArrayList<Object> values = new ArrayList<Object>();
				Operator logop = Operator.EMPTY;
				// "with"�ȍ~�𒲂ׁCfields��values��ݒ肷��
				for(int i=inst.indexOf("with")+1; i<inst.size(); i++){
					if(inst.get(i).equals("=")){
						fields.add(inst.get(i-1).toLowerCase());
						// values�̐ݒ�
						// ������int�^�ɕϊ�����
						if(inst.get(i+1).matches("[0-9]+")){
							values.add(Integer.valueOf(inst.get(i+1)).intValue());
						// ������double�^�ɕϊ�����
						}else if(inst.get(i+1).matches("[0-9]*\\.[0-9]+")){
							values.add(Double.valueOf(inst.get(i+1)).doubleValue());
						}else{
							values.add(inst.get(i+1));
						}
					}
				}
				if(inst.contains("and")){
					logop = Operator.AND;
				}else if(inst.contains("or")){
					logop = Operator.OR;
				}
				//System.out.println("fields:" + fields + "\n" + "values:" + values);
				clear();
				return database.selectTable(table, fields, values, logop);
			
			// PROJECT���̎��s
			}else if(inst.get(0).toLowerCase().equals("project")){
				Table table = new Table();
				// �e�[�u��������q[]�̕��ŗ^����ꂽ�ꍇ�Csubinst�����s���ĕԂ����e�[�u����ݒ肷��
				if(inst.get(1).equals("TABLE")){
					table = subinst.get(0).execInstruction(database, subinst.get(0).getInst());
					if(table.isEmpty()){
						System.out.println("Table \"" + inst.get(1) + "\" doesn't exist.");
						clear();
						return table;
					}
				// �e�[�u�����e�[�u�����ŗ^����ꂽ�ꍇ�C�e�[�u���W������e�[�u�����擾����
				}else{table = database.getTables().get(inst.get(1).toLowerCase());
					if(database.getTables().containsKey(inst.get(1).toLowerCase())){
						table = database.getTables().get(inst.get(1).toLowerCase());
					}else{
						System.out.println("Table \"" + inst.get(1) + "\" doesn't exist.");
						clear();
						return table;
					}
				}
				
				ArrayList<String> fields = new ArrayList<String>();
				int i;
				// ���ߕ�����"over"�����݂��邩�𒲂ׂ�
				// ���݂���Ȃ�C����index��p����̂�for�����g���Ē��ׂ�
				for(i=0; i<inst.size() && !inst.get(i).equals("over"); i++);
				if(i < inst.size()){
					fields.add(inst.get(++i).toLowerCase());
					for(; i<inst.size(); i++){
						if(inst.get(i).equals("and")){
							fields.add(inst.get(i+1).toLowerCase());
						}
					}
				}
				
				//System.out.println("fields:" + fields);
				clear();
				return database.projectTable(table, fields);
			
			// JOIN���̎��s
			}else if(inst.get(0).toLowerCase().equals("join")){
				int i = inst.indexOf("and");
				int sub = 0;
				// table1��ݒ肷��
				Table table1 = new Table();
				// �e�[�u��������q[]�̕��ŗ^����ꂽ�ꍇ�Csubinst�����s���ĕԂ����e�[�u����ݒ肷��
				if(inst.get(i-1).equals("TABLE")){
					table1 = subinst.get(sub).execInstruction(database, subinst.get(sub).getInst());
					sub++;
					if(table1.isEmpty()){
						System.out.println("Table \"" + inst.get(i-1) + "\" doesn't exist.");
						clear();
						return table1;
					}
				// �e�[�u�����e�[�u�����ŗ^����ꂽ�ꍇ�C�e�[�u���W������e�[�u�����擾����
				}else{
					if(database.getTables().containsKey(inst.get(i-1).toLowerCase())){
						table1 = database.getTables().get(inst.get(i-1).toLowerCase());
					}else{
						System.out.println("Table \"" + inst.get(i-1) + "\" doesn't exist.");
						clear();
						return table1;
					}
				}
				// table2�̐ݒ�
				Table table2 = new Table();
				// �e�[�u����[]�̕��ŗ^����ꂽ�ꍇ�Csubinst�����s���ĕԂ����e�[�u����ݒ肷��
				if(inst.get(i+1).equals("TABLE")){
					table2 = subinst.get(sub).execInstruction(database, subinst.get(sub).getInst());
					if(table2.isEmpty()){
						System.out.println("Table \"" + inst.get(i+1) + "\" doesn't exist.");
						clear();
						return table2;
					}
				// �e�[�u�����e�[�u�����ŗ^����ꂽ�ꍇ�C�e�[�u���W������e�[�u�����擾����
				}else{
					if(database.getTables().containsKey(inst.get(i+1).toLowerCase())){
						table2 = database.getTables().get(inst.get(i+1).toLowerCase());
					}else{
						System.out.println("Table \"" + inst.get(1+1) + "\" doesn't exist.");
						clear();
						return table2;
					}
				}
				if(table1.isEmpty() || table2.isEmpty()){
					System.out.println("Table \"" + inst.get(i-1) + "\" doesn't exist.");
				}
				// ��r����t�B�[���h���擾����
				ArrayList<String> fields1 = new ArrayList<String>();
				ArrayList<String> fields2 = new ArrayList<String>();
				// with�ȍ~�̖��߂���C"="�̑O��̌����t�B�[���h�Ƃ��Ď擾����
				for(i=inst.indexOf("with")+1; i<inst.size(); i++){
					if(inst.get(i).equals("=")){
						fields1.add(inst.get(i-1).toLowerCase());
						fields2.add(inst.get(i+1).toLowerCase());
					}
				}
				//System.out.println("fields1:" + fields1 + "\n" + "fields2:" + fields2);
				clear();
				return database.joinTable(table1, table2, fields1, fields2);
				
			// list���߂̎��s
			}else if(inst.get(0).toLowerCase().equals("list")){
				database.printTableNames();
			// help���߂̎��s
			}else if(inst.get(0).toLowerCase().equals("help")){
				commandHelp();
			// ����ȊO�̏ꍇ�C�w���v��\������
			}else{
				System.out.println("Invalid input.");
				commandHelp();
			}
		}
		clear();
		return new Table();
	}
	
	public void clear(){
		inst.clear();
		subinst.clear();
	}

	// ���s�\�ȃR�}���h��\������
	public static void commandHelp(){
		System.out.println("Instructions:");
		System.out.println("  load \"filename\": Load table from CSV file(without file extentions)");
		System.out.println("  save \"table\"[ as \"filename\"]: Save table to CSV file");
		System.out.println("  select \"table\" with \"field\" = \"value\" [and ...]: Execute SELECT");
		System.out.println("  project \"table\" over \"field\"[ and ...]: Execute PROJECT");
		System.out.println("  join \"table1\" and \"table2\" with \"field\" = \"value\" [and ...]: Execute JOIN");
		System.out.println("  show: Show names of every table");
		System.out.println("  help: Show this command information");
		System.out.println("  quit: Quit this program\n");
		System.out.println("You can use [select ...] or [project ...] or [join ...] as \"table\".\n");
	}
}
