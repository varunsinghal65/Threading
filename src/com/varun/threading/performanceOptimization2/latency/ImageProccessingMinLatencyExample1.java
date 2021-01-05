package com.varun.threading.performanceOptimization2.latency;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * Aim is to demonstrate by an example how multithreading can be employed for achieving minimum latency
 *
 * Minimum latency also means, finishing the task in minimum possible time.
 */
public class ImageProccessingMinLatencyExample1 {


    public static void main(String[] args) throws IOException, InterruptedException {
        BufferedImage originalImage = ImageIO.read(new File("./resources/many-flowers.jpg"));
        BufferedImage resultImage = new BufferedImage(
                originalImage.getWidth(),
                originalImage.getHeight(),
                BufferedImage.TYPE_INT_RGB);

        //slowest - highest latency -  overhead of creating a new thread.
        Instant start = Instant.now();
        recolorImageUsingThreads(originalImage, resultImage, 1);
        Instant end = Instant.now();
        System.out.println("Single separate thread, elapsed time in ms: " + Duration.between(start, end).toMillis());

        // medium - medium latency (No separate thread created)
        start = Instant.now();
        recolorImageSingleThread(originalImage, resultImage);
        end = Instant.now();
        System.out.println("Main Thread, elapsed time in ms: " + Duration.between(start, end).toMillis());

        // fastest - lowest latency - OPTIMUM threads
        // (optimum : number of threads should be equal to total cores,
        // my machine has 4 physical cores, but because of hyper-threading i can run 8 threads in parallel(very close to 100% parallel - 8 virtual cores)
        // so, for me 8 is the optimum threads and not 4)
        start = Instant.now();
        recolorImageUsingThreads(originalImage, resultImage, 8);
        end = Instant.now();
        System.out.println("Multiple Optimal threads, elapsed time in ms: " + Duration.between(start, end).toMillis());

        File file = new File("./out/many-flowers.jpg");
        ImageIO.write(resultImage, "jpg", file);



    }

    public static void recolorImageUsingThreads(BufferedImage originalImg, BufferedImage resultImage, int numberOfThreads) throws InterruptedException {
        int heightChunk = originalImg.getHeight() / numberOfThreads;
        List<Thread> workerThreads = new ArrayList<>();

        for (int i = 0; i < numberOfThreads; i++) {
            int topCorner = i * heightChunk;
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    recolorImage(originalImg, resultImage, 0, topCorner, originalImg.getWidth(), heightChunk);
                }
            });
            workerThreads.add(thread);
        }

        for (Thread thread: workerThreads) {
            thread.start();
        }

        for (Thread thread: workerThreads) {
            thread.join();
        }

    }

    public static void recolorImageSingleThread(BufferedImage originalImg, BufferedImage resultImage) {
        recolorImage(originalImg, resultImage, 0, 0, originalImg.getWidth(), originalImg.getHeight());
    }


    /**
     * This method will recolor image horizontally
     * <p>
     * from leftCorner to leftCorner + width
     * <p>
     * vertically
     * <p>
     * from topCorner to topCorner + height
     * <p>
     * Imagine the image pixels to be x and y intersection points in a cartesian plane.
     *
     * @param originalImg
     * @param resultImage
     * @param leftCorner
     * @param topCorner
     * @param width
     * @param height
     */
    public static void recolorImage(BufferedImage originalImg,
                                    BufferedImage resultImage,
                                    int leftCorner,
                                    int topCorner,
                                    int width,
                                    int height) {
        for (int x = leftCorner; x < leftCorner + width && x < originalImg.getWidth(); x++) {
            for (int y = topCorner; y < topCorner + height && y < originalImg.getHeight(); y++) {
                recolorPixel(originalImg, resultImage, x, y);
            }
        }
    }

    /**
     * Aim: Detect if pixel at x, y is shade of grey and
     * recolor it to purple and then set the pixel at x,y in the new image
     *
     * @param originalImg
     * @param resultImage
     * @param x
     * @param y
     */
    public static void recolorPixel(BufferedImage originalImg, BufferedImage resultImage, int x, int y) {
        int originalRgb = originalImg.getRGB(x, y);

        int red = getRed(originalRgb);
        int green = getGreen(originalRgb);
        int blue = getBlue(originalRgb);

        int newRed;
        int newGreen;
        int newBlue;

        if (isShadeOfGrey(red, green, blue)) {
            /**
             * A purple is obtained by high shades of red and blue and low shade of green
             */
            newRed = Math.min(255, red + 20);
            newGreen = Math.max(0, green - 80);
            newBlue = Math.max(0, blue - 10);
        } else {
            newRed = red;
            newGreen = green;
            newBlue = blue;
        }

        int newRgb = createRgbFromColors(newRed, newGreen, newBlue);
        //recolor the pixel in the new image
        resultImage.getRaster().setDataElements(x, y, resultImage.getColorModel().getDataElements(newRgb, null));
    }

    public static int createRgbFromColors(int red, int green, int blue) {
        int rgb = 0; // 00000000 00000000 00000000 00000000
        /**
         * this is equivalent to rgb = rgb | blue
         */
        rgb |= blue;
        /**
         * rgb = rgb | (green << 8)
         */
        rgb |= green << 8;
        rgb |= red << 16;
        /**
         * For alpha component.
         */
        rgb |= 0xFF000000;
        return rgb;
    }

    /**
     * A rgb combination will be a grey, if the different in their int values is quite less.
     * OR, all three values are close to each other.
     *
     * @param red
     * @param green
     * @param blue
     */
    public static boolean isShadeOfGrey(int red, int green, int blue) {
        return Math.abs(red - green) < 30 && Math.abs(red - blue) < 30 && Math.abs(green - blue) < 30;
    }

    public static int getRed(int rgb) {
        return (rgb & 0x00FF0000) >> 16;
    }

    /*
     * After doing a bit wise AND between rgb binary and solid green binary, the output will look like this
     *
     * 00000000
     * 00000000
     * XXXXXXXX --> green component
     * 00000000
     *
     * To get the green component's INT rep, we need to shift by 8 bits in right so to obtain
     *
     * 00000000
     * 00000000
     * 00000000
     * XXXXXXXX --> green component
     *
     * @param rgb
     * @return
     */
    public static int getGreen(int rgb) {
        return (rgb & 0x0000FF00) >> 8;
    }

    /*
     * A color in a computer is a 32 bit binary number.
     * 32 bit binary consists of 4 bytes.
     * These 4 bytes are 4 components that together for a color
     * 1st 8bits - 1st byte - Alpha components - controls transparency
     * 2nd 8 bits - 2nd byte - Red component
     * 3rd 8 bits - 3rd byte - Green component
     * 4th 8 bits - 4th byte - Blue component
     * All colors are formed with these components.
     *
     * To make binary representation easier, we have hexadecimal, that is of 8 digits, representing 4 bytes OR a 32 but binary
     *
     * AIM :
     * extract blue component from a rgb value.
     *
     * sample input : 16724991
     * binary : 00000000 11111111 00110011 11111111
     * argb : 0(00000000) 255(11111111) 51(00110011) 255(11111111)
     * hex : 00FF33FF
     * we need int representation of blue : 255
     *
     * LOGIC :
     * 1. Convert input rgb to binary
     * 2. Perform an AND bit wise op with a binary of BLUE color (00000000 00000000 00000000 11111111).
     * 3. Output will have only 4th set of 8 bits set.
     * 4. Convert output binary to int
     *
     * @param rgb - integer, which represents a RGB value
     * @return
     */
    public static int getBlue(int rgb) {
        return rgb & 0x000000FF;
    }
}
