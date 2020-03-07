package com.newbiechen.nbreader.ui.component.widget.page.anim

import android.graphics.*
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.view.View
import java.lang.Math.toDegrees
import kotlin.math.*
import kotlin.properties.Delegates


/**
 *  author : newbiechen
 *  date : 2019-09-05 16:45
 *  description :
 */

class SimulationPageAnimation(view: View, pageManager: IPageAnimCallback) :
    PageAnimation(view, pageManager) {

    companion object {
        private const val TAG = "SimulationPageAnimation"
    }

    private var mCornerX = 1 // 拖拽点对应的页脚
    private var mCornerY = 1
    private val mPath0: Path = Path()
    private val mPath1: Path = Path()

    private var mBezierStart1 = PointF() // 贝塞尔曲线起始点
    private var mBezierControl1 = PointF() // 贝塞尔曲线控制点
    private var mBezierVertex1 = PointF() // 贝塞尔曲线顶点
    private var mBezierEnd1 = PointF() // 贝塞尔曲线结束点

    private var mBezierStart2 = PointF() // 另一条贝塞尔曲线
    private var mBezierControl2 = PointF()
    private var mBezierVertex2 = PointF()
    private var mBezierEnd2 = PointF()

    private var mMiddleX: Float = 0f
    private var mMiddleY: Float = 0f
    private var mDegrees: Float = 0f
    private var mTouchToCornerDis: Float = 0f
    private var mColorMatrixFilter: ColorMatrixColorFilter = ColorMatrixColorFilter(
        ColorMatrix(
            floatArrayOf(
                1f, 0f, 0f, 0f, 0f,
                0f, 1f, 0f, 0f, 0f,
                0f, 0f, 1f, 0f, 0f,
                0f, 0f, 0f, 1f, 0f
            )
        )
    )

    private var mMatrix: Matrix = Matrix()
    private val mMatrixArray = floatArrayOf(0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 1.0f)

    private var mIsRTandLB: Boolean = false // 是否属于右上左下
    private var mMaxLength: Float? = null
    private val mPaint: Paint = Paint()
    // 适配 android 高版本无法使用 XOR 的问题
    private val mXORPath: Path = Path()

    private lateinit var mBackShadowColors: IntArray// 背面颜色组
    private lateinit var mFrontShadowColors: IntArray// 前面颜色组
    private lateinit var mBackShadowDrawableLR: GradientDrawable // 有阴影的GradientDrawable
    private lateinit var mBackShadowDrawableRL: GradientDrawable
    private lateinit var mFolderShadowDrawableLR: GradientDrawable
    private lateinit var mFolderShadowDrawableRL: GradientDrawable

    private lateinit var mFrontShadowDrawableHBT: GradientDrawable
    private lateinit var mFrontShadowDrawableHTB: GradientDrawable
    private lateinit var mFrontShadowDrawableVLR: GradientDrawable
    private lateinit var mFrontShadowDrawableVRL: GradientDrawable

    private var mFloatTouchX: Float = 0f
    private var mFloatTouchY: Float = 0f

    // 将 int 类型转换成 float 类型
    override var mTouchX: Int
        get() = mFloatTouchX.toInt()
        set(value) {
            mFloatTouchX = value.toFloat()
        }

    override var mTouchY: Int
        get() = mFloatTouchY.toInt()
        set(value) {
            mFloatTouchY = value.toFloat()

            // 限制范围
            if (mStartY > mViewHeight / 3 && mStartY < mViewHeight * 2 / 3 || mDirection == Direction.PREVIOUS) {
                mFloatTouchY = mViewHeight.toFloat()
            }

            if (mStartY > mViewHeight / 3 && mStartY < mViewHeight / 2 && mDirection == Direction.NEXT) {
                mFloatTouchY = 1.0f
            }
        }

    override var mDirection: Direction by Delegates.observable(Direction.NONE, { _, _, newValue ->
        when (newValue) {
            Direction.PREVIOUS ->
                //上一页滑动不出现对角
                if (mStartX > mViewWidth / 2) {
                    calcCornerXY(mStartX, mViewHeight)
                } else {
                    calcCornerXY(mViewWidth - mStartX, mViewHeight)
                }
            Direction.NEXT -> if (mViewWidth / 2 > mStartX) {
                calcCornerXY(mViewWidth - mStartX, mStartY)
            } else {
                calcCornerXY(mStartX, mStartY)
            }
            else -> {
                // 不处理
            }
        }
    })

    init {
        // 初始化 paint
        mPaint.style = Paint.Style.FILL
        // 创建阴影
        createDrawable()
        // 不让x,y为0,否则在点计算时会有问题
        mFloatTouchX = 0.01f
        mFloatTouchY = 0.01f
    }

    private fun createDrawable() {
        val color = intArrayOf(0x333333, -0x4fcccccd)
        mFolderShadowDrawableRL = GradientDrawable(
            GradientDrawable.Orientation.RIGHT_LEFT, color
        )
        mFolderShadowDrawableRL.gradientType = GradientDrawable.LINEAR_GRADIENT

        mFolderShadowDrawableLR = GradientDrawable(
            GradientDrawable.Orientation.LEFT_RIGHT, color
        )
        mFolderShadowDrawableLR.gradientType = GradientDrawable.LINEAR_GRADIENT

        mBackShadowColors = intArrayOf(-0xeeeeef, 0x111111)
        mBackShadowDrawableRL = GradientDrawable(
            GradientDrawable.Orientation.RIGHT_LEFT, mBackShadowColors
        )
        mBackShadowDrawableRL.gradientType = GradientDrawable.LINEAR_GRADIENT

        mBackShadowDrawableLR = GradientDrawable(
            GradientDrawable.Orientation.LEFT_RIGHT, mBackShadowColors
        )
        mBackShadowDrawableLR.gradientType = GradientDrawable.LINEAR_GRADIENT

        mFrontShadowColors = intArrayOf(-0x7feeeeef, 0x111111)
        mFrontShadowDrawableVLR = GradientDrawable(
            GradientDrawable.Orientation.LEFT_RIGHT, mFrontShadowColors
        )
        mFrontShadowDrawableVLR.gradientType = GradientDrawable.LINEAR_GRADIENT
        mFrontShadowDrawableVRL = GradientDrawable(
            GradientDrawable.Orientation.RIGHT_LEFT, mFrontShadowColors
        )
        mFrontShadowDrawableVRL.gradientType = GradientDrawable.LINEAR_GRADIENT

        mFrontShadowDrawableHTB = GradientDrawable(
            GradientDrawable.Orientation.TOP_BOTTOM, mFrontShadowColors
        )
        mFrontShadowDrawableHTB.gradientType = GradientDrawable.LINEAR_GRADIENT

        mFrontShadowDrawableHBT = GradientDrawable(
            GradientDrawable.Orientation.BOTTOM_TOP, mFrontShadowColors
        )
        mFrontShadowDrawableHBT.gradientType = GradientDrawable.LINEAR_GRADIENT
    }

    override fun setViewPort(w: Int, h: Int) {
        super.setViewPort(w, h)
        mMaxLength = hypot(w.toDouble(), h.toDouble()).toFloat()
    }

    override fun drawStatic(canvas: Canvas) {
        canvas.drawBitmap(getFromPage(), 0f, 0f, null)
    }

    override fun drawMove(canvas: Canvas) {
        when (mDirection) {
            Direction.PREVIOUS -> {
                calcPoints()
                drawCurrentPageArea(canvas, getToPage(), mPath0)
                drawNextPageAreaAndShadow(canvas, getFromPage())
                drawCurrentPageShadow(canvas)
                drawCurrentBackArea(canvas, getToPage())
            }
            Direction.NEXT -> {
                calcPoints()
                drawCurrentPageArea(canvas, getFromPage(), mPath0)
                drawNextPageAreaAndShadow(canvas, getToPage())
                drawCurrentPageShadow(canvas)
                drawCurrentBackArea(canvas, getFromPage())
            }
            else -> {
                // 不处理
            }
        }
    }

    override fun startAnim() {
        var dx: Float
        var dy: Float
        // dx 水平方向滑动的距离，负值会使滚动向左滚动
        // dy 垂直方向滑动的距离，负值会使滚动向上滚动
        if (mStatus == Status.AutoBackward) {
            dx = if (mCornerX > 0 && mDirection == Direction.NEXT) {
                (mViewWidth - mFloatTouchX)
            } else {
                -mFloatTouchX
            }

            if (mDirection != Direction.NEXT) {
                dx = -(mViewWidth + mFloatTouchX)
            }

            dy = if (mCornerY > 0) {
                (mViewHeight - mFloatTouchY)
            } else {
                -mFloatTouchY // 防止 mFloatTouchY 最终变为0
            }
        } else {
            dx = if (mCornerX > 0 && mDirection == Direction.NEXT) {
                -(mViewWidth + mFloatTouchX)
            } else {
                (mViewWidth - mFloatTouchX + mViewWidth)
            }

            dy = if (mCornerY > 0) {
                (mViewHeight - mFloatTouchY)
            } else {
                (1 - mFloatTouchY) // 防止 mFloatTouchY 最终变为0
            }
        }
        mScroller.startScroll(
            mFloatTouchX.toInt(),
            mFloatTouchY.toInt(),
            dx.toInt(),
            dy.toInt(),
            400
        )
    }

    /**
     * 绘制翻起页背面
     *
     * @param canvas
     * @param bitmap
     */
    private fun drawCurrentBackArea(canvas: Canvas, bitmap: Bitmap) {
        val i = (mBezierStart1.x + mBezierControl1.x).toInt() / 2
        val f1 = abs(i - mBezierControl1.x)
        val i1 = (mBezierStart2.y + mBezierControl2.y).toInt() / 2
        val f2 = abs(i1 - mBezierControl2.y)
        val f3 = min(f1, f2)
        mPath1.reset()
        mPath1.moveTo(mBezierVertex2.x, mBezierVertex2.y)
        mPath1.lineTo(mBezierVertex1.x, mBezierVertex1.y)
        mPath1.lineTo(mBezierEnd1.x, mBezierEnd1.y)
        mPath1.lineTo(mFloatTouchX, mFloatTouchY)
        mPath1.lineTo(mBezierEnd2.x, mBezierEnd2.y)
        mPath1.close()
        val mFolderShadowDrawable: GradientDrawable
        val left: Int
        val right: Int
        if (mIsRTandLB) {
            left = (mBezierStart1.x - 1).toInt()
            right = (mBezierStart1.x + f3 + 1f).toInt()
            mFolderShadowDrawable = mFolderShadowDrawableLR
        } else {
            left = (mBezierStart1.x - f3 - 1f).toInt()
            right = (mBezierStart1.x + 1).toInt()
            mFolderShadowDrawable = mFolderShadowDrawableRL
        }
        canvas.save()
        try {
            canvas.clipPath(mPath0)
            canvas.clipPath(mPath1)
        } catch (e: Exception) {
        }

        mPaint.colorFilter = mColorMatrixFilter
        //对Bitmap进行取色
        val color = bitmap.getPixel(1, 1)
        //获取对应的三色
        val red = color and 0xff0000 shr 16
        val green = color and 0x00ff00 shr 8
        val blue = color and 0x0000ff
        //转换成含有透明度的颜色
        val tempColor = Color.argb(200, red, green, blue)


        val dis = hypot(
            (mCornerX - mBezierControl1.x).toDouble(),
            (mBezierControl2.y - mCornerY).toDouble()
        ).toFloat()
        val f8 = (mCornerX - mBezierControl1.x) / dis
        val f9 = (mBezierControl2.y - mCornerY) / dis
        mMatrixArray[0] = 1 - 2f * f9 * f9
        mMatrixArray[1] = 2f * f8 * f9
        mMatrixArray[3] = mMatrixArray[1]
        mMatrixArray[4] = 1 - 2f * f8 * f8
        mMatrix.reset()
        mMatrix.setValues(mMatrixArray)
        mMatrix.preTranslate(-mBezierControl1.x, -mBezierControl1.y)
        mMatrix.postTranslate(mBezierControl1.x, mBezierControl1.y)
        canvas.drawBitmap(bitmap, mMatrix, mPaint)
        //背景叠加
        canvas.drawColor(tempColor)

        mPaint.colorFilter = null

        canvas.rotate(mDegrees, mBezierStart1.x, mBezierStart1.y)
        mFolderShadowDrawable.setBounds(
            left, mBezierStart1.y.toInt(), right,
            (mBezierStart1.y + mMaxLength!!).toInt()
        )
        mFolderShadowDrawable.draw(canvas)

        canvas.restore()
    }

    /**
     * 绘制翻起页的阴影
     *
     * @param canvas
     */
    private fun drawCurrentPageShadow(canvas: Canvas) {
        val degree: Double = if (mIsRTandLB) {
            Math.PI / 4 - atan2(mBezierControl1.y - mFloatTouchY, mFloatTouchX - mBezierControl1.x)
        } else {
            Math.PI / 4 - atan2(mFloatTouchY - mBezierControl1.y, mFloatTouchX - mBezierControl1.x)
        }
        // 翻起页阴影顶点与touch点的距离
        val d1 = 25.toFloat().toDouble() * 1.414 * cos(degree)
        val d2 = 25.toFloat().toDouble() * 1.414 * sin(degree)
        val x = (mFloatTouchX + d1).toFloat()
        val y = if (mIsRTandLB) {
            (mFloatTouchY + d2).toFloat()
        } else {
            (mFloatTouchY - d2).toFloat()
        }
        mPath1.reset()
        mPath1.moveTo(x, y)
        mPath1.lineTo(mFloatTouchX, mFloatTouchY)
        mPath1.lineTo(mBezierControl1.x, mBezierControl1.y)
        mPath1.lineTo(mBezierStart1.x, mBezierStart1.y)
        mPath1.close()
        canvas.save()
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                mXORPath.reset()
                mXORPath.moveTo(0f, 0f)
                mXORPath.lineTo(canvas.width.toFloat(), 0f)
                mXORPath.lineTo(canvas.width.toFloat(), canvas.height.toFloat())
                mXORPath.lineTo(0f, canvas.height.toFloat())
                mXORPath.close()

                // 取 path 的补给，作为 canvas 的交集
                mXORPath.op(mPath0, Path.Op.XOR)
                canvas.clipPath(mXORPath)
            } else {
                canvas.clipPath(mPath0, Region.Op.XOR)
            }
            canvas.clipPath(mPath1)
        } catch (e: Exception) {
            // TODO: handle exception
        }

        var leftX: Int
        var rightY: Int
        var mCurrentPageShadow: GradientDrawable
        if (mIsRTandLB) {
            leftX = mBezierControl1.x.toInt()
            rightY = mBezierControl1.x.toInt() + 25
            mCurrentPageShadow = mFrontShadowDrawableVLR
        } else {
            leftX = (mBezierControl1.x - 25).toInt()
            rightY = mBezierControl1.x.toInt() + 1
            mCurrentPageShadow = mFrontShadowDrawableVRL
        }

        var rotateDegrees: Float = toDegrees(
            atan2(
                mFloatTouchX - mBezierControl1.x,
                mBezierControl1.y - mFloatTouchY
            ).toDouble()
        ).toFloat()
        canvas.rotate(rotateDegrees, mBezierControl1.x, mBezierControl1.y)
        mCurrentPageShadow.setBounds(
            leftX,
            (mBezierControl1.y - mMaxLength!!).toInt(), rightY,
            mBezierControl1.y.toInt()
        )
        mCurrentPageShadow.draw(canvas)
        canvas.restore()

        mPath1.reset()
        mPath1.moveTo(x, y)
        mPath1.lineTo(mFloatTouchX, mFloatTouchY)
        mPath1.lineTo(mBezierControl2.x, mBezierControl2.y)
        mPath1.lineTo(mBezierStart2.x, mBezierStart2.y)
        mPath1.close()
        canvas.save()
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                mXORPath.reset()
                mXORPath.moveTo(0f, 0f)
                mXORPath.lineTo(canvas.width.toFloat(), 0f)
                mXORPath.lineTo(canvas.width.toFloat(), canvas.height.toFloat())
                mXORPath.lineTo(0f, canvas.height.toFloat())
                mXORPath.close()
                // 取 path 的补给，作为 canvas 的交集
                mXORPath.op(mPath0, Path.Op.XOR)
                canvas.clipPath(mXORPath)
            } else {
                canvas.clipPath(mPath0, Region.Op.XOR)
            }
            canvas.clipPath(mPath1)
        } catch (e: Exception) {
        }

        if (mIsRTandLB) {
            leftX = mBezierControl2.y.toInt()
            rightY = (mBezierControl2.y + 25).toInt()
            mCurrentPageShadow = mFrontShadowDrawableHTB
        } else {
            leftX = (mBezierControl2.y - 25).toInt()
            rightY = (mBezierControl2.y + 1).toInt()
            mCurrentPageShadow = mFrontShadowDrawableHBT
        }
        rotateDegrees =
            toDegrees(
                atan2(
                    mBezierControl2.y - mFloatTouchY,
                    mBezierControl2.x - mFloatTouchX
                ).toDouble()
            ).toFloat()
        canvas.rotate(rotateDegrees, mBezierControl2.x, mBezierControl2.y)
        val temp: Float = if (mBezierControl2.y < 0)
            mBezierControl2.y - mViewHeight
        else
            mBezierControl2.y

        val hmg = hypot(mBezierControl2.x.toDouble(), temp.toDouble()).toInt()
        if (hmg > mMaxLength!!) {
            mCurrentPageShadow
                .setBounds(
                    (mBezierControl2.x - 25).toInt() - hmg, leftX,
                    (mBezierControl2.x + mMaxLength!!).toInt() - hmg,
                    rightY
                )
        } else {
            mCurrentPageShadow.setBounds(
                (mBezierControl2.x - mMaxLength!!).toInt(), leftX,
                mBezierControl2.x.toInt(), rightY
            )
        }

        mCurrentPageShadow.draw(canvas)
        canvas.restore()
    }

    private fun drawNextPageAreaAndShadow(canvas: Canvas, bitmap: Bitmap) {
        mPath1.reset()
        mPath1.moveTo(mBezierStart1.x, mBezierStart1.y)
        mPath1.lineTo(mBezierVertex1.x, mBezierVertex1.y)
        mPath1.lineTo(mBezierVertex2.x, mBezierVertex2.y)
        mPath1.lineTo(mBezierStart2.x, mBezierStart2.y)
        mPath1.lineTo(mCornerX.toFloat(), mCornerY.toFloat())
        mPath1.close()

        mDegrees = toDegrees(
            atan2(
                (mBezierControl1.x - mCornerX).toDouble(),
                (mBezierControl2.y - mCornerY).toDouble()
            )
        ).toFloat()
        val leftX: Int
        val rightX: Int
        val mBackShadowDrawable: GradientDrawable
        if (mIsRTandLB) {  //左下及右上
            leftX = mBezierStart1.x.toInt()
            rightX = (mBezierStart1.x + mTouchToCornerDis / 4).toInt()
            mBackShadowDrawable = mBackShadowDrawableLR
        } else {
            leftX = (mBezierStart1.x - mTouchToCornerDis / 4).toInt()
            rightX = mBezierStart1.x.toInt()
            mBackShadowDrawable = mBackShadowDrawableRL
        }
        canvas.save()
        try {
            canvas.clipPath(mPath0)
            canvas.clipPath(mPath1)
        } catch (e: Exception) {
        }
        canvas.drawBitmap(bitmap, 0f, 0f, null)
        canvas.rotate(mDegrees, mBezierStart1.x, mBezierStart1.y)
        mBackShadowDrawable.setBounds(
            leftX, mBezierStart1.y.toInt(), rightX,
            (mMaxLength!! + mBezierStart1.y).toInt()
        )//左上及右下角的xy坐标值,构成一个矩形
        mBackShadowDrawable.draw(canvas)
        canvas.restore()
    }

    private fun drawCurrentPageArea(canvas: Canvas, bitmap: Bitmap, path: Path) {
        mPath0.reset()
        mPath0.moveTo(mBezierStart1.x, mBezierStart1.y)
        mPath0.quadTo(
            mBezierControl1.x, mBezierControl1.y, mBezierEnd1.x,
            mBezierEnd1.y
        )
        mPath0.lineTo(mFloatTouchX, mFloatTouchY)
        mPath0.lineTo(mBezierEnd2.x, mBezierEnd2.y)
        mPath0.quadTo(
            mBezierControl2.x, mBezierControl2.y, mBezierStart2.x,
            mBezierStart2.y
        )
        mPath0.lineTo(mCornerX.toFloat(), mCornerY.toFloat())
        mPath0.close()

        canvas.save()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            mXORPath.reset()
            mXORPath.moveTo(0f, 0f)
            mXORPath.lineTo(canvas.width.toFloat(), 0f)
            mXORPath.lineTo(canvas.width.toFloat(), canvas.height.toFloat())
            mXORPath.lineTo(0f, canvas.height.toFloat())
            mXORPath.close()
            mXORPath.op(path, Path.Op.XOR)

            canvas.clipPath(mXORPath)
        } else {
            canvas.clipPath(path, Region.Op.XOR)
        }
        canvas.drawBitmap(bitmap, 0f, 0f, null)
        canvas.restore()
    }

    /**
     * 计算拖拽点对应的拖拽脚
     *
     * @param x
     * @param y
     */
    private fun calcCornerXY(x: Int, y: Int) {
        mCornerX = if (x <= mViewWidth / 2) 0 else mViewWidth
        mCornerY = if (y <= mViewHeight / 2) 0 else mViewHeight

        mIsRTandLB =
            ((mCornerX == 0 && mCornerY == mViewHeight) || (mCornerX == mViewWidth && mCornerY == 0))
    }

    private fun calcPoints() {
        mMiddleX = (mFloatTouchX + mCornerX) / 2
        mMiddleY = (mFloatTouchY + mCornerY) / 2
        mBezierControl1.x =
            mMiddleX - (mCornerY - mMiddleY) * (mCornerY - mMiddleY) / (mCornerX - mMiddleX)
        mBezierControl1.y = mCornerY.toFloat()
        mBezierControl2.x = mCornerX.toFloat()

        val f4 = mCornerY - mMiddleY
        if (f4 == 0f) {
            mBezierControl2.y = mMiddleY - (mCornerX - mMiddleX) * (mCornerX - mMiddleX) / 0.1f

        } else {
            mBezierControl2.y =
                mMiddleY - (mCornerX - mMiddleX) * (mCornerX - mMiddleX) / (mCornerY - mMiddleY)
        }
        mBezierStart1.x = mBezierControl1.x - (mCornerX - mBezierControl1.x) / 2
        mBezierStart1.y = mCornerY.toFloat()

        // 当mBezierStart1.x < 0或者mBezierStart1.x > 480时
        // 如果继续翻页，会出现BUG故在此限制
        if (mFloatTouchX > 0 && mFloatTouchX < mViewWidth) {
            if (mBezierStart1.x < 0 || mBezierStart1.x > mViewWidth) {
                if (mBezierStart1.x < 0)
                    mBezierStart1.x = mViewWidth - mBezierStart1.x

                val f1 = abs(mCornerX - mFloatTouchX)
                val f2 = mViewWidth * f1 / mBezierStart1.x
                mFloatTouchX = abs(mCornerX - f2)

                val f3 = abs(mCornerX - mFloatTouchX) * abs(mCornerY - mFloatTouchY) / f1
                mFloatTouchY = abs(mCornerY - f3)

                mMiddleX = (mFloatTouchX + mCornerX) / 2
                mMiddleY = (mFloatTouchY + mCornerY) / 2

                mBezierControl1.x =
                    mMiddleX - (mCornerY - mMiddleY) * (mCornerY - mMiddleY) / (mCornerX - mMiddleX)
                mBezierControl1.y = mCornerY.toFloat()

                mBezierControl2.x = mCornerX.toFloat()

                val f5 = mCornerY - mMiddleY
                if (f5 == 0f) {
                    mBezierControl2.y =
                        mMiddleY - (mCornerX - mMiddleX) * (mCornerX - mMiddleX) / 0.1f
                } else {
                    mBezierControl2.y =
                        mMiddleY - (mCornerX - mMiddleX) * (mCornerX - mMiddleX) / (mCornerY - mMiddleY)
                }

                mBezierStart1.x = mBezierControl1.x - (mCornerX - mBezierControl1.x) / 2
            }
        }
        mBezierStart2.x = mCornerX.toFloat()
        mBezierStart2.y = mBezierControl2.y - (mCornerY - mBezierControl2.y) / 2

        mTouchToCornerDis = hypot(
            mFloatTouchX - mCornerX,
            mFloatTouchY - mCornerY
        )

        mBezierEnd1 = getCross(
            PointF(mFloatTouchX, mFloatTouchY), mBezierControl1, mBezierStart1,
            mBezierStart2
        )

        mBezierEnd2 = getCross(
            PointF(mFloatTouchX, mFloatTouchY), mBezierControl2, mBezierStart1,
            mBezierStart2
        )

        mBezierVertex1.x = (mBezierStart1.x + 2 * mBezierControl1.x + mBezierEnd1.x) / 4
        mBezierVertex1.y = (2 * mBezierControl1.y + mBezierStart1.y + mBezierEnd1.y) / 4
        mBezierVertex2.x = (mBezierStart2.x + 2 * mBezierControl2.x + mBezierEnd2.x) / 4
        mBezierVertex2.y = (2 * mBezierControl2.y + mBezierStart2.y + mBezierEnd2.y) / 4
    }

    /**
     * 求解直线P1P2和直线P3P4的交点坐标
     *
     * @param P1
     * @param P2
     * @param P3
     * @param P4
     * @return
     */
    private fun getCross(P1: PointF, P2: PointF, P3: PointF, P4: PointF): PointF {
        val crossP = PointF()
        // 二元函数通式： y=ax+b
        val a1 = (P2.y - P1.y) / (P2.x - P1.x)
        val b1 = (P1.x * P2.y - P2.x * P1.y) / (P1.x - P2.x)

        val a2 = (P4.y - P3.y) / (P4.x - P3.x)
        val b2 = (P3.x * P4.y - P4.x * P3.y) / (P3.x - P4.x)
        crossP.x = (b2 - b1) / (a1 - a2)
        crossP.y = a1 * crossP.x + b1
        return crossP
    }
}