import java.io.*;
import java.util.*;

public class Instruction {
	// 命令を語句単位に区切り，文字列配列に格納する
	private ArrayList<String> inst = new ArrayList<String>();
	// 入れ子部分が存在するとき，subinstに格納する
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
	
	// 標準入力から取得した文字列を分割して返すメソッド
	public ArrayList<String> getInstruction(){
		try{
			BufferedReader buf = new BufferedReader(new InputStreamReader(System.in));
			String st = buf.readLine();
			inst = splitInstruction(st);
		} catch (IOException e) {
			// BufferedReaderオブジェクトのクローズ時の例外捕捉
			e.printStackTrace();
		}
		return inst;
	}
	
	// 標準入力から取得した文字列を分割して返すメソッド
	public ArrayList<String> getInstruction(String st){
		inst = splitInstruction(st);
		return inst;
	}
	
	// 命令として与えられた文字列を語句単位に分割する
	// []の内側の命令から順に格納される
	// 元の命令の[]部分は"TABLE"に置換される
	// 戻り値は語句に分割された文字列配列の配列となる
	public ArrayList<String> splitInstruction(String st){
		st = st.trim();
		// depthは入れ子の深さを表し，beginとendはそれぞれ外側の'['，']'のindexを保存する
		int depth = 0;
		ArrayList<Integer> begin = new ArrayList<Integer>();
		ArrayList<Integer> end = new ArrayList<Integer>();
		// []が存在する場合，[]の外の命令を分割し，元の命令の[]はTABLEに置換する
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
			// 置換によって文字列が変動するので，後方から置換する
			for(int i=begin.size()-1; i>=0; i--){
				subinst.get(i).splitInstruction(st.substring(begin.get(i), end.get(i)));
				st = st.replace(st.subSequence(begin.get(i)-1, end.get(i)+1), "TABLE");
			}
		}
		// 空白を区切り文字として分割する
		String[] starr = st.split(" +");
		for(int i=0; i<starr.length; i++){
			inst.add(starr[i]);
		}
		return inst;
	}
	
	// 命令を実行するメソッド
	public Table execInstruction(Database database, ArrayList<String> inst){
		//System.out.println(inst);
		if(!inst.isEmpty()){
			// load命令の実行
			if(inst.get(0).toLowerCase().equals("load")){
				database.readTableFromCSV(inst.get(1));
			// save命令の実行
			}else if(inst.get(0).toLowerCase().equals("save")){
				Table table = new Table();
				// テーブルが入れ子[]の文で与えられた場合，subinstを実行して返されるテーブルを設定する
				if(inst.get(1).equals("TABLE")){
					table = subinst.get(0).execInstruction(database, subinst.get(0).getInst());
					if(table.isEmpty()){
						System.out.println("Table \"" + inst.get(1) + "\" doesn't exist.");
						clear();
						return table;
					}
				// テーブルがテーブル名で与えられた場合，テーブル集合からテーブルを取得する
				}else{
					if(database.getTables().containsKey(inst.get(1).toLowerCase())){
						table = database.getTables().get(inst.get(1).toLowerCase());
					}else{
						System.out.println("Table \"" + inst.get(1) + "\" doesn't exist.");
						clear();
						return table;
					}
				}
				// 別のテーブル名で保存する場合，tablesにも追加する
				if(inst.contains("as")){
					String tableName = inst.get(inst.indexOf("as")+1);
					database.writeTableToCSV(table, tableName);
					database.readTableFromCSV(tableName);
				}else{
					database.writeTableToCSV(table, table.getName());
				}
				return table;
			// ADD文の実行
			}else if(inst.get(0).toLowerCase().equals("add")){
				Table table = new Table();
				int to = inst.indexOf("to");
				// テーブルが入れ子[]の文で与えられた場合，subinstを実行して返されるテーブルを設定する
				if(inst.get(to+1).equals("TABLE")){
					table = subinst.get(0).execInstruction(database, subinst.get(0).getInst());
					if(table.isEmpty()){
						System.out.println("Table \"" + inst.get(to+1) + "\" doesn't exist.");
						clear();
						return table;
					}
				// テーブルがテーブル名で与えられた場合，テーブル集合からテーブルを取得する
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
				
			// SELECT文の実行
			}else if(inst.get(0).toLowerCase().equals("select")){
				Table table = new Table();
				// テーブルが入れ子[]の文で与えられた場合，subinstを実行して返されるテーブルを設定する
				if(inst.get(1).equals("TABLE")){
					table = subinst.get(0).execInstruction(database, subinst.get(0).getInst());
					if(table.isEmpty()){
						System.out.println("Table \"" + inst.get(1) + "\" doesn't exist.");
						clear();
						return table;
					}
				// テーブルがテーブル名で与えられた場合，テーブル集合からテーブルを取得する
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
				// "with"以降を調べ，fieldsとvaluesを設定する
				for(int i=inst.indexOf("with")+1; i<inst.size(); i++){
					if(inst.get(i).equals("=")){
						fields.add(inst.get(i-1).toLowerCase());
						// valuesの設定
						// 整数をint型に変換する
						if(inst.get(i+1).matches("[0-9]+")){
							values.add(Integer.valueOf(inst.get(i+1)).intValue());
						// 実数をdouble型に変換する
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
			
			// PROJECT文の実行
			}else if(inst.get(0).toLowerCase().equals("project")){
				Table table = new Table();
				// テーブルが入れ子[]の文で与えられた場合，subinstを実行して返されるテーブルを設定する
				if(inst.get(1).equals("TABLE")){
					table = subinst.get(0).execInstruction(database, subinst.get(0).getInst());
					if(table.isEmpty()){
						System.out.println("Table \"" + inst.get(1) + "\" doesn't exist.");
						clear();
						return table;
					}
				// テーブルがテーブル名で与えられた場合，テーブル集合からテーブルを取得する
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
				// 命令文中に"over"が存在するかを調べる
				// 存在するなら，そのindexを用いるのでfor文を使って調べる
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
			
			// JOIN文の実行
			}else if(inst.get(0).toLowerCase().equals("join")){
				int i = inst.indexOf("and");
				int sub = 0;
				// table1を設定する
				Table table1 = new Table();
				// テーブルが入れ子[]の文で与えられた場合，subinstを実行して返されるテーブルを設定する
				if(inst.get(i-1).equals("TABLE")){
					table1 = subinst.get(sub).execInstruction(database, subinst.get(sub).getInst());
					sub++;
					if(table1.isEmpty()){
						System.out.println("Table \"" + inst.get(i-1) + "\" doesn't exist.");
						clear();
						return table1;
					}
				// テーブルがテーブル名で与えられた場合，テーブル集合からテーブルを取得する
				}else{
					if(database.getTables().containsKey(inst.get(i-1).toLowerCase())){
						table1 = database.getTables().get(inst.get(i-1).toLowerCase());
					}else{
						System.out.println("Table \"" + inst.get(i-1) + "\" doesn't exist.");
						clear();
						return table1;
					}
				}
				// table2の設定
				Table table2 = new Table();
				// テーブルが[]の文で与えられた場合，subinstを実行して返されるテーブルを設定する
				if(inst.get(i+1).equals("TABLE")){
					table2 = subinst.get(sub).execInstruction(database, subinst.get(sub).getInst());
					if(table2.isEmpty()){
						System.out.println("Table \"" + inst.get(i+1) + "\" doesn't exist.");
						clear();
						return table2;
					}
				// テーブルがテーブル名で与えられた場合，テーブル集合からテーブルを取得する
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
				// 比較するフィールドを取得する
				ArrayList<String> fields1 = new ArrayList<String>();
				ArrayList<String> fields2 = new ArrayList<String>();
				// with以降の命令から，"="の前後の語句をフィールドとして取得する
				for(i=inst.indexOf("with")+1; i<inst.size(); i++){
					if(inst.get(i).equals("=")){
						fields1.add(inst.get(i-1).toLowerCase());
						fields2.add(inst.get(i+1).toLowerCase());
					}
				}
				//System.out.println("fields1:" + fields1 + "\n" + "fields2:" + fields2);
				clear();
				return database.joinTable(table1, table2, fields1, fields2);
				
			// list命令の実行
			}else if(inst.get(0).toLowerCase().equals("list")){
				database.printTableNames();
			// help命令の実行
			}else if(inst.get(0).toLowerCase().equals("help")){
				commandHelp();
			// それ以外の場合，ヘルプを表示する
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

	// 実行可能なコマンドを表示する
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
