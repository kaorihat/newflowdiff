package ocha.itolab.flowdiff.core.data;

public class GridPoint {
	double pos[] = new double[3];
	double vec[] = new double[3];
	
	public void setPosition(double x, double y, double z) {
		pos[0] = x;    pos[1] = y;    pos[2] = z;
	}
	
	public void setVector(double u, double v, double w) {
		vec[0] = u;    vec[1] = v;    vec[2] = w;
	}
	
	public double[] getPosition() {
		return pos;
	}
	
	public double[] getVector() {
		return vec;
	}
}
