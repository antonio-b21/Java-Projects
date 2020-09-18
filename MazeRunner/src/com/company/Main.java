package com.company;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Main {
    private static final Scanner sc = new Scanner(System.in);
    private static Maze maze = null;

    private static void printMenu() {
        System.out.println();
        System.out.println("=== Menu ===");
        System.out.println("1. Generate a new maze");
        System.out.println("2. Load a maze");
        if (maze != null) {
            System.out.println("3. Save the maze");
            System.out.println("4. Display the maze");
            System.out.println("5. Find the escape.");
        }
        System.out.println("0. Exit");
    }

    public static void main(String[] args) {
        int option;
        do {
            printMenu();
            option = sc.nextInt();
            switch (option) {
                case 1:
                    System.out.println("Enter the size of a maze ");
                    int rows = sc.nextInt();
                    int cols = rows;

                    maze = new Maze(rows, cols);
                    System.out.println(maze);
                    break;

                case 2:
                    try {
                        FileInputStream fileIn = new FileInputStream(sc.next());
                        ObjectInputStream in = new ObjectInputStream(fileIn);
                        maze = (Maze) in.readObject();
                        in.close();
                        fileIn.close();
                    } catch (FileNotFoundException e) {
                        System.out.println("The file ... does not exist");
                    } catch (ClassNotFoundException | IOException e) {
                        System.out.println("Cannot load the maze. It has an invalid format");
                    }
                    break;

                case 3:
                    if (maze == null) {
                        doDefault();
                    } else {
                        try {
                            FileOutputStream fileOut = new FileOutputStream(sc.next());
                            ObjectOutputStream out = new ObjectOutputStream(fileOut);
                            out.writeObject(maze);
                            out.close();
                            fileOut.close();
                        } catch (IOException e) {
                            System.out.println("Cannot save the maze.");
                        }
                    }
                    break;

                case 4:
                    if (maze == null) {
                        doDefault();
                    } else {
                        System.out.println(maze);
                    }
                    break;

                case 5:
                    System.out.println(maze.solve());
                    break;

                case 0:
                    System.out.println("Bye!");
                    break;

                default:
                    doDefault();
            }
        } while (option != 0);
    }

    private static void doDefault() {
        System.out.println("Incorrect option. Please try again");
    }
}

class Maze implements Serializable {
    private final int rows;
    private final int cols;
    private final int[] start;
    private final int[] finish;
    private int[][] maze;


    public Maze(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        initialize();
        generateInside();
        start = generateExit();
        finish = generateExit();
    }

    private void initialize() {
        maze = new int[rows][cols];

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (i < rows - 1 && i % 2 == 1 &&
                        j < cols - 1 && j % 2 == 1) {
                    maze[i][j] = 0; //place 0 where nodes should be
                } else {
                    maze[i][j] = 1;
                }
            }
        }

        if (cols % 2 == 0) {
            for (int i = 1; i < rows - 1; i += 2) {
                maze[i][cols - 2] = 0;
            }
        }
        if (rows % 2 == 0) {
            for (int j = 1; j < cols - 1; j += 2) {
                maze[rows - 2][j] = 0;
            }
        }
    }

    private void generateInside() {
        int graphRows = rows % 2 == 0 ? (rows - 1) / 2 : rows / 2;
        int graphCols = cols % 2 == 0 ? (cols - 1) / 2 : cols / 2;
        Graph graph = new Graph(graphRows, graphCols);
        List<Edge> mst = graph.getMinimumSpanningTree();
        for (Edge edge : mst) { // for every edge place 0 between nodes
            int row = (edge.getSrc() / graphCols) * 2 + 1;
            int col = (edge.getSrc() % graphCols) * 2 + 1;
            if (edge.getSrc() + graphCols == edge.getDest()) {
                maze[row + 1][col] = 0;
            } else {
                maze[row][col + 1] = 0;
            }
        }
    }

    private int[] generateExit() {
        Random rand = new Random();
        int spot;
        while (true) {
            if (rand.nextInt(2) == 0) {
                //vertical walls
                spot = rand.nextInt(rows - 2) + 1;
                if (rand.nextInt(2) == 0) {
                    //left wall
                    if (maze[spot][0] == 1 && //is wall
                            maze[spot][1] == 0 && //leads to the maze
                            maze[spot - 1][0] == 1 && //it is not right near another exit
                            maze[spot + 1][0] == 1) {
                        maze[spot][0] = 0;

                        return new int[]{spot, 0};
                    }
                } else {
                    //right wall
                    if (maze[spot][cols - 1] == 1 && //is wall
                            maze[spot][cols - 2] == 0 && //leads to the maze
                            maze[spot - 1][cols - 1] == 1 && //it is not right near another exit
                            maze[spot + 1][cols - 1] == 1) {
                        maze[spot][cols - 1] = 0;
                        return new int[]{spot, cols - 1};
                    }
                }
            } else {
                //horizontal walls
                spot = rand.nextInt(cols - 2) + 1;
                if (rand.nextInt(2) == 0) {
                    //top wall
                    if (maze[0][spot] == 1 && //is wall
                            maze[1][spot] == 0 && //leads to the maze
                            maze[0][spot - 1] == 1 && //it is not right near another exit
                            maze[0][spot + 1] == 1) {
                        maze[0][spot] = 0;
                        return new int[]{0, spot};
                    }
                } else {
                    //bottom wall
                    if (maze[rows - 1][spot] == 1 && //is wall
                            maze[rows - 2][spot] == 0 && //leads to the maze
                            maze[rows - 1][spot - 1] == 1 && //it is not right near another exit
                            maze[rows - 1][spot + 1] == 1) {
                        maze[rows - 1][spot] = 0;
                        return new int[]{rows - 1, spot};
                    }
                }
            }
        }
    }

    public String solve() {
        int[][] solvedMaze = new int[rows][cols];
        for (int i = 0; i < rows; i++) {
            System.arraycopy(maze[i], 0, solvedMaze[i], 0, cols);
        }
        solvedMaze[start[0]][start[1]] = 2;
        int[] nextSpot = new int[0];
        if (start[0] == 0) {
            nextSpot = new int[]{1, start[1]};
        } else if (start[1] == 0) {
            nextSpot = new int[]{start[0], 1};
        } else if (start[0] == rows - 1) {
            nextSpot = new int[]{rows - 2, start[1]};
        } else if (start[1] == cols - 1) {
            nextSpot = new int[]{start[0], cols - 2};
        }
        recursiveStep(nextSpot, solvedMaze);

        return stringify(solvedMaze);
    }

    private boolean recursiveStep(int[] currentSpot, int[][] solvedMaze) {
        solvedMaze[currentSpot[0]][currentSpot[1]] = 2;
        if (Arrays.equals(currentSpot, finish)) {
            return true;
        }

        if (solvedMaze[currentSpot[0] - 1][currentSpot[1]] == 0 && //can advance up
                recursiveStep(new int[]{currentSpot[0] - 1, currentSpot[1]}, solvedMaze)) { //found a solution
            return true;
        }
        if (solvedMaze[currentSpot[0] + 1][currentSpot[1]] == 0 && //can advance down
                recursiveStep(new int[]{currentSpot[0] + 1, currentSpot[1]}, solvedMaze)) { //found a solution
            return true;
        }
        if (solvedMaze[currentSpot[0]][currentSpot[1] - 1] == 0 && //can advance left
                recursiveStep(new int[]{currentSpot[0], currentSpot[1] - 1}, solvedMaze)) { //found a solution
            return true;
        }
        if (solvedMaze[currentSpot[0]][currentSpot[1] + 1] == 0 && //can advance right
                recursiveStep(new int[]{currentSpot[0], currentSpot[1] + 1}, solvedMaze)) { //found a solution
            return true;
        }


        solvedMaze[currentSpot[0]][currentSpot[1]] = 0;
        return false;
    }

    private String stringify(int[][] maze) {
        return Arrays.stream(maze)
                .map(row -> Arrays.stream(row)
                        .mapToObj(spot -> switch (spot) {
                            case 0 -> "  ";
                            case 1 -> "\u2588\u2588";
                            case 2 -> "//";
                            default -> "";
                        })
                        .collect(Collectors.joining()))
                .collect(Collectors.joining("\n"));
    }

    @Override
    public String toString() {
        return stringify(maze);
    }
}

class Graph {
    private final int nodesNo;
    private final List<Edge> edges;
    private List<Edge> minimumSpanningTree;

    Graph(int rows, int cols) {
        this.nodesNo = rows * cols;
        int edgesNo = 2 * this.nodesNo - rows - cols;
        this.edges = new ArrayList<>(edgesNo);
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                int current = i * cols + j;
                if (j > 0) {
                    int left = current - 1;
                    edges.add(new Edge(left, current));
                }
                if (i > 0) {
                    int top = current - cols;
                    edges.add(new Edge(top, current));
                }
            }
        }
        kruskalMST();
    }

    private int find(List<Subset> subsets, int i) {
        if (subsets.get(i).parent != i) {
            subsets.get(i).parent = find(subsets, subsets.get(i).parent);
        }
        return subsets.get(i).parent;
    }

    private void union(List<Subset> subsets, int x, int y) {
        int xRoot = find(subsets, x);
        int yRoot = find(subsets, y);

        if (subsets.get(xRoot).rank < subsets.get(yRoot).rank) {
            subsets.get(xRoot).parent = yRoot;
        } else if (subsets.get(xRoot).rank > subsets.get(yRoot).rank) {
            subsets.get(yRoot).parent = xRoot;
        } else {
            subsets.get(yRoot).parent = xRoot;
            subsets.get(xRoot).rank++;
        }
    }

    private void kruskalMST() {
        List<Edge> result = new ArrayList<>(nodesNo - 1);

        Collections.sort(edges);

        List<Subset> subsets = new ArrayList<>(nodesNo);
        IntStream.range(0, nodesNo)
                .forEach(i -> subsets.add(new Subset(i)));

        for (Edge nextEdge : edges) {
            int x = find(subsets, nextEdge.getSrc());
            int y = find(subsets, nextEdge.getDest());

            if (x != y) {
                result.add(nextEdge);
                union(subsets, x, y);

                if (result.size() >= nodesNo - 1) {
                    break;
                }
            }
        }

        this.minimumSpanningTree = result;
    }

    public List<Edge> getMinimumSpanningTree() {
        return minimumSpanningTree;
    }

    private static class Subset {
        int parent;
        int rank;

        public Subset(int parent) {
            this.parent = parent;
            this.rank = 0;
        }
    }
}

class Edge implements Comparable<Edge> {
    private static final Random rand = new Random();
    private final int src;
    private final int dest;
    private final int weight;

    Edge(int src, int dest) {
        this.src = src;
        this.dest = dest;
        this.weight = rand.nextInt(100) + 1;
    }

    public int getSrc() {
        return src;
    }

    public int getDest() {
        return dest;
    }

    public int getWeight() {
        return weight;
    }

    @Override
    public int compareTo(Edge o) {
        return this.getWeight() - o.getWeight();
    }
}
