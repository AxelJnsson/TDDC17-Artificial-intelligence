package tddc17;

import aima.core.environment.liuvacuum.*;
import aima.core.agent.Action;
import aima.core.agent.AgentProgram;
import aima.core.agent.Percept;
import aima.core.agent.impl.*;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.PriorityQueue;
import java.util.Random;

class Node implements Comparable<Node> {

	Integer xcord;
	Integer ycord;
	Integer weight;
	Node parent;

	Node(Integer xcord, Integer ycord, Integer weight, Node parent) {
		this.xcord = xcord;
		this.ycord = ycord;
		this.weight = weight;
		this.parent = parent;
	}

	@Override

	public int compareTo(Node no) {

		int xcord = this.xcord.compareTo(no.xcord);
		return xcord == 0 ? this.ycord.compareTo(no.xcord) : xcord;
	}
}

class MyAgentState {
	public int[][] world = new int[30][30];
	public int initialized = 0;
	final int UNKNOWN = 0;
	final int WALL = 1;
	final int CLEAR = 2;
	final int DIRT = 3;
	final int HOME = 4;
	final int ACTION_NONE = 0;
	final int ACTION_MOVE_FORWARD = 1;
	final int ACTION_TURN_RIGHT = 2;
	final int ACTION_TURN_LEFT = 3;
	final int ACTION_SUCK = 4;

	public int agent_x_position = 1;
	public int agent_y_position = 1;
	public int agent_last_action = ACTION_NONE;
	public static final int NORTH = 0;
	public static final int EAST = 1;
	public static final int SOUTH = 2;
	public static final int WEST = 3;
	public int agent_direction = EAST;

	MyAgentState() {
		for (int i = 0; i < world.length; i++)
			for (int j = 0; j < world[i].length; j++)
				world[i][j] = UNKNOWN;
		world[1][1] = HOME;
		agent_last_action = ACTION_NONE;
	}

	// Based on the last action and the received percept updates the x & y agent
	// position
	public void updatePosition(DynamicPercept p) {
		Boolean bump = (Boolean) p.getAttribute("bump");

		if (agent_last_action == ACTION_MOVE_FORWARD && !bump) {
			switch (agent_direction) {
			case MyAgentState.NORTH:
				agent_y_position--;
				break;
			case MyAgentState.EAST:
				agent_x_position++;
				break;
			case MyAgentState.SOUTH:
				agent_y_position++;
				break;
			case MyAgentState.WEST:
				agent_x_position--;
				break;
			}
		}

	}

	public void updateWorld(int x_position, int y_position, int info) {
		world[x_position][y_position] = info;
	}

	public void printWorldDebug() {
		for (int i = 0; i < world.length; i++) {
			for (int j = 0; j < world[i].length; j++) {
				if (world[j][i] == UNKNOWN)
					System.out.print(" ? ");
				if (world[j][i] == WALL)
					System.out.print(" # ");
				if (world[j][i] == CLEAR)
					System.out.print(" . ");
				if (world[j][i] == DIRT)
					System.out.print(" D ");
				if (world[j][i] == HOME)
					System.out.print(" H ");
			}
			System.out.println("");
		}
	}
}

class MyAgentProgram implements AgentProgram {

	private int initnialRandomActions = 10;
	private Random random_generator = new Random();

	// Here you can define your variables!
	public ArrayList<Node> pq = new ArrayList<Node>();
	public ArrayList<Node> visited = new ArrayList<Node>();
	public ArrayList<Point> queueContain = new ArrayList<Point>();
	public ArrayList<Node> path = new ArrayList<Node>();
	public ArrayList<Node> map = new ArrayList<Node>();
	public Node currentNode;
	public Node goalNode;
	public int iterationCounter = 10000;
	public int northCounter = 0;
	public int southCounter = 0;
	public int westCounter = 0;
	public int eastCounter = 0;
	public int totalCounter = 0;
	public boolean goneHome = false;
	public MyAgentState state = new MyAgentState();

	// moves the Agent to a random start position
	// uses percepts to update the Agent position - only the position, other
	// percepts are ignored
	// returns a random action
	private Action moveToRandomStartPosition(DynamicPercept percept) {
		int action = random_generator.nextInt(6);
		initnialRandomActions--;
		state.updatePosition(percept);
		if (action == 0) {
			state.agent_direction = ((state.agent_direction - 1) % 4);
			if (state.agent_direction < 0)
				state.agent_direction += 4;
			state.agent_last_action = state.ACTION_TURN_LEFT;
			return LIUVacuumEnvironment.ACTION_TURN_LEFT;
		} else if (action == 1) {
			state.agent_direction = ((state.agent_direction + 1) % 4);
			state.agent_last_action = state.ACTION_TURN_RIGHT;
			return LIUVacuumEnvironment.ACTION_TURN_RIGHT;
		}
		state.agent_last_action = state.ACTION_MOVE_FORWARD;
		return LIUVacuumEnvironment.ACTION_MOVE_FORWARD;
	}

	
	public Action forward() {
		visited.add(new Node(currentNode.xcord, currentNode.ycord, 0, currentNode.parent));
		//addNeigh(currentNode);
		state.agent_last_action = state.ACTION_MOVE_FORWARD;
		return LIUVacuumEnvironment.ACTION_MOVE_FORWARD;
	}

	public Action walk() {

		if (northCounter > 0) {
			if (state.agent_direction != state.NORTH) {
				return pivot(state.NORTH);
			} else {
				northCounter--;
				return forward();
			}
		} else if (southCounter > 0) {
			if (state.agent_direction != state.SOUTH) {
				return pivot(state.SOUTH);
			} else {
				southCounter--;
				return forward();
			}
		} else if (eastCounter > 0) {
			if (state.agent_direction != state.EAST) {
				return pivot(state.EAST);
			} else {
				eastCounter--;
				return forward();
			}
		} else {{
			if (state.agent_direction != state.WEST) {
				return pivot(state.WEST);
			} else {
				westCounter--;
				return forward();
			}
		}
	}
	}

	// public moveNorth(int steps)

	// state.agent_last_action = state.ACTION_MOVE_FORWARD;
	// return LIUVacuumEnvironment.ACTION_MOVE_FORWARD;

	// }

	public Action pivot(int desired_dir) {

		if (desired_dir == state.NORTH) {
			if (state.agent_direction == state.WEST) {
				state.agent_direction = state.NORTH;
				state.agent_last_action = state.ACTION_TURN_RIGHT;
				return LIUVacuumEnvironment.ACTION_TURN_RIGHT;
			} else if (state.agent_direction == state.EAST) {
				state.agent_direction = state.NORTH;
				state.agent_last_action = state.ACTION_TURN_LEFT;
				return LIUVacuumEnvironment.ACTION_TURN_LEFT;
			} else if (state.agent_direction == state.SOUTH) {
				state.agent_direction = state.WEST;
				state.agent_last_action = state.ACTION_TURN_RIGHT;
				return LIUVacuumEnvironment.ACTION_TURN_RIGHT;
			}

		} else if (desired_dir == state.SOUTH) {
			if (state.agent_direction == state.WEST) {
				state.agent_direction = state.SOUTH;
				state.agent_last_action = state.ACTION_TURN_LEFT;
				return LIUVacuumEnvironment.ACTION_TURN_LEFT;
			} else if (state.agent_direction == state.EAST) {
				state.agent_direction = state.SOUTH;
				state.agent_last_action = state.ACTION_TURN_RIGHT;
				return LIUVacuumEnvironment.ACTION_TURN_RIGHT;
			} else if (state.agent_direction == state.NORTH) {
				state.agent_direction = state.EAST;
				state.agent_last_action = state.ACTION_TURN_RIGHT;
				return LIUVacuumEnvironment.ACTION_TURN_RIGHT;

			}
		} else if (desired_dir == state.WEST) {
			if (state.agent_direction == state.SOUTH) {
				state.agent_direction = state.WEST;
				state.agent_last_action = state.ACTION_TURN_RIGHT;
				return LIUVacuumEnvironment.ACTION_TURN_RIGHT;
			} else if (state.agent_direction == state.EAST) {
				state.agent_direction = state.SOUTH;
				state.agent_last_action = state.ACTION_TURN_RIGHT;
				return LIUVacuumEnvironment.ACTION_TURN_RIGHT;
			} else if (state.agent_direction == state.NORTH) {
				state.agent_direction = state.WEST;
				state.agent_last_action = state.ACTION_TURN_LEFT;
				return LIUVacuumEnvironment.ACTION_TURN_LEFT;
			}
		} else if (desired_dir == state.EAST) {
			if (state.agent_direction == state.SOUTH) {
				state.agent_direction = state.EAST;
				state.agent_last_action = state.ACTION_TURN_LEFT;
				return LIUVacuumEnvironment.ACTION_TURN_LEFT;
			} else if (state.agent_direction == state.WEST) {
				state.agent_direction = state.NORTH;
				state.agent_last_action = state.ACTION_TURN_RIGHT;
				return LIUVacuumEnvironment.ACTION_TURN_RIGHT;
			} else if (state.agent_direction == state.NORTH) {
				state.agent_direction = state.EAST;
				state.agent_last_action = state.ACTION_TURN_RIGHT;
				return LIUVacuumEnvironment.ACTION_TURN_RIGHT;
			}
		}
		return null;

//		if (state.agent_direction == state.WEST) {
//			state.agent_direction = state.NORTH;
//			state.agent_last_action = state.ACTION_TURN_RIGHT;
//			return LIUVacuumEnvironment.ACTION_TURN_RIGHT;
//		} else {
//			state.agent_direction++;
//			state.agent_last_action = state.ACTION_TURN_RIGHT;
//			return LIUVacuumEnvironment.ACTION_TURN_RIGHT;
//		}

	}
	public boolean checkNew (Node n) {
		
		if (!map.isEmpty()) {
			for (int i= 0; i < map.size(); i++) {
				if (map.get(i).xcord == n.xcord && map.get(i).ycord == n.ycord) {
					return false;
				}
			}
		}
		return true;
	}
	public boolean checkNeigh(Node n) {
		if (visited.isEmpty() && pq.isEmpty()) {
			// System.out.println("Bpda är tomma");
			return true;
		}

		if (!visited.isEmpty()) {
			for (int i = 0; i < visited.size(); i++) {
				// System.out.println("Visited ar inte tom");
				if ((visited.get(i).xcord == n.xcord && visited.get(i).ycord == n.ycord)) {
					return false;

				}
			}
		}

		if (!pq.isEmpty()) {
			// System.out.println("Visited ar inte tom och pq ar inte tom");
			for (int j = 0; j < pq.size(); j++) {

				if ((pq.get(j).xcord == n.xcord && pq.get(j).ycord == n.ycord)) {
					// System.out.println("Letar efter lika");
					return false;
				}

			}
		}

		// System.out.println("Lägger till ");
		return true;
	}

	public void addNeigh(Node parentNode) {
		Node n = addNode(1, 0, parentNode);

		if (checkNeigh(n)) {
			pq.add(n);
		}
		if (checkNew(n)) {
			map.add(n);
		}
		
		
		n = addNode(-1, 0, parentNode);
		if (checkNeigh(n)) {
			pq.add(n);
		}
		if (checkNew(n)) {
			map.add(n);
		}
		

		n = addNode(0, 1, parentNode);
		if (checkNeigh(n)) {
			pq.add(n);
		}
		if (checkNew(n)) {
			map.add(n);
		}
		

		n = addNode(0, -1, parentNode);
		if (checkNeigh(n)) {
			pq.add(n);
		}
		if (checkNew(n)) {
			map.add(n);
		}
		

	}

	public Node addNode(int xNeigh, int yNeigh, Node parentNode) {
		Node new_node = new Node(currentNode.xcord + xNeigh, currentNode.ycord + yNeigh, 0, parentNode);
		return new_node;
	}

	public void search() {
		Point point;
	

		if (!pq.isEmpty()) {
			Node newNode = new Node(pq.get(0).xcord, pq.get(0).ycord, pq.get(0).weight, pq.get(0).parent);
			pq.remove(0);
			//visited.add(new Node(newNode.xcord, newNode.ycord, 0, newNode.parent));
			point = new Point(newNode.xcord, newNode.ycord);
			goalNode = new Node(newNode.xcord, newNode.ycord, newNode.weight, newNode.parent);
			path = path(currentNode);
			//pathTo(path);
		} else if (pq.isEmpty()) {
			Node home = new Node(1, 1, 0, null);
			path.add(home);
			pathTo(path);
		}

	}

	public ArrayList<Node> path(Node start) {
		path.clear();
		path = new ArrayList<Node>();
		boolean pathRoot = false;
		boolean startRoot = false;
		ArrayList<Node> startPath = new ArrayList<Node>();
		startPath.clear();
		Node startNode = new Node(start.xcord, start.ycord, start.weight, start.parent);

		Node goal = new Node(goalNode.xcord, goalNode.ycord, goalNode.weight, goalNode.parent);

		//System.out.println("Har borjar Path");
		
		while(!(goal.xcord == start.xcord && goal.ycord == start.ycord)){
			if ((goal.xcord == startNode.xcord && goal.ycord == startNode.ycord)) {
				break;
			}
			
			for (int i = 0; i <path.size(); i++) {
				for (int j = 0; j < startPath.size(); j++) {
				if (path.get(i).xcord == startPath.get(j).xcord && path.get(i).ycord == startPath.get(j).ycord) {
				break;	
				}
				}
			}
			
			if (pathRoot == false) {
				path.add(new Node(goal.xcord, goal.ycord, goal.weight, goal.parent));

			goal = new Node(goal.parent.xcord, goal.parent.ycord, goal.parent.weight, goal.parent.parent);
			path.add(new Node(goal.xcord, goal.ycord, goal.weight, goal.parent));


			}
			if (goal.parent == null) {
				pathRoot = true;
			}
			if (startRoot == false) {
			if (startNode.parent != null) {
				
			
			startPath.add(new Node(startNode.xcord, startNode.ycord, startNode.weight, startNode.parent));
			startNode = new Node(startNode.parent.xcord, startNode.parent.ycord, startNode.parent.weight,
					startNode.parent.parent);
			}
			}
			if (startNode.parent == null) {
				startRoot = true;
			
			}
			if (pathRoot == true && startRoot == true) {
				break;
			}
			
			
		}
//		System.out.println("DelPath:");
//		for (int i = 0; i < path.size(); i++) {
//			System.out.println("X:" + path.get(i).xcord + " Y: " + path.get(i).ycord);
//		}
//		System.out.println("Startpath");
//		// System.out.println("GoalX: " + goalNode.xcord + "GoalY: " + goalNode.ycord);
//		// System.out.println("Path:");
//		for (int i = 0; i < startPath.size(); i++) {
//			System.out.println("X:" + startPath.get(i).xcord + " Y: " + startPath.get(i).ycord);
//		}
		if (!(goal.xcord == start.xcord && goal.ycord==start.ycord)) {
		while (!startPath.isEmpty()) {
			path.add(new Node(startPath.get(startPath.size() - 1).xcord, startPath.get(startPath.size() - 1).ycord,
					startPath.get(startPath.size() - 1).weight, startPath.get(startPath.size() - 1).parent));
			startPath.remove(startPath.size() - 1);
		}
		}

		
		
//		System.out.println("GoalX: " + goalNode.xcord + "GoalY: " + goalNode.ycord);
//		System.out.println("Hela Path:");
//		for (int i = 0; i < path.size(); i++) {
//			System.out.println("X:" + path.get(i).xcord + " Y: " + path.get(i).ycord);
//		}


		
		
		
		
//		while (!(goal.xcord == start.xcord && goal.ycord == start.ycord)) {
//			// Systen.out.println(goal.xcord + "goal" + goal.ycord);
//			// Systen.out.println(start.xcord + "start" + start.ycord);
//			path.add(new Node(goal.xcord, goal.ycord, goal.weight, goal.parent));
//			if (goal.parent == null) {
//				break;
//
//			}
//
//			goal = new Node(goal.parent.xcord, goal.parent.ycord, goal.parent.weight, goal.parent.parent);
//		}
//		System.out.println("DelPath:");
//		for (int i = 0; i < path.size(); i++) {
//			System.out.println("X:" + path.get(i).xcord + " Y: " + path.get(i).ycord);
//		}
//		if (goal.parent == null && start.parent != null) {
//			while (startNode.parent.parent != null) {
//				if (startNode.xcord == goal.xcord && startNode.ycord == goal.ycord) {
//					break;
//				}
//				startPath.add(new Node(startNode.xcord, startNode.ycord, startNode.weight, startNode.parent));
//				startNode = new Node(startNode.parent.xcord, startNode.parent.ycord, startNode.parent.weight,
//						startNode.parent.parent);
//			}
//			System.out.println("Startpath");
//			// System.out.println("GoalX: " + goalNode.xcord + "GoalY: " + goalNode.ycord);
//			// System.out.println("Path:");
//			for (int i = 0; i < startPath.size(); i++) {
//				System.out.println("X:" + startPath.get(i).xcord + " Y: " + startPath.get(i).ycord);
//			}
//
//			while (!startPath.isEmpty()) {
//				path.add(new Node(startPath.get(startPath.size() - 1).xcord, startPath.get(startPath.size() - 1).ycord,
//						startPath.get(startPath.size() - 1).weight, startPath.get(startPath.size() - 1).parent));
//				startPath.remove(startPath.size() - 1);
//			}
//
//		}
//		// System.out.println("GoalX: " + goalNode.xcord + "GoalY: " + goalNode.ycord);
//		// System.out.println("Path:");
//		// for (int i = 0; i<path.size(); i++) {
//		// System.out.println("X:" + path.get(i).xcord +" Y: " + path.get(i).ycord);
//		// }
//		System.out.println("GoalX: " + goalNode.xcord + "GoalY: " + goalNode.ycord);
//		System.out.println("Hela Path:");
//		for (int i = 0; i < path.size(); i++) {
//			System.out.println("X:" + path.get(i).xcord + " Y: " + path.get(i).ycord);
//		}
//		
//		for (int i = 0; i <path.size(); i++) {
//			for (int j = 0; j < path.size(); j++) {
//			if (path.get(i).xcord == path.get(j).xcord && path.get(i).ycord == path.get(j).ycord) {
//				for (int k = i +1; k <= j; k++) {
//					if (path.size() -1 < k ) {
//						break;
//					}
//					path.remove(k);
//				}
//			}
//			}
//		}
//		System.out.println("Path efter rens");
//		for (int i = 0; i < path.size(); i++) {
//			System.out.println("X:" + path.get(i).xcord + " Y: " + path.get(i).ycord);
//		}
		

		return path;
	}

	public void pathTo(ArrayList<Node> path) {
		int xpath;
		int ypath;
		northCounter = 0;
		southCounter = 0;
		westCounter = 0;
		eastCounter = 0;
		Point newPoint = new Point(0, 0);

		newPoint.setLocation(path.get(path.size() - 1).xcord, path.get(path.size() - 1).ycord);
		path.remove(path.size() - 1);
		System.out.println("ar i pathTo");

		xpath = (int) (newPoint.getX() - state.agent_x_position);
		ypath = (int) (newPoint.getY() - state.agent_y_position);
		// System.out.println(coord.getX() + "mallan" + coord.getY());
		System.out.println("GoalX: " + goalNode.xcord + "GoalY: " + goalNode.ycord);
		System.out.println("Path:");
		for (int i = 0; i < path.size(); i++) {
			System.out.println("X:" + path.get(i).xcord + " Y: " + path.get(i).ycord);
		}

		if (ypath < 0) {
			northCounter = 1;
		} else if (ypath > 0) {
			southCounter = 1;
		}

		if (xpath < 0) {
			westCounter = 1;
		} else if (xpath > 0) {
			eastCounter = 1;
		}
		totalCounter = northCounter + southCounter + eastCounter + westCounter;

	}

	
	@Override
	public Action execute(Percept percept) {
		state.updateWorld(1, 1, 4);
		// DO NOT REMOVE this if condition!!!
		if (initnialRandomActions > 0) {
			return moveToRandomStartPosition((DynamicPercept) percept);
		} else if (initnialRandomActions == 0) {
			// process percept for the last step of the initial random actions
			initnialRandomActions--;
			state.updatePosition((DynamicPercept) percept);
			System.out.println("Processing percepts after the last execution of moveToRandomStartPosition()");
			state.agent_last_action = state.ACTION_SUCK;
			currentNode = new Node(state.agent_x_position, state.agent_y_position, 0, null);
			map.add(new Node(state.agent_x_position, state.agent_y_position, 0, null));
			addNeigh(currentNode);
			return LIUVacuumEnvironment.ACTION_SUCK;
		}

		// This example agent program will update the internal agent state while only
		// moving forward.
		// START HERE - code below should be modified!

		System.out.println("x=" + state.agent_x_position);
		System.out.println("y=" + state.agent_y_position);
		System.out.println("dir=" + state.agent_direction);

		iterationCounter--;
		System.out.println(iterationCounter);
		if (iterationCounter == 0)
			return NoOpAction.NO_OP;

		DynamicPercept p = (DynamicPercept) percept;
		Boolean bump = (Boolean) p.getAttribute("bump");
		Boolean dirt = (Boolean) p.getAttribute("dirt");
		Boolean home = (Boolean) p.getAttribute("home");
		System.out.println("percept: " + p);

		// State update based on the percept value and the last action
		state.updatePosition((DynamicPercept) percept);
		addNeigh(currentNode);
		System.out.println("Innan" + "CurrentX: " + currentNode.xcord + " CurrentY: " + currentNode.ycord);

		for (int i = 0; i < map.size(); i++) {
			if (state.agent_x_position == map.get(i).xcord && state.agent_y_position == map.get(i).ycord) {
				currentNode = map.get(i);
			}
		}
		for (int i=0; i<pq.size(); i++) {
			if (pq.get(i).xcord == currentNode.xcord && pq.get(i).ycord == currentNode.ycord) {
				pq.remove(i);
				visited.add(new Node(currentNode.xcord, currentNode.ycord, currentNode.weight, currentNode.parent));
			}
		}
		System.out.println("CurrentX: " + currentNode.xcord + " CurrentY: " + currentNode.ycord);

		
		 addNeigh(currentNode);
		if (bump) {
			switch (state.agent_direction) {
			case MyAgentState.NORTH:
				state.updateWorld(state.agent_x_position, state.agent_y_position - 1, state.WALL);
				break;
			case MyAgentState.EAST:
				state.updateWorld(state.agent_x_position + 1, state.agent_y_position, state.WALL);
				break;
			case MyAgentState.SOUTH:
				state.updateWorld(state.agent_x_position, state.agent_y_position + 1, state.WALL);
				break;
			case MyAgentState.WEST:
				state.updateWorld(state.agent_x_position - 1, state.agent_y_position, state.WALL);
				break;
			}
		}
		if (dirt)
			state.updateWorld(state.agent_x_position, state.agent_y_position, state.DIRT);
		else
			state.updateWorld(state.agent_x_position, state.agent_y_position, state.CLEAR);

		state.printWorldDebug();

		if (dirt) {
			System.out.println("DIRT -> choosing SUCK action!");
			state.agent_last_action = state.ACTION_SUCK;

			return LIUVacuumEnvironment.ACTION_SUCK;
		}
		if (!bump && state.agent_last_action == state.ACTION_MOVE_FORWARD) {
			
		}
		if (goneHome == true && state.agent_x_position == 1 && state.agent_y_position == 1) {
			state.updateWorld(state.agent_x_position, state.agent_y_position, state.HOME);
			state.agent_last_action = state.ACTION_NONE;
			System.out.println("Visited:");
			for (int j = 0; j < visited.size(); j++) {
				System.out.println("X: " + visited.get(j).xcord + " Y: " + visited.get(j).ycord);
			}
			System.out.println("Queue:");
		
			for (int j = 0; j < pq.size(); j++) {
				System.out.println("X: " + pq.get(j).xcord + " Y: " + pq.get(j).ycord);
			}
			return NoOpAction.NO_OP;
		} //else {
			//if (state.agent_last_action == state.ACTION_MOVE_FORWARD) {
				//if (currentNode.parent == null) {
					//Node tempNode = new Node(currentNode.xcord, currentNode.ycord, currentNode.weight, null);
					//currentNode = new Node(state.agent_x_position, state.agent_y_position, 0, tempNode);

//				} else {
	//				Node tempNode = new Node(currentNode.xcord, currentNode.ycord, currentNode.weight,
		//					currentNode.parent);
			//		currentNode = new Node(state.agent_x_position, state.agent_y_position, 0, tempNode);

				//}

		//	}

			if (pq.isEmpty()) {
				goneHome = true;
			} else {
				goneHome = false;
			}

			System.out.println("Queue:");

			for (int i = 0; i < pq.size(); i++) {
				System.out.println("X: " + pq.get(i).xcord + " Y: " + pq.get(i).ycord);
			}

			if (goalNode == null) {
				search();
			} else if (goalNode.xcord == currentNode.xcord && goalNode.ycord == currentNode.ycord) {
				search();
			} else if (bump && state.world[goalNode.xcord][goalNode.ycord] == 1) {
				for (int i = 0; i<pq.size(); i++) {
					if(goalNode.xcord == pq.get(i).xcord && goalNode.ycord == pq.get(i).ycord) {
						pq.remove(i);
						visited.add(new Node(goalNode.xcord, goalNode.ycord, goalNode.weight, goalNode.parent));
						
					}
				}
				for (int i = 0; i<map.size(); i++) {
					if(goalNode.xcord == map.get(i).xcord && goalNode.ycord == map.get(i).ycord) {
						map.remove(i);
					}
				}
				search();
			}
			totalCounter = westCounter + eastCounter + northCounter + southCounter;

			if (totalCounter > 0) {
			
				return walk();
			} else if (!path.isEmpty()) {
				pathTo(path);
				return walk();
			} else {
				search();
				return walk();
			}

		}
	}


public class MyVacuumAgent extends AbstractAgent {
	public MyVacuumAgent() {
		super(new MyAgentProgram());
	}
}
