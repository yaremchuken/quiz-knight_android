package yaremchuken.quizknight.draw

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Point
import android.util.AttributeSet
import android.view.SurfaceHolder
import android.view.SurfaceView
import yaremchuken.quizknight.GameStateMachine
import yaremchuken.quizknight.GameStats
import yaremchuken.quizknight.PersonageType
import yaremchuken.quizknight.R
import yaremchuken.quizknight.StateMachineType

class DrawView(context: Context, attributes: AttributeSet):
    SurfaceView(context, attributes), SurfaceHolder.Callback
{
    companion object {
        const val WORLD_SPEED = 12F
        const val FAR_WORLD_SPEED = WORLD_SPEED * .5F
    }

    private lateinit var thread: DrawThread

    private var frameStart = 0L
    var drawMs = 0L

    private lateinit var hero: Personage
    private var opponent: Personage? = null

    private val skyBG: Bitmap
    private val farBG: Bitmap
    private val road: Bitmap

    private var roadOffset: Float = 0F
    private var roadOffset2: Float = 0F

    private var farOffset: Float = 0F
    private var farOffset2: Float = 0F

    init {
        holder.addCallback(this)

        skyBG = BitmapFactory.decodeResource(context.resources, R.drawable.gw_bg_sky)
        farBG = BitmapFactory.decodeResource(context.resources, R.drawable.gw_far_mountains)
        road = BitmapFactory.decodeResource(context.resources, R.drawable.gw_road)

        farOffset2 = farBG.width.toFloat()
        roadOffset2 = road.width.toFloat()

        GameStateMachine.drawer = this
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        thread = DrawThread(holder, this)
        thread.running = true
        thread.start()

        GameStateMachine.canvasReady = true
        GameStateMachine.startMachine()
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {}

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        thread.running = false
        thread.join()
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

        canvas.drawBitmap(skyBG, 0F, 0F, null)

        updatePositions()

        canvas.drawBitmap(farBG, farOffset, (height - farBG.height).toFloat(), null)
        canvas.drawBitmap(farBG, farOffset2, (height - farBG.height).toFloat(), null)

        if (GameStateMachine.state == StateMachineType.EMPTY) return

        opponent?.draw(canvas, height)
        hero.draw(canvas, height)

        canvas.drawBitmap(road, roadOffset, (height - 100).toFloat(), null)
        canvas.drawBitmap(road, roadOffset2, (height - 100).toFloat(), null)

        drawMs = System.currentTimeMillis() - frameStart;
    }

    private fun updatePositions() {
        if (GameStateMachine.state == StateMachineType.MOVING) {
            roadOffset -= WORLD_SPEED
            roadOffset2 -= WORLD_SPEED

            if (roadOffset <= -road.width - 20) {
                roadOffset = roadOffset2 + road.width
            }
            if (roadOffset2 <= -road.width - 20) {
                roadOffset2 = roadOffset + road.width
            }

            farOffset -= FAR_WORLD_SPEED
            farOffset2 -= FAR_WORLD_SPEED

            if (farOffset <= -farBG.width - 20) {
                farOffset = farOffset2 + farBG.width
            }
            if (farOffset2 <= -farBG.width - 20) {
                farOffset2 = farOffset + farBG.width
            }

            opponent?.updatePos()
        }
    }
}