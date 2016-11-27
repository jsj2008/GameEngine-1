package com.dferreira.game_controller;

import android.view.MotionEvent;
import android.view.View;

/**
 * Handles the touch in the fragment
 */
public class GameEngineTouchListener implements View.OnTouchListener {

    private static boolean pressed = false;
    private static float glX = 0.0f;
    private static float glY = 0.0f;

    /**
     * The width of the draw window
     */
    private float width;

    /**
     * The height of the window
     */
    private float height;

    /**
     * @return Indicates if the the interface is pressed
     */
    public static boolean getIsPressed() {
        return pressed;
    }

    /**
     * @return The position that is was pressed in the x-coordinate
     */
    public static float getGlX() {
        return glX;
    }

    /**
     * @return The position that is was pressed in the y-coordinate
     */
    public static float getGlY() {
        return glY;
    }

    /**
     * @param xScreenPosition The position in the coordinates of the screen
     * @return the value in gl format between -1.0 and 1
     */
    private float getGLXPosition(float xScreenPosition) {
        return ((xScreenPosition / width) * 2.0f) - 1.0f;
    }

    /**
     * @param yScreenPosition The position in the coordinates of the screen
     * @return the value in gl format between -1.0 and 1
     */
    private float getGLYPosition(float yScreenPosition) {
        return 1.0f - ((yScreenPosition / height) * 2.0f);
    }

    /**
     * Called when a touch event is dispatched to a view. This allows listeners to
     * get a chance to respond before the target view.
     *
     * @param v           The view the touch event has been dispatched to.
     * @param motionEvent The MotionEvent object containing full information about
     *                    the event.
     * @return True if the listener has consumed the event, false otherwise.
     */
    @Override
    public boolean onTouch(View v, MotionEvent motionEvent) {
        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
                float positionX = motionEvent.getX();
                float positionY = motionEvent.getY();
                glX = getGLXPosition(positionX);
                glY = getGLYPosition(positionY);
                pressed = true;
                break;
            case MotionEvent.ACTION_UP:
                pressed = false;
                break;
        }
        return true;
    }

    /**
     * Set the width of the view that is going to be used to find out which element of the interface
     * Was pressed
     *
     * @param width The width of the view
     */
    public void setWidth(int width) {
        this.width = width;
    }

    /**
     * Set the height of the view that is going to be used to find out which element of the interface
     * Was pressed
     *
     * @param height The height of the view
     */
    public void setHeight(int height) {
        this.height = height;
    }
}
