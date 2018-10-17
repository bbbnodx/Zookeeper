public class Robbie {
	final String zookeeper = "Zookeeper";
	final String feat = "features";
	final String iden = "identifier";
	final String ante = "antecedent";
	final String cons = "consequent";
	
	public Robbie(){}
	
	public void driveZookeeper(){
		Zookeeper zoo = new Zookeeper();
		// データベースの作成等初期設定をおこなう
		zoo.setConstant(feat, ante, cons);
		zoo.setDatabase(zookeeper);
		// features, rulesの取得
		zoo.gainTable(feat);
		zoo.gainTable(ante);
		zoo.gainTable(cons);
		// 推論
		zoo.deduct();
		zoo.gainTable(iden);
		zoo.identify().printTable();
	}
	
}
