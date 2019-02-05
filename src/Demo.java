import java.io.*;
import java.util.TreeSet;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import javax.imageio.*;
import javax.swing.*;
import java.util.Random;

public class Demo extends Component implements ActionListener {

    //************************************
    // List of the options(Original, Negative); correspond to the cases:
    //************************************

    String descs[] = {
            "Original",
            "Negative",
            "Rescale",
            "Shift",
            "RandomShiftAndRescale",
            "Arithmetic Operations",
    };

    int opIndex;  //option index for
    int lastOp;
    String paraText = "1";
    final int OR = 1;
    final int XOR = 2;
    final int AND = 3;
    final int ADDITION = 1;
    final int SUBTRACTION = 2;
    final int MULTIPLATICATION = 3;
    final int DIVISION = 4;

    private BufferedImage bi, biFiltered, biAlt, concImage;   // the input image saved as bi;//
    int w, h;

    public Demo() {
        try {
            bi = ImageIO.read(new File("image/BaboonRGB.bmp"));
            biAlt = ImageIO.read(new File("image/PeppersRGB.bmp"));

            w = bi.getWidth(null);
            h = bi.getHeight(null);
            System.out.println(bi.getType());
            if (bi.getType() != BufferedImage.TYPE_INT_RGB) {
                BufferedImage bi2 = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
                Graphics big = bi2.getGraphics();
                big.drawImage(bi, 0, 0, null);

                biFiltered = bi = bi2;
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
        filterImage();

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

    public BufferedImage BitwiseNotTransformation(BufferedImage timg, BufferedImage timg2){ //Lab 3 Exercise 2
        return timg;
    }

    public BufferedImage BitwiseTransformation(BufferedImage timg, BufferedImage timg2, int operator){ //Lab 3 Exercise 3
        return timg;
    }

    public BufferedImage ROIProcessing(BufferedImage timg){ //Lab 3 Exercise 4
        return timg;
    }

    public BufferedImage NegativeLinearTransform(BufferedImage timg){ //Lab 4 Exercise 1
        return timg;
    }

    public BufferedImage LogarithmicFunction(BufferedImage timg){ //Lab 4 Exercise 2
        return timg;
    }

    public BufferedImage PowerLaw(BufferedImage timg){ //Lab 4 Exercise 3
        return timg;
    }

    public BufferedImage RandomLookupTransform(BufferedImage timg){ //Lab 4 Exercise 4
        return timg;
    }

    public BufferedImage BitplaneSlicing(BufferedImage timg, int planeCounter){ //Lab 4 Exercise 5
        return timg;
    }

    public void FindHistogram(BufferedImage timg, int[][] histogramMatrix){ //Lab 5 Exercise 1
        return;
    }

    public void NormaliseHistogram(int[][] inputHistogram, int[][] outputHistogram){ //Lab 5 Exercise 2
        return;
    }

    public void EqualiseHistogram(int[][] inputHistogram, int[][] outputHistogam){ //Lab 5 Exercise 3
        return;
    }

    public BufferedImage ConvolutionFiltering(BufferedImage timg, int[][] filterMaxtrix){  //Lab 6 Exercise 1
        return timg;
    }

    public BufferedImage SANDPNoise(BufferedImage timg){ //Lab 7 Exercise 1
        return timg;
    }

    public BufferedImage MinFiltering(BufferedImage timg){ //Lab 7 Exercise 2
        return timg;
    }

    public BufferedImage MaxFiltering(BufferedImage timg){ //Lab 7 Exercise 3
        return timg;
    }

    public BufferedImage MidpointFiltering(BufferedImage timg){ //Lab 7 Exercise 4
        return timg;
    }

    public BufferedImage MedianFilteing(BufferedImage timg){ //Lab 7 Exercise 5
        return timg;
    }

    public BufferedImage MeanAndSTDDeviation(BufferedImage timg, int[] meanAndSTDDeviation){ //Lab 8 Exercise 1
        return timg;
    }

    public BufferedImage SimpleThresholding(BufferedImage timg, int thresholdValue){ //Lab 8 Exercise 2
        return timg;
    }

    public BufferedImage AutomatedThresholding(BufferedImage timg){ //Lab 8 Exercise 3
        return timg;
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
    public void ArithmeticOperatationsParse(){
        int i = 0;
        boolean b = true;
        try{
            i = Integer.parseInt(paraText);
        }catch(NumberFormatException e){
            b = false;
            i = 0;
        }
        if(b) {
            biFiltered = ArithmeticOperations(bi, biAlt, i);
        }else{
            biFiltered = bi;
        }
    }
    public void filterImage() {

        lastOp = 5;
        switch (opIndex) {
            case 0: biFiltered = bi; /* original */
                return;
            case 1: biFiltered = ImageNegative(bi); /* Image Negative */
                return;
            case 2: RescalingParse();
                return;
            case 3: ImagePixelShiftingParse();
                return;
            case 4: biFiltered = ImagePixelRandShiftAndRescale(bi);
                return;
            case 5: ArithmeticOperatationsParse();
                return;
            case 6:
                return;
            case 7:
                return;
            case 8:
                return;
            case 9:
                return;
            case 10:
                return;
            case 11:
                return;
            case 12:
                return;
            case 13:
                return;
            case 14:
                return;
            case 15:
                return;
            case 16:
                return;

        }
    }



    public void actionPerformed(ActionEvent e) {
        Object cbtemp = e.getSource();
        JComboBox cb;
        JTextField tx;
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
        }else{
             tx = (JTextField)cbtemp;
             paraText = (String)tx.getText();
             repaint();
        }
    }

    public static void main(String s[]) {
        JFrame f = new JFrame("Image Processing Demo");
        f.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {System.exit(0);}
        });
        Demo de = new Demo();

        f.add("Center", de);
        JComboBox choices = new JComboBox(de.getDescriptions());
        choices.setActionCommand("SetFilter");
        choices.addActionListener(de);
        JComboBox formats = new JComboBox(de.getFormats());
        formats.setActionCommand("Formats");
        formats.addActionListener(de);
        JTextField textbox = new JTextField("", 10);
        textbox.setActionCommand("textEntered");
        textbox.addActionListener(de);
        JPanel panel = new JPanel();
        panel.add(choices);
        panel.add(new JLabel("Save As"));
        panel.add(formats);
        panel.add(new JLabel("Parameter"));
        panel.add(textbox);
        f.add("North", panel);
        f.pack();
        f.setVisible(true);

    }
}