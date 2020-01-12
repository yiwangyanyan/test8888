package com.itheima.test;

import com.google.gson.Gson;
import com.qiniu.common.QiniuException;
import com.qiniu.common.Zone;
import com.qiniu.http.Response;
import com.qiniu.storage.BucketManager;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.DefaultPutRet;
import com.qiniu.util.Auth;
import org.junit.Test;

/**
 * @Description:
 * @Author: yp
 */
public class QiniuTest {
    @Test
    //文件上传
    public void fun01() {
        //1,构造一个带指定Zone对象的配置类
        //华东	Zone.zone0()
        //华北	Zone.zone1()
        //华南	Zone.zone2()
        //北美	Zone.zoneNa0()
        //东南亚	Zone.zoneAs0()
        Configuration cfg = new Configuration(Zone.zone0());

        UploadManager uploadManager = new UploadManager(cfg);
        //2.生成上传凭证，然后准备上传
        String accessKey = "Qu57Bn-tNWVfUGsx8dOYDQ8Zw-qTkCYdcNd0aToF";
        String secretKey = "3bT2rIfXzv3xG1TmCeV44BJuQ22-65r2IZYxzK-9";
        String bucket = "sz_70";
        //如果是Windows情况下，格式是 D:\\qiniu\\test.png
        String localFilePath = "G:/tu_1.jpg";
        //默认不指定key的情况下，以文件内容的hash值作为文件名
        String key = null;

        Auth auth = Auth.create(accessKey, secretKey);
        String upToken = auth.uploadToken(bucket);
        try {
            Response response = uploadManager.put(localFilePath, key, upToken);
            //解析上传成功的结果
            DefaultPutRet putRet = new Gson().fromJson(response.bodyString(), DefaultPutRet.class);
            System.out.println(putRet.key);
            System.out.println(putRet.hash);
        } catch (QiniuException ex) {
            Response r = ex.response;
            System.err.println(r.toString());
            try {
                System.err.println(r.bodyString());
            } catch (QiniuException ex2) {
            }
        }

    }

    @Test
    //文件删除
    public void fun02() {
        //构造一个带指定Zone对象的配置类
        Configuration cfg = new Configuration(Zone.zone0());
        //...其他参数参考类注释

        String accessKey = "Qu57Bn-tNWVfUGsx8dOYDQ8Zw-qTkCYdcNd0aToF";
        String secretKey = "3bT2rIfXzv3xG1TmCeV44BJuQ22-65r2IZYxzK-9";
        String bucket = "sz_70";

        String key = "FuM1Sa5TtL_ekLsdkYWcf5pyjKGu"; //文件名

        Auth auth = Auth.create(accessKey, secretKey);
        BucketManager bucketManager = new BucketManager(auth, cfg);
        try {
            bucketManager.delete(bucket, key);
        } catch (QiniuException ex) {
            //如果遇到异常，说明删除失败
            System.err.println(ex.code());
            System.err.println(ex.response.toString());
        }


    }

}
