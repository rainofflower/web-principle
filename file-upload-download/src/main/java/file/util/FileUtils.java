package file.util;

import com.yanghui.learn.common.IOUtils;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author YangHui
 */
@Slf4j
public class FileUtils {

    private static int BUFFER_SIZE = 1024;

    /**
     * @param file
     * @param response
     */
    public static void downloadFile0(String file, HttpServletResponse response) {
        OutputStream os = null;
        try {
            // 取得输出流
            os = response.getOutputStream();
            long start = System.currentTimeMillis();
            Path path = Paths.get(file);
            log.info("download0---file,cost {} ms",System.currentTimeMillis() - start);
            String contentType = Files.probeContentType(path);
            response.setHeader("Content-Type", contentType);
            String fileName1 = URLEncoder.encode(new File(file).getName(), "UTF-8");
            response.setHeader("Content-Disposition", "attachment;filename*=utf-8'zh_cn'" + fileName1);
            long start2 = System.currentTimeMillis();
            Files.copy(path, os);
            log.info("download0---copy,cost {} ms",System.currentTimeMillis() - start2);
        } catch (IOException e) {
            log.error("download0 error",e);
        }
        finally {
            IOUtils.closeQuietly(os);
        }
    }

    /**
     * @param file
     * @param response
     */
    public static void downloadFile1(File file, HttpServletResponse response) {
        FileInputStream fileInputStream = null;
        OutputStream os = null;
        FileChannel fileChannel = null;
        WritableByteChannel writableByteChannel = null;
        try {
            // 取得输出流
            os = response.getOutputStream();
            String contentType = Files.probeContentType(Paths.get(file.getAbsolutePath()));
            response.setHeader("Content-Type", contentType);
            String fileName1 = URLEncoder.encode(file.getName(), "UTF-8");
            response.setHeader("Content-Disposition", "attachment;filename*=utf-8'zh_cn'" + fileName1);
            writableByteChannel = Channels.newChannel(os);
            long start = System.currentTimeMillis();
            fileInputStream = new FileInputStream(file);
            fileChannel = fileInputStream.getChannel();
            log.info("download1---file,cost {} ms",System.currentTimeMillis() - start);
            long size = fileChannel.size();
            long position = 0;
            long loaded;
            long start2 = System.currentTimeMillis();
            while((loaded = fileChannel.transferTo(position, size, writableByteChannel)) > 0){
                position += loaded;
            }
            log.info("download1---copy,cost {} ms",System.currentTimeMillis() - start2);
        } catch (IOException e) {
            log.error("download1 error",e);
        }
        finally {
            IOUtils.closeQuietly(fileInputStream);
            IOUtils.closeQuietly(writableByteChannel);
            IOUtils.closeQuietly(fileChannel);
            IOUtils.closeQuietly(os);
        }
    }

    public static void downloadFile2(File file, HttpServletResponse response){
        FileInputStream fileInputStream = null;
        FileChannel fileChannel = null;
        try {
            long start = System.currentTimeMillis();
            //拼接文件
            fileInputStream = new FileInputStream(file);
            long fileLength = file.length();
            //对文件名进行编码，解决中文名乱码
            String fileName1 = URLEncoder.encode(file.getName(), "UTF-8");
            String contentType = Files.probeContentType(Paths.get(file.getAbsolutePath()));
            response.setContentType("application/octet-stream");
            response.setHeader("Access-Control-Expose-Headers", "Content-Disposition");
            //指定文件下载，并解决中文名乱码
            response.setHeader("Content-Disposition", "attachment;filename*=utf-8'zh_cn'" + fileName1);
            response.setHeader("Content-Length", String.valueOf(fileLength));
            response.setContentType(contentType);
            fileChannel = fileInputStream.getChannel();

            int bufferSize = BUFFER_SIZE;
            ByteBuffer buff = ByteBuffer.allocateDirect(BUFFER_SIZE);
            byte[] byteArr = new byte[bufferSize];
            int nGet;
            while(fileChannel.read(buff)!=-1){
                buff.flip();
                while (buff.hasRemaining()) {
                    nGet = Math.min(buff.remaining(), bufferSize);
                    // read bytes from disk
                    buff.get(byteArr, 0, nGet);
                    // write bytes to output
                    response.getOutputStream().write(byteArr);
                }
                buff.clear();
            }
            log.info("download2,cost {} ms",System.currentTimeMillis() - start);
        } catch (Exception e) {
            log.error("download2 error",e);
        } finally {
            IOUtils.closeQuietly(fileInputStream);
            IOUtils.closeQuietly(fileChannel);
        }
    }

    /**
     *
     * @param file
     * @param response
     */
    public static void downloadFile3(File file, HttpServletResponse response){
        FileInputStream fileInputStream = null;
        OutputStream outputStream = null;
        FileChannel fileChannel = null;
        WritableByteChannel writableByteChannel = null;
        try {
            //拼接文件
            long fileLength = file.length();
            //对文件名进行编码，解决中文名乱码
            String fileName1 = URLEncoder.encode(file.getName(), "UTF-8");
            String contentType = Files.probeContentType(Paths.get(file.getAbsolutePath()));
            response.setContentType("application/octet-stream");
            response.setHeader("Access-Control-Expose-Headers", "Content-Disposition");
            //指定文件下载，并解决中文名乱码
            response.setHeader("Content-Disposition", "attachment;filename*=utf-8'zh_cn'" + fileName1);
            response.setHeader("Content-Length", String.valueOf(fileLength));
            response.setContentType(contentType);
            long start = System.currentTimeMillis();
            fileInputStream = new FileInputStream(file);
            fileChannel = fileInputStream.getChannel();
            log.info("download3---file,cost {} ms",System.currentTimeMillis() - start);
            outputStream = response.getOutputStream();
            writableByteChannel = Channels.newChannel(outputStream);
            ByteBuffer buff = ByteBuffer.allocateDirect(BUFFER_SIZE);
            long start2 = System.currentTimeMillis();
            while(fileChannel.read(buff) != -1){
                buff.flip();
                while(buff.hasRemaining()){
                    writableByteChannel.write(buff);
                }
                buff.clear();
            }
            log.info("download3---copy,cost {} ms",System.currentTimeMillis() - start2);
        } catch (Exception e) {
            log.error("download3 error",e);
        } finally {
            IOUtils.closeQuietly(fileInputStream);
            IOUtils.closeQuietly(writableByteChannel);
            IOUtils.closeQuietly(fileChannel);
            IOUtils.closeQuietly(outputStream);
        }
    }

    public static void downloadFile4(File file, HttpServletResponse response){
        FileInputStream fis = null; //文件输入流
        BufferedInputStream bis = null;

        OutputStream os = null; //输出流
        try {
            long start = System.currentTimeMillis();
            //对文件名进行编码，解决中文名乱码
            String fileName1 = URLEncoder.encode(file.getName(), "UTF-8");
            long fileLength = file.length();
            String contentType = Files.probeContentType(Paths.get(file.getAbsolutePath()));
            response.setContentType("application/octet-stream");
            response.setHeader("Access-Control-Expose-Headers", "Content-Disposition");
            //指定文件下载，并解决中文名乱码
            response.setHeader("Content-Disposition", "attachment;filename*=utf-8'zh_cn'" + fileName1);
            response.setHeader("Content-Length", String.valueOf(fileLength));
            response.setContentType(contentType);
            os = response.getOutputStream();
            fis = new FileInputStream(file);
            bis = new BufferedInputStream(fis);
            byte[] buffer = new byte[BUFFER_SIZE];
            int i = bis.read(buffer);
            while (i != -1) {
                os.write(buffer);
                i = bis.read(buffer);
            }
            log.info("download4,cost {} ms",System.currentTimeMillis() - start);
        }catch (Exception e){
            log.info("download4 error",e);
        }finally {
            IOUtils.closeQuietly(fis);
            IOUtils.closeQuietly(bis);
            IOUtils.closeQuietly(os);
        }
    }
}
