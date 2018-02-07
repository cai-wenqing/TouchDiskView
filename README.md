## TouchDiskView
触摸刻度盘效果演示(注意：刻度盘是逐格走的，也就是旋转单位为一格刻度)

![](https://github.com/WernerZeiss/TouchDiskView/blob/master/screenshot/GIF1.gif)

## 特点
* 中间图片可以设置也可以不设置
* 图片可设置是否跟随刻度旋转
* 可设置是否禁止手势触摸旋转
* 可设置左转右转监听

## 使用
主要提供思路方案，细节没有实现，比如控制刻度颜色、刻度数量等等，大家可以拷贝代码自行随意定制

## 目前实现的属性

属性 | 说明
-----|-----
disk_bg | xml中设置背景图片
rotateLeft() | 逆时针旋转一格
rotateRight() | 顺时针旋转一格
setTouch(boolean isTouchRotate) | 设置是否禁止手势滑动旋转
isTouch() | 获取是否禁止了滑动旋转
setPictureRotate(boolean isRotate) | 设置背景图片是否跟随刻度旋转
isPictureRotate() | 获取图片是否跟随旋转
setOnRotate(onRotateListener onRotateListener) | 设置监听
