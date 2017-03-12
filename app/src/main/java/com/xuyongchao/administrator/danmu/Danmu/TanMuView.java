package com.xuyongchao.administrator.danmu.Danmu;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.xuyongchao.administrator.danmu.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;




/**
 * Created by xuyongchao
 * on 2016/11/11.
 * 邮箱:18036699151@163.com
 * QQ：870867914
 */
public class TanMuView extends RelativeLayout {

    private OnClickActionListener mClick = null;
    private TextView tv_tanmu;
    private List<String> viewpointListBlue;
    private List<String> viewpointListRed;
    private int blueSize;
    private int redSize;

    // 为接口设置监听器
    public void setOnClickActionListener(OnClickActionListener down) {
        mClick = down;
    }




    //定义接口
    public interface OnClickActionListener {
        void onClick(String str);
    }

    private Context mContext;
    private BarrageHandler mHandler = new BarrageHandler();
    private BarrageHandler2 mHandler2 = new BarrageHandler2();
    private Random random = new Random(System.currentTimeMillis());

    private static final long BARRAGE_GAP_MIN_DURATION = 1000;//两个弹幕的最小间隔时间
    private static final long BARRAGE_GAP_MAX_DURATION = 2000;//两个弹幕的最大间隔时间
    private int maxSpeed = 10000;   // 最小速度，ms，越大越慢
    private int minSpeed = 6000;    //  最快速度，ms，越大越慢
    private int maxSize = 30;       //最大字体文字，dp
    private int minSize = 15;       //最小文字大小，dp

    private int totalHeight = 0;    //总高度
    private int lineHeight = 0;     //每一行弹幕的高度
    private int totalLine = 0;      //弹幕的行数
    private List<String> itemText = new ArrayList<>();  //内容list

    public TanMuView(Context context) {
        this(context, null);
    }

    public TanMuView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TanMuView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        initData();
//        init();
    }

    /**
     * 初始化数据
     * **/

   public void initData(){
       viewpointListBlue = new LinkedList<>();
       viewpointListRed = new LinkedList<>();
       for (int i = 0; i < 10; i++) {
           viewpointListBlue.add("怼怼怼红方"+i);
           viewpointListRed.add("干干干蓝方"+i);
       }
   }

    public int type = 0;
    public int redIndex = 0;
    public int blueIndex = 0;
    private void generateItem() {
        synchronized(this) {
            TanMmItem item = new TanMmItem();
            String tx="";
             item.linearLayout = (RelativeLayout) LayoutInflater.from(mContext).inflate(R.layout.textview_tanmu, this, false);

            RelativeLayout linearLayout = item.linearLayout;
            if(linearLayout == null) return;
            tv_tanmu = (TextView) linearLayout.findViewById(R.id.tv_tanmu);
            ImageView iv_headphoto = (ImageView) linearLayout.findViewById(R.id.iv_headphoto);

            if (type % 2 == 0) {
                linearLayout.setBackgroundResource(R.drawable.red_bg_normal);
                tx = setDanMuData(tx, iv_headphoto,redIndex,viewpointListRed);
                tx = "红方:"+tx;
                redIndex++;
                tv_tanmu.setTextColor(Color.parseColor("#e8110f"));
            } else {
                linearLayout.setBackgroundResource(R.drawable.blue_bg_normal);
                tx =setDanMuData(tx, iv_headphoto,blueIndex,viewpointListBlue);
                tx = "蓝方:"+tx;
                blueIndex++;
                tv_tanmu.setTextColor(Color.parseColor("#0e6ac7"));
            }

            tv_tanmu.setText(tx);

            linearLayout.measure(0, 0);

            item.textMeasuredWidth = linearLayout.getMeasuredWidth();

            //设置随机移动速度
            item.moveSpeed = 10000;//(int) (minSpeed + (maxSpeed - minSpeed) * Math.random());

            //为0则,初始化
            if (totalLine == 0) {
                //获取当前View的实际高度
                totalHeight = getMeasuredHeight();
                //获取行高
                lineHeight = linearLayout.getMeasuredHeight();
                //获取总行数
                totalLine = totalHeight / lineHeight;
            }

            //垂直方向显示位置,行数的随机一行，nextInt(n) 返回一个大于等于0小于n的随机数
            System.out.println(totalLine + " " + lineHeight);
            item.verticalPos = lineHeight;//这是控制显示在第几行的坐标，高度信息,
            //显示滚屏
            showBarrageItem(item);
        }
    }

    private String setDanMuData(String tx, final ImageView iv_headphoto, int index, List<String> danMuBlueListBeen) {

           tx= danMuBlueListBeen.get(index%9);

           iv_headphoto.setBackgroundResource(R.mipmap.header_pic);

        return tx;
    }


    /**
     * 显示TextView 的动画效果
     * @param item
     */
    private void showBarrageItem(final TanMmItem item) {
//        if(item == null || item.linearLayout == null)return;
        //屏幕宽度 像素
        int leftMargin = this.getRight() - this.getLeft() - this.getPaddingLeft();
        //显示的TextView 的位置，
        LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        if(type % 2 == 0){
            params.topMargin =0;// (int)(item.verticalPos*0.5);//通过topMargin 来控制高度的
        }else {
            params.topMargin =(int)(item.verticalPos*1.3);//通过topMargin 来控制高度的
        }
        this.addView(item.linearLayout, params);

        //设置回调回调点击
        final String temp =tv_tanmu.getText().toString();
        item.linearLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mClick != null){
                    mClick.onClick(temp);
                }
            }
        });

        transAnimRun(item, leftMargin);
    }

    private void transAnimRun(final TanMmItem item, int leftMargin) {
        ObjectAnimator objAnim = null;
        if(type % 2 == 0){
             objAnim = ObjectAnimator
                    //滑动位置是x方向滑动，从屏幕宽度+View的长度到左边0-View的长度//这里偏移量，不是坐标
                    .ofFloat(item.linearLayout,"translationX" ,  -item.textMeasuredWidth,leftMargin)//这个是对textview进行移动，translationX是view左上角相对于父容器的偏移量
                    .setDuration(item.moveSpeed);
//        //设置移动的过程速度，开始快之后满
//        objAnim.setInterpolator(new DecelerateInterpolator());
            //开始动画
            objAnim.start();
        }else {
            objAnim = ObjectAnimator
                    //滑动位置是x方向滑动，从屏幕宽度+View的长度到左边0-View的长度//这里偏移量，不是坐标
                    .ofFloat(item.linearLayout,"translationX" ,leftMargin,  -item.textMeasuredWidth)//这个是对textview进行移动，translationX是view左上角相对于父容器的偏移量
                    .setDuration(item.moveSpeed);
//        //设置移动的过程速度，开始快之后满
//        objAnim.setInterpolator(new DecelerateInterpolator());
            //开始动画
            objAnim.start();
        }

        type ++;
        objAnim.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                //动画执行完毕，清除动画，删除view，
                item.linearLayout.clearAnimation();
                TanMuView.this.removeView(item.linearLayout);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }


    /**
     * 计算TextView中字符串的长度
     *
     * @param text 要计算的字符串
     * @param Size 字体大小
     * @return TextView中字符串的长度
     */
//    public float getTextWidth(BarrageItem item, String text, float Size) {
//        //Rect表示一个矩形，由四条边的坐标组成
//        Rect bounds = new Rect();
//        TextPaint paint;
//        paint = item.textView.getPaint();
//        paint.getTextBounds(text, 0, text.length(), bounds);
//        //System.out.println(item.textView.getText()+(bounds.width()+"")+"宽度");
//        return bounds.width();
//    }

    /**
     * 获得每一行弹幕的最大高度
     *
     * @return
     */
//    private int getLineHeight() {
//        BarrageItem item = new BarrageItem();
//        String tx;
//        tx = itemText.get(0);
//        item.textView = new TextView(mContext);
//        item.textView.setText(tx);
//        item.textView.setTextSize(maxSize);
//
//        Rect bounds = new Rect();
//        TextPaint paint;
//        paint = item.textView.getPaint();
//        paint.getTextBounds(tx, 0, tx.length(), bounds);
//        return bounds.height();
//    }

    class BarrageHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            //每个弹幕产生的间隔时间随机
            int duration = 6000;//(int) ((BARRAGE_GAP_MAX_DURATION - BARRAGE_GAP_MIN_DURATION) * Math.random());
            generateItem();
            this.sendEmptyMessageDelayed(0, duration);
        }
    }
    class BarrageHandler2 extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            //每个弹幕产生的间隔时间随机
            int duration = 6000;//(int) ((BARRAGE_GAP_MAX_DURATION - BARRAGE_GAP_MIN_DURATION) * Math.random());
            generateItem();
            this.sendEmptyMessageDelayed(0, duration);
        }
    }
    /**
     * 当view显示在窗口的时候，回调的visibility等于View.VISIBLE。。当view不显示在窗口时，回调的visibility等于View.GONE
     *
     * 窗口隐藏了，把内容全部清空，防止onPause时候内容停滞
     *
     * **/
    @Override
    protected void onWindowVisibilityChanged(int visibility) {
        super.onWindowVisibilityChanged(visibility);
        if(visibility == View.GONE || getVisibility() == View.GONE ){
            mHandler.removeMessages(0);
            mHandler2.removeMessages(0);
            int childCount = TanMuView.this.getChildCount();
            for (int i = 0; i < childCount; i++) {
               View view = getChildAt(i);
                if(view instanceof LinearLayout){
                    LinearLayout ll = (LinearLayout)view;
                    ll.clearAnimation();
                    removeView(ll);
                }
            }
            removeAllViews();
//            setVisibility(GONE);//用于从下一页返回的时候，不会立马就出现
        }else if(visibility == View.VISIBLE && getVisibility() == View.VISIBLE){
//            if(redSize > 0){
                mHandler.sendEmptyMessage(0);
                mHandler2.sendEmptyMessage(0);
//            }
//            if(blueSize > 0){

//            }
        }
    }

}
