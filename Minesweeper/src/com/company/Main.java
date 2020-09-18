package com.company;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.print("How many mines do you want on the field? ");
        int minesNo = sc.nextInt();
        Minesweeper game = new Minesweeper(minesNo);
        game.play();
    }
}