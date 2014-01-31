package ocha.itolab.flowdiff.core.data;

import ocha.itolab.flowdiff.core.streamline.*;

public class Grid {
	int num[] = new int[3];
	int gtotal, etotal;
	GridPoint garray[];
	Element earray[];
	double minmaxPos[] = new double[6]; // xmin, xmax, ymin, ymax, zmin, zmax
	public int startPoint[] = new int [3]; // 始点となる格子の座標値を格納する
	public int target[] = new int [3]; // 的となる格子の座標値を格納する
	public int levelMode = 1; // 初期設定は「ちょいむず」レベル（「かなりむず」は0になる）
	
	
	/**
	 * 格子の頂点数を設定する
	 */
	public void setNumGridPoint(int nx, int ny, int nz) {
		num[0] = nx;   num[1] = ny;   num[2] = nz;
		gtotal = nx * ny * nz;
		etotal = (nx - 1) * (ny - 1) * (nz - 1);

		// 格子点の配列を確保する
		garray = new GridPoint[gtotal];
		for(int i = 0; i < gtotal; i++)
			garray[i] = new GridPoint();
		
		// 要素の配列を確保する
		earray = new Element[etotal];
		for(int i = 0; i < etotal; i++)
			earray[i] = new Element();
	}
	

	/**
	 * 格子の頂点数を返す
	 */
	public int[] getNumGridPoint() {
		return num;
	}
	
	
	/**
	 * 所定の格子点を返す
	 */
	public GridPoint getGridPoint(int id) {
		return garray[id];
	}
	
	
	/**
	 * 所定の要素を返す
	 */
	public Element getElement(int id) {
		return earray[id];
	}
	
	
	/**
	 * 座標値の最小値・最大値を返す
	 */
	public double[] getMinmaxPos() {
		return minmaxPos;
	}
	
	/**
	 * 格子点の通し番号を求める
	 */
	public int calcGridPointId(int i, int j, int k) {
		return (k * num[0] * num[1] + j * num[0] + i);
	}

	
	/**
	 * 要素の通し番号を求める
	 */
	public int calcElementId(int i, int j, int k) {
		return (k * (num[0] - 1) * (num[1] - 1) + j * (num[0] - 1) + i);
	}
	
	/**
	 * 始点の座標値を定める
	 */
	public void setStartPoint(int i, int j, int k) {
		startPoint[0] = i;
		startPoint[1] = j;
		startPoint[2] = k;
	}
	
	/**
	 * 的の座標値を定める
	 */
	public void setTarget(int i, int j, int k) {
		target[0] = i;
		target[1] = j;
		target[2] = k;
	}	
	

	/**
	 * データを読み終えたあとのまとめ作業
	 */
	public void finalize() {
		
		// 座標値の最小値・最大値の初期化
		minmaxPos[0] = minmaxPos[2] = minmaxPos[4] = 1.0e+30;
		minmaxPos[1] = minmaxPos[3] = minmaxPos[5] = -1.0e+30;

		// 各格子点について：
		//   座標値の最小・最大を更新する
		for(int i = 0; i < gtotal; i++) {
			double pos[] = garray[i].getPosition();
			minmaxPos[0] = (minmaxPos[0] > pos[0]) ? pos[0] : minmaxPos[0];
			minmaxPos[1] = (minmaxPos[1] < pos[0]) ? pos[0] : minmaxPos[1];
			minmaxPos[2] = (minmaxPos[2] > pos[1]) ? pos[1] : minmaxPos[2];
			minmaxPos[3] = (minmaxPos[3] < pos[1]) ? pos[1] : minmaxPos[3];
			minmaxPos[4] = (minmaxPos[4] > pos[2]) ? pos[2] : minmaxPos[4];
			minmaxPos[5] = (minmaxPos[5] < pos[2]) ? pos[2] : minmaxPos[5];
		}
		
		// 各要素について：
		//   8個の頂点をセットする
		int count = 0;
		for(int k = 0; k < (num[2] - 1); k++) {
			for(int j = 0; j < (num[1] - 1); j++) {
				for(int i = 0; i < (num[0] - 1); i++, count++) {
					Element e = earray[count];
					e.gp[0] = garray[calcGridPointId(i, j, k)];
					e.gp[1] = garray[calcGridPointId((i + 1), j, k)];
					e.gp[2] = garray[calcGridPointId(i, (j + 1), k)];
					e.gp[3] = garray[calcGridPointId((i + 1), (j + 1), k)];
					e.gp[4] = garray[calcGridPointId(i, j, (k + 1))];
					e.gp[5] = garray[calcGridPointId((i + 1), j, (k + 1))];
					e.gp[6] = garray[calcGridPointId(i, (j + 1), (k + 1))];
					e.gp[7] = garray[calcGridPointId((i + 1), (j + 1), (k + 1))];
				}
			}
		}
	}
	
	// targetとなっているelementとstreamlineの交差判定
	public boolean intersectWithTarget(Streamline sl) {
		int targetId = calcElementId(target[0],target[1], target[2]);
		return getElement(targetId).intersect(targetId, sl, levelMode);
	}
}
