package com.tuanlq.capture.demo;

import com.suprema.BioMiniSDK;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;

public class DemoBioMini {
    BioMiniSDK p = new BioMiniSDK();
    private int nInitFlag = 0;
    static Queue<byte[]> queueData = new LinkedList<byte[]>();

    public DemoBioMini(){

    }

    public byte[] getMessage(){
        return DemoBioMini.getQueueData().poll();
    }

    public static Queue<byte[]> getQueueData(){
        return DemoBioMini.queueData;
    }

    public long[] getCurrentScannerHandle() {
        long[] hScanner = new long[1];
        int nRes = 0;
        int[] nNumber = new int[1];

        nRes = p.UFS_GetScannerNumber(nNumber);

        if (nRes == 0) {
            if (nNumber[0] <= 0) {
                return null;
            }
        } else {
            return null;
        }

        int index = 0;
        nRes = p.UFS_GetScannerHandle(index, hScanner);

        if(nRes == 0 && hScanner != null){
            return hScanner;
        }
        return null;

    }

    public void doInit() {
        if (nInitFlag != 0) {
            System.out.println("already init...");
            return;
        }
        int nRes = 0;
        nRes = p.UFS_Init();
        if (nRes == 0) {
            System.out.println("UFS_Init() success!!");
            nInitFlag = 1;
            p.UFS_SetClassName("com.tuanlq.capture.demo.DemoBioMini");
        } else {
            System.out.println("Init() fail!!");
            System.out.println("Init fail!! return code:" + nRes);
        }

    }

    public void doUnInit(){
        int nRes = p.UFS_Uninit();
        if(nRes == 0){
            System.out.println("UnInit Success");
            nInitFlag = 0;
        } else {
            System.out.println("UnInit fail!!!");
        }
    }

    public void doStartingCapturing(){
        if(nInitFlag == 1){
            long[] hScanner = new long[1];

            hScanner = getCurrentScannerHandle();

            if (hScanner != null) {
                System.out.println("UFS_StartCapturing, get current scanner handle success! : " + hScanner[0]);
            } else {
                System.out.println("UFS_StartCapturing,GetScannerHandle fail!!");
            }

            //set the callback function name for getting captured frame and the information as second parameter
            int nRes = p.UFS_StartCapturing(hScanner[0], "captureCallback");

            if (nRes == 0) {
                System.out.println("UFS_StartCapturing success!!");
            } else {
                System.out.println("UFS_StartCapturing fail!! code:" + nRes);
            }
        } else {
            System.out.println("You have to initiate before start capturing");
        }
    }

    public void captureCallback(int bFingerOn, byte[] pImage, int nWidth, int nHeight, int nResolution) throws IOException {
        addByteArrayImageToQueue();
    }

    public byte[] getByteArrayCurrentImage() throws IOException {
        byte[] resultByte = null;

        int[] Resolution = new int[1];
        int[] Height = new int[1];
        int[] Width = new int[1];
        long[] hScanner = new long[1];

        hScanner = getCurrentScannerHandle();

        p.UFS_GetCaptureImageBufferInfo(hScanner[0], Width, Height, Resolution);

        byte[] pImageData = new byte[Width[0] * Height[0]];

        p.UFS_GetCaptureImageBuffer(hScanner[0], pImageData);

        BufferedImage buffImage = new BufferedImage(Width[0], Height[0], BufferedImage.TYPE_BYTE_GRAY);
        buffImage.getRaster().setDataElements(0, 0, Width[0], Height[0], pImageData);

        resultByte =  toByteArray(buffImage, "jpg");
        return resultByte;
    }

    public void addByteArrayImageToQueue() throws IOException {
        byte[] byteImageItem = getByteArrayCurrentImage();
        DemoBioMini.queueData.add(byteImageItem);
    }

    public byte[] toByteArray(BufferedImage bi, String format)
            throws IOException {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(bi, format, baos);
        byte[] bytes = baos.toByteArray();
        return bytes;

    }
}
