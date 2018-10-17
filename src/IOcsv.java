import java.io.*;
import java.util.*;
import jp.ac.wakhok.tomoharu.csv.*;


public class IOcsv {
	public static ArrayList<ArrayList<String>> readCSV(String fname) {
		ArrayList<ArrayList<String>> csvdata = new ArrayList<ArrayList<String>>();
		try {
			File csvfile = new File(fname + ".csv"); // CSVデータファイル
			BufferedReader br = new BufferedReader(new FileReader(csvfile));

			// 最終行まで読み込む
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
			// Fileオブジェクト生成時の例外捕捉
			e.printStackTrace();
		} catch (IOException e) {
			// BufferedReaderオブジェクトのクローズ時の例外捕捉
			e.printStackTrace();
		}
		return csvdata;
	}
	
	// 文字列の配列を1行文のデータとしてCSV形式でファイル"fname.csv"に追記する
	public static void writeLineCSV(String fname, ArrayList<String> elements) {
		try {
			File csvfile = new File(fname + ".csv"); // CSVデータファイル
			// 追記モード(trueによる)
			BufferedWriter bw = new BufferedWriter(new FileWriter(csvfile, true)); 
			// 新たなデータ行の追加
			CSVLine csvline = new CSVLine();
			for(int i=0; i<elements.size(); i++){
				csvline.addItem(elements.get(i));
			}
			bw.write(csvline.getLine());
			bw.newLine();
			bw.close();
		} catch (FileNotFoundException e) {
			// Fileオブジェクト生成時の例外捕捉
			e.printStackTrace();
		} catch (IOException e) {
			// BufferedWriterオブジェクトのクローズ時の例外捕捉
			e.printStackTrace();
		}
	}
	
	// 文字列の2次元配列をCSV形式でファイル"fname.csv"に書き込む
	public static void writeCSV(String fname, ArrayList<ArrayList<String>> elements) {
		try {
			File csvfile = new File(fname + ".csv"); // CSVデータファイル
			BufferedWriter bw = new BufferedWriter(new FileWriter(csvfile, false)); 
			for(int i=0; i<elements.size(); i++){
				// 新たなデータ行の追加
				CSVLine csvline = new CSVLine();
				for(int j=0; j<elements.get(i).size(); j++){
					csvline.addItem(elements.get(i).get(j));
				}
				bw.write(csvline.getLine());
				bw.newLine();
			}
			bw.close();
		} catch (FileNotFoundException e) {
			// Fileオブジェクト生成時の例外捕捉
			e.printStackTrace();
		} catch (IOException e) {
			// BufferedWriterオブジェクトのクローズ時の例外捕捉
			e.printStackTrace();
		}
	}
	
	public static boolean existsCSV(String fname){
		File csvfile = new File(fname + ".csv");
		return csvfile.exists();
	}
}
