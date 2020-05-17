package com.varun.threading.InterThreadComm;

import java.io.*;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;
import java.util.StringJoiner;

/**
 * AIM : To show a problem assosciated with producer consumer scenario
 *
 * HIGH LEVEL PROBLEM : Producer is fast as compared to consumer,
 * thus number of items in a queue can grow to a very large number,
 * increasing the memory consumed by the queue, and thus possibly crashing the application.
 *
 * LOW LEVEL PROBLEM :
 * Producer thread has to read matrices from matrices file.
 * Once producer has read 2 matrices, it has to add them in the queue.
 * Consumer has to pick up the matrix pair from queue, perform multiplication on the pair.
 * store the result in matrices_results.txt.
 *
 * SOLUTION : Protect consumer from back preassure created by producer.
 * How ? Fix the size of the queue, and suspend the producer thread, if tries to add an item to a an already full queue.
 */
public class MatricesMultiplicationPipelinePCBackPreassureProblem {

    private static final int N = 10;
    private static final String INPUT_FILE = "./resources/matrices";
    private static final String OUTPUT_FILE = "./out/matrices_results.txt";

    public static void main(String[] args) throws IOException {
        ThreadSafeMatricesQueue queue = new ThreadSafeMatricesQueue();
        File inputFile  =new File(INPUT_FILE);
        File outputFile = new File(OUTPUT_FILE);
        MatricesReaderProducerThread producer = new MatricesReaderProducerThread(new FileReader(inputFile), queue);
        MatricesMultiplierConsumer consumer = new MatricesMultiplierConsumer(new FileWriter(outputFile), queue);
        producer.start();
        consumer.start();
    }

    private static class MatricesReaderProducerThread extends Thread {
        private ThreadSafeMatricesQueue queue = null;
        private Scanner scanner = null;

        public MatricesReaderProducerThread(FileReader fileR, ThreadSafeMatricesQueue queue) {
            this.scanner = new Scanner(fileR);
            this.queue = queue;
        }

        @Override
        public void run() {
            while (true) {
                MatrixPair mPair = new MatrixPair();
                mPair.m1 = readMatrix();
                mPair.m2 = readMatrix();
                if (mPair.m1 == null || mPair.m2 == null) {
                    System.out.println("There are no matrices left to be read, terminating producer reader thread");
                    System.out.println("Sending terminate signal to queue");
                    queue.terminate();
                    break;
                }
                queue.add(mPair);
            }
        }

        private float[][] readMatrix() {
            float[][] result = new float[N][N];
            for (int row = 0; row < N; row++) {
                if (scanner.hasNext()) {
                    String[] rowData = scanner.nextLine().split(", ");
                    for (int col = 0; col < N; col++) {
                        result[row][col] = Float.valueOf(rowData[col]);
                    }
                } else {
                    return null;
                }
            }
            scanner.nextLine();
            return result;
        }
    }

    private static class MatricesMultiplierConsumer extends Thread {
        private FileWriter fileW = null;
        private ThreadSafeMatricesQueue queue = null;

        public MatricesMultiplierConsumer(FileWriter fileWriter, ThreadSafeMatricesQueue queue) {
            this.fileW = fileWriter;
            this.queue = queue;
        }

        @Override
        public void run() {
            while (true) {
                MatrixPair matrixPair = null;
                try {
                    matrixPair = queue.remove();
                    if (matrixPair == null) {
                        System.out.println("No more matrices in queue, terminating the consumer thread");
                        break;
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                float[][] resultMatrixPair = multiplyMatrices(matrixPair.m1, matrixPair.m2);
                try {
                    saveMatrixToFile(resultMatrixPair);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            try {
                fileW.flush();
                fileW.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private float[][] multiplyMatrices(float[][] m1, float[][] m2) {
            float[][] result = new float[N][N];
            for (int row = 0; row < N; row++) {
                for (int col = 0; col < N; col++) {
                    for (int k = 0; k < N; k++) {
                        result[row][col] = result[row][col] + (m1[row][k] * m2[k][col]);
                    }
                }
            }
            return result;
        }

        private void saveMatrixToFile(float[][] matrix) throws IOException {
            for (int r = 0; r < N; r++) {
                StringJoiner joiner = new StringJoiner(", ");
                for (int c = 0; c < N; c++) {
                    joiner.add(String.format("%.2f", matrix[r][c]));
                }
                fileW.write(joiner.toString());
                fileW.write('\n');
            }
            fileW.write('\n');
        }
    }

    private static class MatrixPair {
        public float[][] m1;
        public float[][] m2;
    }

    private static class ThreadSafeMatricesQueue {
        private Queue<MatrixPair> queue = new LinkedList<>();
        private boolean isTerminated = false;

        public synchronized void add(MatrixPair matrixPair) {
            if (!isTerminated && matrixPair != null) {
                queue.add(matrixPair);
                notify();
            }
        }

        public synchronized MatrixPair remove() throws InterruptedException {
            MatrixPair mPair = null;
            if (queue.isEmpty() && !isTerminated) {
                wait();
            }
            if (queue.isEmpty() && isTerminated) {
                return null;
            }
            System.out.println("Queue size before consumption: " + queue.size());
            return queue.remove();
        }

        public synchronized void terminate() {
            isTerminated = true;
            notifyAll();
        }
    }

}
