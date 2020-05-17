package com.varun.threading.InterThreadComm;

import java.io.*;
import java.time.Duration;
import java.time.Instant;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;
import java.util.StringJoiner;

/**
 * For problem refer {@link: MatricesMultiplicationPipelinePCBackPreassureProblem}
 *
 * SOLUTION:
 * Maintain a queue between producer and consumer, producer adds in queue and consumer removes from queue.
 * Use object as condition variables to coordinate the thread comm between producer and consumer, and thus to achieve the following :
 * 1. Producer should not produce, if queue is full. After producing an item, notify the consumer.
 * 2. Consumer should not consume, if queue is empty. After consuming an item, inform producer to produce, since 1 slot is free in the queue.
 * 3. Producer should terminate when, there are no more inputs and send a terminate signal to queue.
 * 4. Consumer should terminate when, queue is empty and terminate signal has been issues by producer.
 * We used object as condition variables.
 */
public class MatricesMultiplicationPipelinePCBackPreassureSolution1 {

    private static final int N = 10;
    private static int QUEUE_CAPACITY_TO_AVOID_PREASSURE_ON_CONSUMER = 5;
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
            Instant start = Instant.now();
            while (true) {
                MatrixPair mPair = new MatrixPair();
                mPair.m1 = readMatrix();
                mPair.m2 = readMatrix();
                if (mPair.m1 == null || mPair.m2 == null) {
                    System.out.println("There are no matrices left to be read, terminating producer reader thread");
                    System.out.println("Sending terminate signal to queue");
                    queue.terminate();
                    Instant end = Instant.now();
                    System.out.println("Producer thread took: " + Duration.between(start, end).toMillis());
                    break;
                }
                try {
                    queue.add(mPair);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
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
            Instant start = Instant.now();
            while (true) {
                MatrixPair matrixPair = null;
                try {
                    matrixPair = queue.remove();
                    if (matrixPair == null) {
                        System.out.println("No more matrices in queue, terminating the consumer thread");
                        Instant end = Instant.now();
                        System.out.println("Consumer thread took: " + Duration.between(start, end).toMillis());
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

        public synchronized void add(MatrixPair matrixPair) throws InterruptedException {
            if (queue.size() == QUEUE_CAPACITY_TO_AVOID_PREASSURE_ON_CONSUMER) {
                wait();
            }
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
            mPair = queue.remove();
            if (queue.size() == QUEUE_CAPACITY_TO_AVOID_PREASSURE_ON_CONSUMER - 1) {
                notifyAll();
            }
            return mPair;
        }

        public synchronized void terminate() {
            isTerminated = true;
            notifyAll();
        }
    }

}
