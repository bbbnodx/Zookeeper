import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Zookeeper {
	Rdbms rdbms = new Rdbms();
	DeductionSystem deducsys = new DeductionSystem();
	String feat, ante, cons;
	
	// 文字列定数を設定する
	public void setConstant(String features, String antecedent, String consequent){
		feat = features;
		ante = antecedent;
		cons = consequent;
		deducsys.setConstant(features, antecedent, consequent);
	}
	
	public void setDatabase(String zookeeper){
		rdbms.createDatabase(zookeeper);
		deducsys.dbConnect(zookeeper, rdbms);
	}
	
	public void gainTable(String filename){
		if(rdbms.containsTable(filename)){
			System.out.println("The " + filename + " exists.\nInitialize the Identifier");
			rdbms.removeTable(filename);
		}
		rdbms.readTableFromCSV(filename);
	}
	
	// 推論を実行する
	public Table deduct(){
		try{
			System.out.println("Features gained by Robbie:");
			rdbms.execInstruction("select features").printTable();
			// 特定の動物のみを推論するなら，動物名を入力する
			System.out.println("If you want to identify the particular animal, type the animal's name");
			System.out.print("> ");
			BufferedReader buf = new BufferedReader(new InputStreamReader(System.in));			
			return deducsys.forwardChaining(buf.readLine());
		} catch (IOException e) {
			// BufferedReaderオブジェクトのクローズ時の例外捕捉
			e.printStackTrace();
			return new Table();
		}
	}
	
	public Table identify(){
		return deducsys.identify();
	}
	
	public static void main(String[] args) {		
		Robbie robbie = new Robbie();
		robbie.driveZookeeper();
	}
}
