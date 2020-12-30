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

    /**
     * 文件分片下载  fileChannel
     * @param range http请求头Range，用于表示请求指定部分的内容。
     *              格式为：Range: bytes=start-end  [start,end]表示，即是包含请求头的start及end字节的内容
     * @param response
     */
    public static void fileChunkDownload1(File file, String range, HttpServletResponse response) {

        //开始下载位置
        long startByte = 0;

        long fileEndByte = file.length() - 1;

        //结束下载位置
        long endByte = fileEndByte;

        //有range的话
        if (range != null && range.contains("bytes=") && range.contains("-")) {
            range = range.substring(range.lastIndexOf("=") + 1).trim();
            String ranges[] = range.split("-");
            try {
                //根据range解析下载分片的位置区间
                if (ranges.length == 1) {
                    //情况1，如：bytes=-1024  从开始字节到第1024个字节的数据
                    if (range.startsWith("-")) {
                        endByte = Long.parseLong(ranges[0]);
                    }
                    //情况2，如：bytes=1024-  第1024个字节到最后字节的数据
                    else if (range.endsWith("-")) {
                        startByte = Long.parseLong(ranges[0]);
                    }
                }
                //情况3，如：bytes=1024-2048  第1024个字节到2048个字节的数据
                else if (ranges.length == 2) {
                    startByte = Long.parseLong(ranges[0]);
                    endByte = Long.parseLong(ranges[1]);
                }

            } catch (NumberFormatException e) {
                startByte = 0;
                endByte = fileEndByte;
            }
        }
        response.setHeader("Accept-Ranges", "bytes");
        //Content-Range 表示响应了多少数据，格式为：[要下载的开始位置]-[结束位置]/[文件总大小]
        response.setHeader("Content-Range", "bytes " + startByte + "-" + endByte + "/" + file.length());
        if(startByte > fileEndByte || endByte > fileEndByte){
            //416范围请求有误
            response.setStatus(response.SC_REQUESTED_RANGE_NOT_SATISFIABLE);
        }else{
            //要下载的长度
            long contentLength = endByte - startByte + 1;
            OutputStream os = null;
            WritableByteChannel writableByteChannel = null;
            FileInputStream fileInputStream = null;
            FileChannel fileChannel = null;
            try {
                long start = System.currentTimeMillis();
                String contentType = Files.probeContentType(Paths.get(file.getAbsolutePath()));
                response.setHeader("Content-Type", contentType);
                String fileName1 = URLEncoder.encode(file.getName(), "UTF-8");
                //Content-Disposition 表示响应内容以何种形式展示，是以内联的形式（即网页或者页面的一部分），还是以附件的形式下载并保存到本地。
                //inline表示内联的形式，即：浏览器直接下载
                response.setHeader("Content-Disposition", "inline;filename*=utf-8'zh_cn'" + fileName1);

                //Content-Length 表示资源内容长度，即：文件大小
                response.setHeader("Content-Length", String.valueOf(contentLength));
                //206表示返回的body只是原数据的一部分
                response.setStatus(response.SC_PARTIAL_CONTENT);

                os = response.getOutputStream();
                writableByteChannel = Channels.newChannel(os);
                fileInputStream = new FileInputStream(file);
                fileChannel = fileInputStream.getChannel();
                long position = startByte;
                //已传送数据大小
                long transmitted = 0;
                long loaded;
                while((loaded = fileChannel.transferTo(position, contentLength - transmitted, writableByteChannel)) > 0){
                    position += loaded;
                    transmitted += loaded;
                }
                log.info("fileChunkDownload1---copy,cost {} ms",System.currentTimeMillis() - start);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                IOUtils.closeQuietly(fileInputStream);
                IOUtils.closeQuietly(writableByteChannel);
                IOUtils.closeQuietly(fileChannel);
                IOUtils.closeQuietly(os);
            }
        }
    }

    /**
     * 文件分片下载 RandomAccessFile
     * @param range http请求头Range，用于表示请求指定部分的内容。
     *              格式为：Range: bytes=start-end  [start,end]表示，即是包含请求头的start及end字节的内容
     * @param response
     */
    public static void fileChunkDownload2(File file, String range, HttpServletResponse response) {

        //开始下载位置
        long startByte = 0;

        long fileEndByte = file.length() - 1;

        //结束下载位置
        long endByte = fileEndByte;

        //有range的话
        if (range != null && range.contains("bytes=") && range.contains("-")) {
            range = range.substring(range.lastIndexOf("=") + 1).trim();
            String ranges[] = range.split("-");
            try {
                //根据range解析下载分片的位置区间
                if (ranges.length == 1) {
                    //情况1，如：bytes=-1024  从开始字节到第1024个字节的数据
                    if (range.startsWith("-")) {
                        endByte = Long.parseLong(ranges[0]);
                    }
                    //情况2，如：bytes=1024-  第1024个字节到最后字节的数据
                    else if (range.endsWith("-")) {
                        startByte = Long.parseLong(ranges[0]);
                    }
                }
                //情况3，如：bytes=1024-2048  第1024个字节到2048个字节的数据
                else if (ranges.length == 2) {
                    startByte = Long.parseLong(ranges[0]);
                    endByte = Long.parseLong(ranges[1]);
                }

            } catch (NumberFormatException e) {
                startByte = 0;
                endByte = fileEndByte;
            }
        }
        response.setHeader("Accept-Ranges", "bytes");
        //Content-Range 表示响应了多少数据，格式为：[要下载的开始位置]-[结束位置]/[文件总大小]
        response.setHeader("Content-Range", "bytes " + startByte + "-" + endByte + "/" + file.length());
        if(startByte > fileEndByte || endByte > fileEndByte){
            //416范围请求有误
            response.setStatus(response.SC_REQUESTED_RANGE_NOT_SATISFIABLE);
        }else{
            //要下载的长度
            long contentLength = endByte - startByte + 1;
            BufferedOutputStream outputStream = null;
            RandomAccessFile randomAccessFile = null;
            OutputStream os = null;
            try {
                long start = System.currentTimeMillis();
                String contentType = Files.probeContentType(Paths.get(file.getAbsolutePath()));
                response.setHeader("Content-Type", contentType);
                String fileName1 = URLEncoder.encode(file.getName(), "UTF-8");
                //Content-Disposition 表示响应内容以何种形式展示，是以内联的形式（即网页或者页面的一部分），还是以附件的形式下载并保存到本地。
                //inline表示内联的形式，即：浏览器直接下载
                response.setHeader("Content-Disposition", "inline;filename*=utf-8'zh_cn'" + fileName1);

                //Content-Length 表示资源内容长度，即：文件大小
                response.setHeader("Content-Length", String.valueOf(contentLength));
                //206表示返回的body只是原数据的一部分
                response.setStatus(response.SC_PARTIAL_CONTENT);

                //已传送数据大小
                long transmitted = 0;

                os = response.getOutputStream();
                randomAccessFile = new RandomAccessFile(file, "r");
                outputStream = new BufferedOutputStream(os);
                byte[] buff = new byte[2048];
                int len = 0;
                randomAccessFile.seek(startByte);
                //判断是否到了最后不足2048（buff的length）个byte
                while ((transmitted + len) <= contentLength && (len = randomAccessFile.read(buff)) != -1) {
                    outputStream.write(buff, 0, len);
                    transmitted += len;
                }
                //处理不足buff.length部分
                if (transmitted < contentLength) {
                    len = randomAccessFile.read(buff, 0, (int) (contentLength - transmitted));
                    outputStream.write(buff, 0, len);
                }

                outputStream.flush();
                response.flushBuffer();
                log.info("fileChunkDownload2---copy,cost {} ms",System.currentTimeMillis() - start);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (randomAccessFile != null) {
                        randomAccessFile.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
