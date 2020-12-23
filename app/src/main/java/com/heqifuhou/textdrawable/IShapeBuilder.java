package com.heqifuhou.textdrawable;


public interface IShapeBuilder {

    public IConfigBuilder beginConfig();  

    public IBuilder rect();  

    public IBuilder round();  

    public IBuilder roundRect(int radius);  

    public TextDrawable buildRect(String text, int color);  

    public TextDrawable buildRoundRect(String text, int color, int radius);  

    public TextDrawable buildRound(String text, int color);  
}
