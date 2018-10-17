import java.io.*;
import java.util.*;
import jp.ac.wakhok.tomoharu.csv.*;


public class IOcsv {
	public static ArrayList<ArrayList<String>> readCSV(String fname) {
		ArrayList<ArrayList<String>> csvdata = new ArrayList<ArrayList<String>>();
		try {
			File csvfile = new File(fname + ".csv"); // CSV�f�[�^�t�@�C��
			BufferedReader br = new BufferedReader(new FileReader(csvfile));

			// �ŏI�s�܂œǂݍ���
			String line = new String();
			for (int i=0; (line = br.readLine()) != null; i++) {
				CSVTokenizer csvt = new CSVTokenizer(line);
				ArrayList<String> st = new ArrayList<String>();
				while (csvt.hasMoreTokens()) {
					st.add(csvt.nextToken());
				}
				csvdata.add(st);
			}
			// System.out.println(csvdata);
			br.close();

		} catch (FileNotFoundException e) {
			// File�I�u�W�F�N�g�������̗�O�ߑ�
			e.printStackTrace();
		} catch (IOException e) {
			// BufferedReader�I�u�W�F�N�g�̃N���[�Y���̗�O�ߑ�
			e.printStackTrace();
		}
		return csvdata;
	}
	
	// ������̔z���1�s���̃f�[�^�Ƃ���CSV�`���Ńt�@�C��"fname.csv"�ɒǋL����
	public static void writeLineCSV(String fname, ArrayList<String> elements) {
		try {
			File csvfile = new File(fname + ".csv"); // CSV�f�[�^�t�@�C��
			// �ǋL���[�h(true�ɂ��)
			BufferedWriter bw = new BufferedWriter(new FileWriter(csvfile, true)); 
			// �V���ȃf�[�^�s�̒ǉ�
			CSVLine csvline = new CSVLine();
			for(int i=0; i<elements.size(); i++){
				csvline.addItem(elements.get(i));
			}
			bw.write(csvline.getLine());
			bw.newLine();
			bw.close();
		} catch (FileNotFoundException e) {
			// File�I�u�W�F�N�g�������̗�O�ߑ�
			e.printStackTrace();
		} catch (IOException e) {
			// BufferedWriter�I�u�W�F�N�g�̃N���[�Y���̗�O�ߑ�
			e.printStackTrace();
		}
	}
	
	// �������2�����z���CSV�`���Ńt�@�C��"fname.csv"�ɏ�������
	public static void writeCSV(String fname, ArrayList<ArrayList<String>> elements) {
		try {
			File csvfile = new File(fname + ".csv"); // CSV�f�[�^�t�@�C��
			BufferedWriter bw = new BufferedWriter(new FileWriter(csvfile, false)); 
			for(int i=0; i<elements.size(); i++){
				// �V���ȃf�[�^�s�̒ǉ�
				CSVLine csvline = new CSVLine();
				for(int j=0; j<elements.get(i).size(); j++){
					csvline.addItem(elements.get(i).get(j));
				}
				bw.write(csvline.getLine());
				bw.newLine();
			}
			bw.close();
		} catch (FileNotFoundException e) {
			// File�I�u�W�F�N�g�������̗�O�ߑ�
			e.printStackTrace();
		} catch (IOException e) {
			// BufferedWriter�I�u�W�F�N�g�̃N���[�Y���̗�O�ߑ�
			e.printStackTrace();
		}
	}
	
	public static boolean existsCSV(String fname){
		File csvfile = new File(fname + ".csv");
		return csvfile.exists();
	}
}
