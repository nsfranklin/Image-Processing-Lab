import com.sun.org.apache.bcel.internal.generic.LOOKUPSWITCH;

import java.io.*;
import java.util.Arrays;
import java.util.TreeSet;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import javax.imageio.*;
import javax.swing.*;
import java.util.Random;
import java.util.ArrayList;

public class Demo extends Component implements ActionListener, FocusListener {

    //************************************
    // List of the options(Original, Negative); correspond to the cases:
    //************************************

    String descs[] = {
            "Original",                     //done
            "Rescale",                      //done
            "Shift",                        //done
            "Add",                          //done
            "Subtract",                     //done
            "Divide",                       //done
            "Multiply",                     //done
            "Bitwise NOT",                  //done
            "Bitwise OR",                   //done
            "Bitwise XOR",                  //done
            "Bitwise AND",                  //done
            "Bitplane Slice",               //done
            "Smooth Convolution",           //done
            "Edge Detection Convolution",   //done
            "Point Processing Lookup",      //done
            "Convert To Grey",              //done
            "Histogram Equalisation",       //done
            "Order-Static Filter Mediam", //done
            "Order-Static Filter Min", //done
            "Order-Static Filter Max", //done
            "Order-Static Filter Mid-Point", //done
            "Order-Static Filter Alpha-Trimmed", //done (slightly tentative)
            "Thresholding",
    };

    int opIndex;  //option index for
    int lastOp;
    String paraText = "-1";
    final int UNDOLIMIT = 10;
    int[] ROIStart =  new int[]{0,0};
    int[] ROIEnd = new int[]{0,0};


    private BufferedImage bi, biFiltered, biAlt, concImage;   // the input image saved as bi;//
    int w, h;


    private ArrayList<BufferedImage> previousStates;

    public Demo() {
        try {
            bi = ImageIO.read(new File("image/PeppersRGB.bmp"));
            biAlt = ImageIO.read(new File("image/BaboonRGB.bmp"));

            w = bi.getWidth(null);
            h = bi.getHeight(null);
            if (bi.getType() != BufferedImage.TYPE_INT_RGB) {
                BufferedImage bi2 = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
                Graphics big = bi2.getGraphics();
                big.drawImage(bi, 0, 0, null);

                ROIEnd[0] = bi.getWidth();
                ROIEnd[1] = bi.getHeight();
                biFiltered = bi = bi2;
                previousStates = new ArrayList<BufferedImage>();
                previousStates.add(biFiltered);
            }
        } catch (IOException e) {      // deal with the situation that th image has problem;/
            System.out.println("Image could not be read");

            System.exit(1);
        }
    }
    public Dimension getPreferredSize() {
        return new Dimension(w*2, h);
    }
    String[] getDescriptions() {
        return descs;
    }
    // Return the formats sorted alphabetically and in lower case
    public String[] getFormats() {
        String[] formats = {"bmp","gif","jpeg","jpg","png"};
        TreeSet<String> formatSet = new TreeSet<String>();
        for (String s : formats) {
            formatSet.add(s.toLowerCase());
        }
        return formatSet.toArray(new String[0]);
    }
    void setOpIndex(int i) {
        opIndex = i;
    }
    public void paint(Graphics g) { //  Repaint will call this function so the image will change.
        g.drawImage(bi, 0 ,0, null);
        g.drawImage(biFiltered, bi.getWidth(), 0, null);

    }
    //************************************
    //  Convert the Buffered Image to Array
    //************************************
    private static int[][][] convertToArray(BufferedImage image){
        int width = image.getWidth();
        int height = image.getHeight();

        int[][][] result = new int[width][height][4];

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int p = image.getRGB(x,y);
                int a = (p>>24)&0xff;
                int r = (p>>16)&0xff;
                int g = (p>>8)&0xff;
                int b = p&0xff;

                result[x][y][0]=a;
                result[x][y][1]=r;
                result[x][y][2]=g;
                result[x][y][3]=b;
            }
        }
        return result;
    }
    //************************************
    //  Convert the  Array to BufferedImage
    //************************************
    public BufferedImage convertToBimage(int[][][] TmpArray){

        int width = TmpArray.length;
        int height = TmpArray[0].length;

        BufferedImage tmpimg=new BufferedImage(width,height,BufferedImage.TYPE_INT_RGB);

        for(int y=0; y<height; y++){
            for(int x =0; x<width; x++){
                int a = TmpArray[x][y][0];
                int r = TmpArray[x][y][1];
                int g = TmpArray[x][y][2];
                int b = TmpArray[x][y][3];

                //set RGB value

                int p = (a<<24) | (r<<16) | (g<<8) | b;
                tmpimg.setRGB(x, y, p);

            }
        }
        return tmpimg;
    }
    //************************************
    //  Example:  Image Negative
    //************************************
    public BufferedImage ImageNegative(BufferedImage timg){
        int width = timg.getWidth();
        int height = timg.getHeight();

        int[][][] ImageArray = convertToArray(timg);          //  Convert the image to array

        // Image Negative Operation:
        for(int y=0; y<height; y++){
            for(int x =0; x<width; x++){
                ImageArray[x][y][1] = 255-ImageArray[x][y][1];  //r
                ImageArray[x][y][2] = 255-ImageArray[x][y][2];  //g
                ImageArray[x][y][3] = 255-ImageArray[x][y][3];  //b
            }
        }

        return convertToBimage(ImageArray);  // Convert the array to BufferedImage
    }
    //************************************
    //  Your turn now:  Add more function below
    //************************************
    public BufferedImage ImagePixelRescale(BufferedImage timg, float scaleFactor){ //Lab 2 Exercise 1
        if(scaleFactor < 0 || scaleFactor > 2){
            System.out.println("ScaleFactor Out of Range. Original Image Returned");
            return timg;
        }
        int width = timg.getWidth();
        int height = timg.getHeight();
        float rTemp;
        float gTemp;
        float bTemp;
        int[][][] ImageArray = convertToArray(timg);
        for(int y=0; y<height; y++){
            for(int x =0; x<width; x++){
                rTemp = scaleFactor * ImageArray[x][y][1];
                if(rTemp < 0){
                    ImageArray[x][y][1] = 0;
                }else if(rTemp > 255){
                    ImageArray[x][y][1] = 255;
                }else{
                    ImageArray[x][y][1] = Math.round(rTemp);
                }
                gTemp = scaleFactor * ImageArray[x][y][2];
                if(gTemp < 0){
                    ImageArray[x][y][2] = 0;
                }else if(gTemp > 255){
                    ImageArray[x][y][2] = 255;
                }else{
                    ImageArray[x][y][2] = Math.round(gTemp);
                }
                bTemp = scaleFactor * ImageArray[x][y][3];
                if(bTemp < 0){
                    ImageArray[x][y][3] = 0;
                }else if(bTemp > 255){
                    ImageArray[x][y][3] = 255;
                }else{
                    ImageArray[x][y][3] = Math.round(bTemp);
                }
            }
        }
        return convertToBimage(ImageArray);
    }
    public BufferedImage ImagePixelShifting(BufferedImage timg, int shiftFactor){ //Lab 2 Exercise 2
        int width = timg.getWidth();
        int height = timg.getHeight();
        int rTemp;
        int gTemp;
        int bTemp;
        int[][][] ImageArray = convertToArray(timg);
        for(int y=0; y<height; y++){
            for(int x =0; x<width; x++){
                rTemp = shiftFactor + ImageArray[x][y][1];
                if(rTemp < 0){
                    ImageArray[x][y][1] = 0;
                }else if(rTemp > 255){
                    ImageArray[x][y][1] = 255;
                }else{
                    ImageArray[x][y][1] = Math.round(rTemp);
                }
                gTemp = shiftFactor + ImageArray[x][y][2];
                if(gTemp < 0){
                    ImageArray[x][y][2] = 0;
                }else if(gTemp > 255){
                    ImageArray[x][y][2] = 255;
                }else{
                    ImageArray[x][y][2] = Math.round(gTemp);
                }
                bTemp = shiftFactor + ImageArray[x][y][3];
                if(bTemp < 0){
                    ImageArray[x][y][3] = 0;
                }else if(bTemp > 255){
                    ImageArray[x][y][3] = 255;
                }else{
                    ImageArray[x][y][3] = Math.round(bTemp);
                }
            }
        }
        return convertToBimage(ImageArray);
    }
    public BufferedImage ImagePixelRandShiftAndRescale(BufferedImage timg){ //1 for random shift, 2 for random s //Lab 2 Exercise 3
        Random r = new Random();
        int width = timg.getWidth();
        int height = timg.getHeight();
        int[][][] ImageArray = convertToArray(timg);
        for(int y=0; y<height; y++){
            for(int x =0; x<width; x++){
                ImageArray[x][y][1] = (r.nextInt(1000) + ImageArray[x][y][1]);
                ImageArray[x][y][2] = (r.nextInt(1000) + ImageArray[x][y][2]);
                ImageArray[x][y][3] = (r.nextInt(1000) + ImageArray[x][y][3]);
            }
        }
        int max = 255;
        int min = 255;
        for(int y=0; y<height; y++){
            for(int x =0; x<width; x++){
                if(ImageArray[x][y][1] > max){
                    max = ImageArray[x][y][1];
                }else if(ImageArray[x][y][1] < min){
                    min = ImageArray[x][y][1];
                }
                if(ImageArray[x][y][2] > max){
                    max = ImageArray[x][y][2];
                }else if(ImageArray[x][y][2] < min){
                    min = ImageArray[x][y][2];
                }
                if(ImageArray[x][y][3] > max){
                    max = ImageArray[x][y][3];
                }else if(ImageArray[x][y][3] < min){
                    min = ImageArray[x][y][3];
                }
            }
        }
        for(int y=0; y<height; y++){
            for(int x =0; x<width; x++){
                ImageArray[x][y][1] = (255)*(ImageArray[x][y][1] - min)/(max - min);
                ImageArray[x][y][2] = (255)*(ImageArray[x][y][2] - min)/(max - min);
                ImageArray[x][y][3] = (255)*(ImageArray[x][y][3] - min)/(max - min);
            }
        }
        return convertToBimage(ImageArray);
    }
    public BufferedImage ArithmeticOperations(BufferedImage timg, BufferedImage timg2, int operator){ //Lab 3 Exercise 1
        int width = timg.getWidth();
        int height = timg.getHeight();
        int[][][] image1 = convertToArray(timg);
        int[][][] image2 = convertToArray(timg2);
        int[][][] temp = new int[width][height][4];

        switch(operator){
            case 0:
                return timg;
            case 1:
                for(int y=0; y<height; y++){
                    for(int x =0; x<width; x++){
                        temp[x][y][1] = (image1[x][y][1] + image2[x][y][1])/2;  //r
                        temp[x][y][2] = (image1[x][y][2] + image2[x][y][2])/2;  //g
                        temp[x][y][3] = (image1[x][y][3] + image2[x][y][3])/2;  //b
                    }
                }
                return convertToBimage(temp);
            case 2:
                for(int y=0; y<height; y++) {
                    for (int x = 0; x < width; x++) {
                        temp[x][y][1] = (image1[x][y][1] - image2[x][y][1]);  //r
                        temp[x][y][2] = (image1[x][y][2] - image2[x][y][2]);  //g
                        temp[x][y][3] = (image1[x][y][3] - image2[x][y][3]);  //b
                    }
                }
                int max = 255;
                int min = 255;
                for (int j = 0; j < height; j++) {
                    for (int k = 0; k < width; k++) {
                        if (temp[k][j][1] > max) {
                            max = temp[k][j][1];
                        } else if (temp[k][j][1] < min) {
                            min = temp[k][j][1];
                        }
                        if (temp[k][j][2] > max) {
                            max = temp[k][j][2];
                        } else if (temp[k][j][2] < min) {
                            min = temp[k][j][2];
                        }
                        if (temp[k][j][3] > max) {
                            max = temp[k][j][3];
                        } else if (temp[k][j][3] < min) {
                            min = temp[k][j][3];
                        }
                    }
                }
                for (int y = 0; y < height; y++) {
                    for (int x = 0; x < width; x++) {
                       temp[x][y][1] = (255) * (temp[x][y][1] - min) / (max - min);
                       temp[x][y][2] = (255) * (temp[x][y][2] - min) / (max - min);
                       temp[x][y][3] = (255) * (temp[x][y][3] - min) / (max - min);
                    }
                }
                return convertToBimage(temp);
            }
            return timg;
    }
    public BufferedImage ArithmeticOperationsAdd(BufferedImage img1, BufferedImage img2){
        int width = img1.getWidth();
        int height = img1.getHeight();
        int[][][] image1 = convertToArray(img1);
        int[][][] image2 = convertToArray(img2);
        int[][][] temp = new int[width][height][4];
        for(int y=0; y<height; y++){
            for(int x =0; x<width; x++){
                temp[x][y][1] = (image1[x][y][1] + image2[x][y][1])/2;  //r
                temp[x][y][2] = (image1[x][y][2] + image2[x][y][2])/2;  //g
                temp[x][y][3] = (image1[x][y][3] + image2[x][y][3])/2;  //b
            }
        }
        return convertToBimage(temp);
    }
    public BufferedImage ArithemeticOperationsSub(BufferedImage img1, BufferedImage img2){
        int width = img1.getWidth();
        int height = img1.getHeight();
        int[][][] image1 = convertToArray(img1);
        int[][][] image2 = convertToArray(img2);
        int[][][] temp = new int[width][height][4];
        for(int y=0; y<height; y++) {
            for (int x = 0; x < width; x++) {
                temp[x][y][1] = (image1[x][y][1] - image2[x][y][1]);  //r
                temp[x][y][2] = (image1[x][y][2] - image2[x][y][2]);  //g
                temp[x][y][3] = (image1[x][y][3] - image2[x][y][3]);  //b
            }
        }
        int max = 255;
        int min = 255;
        for (int j = 0; j < height; j++) {
            for (int k = 0; k < width; k++) {
                if (temp[k][j][1] > max) {
                    max = temp[k][j][1];
                } else if (temp[k][j][1] < min) {
                    min = temp[k][j][1];
                }
                if (temp[k][j][2] > max) {
                    max = temp[k][j][2];
                } else if (temp[k][j][2] < min) {
                    min = temp[k][j][2];
                }
                if (temp[k][j][3] > max) {
                    max = temp[k][j][3];
                } else if (temp[k][j][3] < min) {
                    min = temp[k][j][3];
                }
            }
        }
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                temp[x][y][1] = (255) * (temp[x][y][1] - min) / (max - min);
                temp[x][y][2] = (255) * (temp[x][y][2] - min) / (max - min);
                temp[x][y][3] = (255) * (temp[x][y][3] - min) / (max - min);
            }
        }
        return convertToBimage(temp);
    }
    public BufferedImage ArithmeticOperationsDivide(BufferedImage img1, BufferedImage img2){
        int width = img1.getWidth();
        int height = img1.getHeight();
        int[][][] image1 = convertToArray(img1);
        int[][][] image2 = convertToArray(img2);
        int[][][] temp = new int[width][height][4];
        int r,g,b, r2, g2, b2;
        for(int y=0; y<height; y++) {
            for (int x = 0; x < width; x++) {
                r = image1[x][y][1];
                g = image1[x][y][2];
                b = image1[x][y][3];
                r2 = image2[x][y][1];
                g2 = image2[x][y][2];
                b2 = image2[x][y][3];
                if(r == 0 && r2 == 0) {
                    temp[x][y][1] = 1;  //r
                }else if(r2 == 0){
                    temp [x][y][1] = image1[x][y][1];
                }else{
                    temp [x][y][1] = DivideAndRescale(image1[x][y][1] , image2[x][y][1]);
                }
                if(g == 0 && g2 == 0) {
                    temp[x][y][2] = 1;  //r
                }else if(g2 == 0){
                    temp [x][y][2] = image1[x][y][2];
                }else{
                    temp [x][y][2] = DivideAndRescale(image1[x][y][2] , image2[x][y][2]);
                }
                if(b == 0 && b2 == 0) {
                    temp[x][y][3] = 1;  //r
                }else if(b2 == 0){
                    temp [x][y][3] = image1[x][y][3];
                }else{
                    temp [x][y][3] = DivideAndRescale(image1[x][y][3] , image2[x][y][3]);
                }
            }
        }
        return convertToBimage(temp);
    }
    public int DivideAndRescale(int a , int b){
        float temp = a / b * 255;
        return Math.round(temp);
    }
    public BufferedImage ArithmeticOperationsMultiply(BufferedImage img1, BufferedImage img2){
        int width = img1.getWidth();
        int height = img1.getHeight();
        int[][][] image1 = convertToArray(img1);
        int[][][] image2 = convertToArray(img2);
        int[][][] temp = new int[width][height][4];
        for(int y=0; y<height; y++) {
            for (int x = 0; x < width; x++) {
                temp[x][y][1] = (image1[x][y][1] * image2[x][y][1]);  //r
                temp[x][y][2] = (image1[x][y][2] * image2[x][y][2]);  //g
                temp[x][y][3] = (image1[x][y][3] * image2[x][y][3]);  //b
            }
        }
        int max = 255;
        int min = 255;
        for (int j = 0; j < height; j++) {
            for (int k = 0; k < width; k++) {
                if (temp[k][j][1] > max) {
                    max = temp[k][j][1];
                } else if (temp[k][j][1] < min) {
                    min = temp[k][j][1];
                }
                if (temp[k][j][2] > max) {
                    max = temp[k][j][2];
                } else if (temp[k][j][2] < min) {
                    min = temp[k][j][2];
                }
                if (temp[k][j][3] > max) {
                    max = temp[k][j][3];
                } else if (temp[k][j][3] < min) {
                    min = temp[k][j][3];
                }
            }
        }
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                temp[x][y][1] = (255) * (temp[x][y][1] - min) / (max - min);
                temp[x][y][2] = (255) * (temp[x][y][2] - min) / (max - min);
                temp[x][y][3] = (255) * (temp[x][y][3] - min) / (max - min);
            }
        }
        return convertToBimage(temp);
    }
    public BufferedImage BitwiseNotTransformation(BufferedImage timg){ //Lab 3 Exercise 2
        int width = timg.getWidth();
        int height = timg.getHeight();
        int[][][] image1 = convertToArray(timg);
        int[][][] image2 = new int[width][height][4];
        int r , g , b;

        for(int y=0; y<height; y++){
            for(int x=0; x<width; x++){
                r = image1[x][y][1]; //r
                g = image1[x][y][2]; //g
                b = image1[x][y][3]; //b
                image2[x][y][1] = (~r)&0xFF; //r
                image2[x][y][2] = (~g)&0xFF; //g
                image2[x][y][3] = (~b)&0xFF; //b
            }
        }
        return convertToBimage(image2);
    }
    public BufferedImage BitwiseORTransformation(BufferedImage timg, BufferedImage timg2){ //Lab 3 Exercise 3
        int width = timg.getWidth();
        int height = timg.getHeight();
        int[][][] image1 = convertToArray(timg);
        int[][][] image2 = convertToArray(timg2);
        int[][][] temp = new int[width][height][4];
        int r,g,b,r2,g2,b2;

        for (int y = 0; y < height; y++) {
             for (int x = 0; x < width; x++) {
                 r = image1[x][y][1]; //r
                 g = image1[x][y][2]; //g
                 b = image1[x][y][3]; //b
                 r2 = image2[x][y][1]; //r
                 g2 = image2[x][y][2]; //g
                 b2 = image2[x][y][3]; //b
                 temp[x][y][1] = r|r2 & 0xFF;;  //r
                 temp[x][y][2] = g|g2 & 0xFF;; //g
                 temp[x][y][3] = b|b2 & 0xFF;;//b
             }
        }
        return convertToBimage(temp);
    }
    public BufferedImage BitwiseXORTransformation(BufferedImage timg, BufferedImage timg2){ //Lab 3 Exercise 3
        int width = timg.getWidth();
        int height = timg.getHeight();
        int[][][] image1 = convertToArray(timg);
        int[][][] image2 = convertToArray(timg2);
        int[][][] temp = new int[width][height][4];
        int r,g,b,r2,g2,b2;

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                r = image1[x][y][1]; //r
                g = image1[x][y][2]; //g
                b = image1[x][y][3]; //b
                r2 = image2[x][y][1]; //r
                g2 = image2[x][y][2]; //g
                b2 = image2[x][y][3]; //b
                temp[x][y][1] = r^r2 & 0xFF;;  //r
                temp[x][y][2] = g^g2 & 0xFF;; //g
                temp[x][y][3] = b^b2 & 0xFF;;//b
            }
        }
        return convertToBimage(temp);
    }
    public BufferedImage BitwiseANDTransformation(BufferedImage timg, BufferedImage timg2){ //Lab 3 Exercise 3
        int width = timg.getWidth();
        int height = timg.getHeight();
        int[][][] image1 = convertToArray(timg);
        int[][][] image2 = convertToArray(timg2);
        int[][][] temp = new int[width][height][4];
        int r,g,b,r2,g2,b2;

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                r = image1[x][y][1]; //r
                g = image1[x][y][2]; //g
                b = image1[x][y][3]; //b
                r2 = image2[x][y][1]; //r
                g2 = image2[x][y][2]; //g
                b2 = image2[x][y][3]; //b
                temp[x][y][1] = r&r2 & 0xFF;  //r
                temp[x][y][2] = g&g2 & 0xFF; //g
                temp[x][y][3] = b&b2 & 0xFF;//b
            }
        }
        return convertToBimage(temp);
    }
    public BufferedImage BitPlaneSlice(BufferedImage timg, int plane){
        int[][][] image1 = convertToArray(timg);
        int height = timg.getHeight();
        int width = timg.getWidth();
        int[][][] image2 = new int[width][height][4];
        int r,g,b;

        for(int y=0; y<height; y++){
            for(int x=0; x<width; x++){
                r = image1[x][y][1]; //r
                g = image1[x][y][2]; //g
                b = image1[x][y][3]; //b
                image2[x][y][1] = (r>>plane)&1; //r
                image2[x][y][2] = (g>>plane)&1; //g
                image2[x][y][3] = (b>>plane)&1; //b
            }
        }
        for(int y=0; y<height; y++){
            for(int x=0; x<width; x++){
                if(image2[x][y][1] == 1){
                    image2[x][y][1] = 255;
                }
                if(image2[x][y][2] == 1){
                    image2[x][y][2] = 255;
                }
                if(image2[x][y][3] == 1){
                    image2[x][y][3] = 255;
                }
            }
        }
        return convertToBimage(image2);
    }
    public BufferedImage SmoothImageConvolution(BufferedImage timg){ //Lab 4 Exercise 1
        int[][] gaussianMatrix = {{1,2,1},{2,4,2},{1,2,1}}; //gaussian blur to smooth the image.
        BufferedImage result = timg;
        float matrixConstant = 0.0625f;
        return generalCorrelation(result, gaussianMatrix, matrixConstant);
    }
    public BufferedImage EdgeDetectionConvolution(BufferedImage timg){ //Lab 4 Exercise 2
        int[][] EdgeDetectionMatrix = {{-1,-1,-1},{-1,8,-1},{-1,-1,-1}}; //{{0,1,0},{1,-4,1},{0,1,0}};
        BufferedImage result = timg;
        return generalCorrelation(result, EdgeDetectionMatrix);
    }
    public boolean Is3x3FilterSymetricAroundCentre(int[][] filter){

        if(filter[0][0] == filter[2][2] && filter[0][1] == filter[2][1] && filter[0][2] == filter[2][0] && filter[1][0] == filter[1][2]){
            return true;
        }
        return false;
    }
    public void RotateThe3x3Matrix(int[][] matrix){
        int temp = matrix[0][0];
        matrix[0][0] = matrix[2][2];
        matrix[2][2] = temp;

        temp = matrix[0][1];
        matrix[0][1] = matrix[2][1];
        matrix[2][1] = temp;

        temp = matrix[0][2];
        matrix[0][2] = matrix[2][0];
        matrix[2][0] = temp;

        temp = matrix[1][0];
        matrix[1][0] = matrix[1][2];
        matrix[1][2] = temp;
    }
    public int[][][] addImageArrayPadding(int[][][] image){  // extending the board to provide padding.
        int xLength = image[0].length +1;
        int yLength = image[0][0].length + 1;
        /*
        int[][][] temp = new int[image[0].length][image[0][0].length][4];
        temp[0][0][1] = image[0][0][1];
        temp[0][0][2] = image[0][0][2];
        temp[0][0][3] = image[0][0][3];
        temp[xLength][0][1] = image[xLength-2][0][1];
        temp[xLength][0][2] = image[xLength-2][0][2];
        temp[xLength][0][3] = image[xLength-2][0][3];
        temp[0][yLength][1] = image[0][0][1];
        temp[0][yLength][2] = image[0][0][2];
        temp[0][yLength][3] = image[0][0][3];
        temp[xLength][yLength][1] = image[xLength-2][temp[0][0].length-1][1];
        temp[xLength][yLength][2] = image[xLength-2][temp[0][0].length-1][2];
        temp[xLength][yLength][3] = image[xLength-2][temp[0][0].length-1][3];

        for(int i = 0 ; i < image[0].length-1 ; i++){
            temp[i][0][1] = image[i][0][1];
            temp[i][0][2] = image[i][0][2];
            temp[i][0][3] = image[i][0][3];
            temp[temp[i].length - 1][i][1] = image[image[i].length-1][i][1];
            temp[temp[i].length - 1][i][2] = image[image[i].length-1][i][2];
            temp[temp[i].length - 1][i][3] = image[image[i].length-1][i][3];
            System.out.println(i);
        }
        */
        return image;
    }
    public int[][][] removeImageArrayPadding(int[][][] image){
        return image;
    }
    public BufferedImage generalCorrelation(BufferedImage timg, int[][] filterMatrix, float matrixConstant){  //Lab 6 Exercise 1
        int[][][] image1 = convertToArray(timg);
        int height = timg.getHeight();
        int width = timg.getWidth();
        int[][][] image2 = image1;
        int[][] Mask = filterMatrix;
        if(!Is3x3FilterSymetricAroundCentre(Mask)){
            RotateThe3x3Matrix(Mask);
        }
        int r,g,b;
        for(int y=1; y<height-1; y++){
            for(int x=1; x<width-1; x++){
                r = 0; g = 0; b = 0;
                for(int s=-1; s<=1; s++){
                    for(int t=-1; t<=1; t++){
                        r = r + Mask[1+s][1+t]*image1[x+s][y+t][1]; //r
                        g = g + Mask[1+s][1+t]*image1[x+s][y+t][2]; //g
                        b = b + Mask[1+s][1+t]*image1[x+s][y+t][3]; //b
                    }
                }
                r = Math.round(r*matrixConstant);
                g = Math.round(g*matrixConstant);
                b = Math.round(b*matrixConstant);
                image2[x][y][1] = r < 0? 0 : r; //r
                image2[x][y][2] = g < 0? 0 : g; //g
                image2[x][y][3] = b < 0? 0 : b; //b

            }
        }
        return convertToBimage(image2);
    }
    public BufferedImage generalCorrelation(BufferedImage timg, int[][] filterMatrix){  //Lab 6 Exercise 1
        int[][][] image1 = convertToArray(timg);
        int height = timg.getHeight();
        int width = timg.getWidth();
        int[][][] image2 = new int[width][height][4];
        int[][] Mask = filterMatrix;
        if(!Is3x3FilterSymetricAroundCentre(Mask)){
            RotateThe3x3Matrix(Mask);
        }
        int r,g,b;
        for(int y=1; y<height-1; y++){
            for(int x=1; x<width-1; x++){
                r = 0; g = 0; b = 0;
                for(int s=-1; s<=1; s++){
                    for(int t=-1; t<=1; t++){
                        r = r + Mask[1+s][1+t]*image1[x+s][y+t][1]; //r
                        g = g + Mask[1+s][1+t]*image1[x+s][y+t][2]; //g
                        b = b + Mask[1+s][1+t]*image1[x+s][y+t][3]; //b
                    }
                }
                image2[x][y][1] = r < 0? 0 : r; //r // some values went negative when applying a edge filter resulting in extreme salt and pepper
                image2[x][y][2] = g < 0? 0 : g; //g
                image2[x][y][3] = b < 0? 0 : b; //b
            }
        }
        return convertToBimage(image2);
    }
    public BufferedImage PointProccessingLookupTable(BufferedImage img){
        int[] LookupTable = new int[256];
        for(int i = 0 ; i < LookupTable.length ; i++){ //fills the look up table with a test stepped look up table
            if(i > 30){
                LookupTable[i] = 10;
            } else if(i > 60){
                LookupTable[i] = 40;
            } else if(i > 90){
                LookupTable[i] = 70;
            } else if(i > 120){
                LookupTable[i] = 100;
            } else if(i > 180){
                LookupTable[i] = 150;
            } else if(i > 240){
                LookupTable[i] = 210;
            }else{
                LookupTable[i] = 255;
            }
        }
        int[][][] image = convertToArray(img);
        int height = img.getHeight();
        int width = img.getWidth();
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                image[x][y][1] =  LookupTable[image[x][y][1]];
                image[x][y][2] =  LookupTable[image[x][y][2]];
                image[x][y][3] =  LookupTable[image[x][y][3]];
            }
        }
        return convertToBimage(image);
    }
    public void FindHistogram(BufferedImage timg, int[] HistgramR, int[] HistgramG, int[] HistgramB){ //Lab 5 Exercise 1
        int[][][] image = convertToArray(timg);
        int height = timg.getHeight();
        int width = timg.getWidth();
        int r,g,b;
        for(int k=0; k<=255; k++) {
            HistgramR[k] = 0;
            HistgramG[k] = 0;
            HistgramB[k] = 0;
        }
        for(int y=0; y<height; y++){
            for(int x=0; x<width; x++){
                r = image[x][y][1];
                g = image[x][y][2];
                b = image[x][y][3];
                HistgramR[r]++;
                HistgramG[g]++;
                HistgramB[b]++;
            }
        }
    }
    public int[] FindGreyLevelHistogram(BufferedImage timg){
        int[][][] image = convertToArray(timg);
        int height = timg.getHeight();
        int width = timg.getWidth();
        int r;
        int[] HistgramGreyLevel = new int[256];
        for(int k=0; k<=255; k++) {
            HistgramGreyLevel[k] = 0;
        }
        for(int y=0; y<height; y++){
            for(int x=0; x<width; x++){
                r = image[x][y][1];
                HistgramGreyLevel[r]++;
            }
        }
        return HistgramGreyLevel;
    }
    public int[][][] convertToGrey(int[][][] imageArray, int width, int height){
        int[][][] temp = new int[width][height][4];;
        int grey;
        for(int y=0; y<height; y++) {
            for (int x = 0; x < width; x++) {
                grey = Math.round(0.299f * imageArray[x][y][1] + 0.587f * imageArray[x][y][2] + 0.114f * imageArray[x][y][3]);
                temp[x][y][1] = grey;
                temp[x][y][2] = grey;  //g
                temp[x][y][3] = grey;  //b
            }
        }
        return temp;
    }
    public BufferedImage greyScaleTransformation(BufferedImage timg){
        int width = timg.getWidth();
        int height = timg.getHeight();
        int[][][] image = convertToArray(timg);
        image = convertToGrey(image, width, height);
        return convertToBimage(image);
    }
    public int[][][] PointProccessingLookupTable(int[][][] image, int[] lookUpTable, int width, int height){
        int[][][] temp = new int[width][height][4];;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                temp[x][y][1] =  lookUpTable[image[x][y][1]];
                temp[x][y][2] =  lookUpTable[image[x][y][2]];
                temp[x][y][3] =  lookUpTable[image[x][y][3]];
            }
        }
        return temp;
    }
    public BufferedImage EqualiseHistogram(BufferedImage timg){ //Lab 5 Exercise 3
        int width = timg.getWidth();
        int height = timg.getHeight();
        int[][][] image = convertToArray(timg);
        int[][][] greyImage = convertToGrey(image, width, height);
        int[] GreyHistogram = new int[256];
        int[] lookUpTable = new int[256];
        float accumulator = 0;
        GreyHistogram = FindGreyLevelHistogram(timg);
        for(int i = 0 ; i < 256 ; i++){
            accumulator = accumulator + (float)(GreyHistogram[i])/(width*height);
            lookUpTable[i] = Math.round(accumulator*255);
        }
        greyImage = PointProccessingLookupTable(greyImage, lookUpTable, width, height);
        return convertToBimage(greyImage);
    }
    public BufferedImage OSFMedian(BufferedImage timg){
        int width = timg.getWidth();
        int height = timg.getHeight();
        int [][][] image = convertToArray(timg);
        int [][][] image2 = new int[width][height][4];
        int[] rWindow = new int[9];
        int[] gWindow = new int[9];
        int[] bWindow = new int[9];
        int k = 0;
        for(int y=1; y<height-1; y++){
            for(int x=1; x<width-1; x++){
                k = 0;
                for(int s=-1; s<=1; s++){
                    for(int t=-1; t<=1; t++){
                        rWindow[k] = image[x+s][y+t][1]; //r
                        gWindow[k] = image[x+s][y+t][2]; //g
                        bWindow[k] = image[x+s][y+t][3]; //b
                        k++;
                    }
                }
                Arrays.sort(rWindow);
                Arrays.sort(gWindow);
                Arrays.sort(bWindow);
                image2[x][y][1] = rWindow[4]; //r
                image2[x][y][2] = gWindow[4]; //g
                image2[x][y][3] = bWindow[4]; //b
            }
        }
        return convertToBimage(image2);
    }
    public BufferedImage OSFMin(BufferedImage timg){
        int width = timg.getWidth();
        int height = timg.getHeight();
        int [][][] image = convertToArray(timg);
        int [][][] image2 = new int[width][height][4];
        int[] rWindow = new int[9];
        int[] gWindow = new int[9];
        int[] bWindow = new int[9];
        int k = 0;
        for(int y=1; y<height-1; y++){
            for(int x=1; x<width-1; x++){
                k = 0;
                for(int s=-1; s<=1; s++){
                    for(int t=-1; t<=1; t++){
                        rWindow[k] = image[x+s][y+t][1]; //r
                        gWindow[k] = image[x+s][y+t][2]; //g
                        bWindow[k] = image[x+s][y+t][3]; //b
                        k++;
                    }
                }
                Arrays.sort(rWindow);
                Arrays.sort(gWindow);
                Arrays.sort(bWindow);
                image2[x][y][1] = rWindow[0]; //r
                image2[x][y][2] = gWindow[0]; //g
                image2[x][y][3] = bWindow[0]; //b
            }
        }
        return convertToBimage(image2);
    }
    public BufferedImage OSFMax(BufferedImage timg){
        int width = timg.getWidth();
        int height = timg.getHeight();
        int [][][] image = convertToArray(timg);
        int [][][] image2 = new int[width][height][4];
        int[] rWindow = new int[9];
        int[] gWindow = new int[9];
        int[] bWindow = new int[9];
        int k = 0;
        for(int y=1; y<height-1; y++){
            for(int x=1; x<width-1; x++){
                k = 0;
                for(int s=-1; s<=1; s++){
                    for(int t=-1; t<=1; t++){
                        rWindow[k] = image[x+s][y+t][1]; //r
                        gWindow[k] = image[x+s][y+t][2]; //g
                        bWindow[k] = image[x+s][y+t][3]; //b
                        k++;
                    }
                }
                Arrays.sort(rWindow);
                Arrays.sort(gWindow);
                Arrays.sort(bWindow);
                image2[x][y][1] = rWindow[8]; //r
                image2[x][y][2] = gWindow[8]; //g
                image2[x][y][3] = bWindow[8]; //b
            }
        }
        return convertToBimage(image2);
    }
    public BufferedImage OSFMidPoint(BufferedImage timg){
        int width = timg.getWidth();
        int height = timg.getHeight();
        int [][][] image = convertToArray(timg);
        int [][][] image2 = new int[width][height][4];
        int[] rWindow = new int[9];
        int[] gWindow = new int[9];
        int[] bWindow = new int[9];
        int k = 0;
        for(int y=1; y<height-1; y++){
            for(int x=1; x<width-1; x++){
                k = 0;
                for(int s=-1; s<=1; s++){
                    for(int t=-1; t<=1; t++){
                        rWindow[k] = image[x+s][y+t][1]; //r
                        gWindow[k] = image[x+s][y+t][2]; //g
                        bWindow[k] = image[x+s][y+t][3]; //b
                        k++;
                    }
                }
                Arrays.sort(rWindow);
                Arrays.sort(gWindow);
                Arrays.sort(bWindow);
                image2[x][y][1] = (rWindow[0] + rWindow[8]) / 2; //r
                image2[x][y][2] = (gWindow[0] + rWindow[8]) / 2; //g
                image2[x][y][3] = (bWindow[0] + rWindow[8]) / 2; //b
            }
        }
        return convertToBimage(image2);
    }
    public BufferedImage OSFAlphaTrimmed(BufferedImage timg, int d){
        if(d== 1 || d ==3 || d == 5 || d == 7){
            System.out.println("Parameter needs to be even and between 0 - 8 Inclusive");
            return timg;
        }
        int width = timg.getWidth();
        int height = timg.getHeight();
        int [][][] image = convertToArray(timg);
        int [][][] image2 = new int[width][height][4];
        int[] rWindow = new int[9];
        int[] gWindow = new int[9];
        int[] bWindow = new int[9];
        int k = 0;
        for(int y=1; y<height-1; y++){
            for(int x=1; x<width-1; x++){
                k = 0;
                for(int s=-1; s<=1; s++){
                    for(int t=-1; t<=1; t++){
                        rWindow[k] = image[x+s][y+t][1]; //r
                        gWindow[k] = image[x+s][y+t][2]; //g
                        bWindow[k] = image[x+s][y+t][3]; //b
                        k++;
                    }
                }
                Arrays.sort(rWindow);
                Arrays.sort(gWindow);
                Arrays.sort(bWindow);
                image2[x][y][1] = Math.round((float)(arrayTotalTrimmed(rWindow, d))/(9-d)); //r
                image2[x][y][2] = Math.round((float)(arrayTotalTrimmed(gWindow, d))/(9-d)); //g
                image2[x][y][3] = Math.round((float)(arrayTotalTrimmed(bWindow, d))/(9-d)); //b
            }
        }
        return convertToBimage(image2);
    }
    public int arrayTotalTrimmed(int[] array, int d){
        int a = 0;
        for(int i = d/2 ; i < (9-(d/2)) ; i++){
            a = a + array[i];
        }
        return a;
    }
    public BufferedImage Thresholding(BufferedImage timg){
        return timg;
    }

    //Parsers and Interface Methods
    public void OSFAlphaTrimmedParse(){
        int i;
        boolean b = true;
        try{
            i = Integer.parseInt(paraText);
        }catch(NumberFormatException e){
            b = false;
            i = 0;
        }
        if(b) {
            biFiltered = OSFAlphaTrimmed(bi, i);
        }else{
            biFiltered = bi;
        }
    }

    public void RescalingParse(){
        float f;
        boolean b = true;
        try{
            f = Float.parseFloat(paraText);
        }catch(NumberFormatException e){
            b = false;
            f = 1;
        }
        if(b) {
            biFiltered = ImagePixelRescale(bi, f);
        }else{
            biFiltered = bi;
        }
    }
    public void ImagePixelShiftingParse(){
        int i = 0;
        boolean b = true;
        try{
            i = Integer.parseInt(paraText);
        }catch(NumberFormatException e){
            b = false;
            i = 0;
        }
        if(b) {
            biFiltered = ImagePixelShifting(bi, i);
        }else{
            biFiltered = bi;
        }
    }
    public void BitplaneSlicingParse(){
        int i = -1;
        boolean b = true;
        try{
            i = Integer.parseInt(paraText);
        }catch(NumberFormatException e){
            b = false;
            i = -1;
        }
        if(b && i != -1) {
            System.out.println(i);
            biFiltered = BitPlaneSlice(bi, i);
        }else{
            biFiltered = bi;
        }
    }
    public void filterImage() {
        previousStates.add(biFiltered);
        lastOp = 17;
        switch (opIndex) {
            case 0:  biFiltered = bi; /* original */
                return;
            case 1:  RescalingParse(); //Complete
                return;
            case 2:  ImagePixelShiftingParse(); //Complete
                return;
            case 3:  biFiltered = ArithmeticOperationsAdd(bi,biAlt); //Complete
                return;
            case 4:  biFiltered = ArithemeticOperationsSub(bi,biAlt);
                return;
            case 5:  biFiltered = ArithmeticOperationsDivide(bi,biAlt);
                return;
            case 6:  biFiltered = ArithmeticOperationsMultiply(bi,biAlt);
                return;
            case 7:  biFiltered = BitwiseNotTransformation(bi); //Complete
                return;
            case 8:  biFiltered = BitwiseORTransformation(bi, biAlt); //Complete
                return;
            case 9:  biFiltered = BitwiseXORTransformation(bi, biAlt); //Complete
                return;
            case 10: biFiltered = BitwiseANDTransformation(bi, biAlt); //Complete
                return;
            case 11: BitplaneSlicingParse(); //Complete
                return;
            case 12: biFiltered = SmoothImageConvolution(bi);
                return;
            case 13: biFiltered = EdgeDetectionConvolution(bi);
                return;
            case 14: biFiltered = PointProccessingLookupTable(bi);
                return;
            case 15: biFiltered = greyScaleTransformation(bi);
                return;
            case 16: biFiltered = EqualiseHistogram(bi);
                return;
            case 17: biFiltered = OSFMedian(bi);
                return;
            case 18: biFiltered = OSFMin(bi);
                return;
            case 19: biFiltered = OSFMax(bi);
                return;
            case 20: biFiltered = OSFMidPoint(bi);
                return;
            case 21: OSFAlphaTrimmedParse();
                return;
            case 22: biFiltered = Thresholding(bi);
                return;
            case 23:
                return;
            case 24:
                return;
            case 25: biFiltered = ImagePixelRandShiftAndRescale(bi); //lab filter not need in final product
                return;
            case 26: biFiltered = ImageNegative(bi); //lab filter not need in final product
                return;

        }
    }
    public void actionPerformed(ActionEvent e) {
        Object cbtemp = e.getSource();
        JComboBox cb;
        JTextField tx;
        JButton bt;
        if(cbtemp instanceof JComboBox){
             cb = (JComboBox)cbtemp;
            if (cb.getActionCommand().equals("SetFilter")) {
                setOpIndex(cb.getSelectedIndex());
                repaint();
            } else if (cb.getActionCommand().equals("Formats")) {
                String format = (String)cb.getSelectedItem();
                File saveFile = new File("savedimage."+format);
                JFileChooser chooser = new JFileChooser();
                chooser.setSelectedFile(saveFile);
                int rval = chooser.showSaveDialog(cb);
                if (rval == JFileChooser.APPROVE_OPTION) {
                    saveFile = chooser.getSelectedFile();
                    try {
                        ImageIO.write(biFiltered, format, saveFile);
                    } catch (IOException ex) {
                    }
                }
            }
        }else if(cbtemp instanceof JTextField){
             tx = (JTextField)cbtemp;
             paraText = (String)tx.getText();
        }else
        {
            bt = (JButton) cbtemp;
            if(bt.getActionCommand().equals("undo")) {
                System.out.println(previousStates.size());
                if (previousStates != null && previousStates.size() > 1) {
                    biFiltered = previousStates.get(previousStates.size() - 1);
                    previousStates.remove(previousStates.size()-1);
                    if(previousStates.size() > UNDOLIMIT){ //the system only stores the last x image states in the undo array
                        previousStates.remove(0);
                    }
                    repaint();
                }
            }else if(bt.getActionCommand().equals("apply")){
                filterImage();
                repaint();
            }
        }
    }
    public void focusLost(FocusEvent e){
        JTextField tx = (JTextField)e.getSource();
        paraText = (String)tx.getText();
    }
    public void focusGained(FocusEvent e){ } // looking for focus lost on the text field. Users don't need to press enter to update text field



    public static void main(String s[]) {
        JFrame f = new JFrame("Image Processing Demo");
        f.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {System.exit(0);}
        });
        Demo de = new Demo();

        f.add("Center", de);
        JButton undo = new JButton("Undo");
        JButton apply = new JButton("Apply");
        undo.setActionCommand("undo");
        undo.addActionListener(de);
        apply.setActionCommand("apply");
        apply.addActionListener(de);
        JComboBox choices = new JComboBox(de.getDescriptions());
        choices.setActionCommand("SetFilter");
        choices.addActionListener(de);
        JComboBox formats = new JComboBox(de.getFormats());
        formats.setActionCommand("Formats");
        formats.addActionListener(de);
        JTextField textbox = new JTextField("", 10);
        textbox.setActionCommand("textEntered");
        textbox.addActionListener(de);
        textbox.addFocusListener(de);
        JPanel panel = new JPanel();
        panel.add(undo);
        panel.add(choices);
        panel.add(new JLabel("Save As"));
        panel.add(formats);
        panel.add(new JLabel("Parameter"));
        panel.add(textbox);
        panel.add(apply);
        f.add("North", panel);
        f.pack();
        f.setVisible(true);

    }
}