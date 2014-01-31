package ocha.itolab.flowdiff.core.data;

import java.util.*;
import java.io.*;
import java.net.URL;

import ocha.itolab.flowdiff.applet.flowdiff.ViewingPanel;


public class FileReader {
	static Grid grid = null;
	static BufferedReader breader;
	static String url = "";
	
	// TODO: 格子点数が固定になっている
	static int nx = 151, ny = 85, nz = 101;
	
	/**
	 * ファイルを読んでGridを得る
	 */
	public static Grid getGrid(String u) {
		url = u;
		Random randX = new Random();
		Random randY = new Random();
		Random randZ = new Random();
		
		ViewingPanel vp = new ViewingPanel();

		// Gridを確保する
		grid = new Grid();
		grid.setTarget(randX.nextInt(69), randY.nextInt(19), randZ.nextInt(29));
		grid.setStartPoint(vp.sliderX.getValue(), vp.sliderY.getValue(), vp.sliderZ.getValue());
		grid.setNumGridPoint(nx, ny, nz);

		// ファイルを順に読む
		read("X.txt", 0);
		read("Y.txt", 1);
		read("Z.txt", 2);
		read("U.txt", 3);
		read("V.txt", 4);
		read("W.txt", 5);
		
		// Gridを返す
		grid.finalize();
		return grid;
	}
	
	
	/**
	 * ファイルを読む
	 */
	static void read(String filename, int id) {
		int count = 0;
		
		// ファイルを開く
		open(filename);
		System.out.print(" ... opening " + filename);
		
		// 1行ずつファイルを読む
		while(true) {
			String words[] = readLine();
			if(words == null) break;
			if(words.length == 1 && words[0].length() <= 0) continue;
			
			// 格子点1個ずつに対して値を代入する
			for(int i = 0; i < words.length; i++) {
				GridPoint gp = grid.getGridPoint(count++);
				double pos[] = gp.getPosition();
				double vec[] = gp.getVector();
				double value = Double.valueOf(words[i]);
						
				// 代入の場合分け
				switch(id) {
				case 0:
					gp.setPosition(value, pos[1], pos[2]);
					break;
				case 1:
					gp.setPosition(pos[0], value, pos[2]);
					break;
				case 2:
					gp.setPosition(pos[0], pos[1], value);
					break;
				case 3:
					gp.setVector(value, vec[1], vec[2]);
					break;
				case 4:
					gp.setVector(vec[0], value, vec[2]);
					break;
				case 5:
					gp.setVector(vec[0], vec[1], value);
					break;
				}
			}
			
		}
		
		// ファイルを閉じる
		close();
		System.out.println(" ... done.");
	}
		
	
	/**
	 * ファイルを開く
	 */
	static void open(String filename) {
		
		try {
			URL u = new URL(url + "/" + filename);
			InputStream is = u.openStream();
			InputStreamReader isr = new InputStreamReader(is, "EUC-JP");
			breader = new java.io.BufferedReader(isr);
			breader.ready();
		} catch (Exception e) {
			System.err.println(e);
		}
	}
	
	
	/**
	 * ファイルを閉じる
	 */
	static void close() {
		try {
			breader.close();
		} catch (Exception e) {
			System.err.println(e);
		}
	}
	
	
	/**
	 * 1行を読み、文字列を配列で返す
	 */
	static String[] readLine() {
		String wordarray[] = null;
		
		try {
			
			// EOFまで読み続ける
			String line = breader.readLine();
			//System.out.println(line);
			if (line == null) return null;

			// 空行であれば無意味な内容を返す
			if(line.length() <= 0 || line.startsWith("#") == true) {
				wordarray = new String[1];
				wordarray[0] = "";
				return wordarray;
			}
			
			// 1行を単語ごとに区切る
			StringTokenizer tokenBuffer = new StringTokenizer(line);
			wordarray = new String[tokenBuffer.countTokens()];
			for(int i = 0; i < wordarray.length; i++) {
				wordarray[i] = tokenBuffer.nextToken();
			}
			
		} catch(Exception e) {
			e.printStackTrace();
		}

		// 解読結果を返す
		return wordarray;
	}
	
}
