package ocha.itolab.flowdiff.core.streamline;

import java.util.*;

public class Streamline {
	Vector vertices = new Vector();
	
	/**
	 * 頂点クラス
	 */
	class Vertex {
		double position[] = new double[3];
		int elementId[] = new int[3];
	}
	
	
	/**
	 * 頂点を全て削除する
	 */
	public void resetAllVertex() {
		vertices.clear();
	}
	
	/**
	 * 1個の頂点を追加する
	 */
	public void addOneVertex() {
		Vertex v = new Vertex();
		vertices.add(v);
	}
	
	
	/**
	 * 頂点の個数を返す
	 */
	public int getNumVertex() {
		return vertices.size();
	}
	
	
	/**
	 * 任意の頂点の座標値を設定する
	 */
	public void setPosition(int id, double x, double y, double z) {
		Vertex v = (Vertex)vertices.elementAt(id);
		v.position[0] = x;
		v.position[1] = y;
		v.position[2] = z;
	}
	
	
	/**
	 * 任意の頂点の座標値を得る
	 */
	public double[] getPosition(int id) {
		Vertex v = (Vertex)vertices.elementAt(id);
		return v.position;
	}
	
	/**
	 * 任意の頂点を内包する要素のIDを設定する
	 */
	public void setElementId(int id, int i, int j, int k) {
		Vertex v = (Vertex)vertices.elementAt(id);
		v.elementId[0] = i;
		v.elementId[1] = j;
		v.elementId[2] = k;
	}
	
	
	/**
	 * 任意の頂点を内包する要素のIDを得る
	 */
	public int[] getElementId(int id) {
		Vertex v = (Vertex)vertices.elementAt(id);
		return v.elementId;
	}
}
