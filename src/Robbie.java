public class Robbie {
	final String zookeeper = "Zookeeper";
	final String feat = "features";
	final String iden = "identifier";
	final String ante = "antecedent";
	final String cons = "consequent";
	
	public Robbie(){}
	
	public void driveZookeeper(){
		Zookeeper zoo = new Zookeeper();
		// �f�[�^�x�[�X�̍쐬�������ݒ�������Ȃ�
		zoo.setConstant(feat, ante, cons);
		zoo.setDatabase(zookeeper);
		// features, rules�̎擾
		zoo.gainTable(feat);
		zoo.gainTable(ante);
		zoo.gainTable(cons);
		// ���_
		zoo.deduct();
		zoo.gainTable(iden);
		zoo.identify().printTable();
	}
	
}
