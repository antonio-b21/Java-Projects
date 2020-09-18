package com.company;

import java.util.*;

public class Minesweeper {
    private static final char UNKNOWN = '.';
    private static final char MINE = 'X';
    private static final char MARKED = '*';
    private static final char EMPTY = '/';
    private static final int SIZE = 9;
    private final char[][] field = new char[SIZE][SIZE];
    private final char[][] displayField = new char[SIZE][SIZE];
    private final int minesNo;
    private boolean uninitialised = true;

    {
        for (char[] row: field) {
            Arrays.fill(row, EMPTY);
        }
        for (char[] row:displayField) {
            Arrays.fill(row, UNKNOWN);
        }
    }

    Minesweeper(int minesNo) {
        this.minesNo = minesNo;
    }

    public void play() {
        Scanner sc = new Scanner(System.in);
        do {
            print();
            System.out.print("Set/unset mines marks or claim a cell as free (col row mine/free): ");
            int col = sc.nextInt() - 1;
            int row = sc.nextInt() - 1;
            String command = sc.next();

            if ("free".equals(command)) {
                if (uninitialised) {
                    placeMines(this.minesNo, row, col);
                }
                if (field[row][col] == MINE) {
                    revealMines();
                    print();
                    System.out.println("You stepped on a mine and failed!");
                    return;
                } else {
                    discover(row, col);
                }
            } else if("mine".equals(command)) {
                if (displayField[row][col] == UNKNOWN) {
                    displayField[row][col] = MARKED;
                } else if (displayField[row][col] == MARKED) {
                    displayField[row][col] = UNKNOWN;
                }
            }
        } while (!isComplete());

        print();
        System.out.println("Congratulations! You found all mines!");
    }

    private void print() {
        System.out.println();
        System.out.println("  | 1 2 3 4 5 6 7 8 9 |");
        System.out.println("- | - - - - - - - - - |");
        for (int i = 0; i < SIZE; i++) {
            StringBuilder row = new StringBuilder();
            row.append(" ");
            for (int j = 0; j < displayField[i].length; j++) {
                row.append(displayField[i][j]).append(" ");
            }
            System.out.printf("%d |%s|%n", i+1, row);
        }
        System.out.println("- | - - - - - - - - - |");
    }

    private void placeMines(int minesToBePlaced, int startRow, int startCol) {
        int minesPlaced = 0;
        Random rand = new Random();
        while (minesPlaced < minesToBePlaced) {
            int row = rand.nextInt(SIZE);
            int col = rand.nextInt(SIZE);
            if (field[row][col] == EMPTY && (row != startRow || col != startCol)) {
                field[row][col] = MINE;
                minesPlaced++;
            }
        }

        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                if (field[row][col] == EMPTY) {
                    int neighbours = calculateNeighbours(row, col);
                    if (neighbours > 0) {
                        field[row][col] = (char)(neighbours + 48);
                    }
                }
            }
        }
        uninitialised = false;
    }

    private int calculateNeighbours(int row, int col) {
        int neighbours = 0;
        if (row > 0 && col > 0 && field[row-1][col-1] == MINE) {
            neighbours++; //TOP_LEFT
        }
        if (row > 0 && field[row-1][col] == MINE) {
            neighbours++; //TOP
        }
        if (row > 0 && col < SIZE - 1 && field[row-1][col+1] == MINE) {
            neighbours++; //TOP-RIGHT
        }
        if (col < SIZE - 1 && field[row][col+1] == MINE) {
            neighbours++; //RIGHT
        }
        if (row < SIZE - 1 && col < SIZE - 1 && field[row+1][col+1] == MINE) {
            neighbours++; //BOTTOM-RIGHT
        }
        if (row < SIZE - 1 && field[row+1][col] == MINE) {
            neighbours++; //BOTTOM
        }
        if (row < SIZE - 1 && col > 0 && field[row+1][col-1] == MINE) {
            neighbours++; //BOTTOM-LEFT
        }
        if (col > 0 && field[row][col-1] == MINE) {
            neighbours++; //LEFT
        }
        return neighbours;
    }

    private void discover(int startRow, int startCol) {
        Queue<Integer> queueRow = new ArrayDeque<>();
        Queue<Integer> queueCol = new ArrayDeque<>();
        queueRow.offer(startRow);
        queueCol.offer(startCol);
        while (!queueRow.isEmpty()) {
            int row = queueRow.poll();
            int col = queueCol.poll();
            if (field[row][col] == EMPTY || field[row][col] == MARKED) {
                if (row > 0 && col > 0 &&
                        (displayField[row-1][col-1] == UNKNOWN || displayField[row-1][col-1] == MARKED)) {
                    queueRow.offer(row-1);
                    queueCol.offer(col-1); //TOP_LEFT
                }
                if (row > 0 &&
                        (displayField[row-1][col] == UNKNOWN || displayField[row-1][col] == MARKED)) {
                    queueRow.offer(row-1);
                    queueCol.offer(col); //TOP
                }
                if (row > 0 && col < SIZE - 1 &&
                        (displayField[row-1][col+1] == UNKNOWN || displayField[row-1][col+1] == MARKED)) {
                    queueRow.offer(row-1);
                    queueCol.offer(col+1); //TOP-RIGHT
                }
                if (col < SIZE - 1 &&
                        (displayField[row][col+1] == UNKNOWN || displayField[row][col+1] == MARKED)) {
                    queueRow.offer(row);
                    queueCol.offer(col+1); //RIGHT
                }
                if (row < SIZE - 1 && col < SIZE - 1 &&
                        (displayField[row+1][col+1] == UNKNOWN || displayField[row+1][col+1] == MARKED)) {
                    queueRow.offer(row+1);
                    queueCol.offer(col+1); //BOTTOM-RIGHT
                }
                if (row < SIZE - 1 &&
                        (displayField[row+1][col] == UNKNOWN || displayField[row+1][col] == MARKED)) {
                    queueRow.offer(row+1);
                    queueCol.offer(col); //BOTTOM
                }
                if (row < SIZE - 1 && col > 0 &&
                        (displayField[row+1][col-1] == UNKNOWN || displayField[row+1][col-1] == MARKED)) {
                    queueRow.offer(row+1);
                    queueCol.offer(col-1); //BOTTOM-LEFT
                }
                if (col > 0 &&
                        (displayField[row][col-1] == UNKNOWN || displayField[row][col-1] == MARKED)) {
                    queueRow.offer(row);
                    queueCol.offer(col-1); //LEFT
                }
            }
            displayField[row][col] = field[row][col];
        }
    }

    private void revealMines() {
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                if (field[row][col] == MINE) {
                    displayField[row][col] = MINE;
                }
            }
        }
    }

    private boolean isComplete() {
        return checkIfAllMinesAndOnlyAreMarked() || checkIfOnlyAllMinesAreUnmarked();
    }

    private boolean checkIfAllMinesAndOnlyAreMarked() {
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                if ((field[row][col] == MINE && displayField[row][col] != MARKED) ||
                        (field[row][col] != MINE && displayField[row][col] == MARKED)) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean checkIfOnlyAllMinesAreUnmarked() {
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                if (field[row][col] != MINE && displayField[row][col] == UNKNOWN) {
                    return false;
                }
            }
        }
        return true;
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.print("How many mines do you want on the field? ");
        int minesNo = sc.nextInt();
        Minesweeper game = new Minesweeper(minesNo);
        game.play();
    }

}
