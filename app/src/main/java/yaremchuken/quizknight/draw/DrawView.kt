package yaremchuken.quizknight.draw

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Point
import android.graphics.Rect
import android.util.AttributeSet
import android.view.SurfaceHolder
import android.view.SurfaceView
import yaremchuken.quizknight.GameStateMachine
import yaremchuken.quizknight.GameStats
import yaremchuken.quizknight.PersonageType
import yaremchuken.quizknight.R
import yaremchuken.quizknight.StateMachineType
import yaremchuken.quizknight.draw.GameWorldLayerType.FARESTGROUND
import yaremchuken.quizknight.draw.GameWorldLayerType.FARGROUND
import yaremchuken.quizknight.draw.GameWorldLayerType.MIDDLEGROUND
import yaremchuken.quizknight.draw.GameWorldLayerType.ROAD
import yaremchuken.quizknight.draw.GameWorldLayerType.SKYBOX
import java.util.EnumMap

class DrawView(context: Context, attributes: AttributeSet):
    SurfaceView(context, attributes), SurfaceHolder.Callback
{
    companion object {
        const val WORLD_SPEED = 7
    }

    private lateinit var thread: DrawThread

    private var frameStart = 0L
    var drawMs = 0L

    private lateinit var hero: Personage
    private var opponent: Personage? = null

    /**
     * Map stores bitmaps for layers of game world
     */
    private val bitmaps: MutableMap<GameWorldLayerType, Bitmap> = EnumMap(GameWorldLayerType::class.java)

    /**
     * Every layer has two frames, one follows another to make the world move
     * When frame leaves scene, it jumps at the end of the next frame
     */
    private val frames: MutableMap<GameWorldLayerType, Pair<Rect, Rect>> = EnumMap(GameWorldLayerType::class.java)

    private val speeds = mapOf(
        Pair(SKYBOX, WORLD_SPEED - 6),
        Pair(FARESTGROUND, WORLD_SPEED - 5),
        Pair(FARGROUND, WORLD_SPEED - 4),
        Pair(MIDDLEGROUND, WORLD_SPEED - 2),
        Pair(ROAD, WORLD_SPEED)
    )

    init {
        GameStateMachine.drawer = this
        holder.addCallback(this)

        bitmaps[SKYBOX] = BitmapFactory.decodeResource(context.resources, R.drawable.gw_skybox)
        bitmaps[FARESTGROUND] = BitmapFactory.decodeResource(context.resources, R.drawable.gw_farestground)
        bitmaps[FARGROUND] = BitmapFactory.decodeResource(context.resources, R.drawable.gw_farground)
        bitmaps[MIDDLEGROUND] = BitmapFactory.decodeResource(context.resources, R.drawable.gw_middleground)
        bitmaps[ROAD] = BitmapFactory.decodeResource(context.resources, R.drawable.gw_road)
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        thread = DrawThread(holder, this)
        thread.running = true
        thread.start()

        bitmaps.forEach { (type, bitmap) ->
            val ratio = bitmap.height.toFloat() / height.toFloat()
            val rectA = Rect(0, 0, (bitmap.width / ratio).toInt(), height)
            val rectB = Rect(rectA.width(), 0,  rectA.width() + (bitmap.width / ratio).toInt(), height)
            frames[type] = Pair(rectA, rectB)
        }

        GameStateMachine.canvasReady = true
        GameStateMachine.startMachine()
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {}

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        stopDraw()
        thread.join()
    }

    fun stopDraw() {
        thread.running = false
    }

    fun propagateStateChanged() {
        when(GameStateMachine.state) {
            StateMachineType.PREPARE_ASSETS -> {
                hero = Personage(PersonageType.HERO, Point(width, height))
                opponent = Personage(GameStats.opponent, Point(width, height))

                hero.switchAction(ActionType.WALK)
                GameStateMachine.switchState(StateMachineType.MOVING)
            }
            StateMachineType.QUIZ -> {
                hero.switchAction(ActionType.IDLE)
            }
            StateMachineType.CONTINUE_MOVING -> {
                opponent?.changePersonage(GameStats.opponent)
                hero.switchAction(ActionType.WALK)
                GameStateMachine.switchState(StateMachineType.MOVING)
            }
            else -> {}
        }
    }

    override fun draw(canvas: Canvas) {
        frameStart = System.currentTimeMillis();

        super.draw(canvas)

        updatePositions()

        bitmaps.forEach { (type, bitmap) ->
            canvas.drawBitmap(bitmap, null, frames[type]!!.first, null)
            canvas.drawBitmap(bitmap, null, frames[type]!!.second, null)
        }

        if (GameStateMachine.state == StateMachineType.EMPTY) return

        opponent?.draw(canvas, height)
        hero.draw(canvas, height)

        drawMs = System.currentTimeMillis() - frameStart;
    }

    private fun updatePositions() {
        if (GameStateMachine.state == StateMachineType.MOVING) {
            bitmaps.forEach { (type, _) ->
                val frameA = frames[type]!!.first
                val frameB = frames[type]!!.second

                // First, move frames to the left
                frameA.left -= speeds[type]!!
                frameA.right -= speeds[type]!!

                frameB.left -= speeds[type]!!
                frameB.right -= speeds[type]!!

                // Secondary - check for scene bounds
                if (frameA.left <= -frameA.width() - 20) {
                    frameA.left = frameB.left + frameB.width()
                    frameA.right = frameA.left + frameB.width()
                }

                if (frameB.left <= -frameB.width() - 20) {
                    frameB.left = frameA.left + frameA.width()
                    frameB.right = frameB.left + frameA.width()
                }
            }

            opponent?.updatePos()
        }
    }

    /**
     * Scene height changes on soft keyboard appearance, so we need to rise frames for personages to stay on the ground
     */
    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        if (oldh == 0) return

        val diff = h - oldh
        frames.forEach {
            listOf(it.value.first, it.value.second).forEach { frame ->
                frame.top += diff
                frame.bottom += diff
            }
        }
    }
}