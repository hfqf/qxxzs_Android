package ImageUtil.preview;

import android.app.Activity;
import android.content.Context;
import android.media.Image;

import com.ypx.imagepicker.ImagePicker;
import com.ypx.imagepicker.bean.ImageItem;
import com.ypx.imagepicker.bean.MimeType;
import com.ypx.imagepicker.bean.SelectMode;
import com.ypx.imagepicker.data.OnImagePickCompleteListener;
import java.util.ArrayList;


public class SpeImagePreviewUtil{
    public static void preivew(Activity context, int index, ArrayList<String> arrayList){
        ArrayList<ImageItem> allPreviewImageList = new ArrayList<>();
        for(int i=0;i<arrayList.size();i++){
            ImageItem item = ImageItem.withPath(context,arrayList.get(i));
            allPreviewImageList.add(item);
        }
        ImagePicker.preview(context, new WeChatPresenter(), allPreviewImageList, index, new OnImagePickCompleteListener() {
            @Override
            public void onImagePickComplete(ArrayList<ImageItem> items) {
                //图片编辑回调，主线程
            }
        });
    }

    public static void selectMutiImage(Activity context,int max){
        ImagePicker.withMulti(new WeChatPresenter())//指定presenter
                //设置选择的最大数
                .setMaxCount(max)
                //设置列数
                .setColumnCount(4)
                //设置要加载的文件类型，可指定单一类型
                .mimeTypes(MimeType.ofAll())
                //设置需要过滤掉加载的文件类型
                .filterMimeTypes(MimeType.GIF)
                .showCamera(true)//显示拍照
                .setPreview(true)//开启预览
                //大图预览时是否支持预览视频
                .setPreviewVideo(false)
                //设置视频单选
                .setVideoSinglePick(false)
                //设置图片和视频单一类型选择
                .setSinglePickImageOrVideoType(true)
                //当单选或者视频单选时，点击item直接回调，无需点击完成按钮
                .setSinglePickWithAutoComplete(false)
                //显示原图
                .setOriginal(true)
                //显示原图时默认原图选项开关
                .setDefaultOriginal(false)
                //设置单选模式，当maxCount==1时，可执行单选（下次选中会取消上一次选中）
                .setSelectMode(SelectMode.MODE_SINGLE)
                //设置视频可选取的最大时长,同时也是视频可录制的最大时长
                .setMaxVideoDuration(1200000L)
                //设置视频可选取的最小时长
                .setMinVideoDuration(60000L)
                //设置上一次操作的图片列表，下次选择时默认恢复上一次选择的状态
                .setLastImageList(null)
                //设置需要屏蔽掉的图片列表，下次选择时已屏蔽的文件不可选择
                .setShieldList(null)
                .pick(context, new OnImagePickCompleteListener() {
                    @Override
                    public void onImagePickComplete(ArrayList<ImageItem> items) {
                        //图片选择回调，主线程
                    }
                });
    }
}