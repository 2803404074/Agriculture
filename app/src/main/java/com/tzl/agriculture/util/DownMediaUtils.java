package com.tzl.agriculture.util;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;


/**
 * Created by Administrator on 2018/9/14.
 */

//下载图片  视频  等等媒体  包括复制
public class DownMediaUtils {
    public static final String WEIXINPATH = "/agriculture/";
    private DownMediaLisenter mDownMediaLisenter;
    private List<String> mStrings = new ArrayList<>();
    private static DownMediaUtils mDownMediaUtils;
    private Context mContext;
    private int total, intTemp;

    private DownMediaUtils(Context context) {
        this.mContext = context;
    }

    public static DownMediaUtils getmDownMediaUtils(Context context) {
        if (mDownMediaUtils == null) {
            mDownMediaUtils = new DownMediaUtils(context);
        }
        return mDownMediaUtils;
    }

    //接口回调
    public interface DownMediaLisenter {
        // stirngs表示能下载成功 null size=0 表士下载失败
        void downCallBack(List<String> strings);
    }

    //开始分发
    public void setDownMediaLisenter(List<String> path, @NonNull DownMediaLisenter downMediaLisenter) {
        mStrings.clear();
        mDownMediaLisenter = downMediaLisenter;
        if (path == null || path.size() == 0) {
            handler.sendEmptyMessage(3);
            return;
        }
        intTemp = 0;
        total = path.size();
        for (int i = 0; i < total; i++) {
            String s = path.get(i);
            if (!TextUtils.isEmpty(s)) {
                if (s.startsWith("http")) {
                    //网络下载
                    onDownByNet(i + 1, s);
                } else {
                    //本地图片
                    onDownByLocat(i + 1,s);
                }
            }else{
                handler.sendEmptyMessage(1);
            }
        }
    }

    //网络下载
    private void onDownByNet(int index, String s) {
        new ThreadDown(index, s).start();
    }

    //本地复制
    private void onDownByLocat(int index,String s) {
        File file = new File(s);
        //文件不存在
        if (file == null) {
            handler.sendEmptyMessage(1);
            return;
        }
        //des新建目录失败
        File fileDirectory = isCreateFile();
        if (fileDirectory == null || fileDirectory.isFile()) {
            handler.sendEmptyMessage(1);
            return;
        }
        File des = new File(fileDirectory.getAbsolutePath() + File.separator + getFileDateName(s));
        //文件已存在 不用复制  ---避免没有修改，存在替换吧

        //进行复制  不存在
        try {
            copyFile(file.getPath(), des.getPath());
            Message message = handler.obtainMessage();
            message.what=0;
            message.obj=des.getAbsolutePath();
            handler.sendMessage(message);
        } catch (Throwable e) {
            handler.sendEmptyMessage(1);
            return;
        }
    }

    //文件名
    private String getFileDateName(String endT) {
        boolean b = endT.toLowerCase().endsWith(".png");
        return "kd_" + System.currentTimeMillis() +"."+(b?"png":"jpg");
    }

    //判断文件夹是否存在 返回文件夹
    private File isCreateFile() {
        File file = null;
        try {
            file = new File(Environment.getExternalStorageDirectory() + WEIXINPATH);
            if (!file.exists()) {
                file.mkdirs();
            }
        } catch (Throwable t) {
            return file;
        }
        return file;
    }


    //发送广播通知图库
    private void sendNotionToPic(File fileArp) {
        mContext.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(fileArp)));
        mContext.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + fileArp.getPath())));
    }

    //下载图片线程
    class ThreadDown extends Thread {
        private int index;
        private String s;

        public ThreadDown(int index, String s) {
            this.index = index;
            this.s = s;
        }

        @Override
        public void run() {
            super.run();
            try {
                File file = Glide.with(mContext)
                        .load(s)
                        .downloadOnly(640, 960)
                        .get();
                if (file == null) {
                    handler.sendEmptyMessage(1);
                    return;
                }
                //des新建目录失败
                File fileDirectory = isCreateFile();
                if (fileDirectory == null || fileDirectory.isFile()) {
                    handler.sendEmptyMessage(1);
                    return;
                }
                File des = new File(fileDirectory.getAbsolutePath() + File.separator +index+ getFileDateName(s));
                copyFile(file.getPath(), des.getPath());
                Message message = handler.obtainMessage();
                message.what=0;
                message.obj=des.getAbsolutePath();
                handler.sendMessage(message);
            } catch (Exception e) {
                handler.sendEmptyMessage(1);
                return;
            }

        }
    }
    //下载完成回调
    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            //下载成功  或者复制成功
            if (msg.what == 0) {
                sendNotionToPic(new File(msg.obj+""));
                intTemp++;
                mStrings.add(msg.obj+"");
                //进行广播通知
                if(intTemp>=total){
                    sendEmptyMessage(3);
                }
            } else if (msg.what == 1) {
                //下载失败   或者复制失败
                intTemp++;
                if(intTemp>=total){
                    sendEmptyMessage(3);
                }
            } else {
                //回调
                mDownMediaLisenter.downCallBack(mStrings);
            }
        }
    };

    //避免重复图片 每次发送完成需要清理图片
    public void deteleAllFile(List<String> paths){
        if(paths==null||paths.size()==0){
            return;
        }
        for(String sp:paths){
            if(!TextUtils.isEmpty(sp)){
                new DeleteFileThread(sp).start();
            }
        }
        //包括裁剪和压缩后的缓存，要在上传成功后调用，注意：需要系统 sd 卡权限
        //  PictureFileUtils.deleteCacheDirFile(mContext);
    }
    //删除文件线程
    class DeleteFileThread extends Thread{
        private String path;

        public DeleteFileThread(String path) {
            this.path = path;
        }

        @Override
        public void run() {
            super.run();
            File file=new File(path);
            if(file!=null&&file.exists()&&file.isFile()){
                sendNotionToPic(file);
                file.delete();

            }
        }
    }

    //文件比较器
    class FileComparator implements Comparator<File> {

        @Override
        public int compare(File file1, File file2) {
            if (file1.lastModified() < file2.lastModified()) {
                return 1;// 最后修改的文件在前
            } else {
                return -1;
            }
        }
    }

    //选择最近保存的  图片  视频
    public  String getDownByWx(){
        // 如果dir不以文件分隔符结尾，自动添加文件分隔符
        try {
            String dir=Environment.getExternalStorageDirectory() + WEIXINPATH;
            if (!dir.endsWith(File.separator))
                dir = dir + File.separator;
            File dirFile = new File(dir);
            // 如果dir对应的文件不存在，或者不是一个目录，则退出
            if ((!dirFile.exists()) || (!dirFile.isDirectory())) {
                return "";
            }
            File[] files = dirFile.listFiles();
            if(files==null||files.length==0){
                return "";
            }
            Arrays.sort(files,new FileComparator());
            for(File f:files){
                if(f!=null&&f.isFile()){
                    //刷新文件
                    sendNotionToPic(f);
                    return f.getAbsolutePath();
                }
            }
        }catch (Throwable t){
            return "";
        }
        return "";
    }


    private boolean copyFile(String srcPath, String destDir) {
        boolean flag = false;
        File srcFile = new File(srcPath); // 源文件
        if (srcFile==null||!srcFile.exists()) {
            return false;
        }
//        // 获取待复制文件的文件名
//        String fileName = srcPath.substring(srcPath.lastIndexOf(File.separator));
//        String destPath = destDir + fileName;
//        if (destPath.equals(srcPath)){
//            LogUtils.v("源文件路径和目标文件路径重复");
//            return false;
//        }
        File destFile = new File(destDir); // 目标文件
        if(destFile==null){
            return false;
        }
        if (destFile.exists() && destFile.isFile()) {
            return false;
        }
//        File destFileDir = new File(destDir);
//        destFileDir.mkdirs();
        try {
            FileInputStream fis = new FileInputStream(srcPath);
            FileOutputStream fos = new FileOutputStream(destFile);
            byte[] buf = new byte[1024];
            int c;
            while ((c = fis.read(buf)) != -1) {
                fos.write(buf, 0, c);
            }
            fis.close();
            fos.close();
            flag = true;
        } catch (IOException e) {
            // e.printStackTrace();
        }
        return flag;
    }


}