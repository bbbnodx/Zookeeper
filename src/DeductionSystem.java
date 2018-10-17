import java.util.*;

public class DeductionSystem {
	private String features, antecedent, consequent;
	// ポインタ
	private Rdbms rdbms;
	private Table wm;
	private Table ante;
	private Table cons;
	
	// データベースを設定する
	public void dbConnect(String dbName, Rdbms rdb){
		this.rdbms = rdb;
		this.rdbms.connect(dbName);
	}
	
	public void setConstant(String features, String antecedent, String consequent){
		this.features = features;
		this.antecedent = antecedent;
		this.consequent = consequent;
	}
	
	// featuresをworkingmemoryにコピーし，ポインタをセットする	
	public void initWorkingMemory(String animal){
		if(rdbms.containsTable(features)){
			wm = new Table();
			if(animal.isEmpty()){
				wm = rdbms.execInstruction("select features");
			}else{
				wm = rdbms.execInstruction("select features with name = " + animal);
			}
			wm.setName("workingmemory");
			rdbms.addTable(wm);
		}else{
			System.out.println("Features doesn't exist");
		}
	}
	
	public Table getWorkingMemory(){
		return wm;
	}

	// antecedent(if節)とconsequent(then節)のポインタをセットする
	public void initRules(){
		if(rdbms.containsTable(antecedent) && rdbms.containsTable(consequent)){
			ante = rdbms.execInstruction("select antecedent");
			cons = rdbms.execInstruction("select consequent");
		}else{
			System.out.println("Rules doesn't exist");
		}
	}
	
	public Table getAntecedent(){
		return ante;
	}
	
	public Table getConsequent(){
		return cons;
	}
	
	// forwardchainingによる推論
	public Table forwardChaining(String specific){
		initRules();
		initWorkingMemory(specific);
		if(!wm.hasRecords()){
			System.out.println("WorkingMemory is empty\nDeduction can't execution");
			return wm;
		}
		System.out.println("Initial WorkingMemory:");
		wm.printTable();
		int size = 0;
		String query = new String();
		// 新たにassertionが生成されなくなるまでループする
		while(size < wm.getCardinality()){
			size = wm.getCardinality();
			query = "select antecedent";
			// ルール名を配列として取得する
			ArrayList<String> rulesets = rdbms.fetchColumn(rdbms.execInstruction(query), "rule");
			// 各ルールについてループする
			for(Iterator<String> iter=rulesets.iterator(); iter.hasNext(); ){
				String rule = iter.next();
				//System.out.println("Rule: " + rule);
				// ルールにおけるantecedent(if部)の数を取得する
				query = "select antecedent with rule = " + rule;
				int antes = rdbms.execInstruction(query).getCardinality();
				// WorkingMemoryとルールを結合する
				query = "join workingmemory and [select antecedent with rule = " + rule +
						"] with verb = if-verb and feature = if-feature";
				Table joined = rdbms.execInstruction(query);
				rdbms.updateTable(joined);
				// 結合されたテーブルから動物の名前を取得し，それぞれについてループする
				ArrayList<String> animals = rdbms.fetchColumn(joined, "name");
				for(Iterator<String> itr=animals.iterator(); itr.hasNext(); ){
					String animal = itr.next();
					query = "select joined with name = "+ animal;
					Table matched = rdbms.execInstruction(query);
					// すべてのルールに一致したらassertionをWorkingMemoryに追加する
					if(matched.getCardinality() == antes){
						//System.out.println("Add a new assertion");
						int idx = rdbms.indexOfRecord(cons, "rule", rule);
						String consVerb = rdbms.fetchRecord(cons, idx).get(rdbms.indexOfField(cons, "then-verb"));
						String consComp = rdbms.fetchRecord(cons, idx).get(rdbms.indexOfField(cons, "then-feature"));						
						query = "add " + animal + " " + consVerb + " " + consComp + " to workingmemory";
						rdbms.execInstruction(query);
						//wm.printTable();
					}
				}				
			}
		}
		return wm;
	}
	
	// 動物を同定する
	public Table identify(){
		String query = "project [join workingmemory and consequent with feature = then-feature] " +
				"over name and verb and feature";
		Table identifying = rdbms.execInstruction(query);
		if(identifying.getCardinality() == 0){
			System.out.println("Any animals don't match to rules.\nReturn WorkingMemory\n");
			return wm;
		}
		identifying.setName("identifying");
		query = "project [join [select workingmemory with verb = is] and identifier" +
				" with feature = class] over name and verb and class";
		Table identified = rdbms.execInstruction(query);
		identified.setName("identified");
		if(rdbms.fetchColumn(identified, "name").size() == rdbms.fetchColumn(wm, "name").size()){
			System.out.println("complete!!\n");
			return identified;
		}else{
			System.out.println("Identifying is incomplete\n");
			identifying.printTable();
			return wm;
		}
	}
	
}
