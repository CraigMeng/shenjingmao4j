package Game;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;
import java.util.Stack;

/*
 * 神经猫的java实现demo，为了验证 算法
 * */
class MyPoint {
	public MyPoint(int x, int y) {
		this.x = x;
		this.y = y;
	}
	public MyPoint() {
		this.x = 0;
		this.y = 0;
	}
	public void setValue(int x, int y) {
		this.x = x - 1;
		this.y = y - 1;
	}
	
	@Override
	public boolean equals(Object obj) {
		// TODO 自动生成的方法存根
		if (obj instanceof MyPoint) {
			MyPoint para = (MyPoint)obj;
			if (this.x == para.x && this.y == para.y)
				return true;
			else
				return false;
		}
		else
			return false;
	}

	public int x;
	public int y;
}
public class jsGame {
	
	
	
	
	int[][] mapMatrix = new int[9][9];// 9*9的地图矩阵，初始为0
	MyPoint current = new MyPoint(4,4); // 代表当前猫的位置
	final int initPoint = 9; // 初始的堵截点的数目
	public jsGame() {
		for (int row = 0; row < 9; row++) {
			for (int col = 0; col < 9; col ++) {
				mapMatrix[row][col] = 0;
			}
		}
	}
	public static void main(String[] args) {
		// TODO 自动生成的方法存根
		jsGame demo = new jsGame();
		demo.initial();
		//demo.debugFunc();
		demo.printMatrix();
		//Stack<MyPoint> path = new Stack<MyPoint>();
		//System.out.println(demo.getMinStep(new MyPoint(5,5), null,path));
		demo.handler();
		
	}
	public void printMatrix() {
		for (int row = 0; row < 9; row++) {
			if (row % 2 == 1)
				System.out.print(" ");
			for (int col = 0; col < 9; col++) {
				
				System.out.print(mapMatrix[row][col] + " ");
			}
			System.out.println();
		}
	}
	
	// 循环
	public void handler() {
		Scanner sc = new Scanner(System.in);
		String line;
		while (!(line = sc.nextLine()).equals("end")) {
			
			String[] coordinate = line.split(" ");
			int row = Integer.parseInt(coordinate[0]);
			int col = Integer.parseInt(coordinate[1]);
			setValue(row, col);
			// 找到下一步节点
			ArrayList<MyPoint> nextStep = getNextStep(current);
			if (nextStep.size() == 0) {
				this.printMatrix();
				System.out.println("you win");
				break;
			}
			int minstep = -1;
			int minIndex = -1;
			for (int index = 0; index < nextStep.size(); index++) {
				Stack<MyPoint> path = new Stack<MyPoint>();
				path.push(current);
				int step = getMinStep(nextStep.get(index),current,path);
				if (step != -1 && (minstep == -1 || minstep > step)) {
					minstep = step;
					minIndex = index;
				}	
			}
			if (minstep == -1) {
				this.printMatrix();
				System.out.println("you win");
				break;
			}
			
			if (this.mapMatrix[current.x][current.y] == 8)
				this.mapMatrix[current.x][current.y] = 0;
			current.x = nextStep.get(minIndex).x;
			current.y = nextStep.get(minIndex).y;
			this.mapMatrix[current.x][current.y] = 8;
			if (current.x == 0 || current.x == 8 || current.y == 0
					|| current.y == 8) {
				this.printMatrix();
				System.out.println("you lose");
				break;
			}
			this.printMatrix();
		}
	}
	
	/**
	 * 返回point的下一步可走节点
	 * 由于每个点的后继可走节点最多为6个，同时对于当前节点所在行数的奇偶有关，必须
	 * 分情况讨论
	 * */
	private ArrayList<MyPoint> getNextStep(MyPoint point) {
		ArrayList<MyPoint> nextStepList = new ArrayList<MyPoint>();
		MyPoint[] nextstep = this.getNext6Step(point);
		
		for (int i = 0; i < nextstep.length; i++) {
			if (getValue(nextstep[i]) == 0)
				nextStepList.add(nextstep[i]);
		}
		return nextStepList;
	}
	
	/**
	 * 地图的初始化方法，默认设置18个堵截点
	 * */
	public void initial() {
		Random rand = new Random();
		Set<Integer> checkSet = new HashSet<Integer>();
		for (int count = 0; count < this.initPoint; count++) {
			int randPoint = rand.nextInt(81) + 1; // 生成1到81之间的随机数（闭区间）
			while (checkSet.contains(randPoint) || randPoint == 41) {
				randPoint =rand.nextInt(81) + 1;
			}
			checkSet.add(randPoint);
			int row = randPoint / 9;
			if (row == 9)
				row = 8;
			int col = randPoint % 9;
			if (col == 0)
				col = 8;
			else
				col -= 1;
			this.mapMatrix[row][col] = 1;
		}
		this.mapMatrix[4][4] = 8; // 神经猫的初始站位
	}
	
	// 索引从1开始
	private void setValue(int row, int col) {
		if (row < 1 || row > 9)
			return;
		if (col < 1 || col > 9) 
			return;
		// 设置为1，表示已经占领
		this.mapMatrix[row-1][col-1] = 1;
	}
	
	// 超过索引范围的一律返回1, 从0开始
	private int getValue(int row, int col) {
		if (row < 0 || row > 8)
			return 1;
		if (col < 0 || col > 8)
			return 1;
		return this.mapMatrix[row][col];
		
	}
	// 重载方法
	private int getValue(MyPoint point) {
		if (point.x < 0 || point.x > 8)
			return 1;
		if (point.y < 0 || point.y > 8)
			return 1;
		return this.mapMatrix[point.x][point.y];
	}
	
	/**
	 * 一个封装方法，获取参数point的后继六个点，无需判断point所在行数的奇偶性
	 * 返回一个长度为6的MyPoint数组，6个节点的排列顺序如下
	 *              1     2
	 *           6   point   3
	 *              5      4
	 * 数字代表这个点的在数组中的索引号+1
	 * @return
	 * 
	 * */
	private MyPoint[] getNext6Step(MyPoint point) {
		MyPoint[] array = new MyPoint[6];
		if (point.x % 2 == 0) {
			// 奇数行
			MyPoint point1 = new MyPoint(point.x-1, point.y-1);
			array[0] = point1;
			
			MyPoint point2 = new MyPoint(point.x-1, point.y);
			array[1] = point2;
			
			MyPoint point3 = new MyPoint(point.x, point.y+1);
			array[2] = point3;
			
			MyPoint point4 = new MyPoint(point.x+1, point.y);
			array[3] = point4;
			
			MyPoint point5 = new MyPoint(point.x+1, point.y-1);
			array[4] = point5;
			
			MyPoint point6 = new MyPoint(point.x, point.y-1);
			array[5] = point6;
		}
		else {
			// 偶数行
			MyPoint point1 = new MyPoint(point.x-1, point.y);
			array[0] = point1;
			
			MyPoint point2 = new MyPoint(point.x-1, point.y+1);
			array[1] = point2;
			
			MyPoint point3 = new MyPoint(point.x, point.y+1);
			array[2] = point3;
			
			MyPoint point4 = new MyPoint(point.x+1, point.y+1);
			array[3] = point4;
			
			MyPoint point5 = new MyPoint(point.x+1, point.y);
			array[4] = point5;
			
			MyPoint point6 = new MyPoint(point.x, point.y-1);
			array[5] = point6;
		}
		return array;
	}
	
	
	/**
	 * 根据previous的位置，判断出point的下一步可选节点
	 * 根据point和previous的行数的奇偶位置，需要分情况讨论
	 * 对使用者来说这些细节透明，是一个封装方法
	 * @param
	 * */
	private int[] getnextStepWithPrev(MyPoint point, MyPoint previous) {
		int[] result = new int[3];
		if (previous.x == point.x && previous.y < point.y) {
			// 右边  2 3 4
			result[0] = 2;
			result[1] = 3;
			result[2] = 4;
			return result;
		}
		if (previous.x == point.x && previous.y > point.y) {
			// 左边 1 5 6
			result[0] = 1;
			result[1] = 5;
			result[2] = 6;
			return result;
		}
		if (previous.x % 2 == 0) {
			// previous节点在奇数行
			if (previous.x > point.x && previous.y > point.y) {
				// 左上 1 2 6
				result[0] = 1;
				result[1] = 2;
				result[2] = 6;
			}
			else if (previous.x > point.x && previous.y == point.y) {
				// 右上角 1 2 3
				result[0] = 1;
				result[1] = 2;
				result[2] = 3;
			}
			else if (previous.x < point.x && previous.y == point.y) {
				// 右下角 3 4 5
				result[0] = 3;
				result[1] = 4;
				result[2] = 5;
			}
			else {
				// previous.x < point.x && previous.y > point.y
				// 左下角 4 5 6
				result[0] = 4;
				result[1] = 5;
				result[2] = 6;
			}
		}
		else {
			// previous节点在偶数行
			if (previous.x > point.x && previous.y == point.y) {
				// 左上角 1 2 6
				result[0] = 1;
				result[1] = 2;
				result[2] = 6;
			}
			else if (previous.x > point.x && previous.y < point.y) {
				// 右上角  1 2 3
				result[0] = 1;
				result[1] = 2;
				result[2] = 3;
			}
			else if (previous.x < point.x && previous.y < point.y) {
				// 右下角 3 4 5
				result[0] = 3;
				result[1] = 4;
				result[2] = 5;
			}
			else {
				// previous.x < point.x && previous.y == point.y
				// 左下角 4 5 6
				result[0] = 4;
				result[1] = 5;
				result[2] = 6;
			}
		}
		return result;
	}
	
	/**
	 * 获取point的逃亡最短步数，如果逃不出去返回-1
	 * 如果已经在边界，那么返回0
	 * 递归方法
	 * x = row, y = column
	 * @param point 
	 * 
	 * */
	public int getMinStep(MyPoint point, MyPoint previous, Stack<MyPoint> path) {
		if (point.x == 0 || point.x == 8 || point.y == 0 
				|| point.y == 8)
			return 0;
		
		// 将当前节点压栈
		path.push(point);
		// 获取point的下一步的相邻可选节点
		ArrayList<MyPoint> nextStepList = new ArrayList<MyPoint>();
		// 下一步最多可以访问6个节点
		MyPoint[] nextstep = this.getNext6Step(point);
		if (previous == null) {
			for (int i = 0; i < nextstep.length; i++) {
				if (getValue(nextstep[i]) == 0 && !path.contains(nextstep[i]))
					nextStepList.add(nextstep[i]);
			}
		}
		else {
			// 获取可走的三个节点
			int[] nodeIndex = this.getnextStepWithPrev(point, previous);
			for (int i = 0; i < nodeIndex.length; i++) {
				if (getValue(nextstep[nodeIndex[i]-1]) == 0 && !path.contains(nextstep[nodeIndex[i]-1]))
					nextStepList.add(nextstep[nodeIndex[i]-1]);
			}
		}
		int minStep = -1;
		for (int index = 0; index < nextStepList.size(); index++) {
			int distance = -1;
			if ((distance = getMinStep(nextStepList.get(index), point,path)) != -1) {
				// 表示有通路
				distance += 1;
				if (minStep == -1 || minStep > distance)
					minStep = distance;
			}
		}
		nextStepList.clear();
		// 当前节点估计完毕，弹出栈
		path.pop();
		return minStep;
	}
	/*public void debugFunc() {
		this.mapMatrix[0][2] = 1;
		this.mapMatrix[0][3] = 1;
		this.mapMatrix[0][5] = 1;
		this.mapMatrix[1][5] = 1;
		this.mapMatrix[1][8] = 1;
		this.mapMatrix[2][7] = 1;
		this.mapMatrix[2][8] = 1;
		this.mapMatrix[3][0] = 1;
		this.mapMatrix[3][1] = 1;
		this.mapMatrix[3][7] = 1;
		this.mapMatrix[4][0] = 1;
		this.mapMatrix[4][3] = 1;
		this.mapMatrix[5][5] = 1;
		this.mapMatrix[5][6] = 1;
		this.mapMatrix[5][8] = 1;
		this.mapMatrix[6][1] = 1;
		this.mapMatrix[7][0] = 1;
		this.mapMatrix[8][4] = 1;
		this.mapMatrix[4][4] = 8;
	}*/
}
