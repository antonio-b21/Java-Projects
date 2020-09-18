package com.company;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Scanner;


public class Main {

    public static void main(String[] args) {

        String mode = "enc";
        int modeArg = List.of(args).indexOf("-mode");
        if (modeArg != -1) {
            mode = args[modeArg + 1];
        }
        String alg = "shift";
        int algArg = List.of(args).indexOf("-alg");
        if (algArg != -1) {
            alg = args[algArg + 1];
        }
        int key = 0;
        int keyArg = List.of(args).indexOf("-key");
        if (keyArg != -1) {
            key = Integer.parseInt(args[keyArg + 1]);
        }
        String message = "";
        int dataArg = List.of(args).indexOf("-data");
        int inArg = List.of(args).indexOf("-in");
        if (dataArg != -1) {
            message = args[dataArg + 1];
        } else if (inArg != -1) {
            File inFile = new File(args[inArg + 1]);
            try (Scanner sc = new Scanner(inFile)) {
                message = sc.nextLine();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        String out = null;
        int outArg = List.of(args).indexOf("-out");
        if (outArg != -1) {
            out = args[outArg + 1];
        }

        EncryptDecrypt encryptDecrypt = EncryptDecrypt.choose(alg, mode);
        String resultMessage = encryptDecrypt.work(message, key);

        if (out == null) {
            System.out.println(resultMessage);
        } else {
            File file = new File(out);
            try (PrintWriter printer = new PrintWriter(file)) {
                printer.write(resultMessage);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

    }

}

interface EncryptDecrypt {
    String work(String message, int key);

    static EncryptDecrypt choose(String alg, String mode) {
        switch (alg) {
            case "shift":
                switch (mode) {
                    case "enc":
                        return new ShiftEncrypt();
                    case "dec":
                        return new ShiftDecrypt();
                    default:
                        break;
                }
                break;
            case "unicode":
                switch (mode) {
                    case "enc":
                        return new UnicodeEncrypt();
                    case "dec":
                        return new UnicodeDecrypt();
                    default:
                        break;
                }
                break;
            default:
                break;
        }
        return null;
    }
}

abstract class ShiftEncryptDecrypt implements EncryptDecrypt {

    @Override
    public String work(String message, int key) {
        return this.encryptDecrypt(message, this.calculateShift(key));
    }

    protected abstract int calculateShift(int key);

    private String encryptDecrypt(String message, int shift) {
        StringBuilder resultMessage = new StringBuilder();
        for (char letter: message.toCharArray()) {
            if (letter >= 'A' && letter <= 'Z') {
                letter -= 'A';
                letter += shift;
                letter %= 26;
                letter += 'A';
            } else if (letter >= 'a' && letter <= 'z') {
                letter -= 'a';
                letter += shift;
                letter %= 26;
                letter += 'a';
            }
            resultMessage.append(letter);
        }
        return resultMessage.toString();
    }
}

class ShiftEncrypt extends ShiftEncryptDecrypt {

    @Override
    protected int calculateShift(int key) {
        return key;
    }
}

class ShiftDecrypt extends ShiftEncryptDecrypt {

    @Override
    protected int calculateShift(int key) {
        return 26 - key;
    }
}

abstract class UnicodeEncryptDecrypt implements EncryptDecrypt {

    @Override
    public String work(String message, int key) {
        return this.encryptDecrypt(message, this.calculateShift(key));
    }

    protected abstract int calculateShift(int key);

    private String encryptDecrypt(String message, int shift) {
        StringBuilder encryptedMessage = new StringBuilder();
        for (char letter: message.toCharArray()) {
            letter += shift;
            letter %= Character.MAX_VALUE;
            encryptedMessage.append(letter);
        }
        return encryptedMessage.toString();
    }
}

class UnicodeEncrypt extends UnicodeEncryptDecrypt {

    @Override
    protected int calculateShift(int key) {
        return key;
    }
}

class UnicodeDecrypt extends UnicodeEncryptDecrypt {

    @Override
    protected int calculateShift(int key) {
        return -1 * key;
    }
}