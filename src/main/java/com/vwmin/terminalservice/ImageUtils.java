package com.vwmin.terminalservice;


import lombok.extern.slf4j.Slf4j;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.util.Assert;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.OverlappingFileLockException;

/**
 * @author vwmin
 * @version 1.0
 * @date 2020/4/12 21:04
 */
@Slf4j
public class ImageUtils {
    private ImageUtils(){}
    private static String IMAGE_HOME;
    public static void setImageHome(String imageHome){
        IMAGE_HOME = imageHome;
    }


    public static void downloadImage(String filename, String url) throws IOException{
        downloadImage(filename, url, new Header[0]);
    }

    public static void downloadImage(String filename, String url, Header[] headers) throws IOException{
        downloadImage(IMAGE_HOME, filename, url, headers);
    }

    public static void downloadImage(String filePath, String fileName, String url) throws IOException {
        downloadImage(filePath, fileName, url, new Header[0]);
    }

    public static void downloadImage(String filePath, String fileName, String url, Header[] headers) throws IOException {
        CloseableHttpClient client = HttpClients.createDefault();
        HttpGet get = new HttpGet(url);
        if (headers.length != 0){
            get.setHeaders(headers);
        }
        try(CloseableHttpResponse response = client.execute(get)) {

            HttpEntity responseEntity = response.getEntity();
            InputStream inputStream = responseEntity.getContent();

            saveImage(inputStream, filePath, fileName);

        } catch (IOException e) {
            log.warn("图片下载或保存时出现错误");
            throw e;
        }

    }

    public static boolean isExist(String fileName){
        return isExist(IMAGE_HOME, fileName);
    }

    public static boolean isExist(String dirPath, String fileName){
        File file = new File(dirPath.concat(fileName));
        return file.exists();
    }

    /**
     * 图片保存前检查
     * @param in 输入流
     * @param dirPath 文件夹路径
     * @param fileName 指定文件名时
     */
    public static void saveImage(InputStream in, String dirPath, String fileName) throws IOException {
        Assert.notNull(in, "文件输入流不能为空");
        Assert.notNull(dirPath, "文件夹路径不能为空");
        Utils.notEmpty(fileName, "文件名不能为空");

        File dir = new File(dirPath);
        if(!dir.exists()){
            if(!dir.mkdir()) {
                throw new IOException("文件夹创建失败: "+dirPath);
            }
        }

        File file = new File(dirPath.concat(fileName));
        doSave(in, file);
    }

    /**
     * 执行文件写入
     * @param in 输入流
     * @param file 打开的文件句柄
     */
    public static void doSave(InputStream in, File file) throws IOException{
        // file.setReadable(true, false);
        // file.setWritable(true, false);

        if(!file.exists()){
            if(file.createNewFile()){
                try(RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");
                    FileChannel fileChannel = randomAccessFile.getChannel()){

                    fileChannel.tryLock();
                    byte[] buf = new byte[1024];
                    int len;
                    while ((len = in.read(buf)) != -1) {
                        randomAccessFile.write(buf, 0, len);
                    }

                } catch (OverlappingFileLockException e){
                    log.warn("其他线程正在操作文件 >> " + file.getAbsolutePath());
                    throw e;
                }

            }else{
                throw new IOException("文件创建失败: " + file.getAbsolutePath());
            }
        }



    }
}
