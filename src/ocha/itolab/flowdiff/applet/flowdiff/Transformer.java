package ocha.itolab.flowdiff.applet.flowdiff;

import java.lang.Math;


/**
 * 描画の視点操作（拡大縮小、回転、平行移動）のパラメータを管理するクラス
 * @author itot
 */
public class Transformer {

	double viewShift[] = new double[3];
	double viewRotate[] = new double[16];
	double viewScaleX, viewScaleY;
	double viewShiftBak[] = new double[3];
	double viewScaleBakX, viewScaleBakY;
	double Xrotate, Yrotate, XrotateBak, YrotateBak;

	double tableMin[] = new double[3];
	double tableMax[] = new double[3];
	double tableCenter[] = new double[3];
	double tableSize;

	double shiftX, shiftZ;

	/**
	 * Constructor
	 */
	public Transformer() {
		setDefaultValue();
	}

	/**
	 * 視点パラメータをリセットする
	 */
	public void viewReset() {
		for (int i = 0; i < 16; i++) {
			if (i % 5 == 0)
				viewRotate[i] = 1.0;
			else
				viewRotate[i] = 0.0;
		}
		viewScaleX = viewScaleBakX = 5.0; // 箱の大きさの初期値
		viewScaleY = viewScaleBakY = 5.0;
		viewShift[0] = viewShiftBak[0] = 0.0;
		viewShift[1] = viewShiftBak[1] = 0.0;
		viewShift[2] = viewShiftBak[2] = 0.0;
		Xrotate = XrotateBak = 1.0;
		Yrotate = YrotateBak = 0.5;

	}



	/**
	 * マウスボタンが押されたモードを設定する
	 */
	public void mousePressed() {
		viewScaleBakX = viewScaleX;
		viewScaleBakY = viewScaleY;
		viewShiftBak[0] = viewShift[0];
		viewShiftBak[1] = viewShift[1];
		viewShiftBak[2] = viewShift[2];
		XrotateBak = Xrotate;
		YrotateBak = Yrotate;
	}

	/**
	 * マウスのドラッグ操作に応じてパラメータを制御する
	 * @param x マウスポインタのx座標値
	 * @param y マウスポインタのy座標値
	 * @param width 画面領域の幅
	 * @param height 画面領域の高さ
	 * @param dragMode ドラッグモード（1:ZOOM, 2:SHIFT, 3:ROTATE）
	 */
	public void drag(int x, int y, int width, int height, int dragMode) {
		
		if (dragMode == 1) { // ZOOM
			
			if (x > 0) {
				viewScaleX =
					viewScaleBakX * (1 + (double) (2 * x) / (double) width);
			} else {
				viewScaleX = viewScaleBakX * (1 + (double) x / (double) width);
			}
			if (viewScaleX < 0.2)
				viewScaleX = 0.2;
			
			if (y > 0) {
				viewScaleY =
					viewScaleBakY * (1 + (double) (2 * y) / (double) height);
			} else {
				viewScaleY = viewScaleBakY * (1 + (double) y / (double) height);
			}
			if (viewScaleY < 0.2)
				viewScaleY = 0.2;

			viewShift[0] = viewShiftBak[0] * viewScaleX / viewScaleBakX;
			viewShift[1] = viewShiftBak[1] * viewScaleY / viewScaleBakY;

		}
		if (dragMode == 2) { // SHIFT
			/*
			double  viewSqrtScale = (double)Math.sqrt(viewScale);
			viewShift[0] = viewShiftBak[0] + viewSqrtScale * (double)x;
			viewShift[1] = viewShiftBak[1] + viewSqrtScale * (double)y;
			*/
			/*
			viewShift[0] = viewShiftBak[0] + (double) x;
			viewShift[1] = viewShiftBak[1] + (double) y;
			*/
			 float diffX = (float)x * 3.0f / width;
             float diffY = (-3.0f) * (float)y / height;
           
            viewShift[0] = viewShiftBak[0] + diffX;
 			viewShift[1] = viewShiftBak[1] + diffY;
		}
		if (dragMode == 3) { // ROTATE
			Xrotate = XrotateBak + (double) x * Math.PI / (double) width;
			Yrotate = YrotateBak + (double) y * Math.PI / (double) height;
			double cosX = Math.cos(Yrotate);
			double sinX = Math.sin(Yrotate);
			double cosY = Math.cos(Xrotate);
			double sinY = Math.sin(Xrotate);

			viewRotate[0] = cosY;
			viewRotate[1] = 0;
			viewRotate[2] = -sinY;
			viewRotate[4] = sinX * sinY;
			viewRotate[5] = cosX;
			viewRotate[6] = sinX * cosY;
			viewRotate[8] = cosX * sinY;
			viewRotate[9] = -sinX;
			viewRotate[10] = cosX * cosY;
		}

		/*
		System.out.println("R " + viewRotate[0] + " " + viewRotate[1] + " " + viewRotate[2]);
		System.out.println("  " + viewRotate[4] + " " + viewRotate[5] + " " + viewRotate[6]);
		System.out.println("  " + viewRotate[8] + " " + viewRotate[9] + " " + viewRotate[10]);
		System.out.println("S " + viewShift[0] + " " + viewShift[1] + " " + viewShift[2]);
		System.out.println("Z " + viewScaleX + " " + viewScaleY);
		*/
		
	}

	/**
	 * 視点パラメータを初期化する
	 */
	public void setDefaultValue() {

		/*
		viewRotate[0] = 0.971;
		viewRotate[1] = 0.0;
		viewRotate[2] = 0.246;
		viewRotate[3] = 0.0;
		viewRotate[4] = 0.129;
		viewRotate[5] = 0.849;
		viewRotate[6] = -0.511;
		viewRotate[7] = 0.0;
		viewRotate[8] = -0.209;
		viewRotate[9] = 0.528;
		viewRotate[10] = 0.823;
		viewRotate[11] = 0.0;
		viewRotate[12] = 0.0;
		viewRotate[13] = 0.0;
		viewRotate[14] = 0.0;
		viewRotate[15] = 1.0;

		viewShift[0] = 67.0;
		viewShift[1] = -57.0;
		viewShift[2] = 0.0;
		viewScaleX = 0.970;
		viewScaleY = 0.705;
		*/
		Xrotate = 0.5;
		Yrotate = 0.5;
	}

	/**
	 * tableのサイズ値を返す
	 * @return tableのサイズ値
	 */
	public double getTableSize() {
		return tableSize;
	}
	
	/**
	 * tableのサイズ値をセットする
	 * @param g tableのサイズ値
	 */
	public void setTableSize(double g) {
		tableSize = g;
	}

	/**
	 * 表示の拡大度を返す
	 * @return 表示の拡大度
	 */
	public double getViewScaleX() {
		return viewScaleX;
	}
	
	/**
	 * 表示の拡大度を返す
	 * @return 表示の拡大度
	 */
	public double getViewScaleY() {
		return viewScaleY;
	}
	
	/**
	 * 表示の拡大度をセットする
	 * @param v 表示の拡大度
	 */
	public void setViewScaleX(double v) {
		viewScaleX = v;
	}
	
	/**
	 * 表示の拡大度をセットする
	 * @param v 表示の拡大度
	 */
	public void setViewScaleY(double v) {
		viewScaleY = v;
	}


	/**
	 * tableの中心座標値を返す
	 * @param i 座標軸(1:X, 2:Y, 3:Z)
	 * @return 中心座標値
	 */
	public double getTableCenter(int i) {
		return tableCenter[i];
	}
	
	/**
	 * tableの中心座標値をセットする
	 * @param g 中心座標値
	 * @param i 座標軸(1:X, 2:Y, 3:Z)
	 */
	public void setTableCenter(double g, int i) {
		tableCenter[i] = g;
	}

	/**
	 * 視点の回転の行列値を返す
	 * @param i 行列中の要素の位置
	 * @return 行列値
	 */
	public double getViewRotate(int i) {
		return viewRotate[i];
	}
	
	/**
	 * 視点の回転の行列値をセットする
	 * @param v 行列値
	 * @param i 行列中の要素の位置
	 */
	public void setViewRotate(double v, int i) {
		viewRotate[i] = v;
	}

	/**
	 * 視点の平行移動量を返す
	 * @param i 座標軸 (1:X, 2:Y, 3:Z)
	 * @return 平行移動量
	 */
	public double getViewShift(int i) {
		return viewShift[i];
	}
	
	/**
	 * 視点の平行移動量をセットする
	 * @param v 平行移動量
	 * @param i 座標軸 (1:X, 2:Y, 3:Z)
	 */
	public void setViewShift(double v, int i) {
		viewShift[i] = v;
	}

	/**
	 * 表示の回転角度を返す
	 * @return 表示の回転角度
	 */
	public double getViewRotateX() {
		return Xrotate;
	}
	
	/**
	 * 表示の回転角度を返す
	 * @return 表示の回転角度
	 */
	public double getViewRotateY() {
		return Yrotate;
	}
	
	/**
	 * counterの中心座標値を返す
	 * @param i 座標軸(1:X, 2:Y, 3:Z)
	 * @return 中心座標値
	 */
	public double getCenter(int i) {
		return tableCenter[i];
	}
	
	/**
	 * サイズ値を返す
	 * @return サイズ値
	 */
	public double getSize() {
		return tableSize;
	}
}
