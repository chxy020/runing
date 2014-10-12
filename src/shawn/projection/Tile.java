package shawn.projection;

import java.io.Serializable;

public class Tile implements Serializable {

	private static final long serialVersionUID = -3672865664164075574L;

	public int x;
	public int y;

	public Tile(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof Tile))
			return false;
		final Tile t = (Tile) o;
		if (x == t.x && y == t.y)
			return true;
		else
			return false;
	}

	public String toString() {
		return x + "," + y;
	}

	public static String getCNMeshPath(int level, int tileX, int tileY) {
		String r;
		if (level <= 6) {
			r = level + "";
		} else {
			int scale = (int) Math.round(Math.pow(2, level - 5));
			r = level + "/R" + (tileY / scale) + "/C" + (tileX / scale);
		}
		return r;
	}

	public static String getCNMeshFile(int level, int tileX, int tileY) {
		return level + "-" + tileX + "-" + tileY + ".png";
	}
	
//	public static String getURL(int level, int tileX, int tileY) {
//		String path;
//		if (level <= 6) {
//			path = level + "";
//		} else {
//			int scale = (int) Math.round(Math.pow(2, level - 5));
//			path = level + "/R" + (tileY / scale) + "/C" + (tileX / scale);
//		}
//		String file = level + "-" + tileX + "-" + tileY + ".png";
//		return "http://119.254.195.132/dtmimg/" + path + "/" + file;
//	}
//	
//	public static void main(String[] args) {
//		String path = getCNMeshPath(12, 3375, 1552);
//		String file = getCNMeshFile(12, 3375, 1552);
//		System.out.println("http://119.254.195.132/dtmimg/" + path + "/" + file);
//		
//		System.out.println(getURL(12, 3375, 1552));
//		
//		System.out.println("6/3=" + 6/3);
//		System.out.println("6/4=" + 6/4);
//		System.out.println("6/5=" + 6/5);
//	}
	
}
