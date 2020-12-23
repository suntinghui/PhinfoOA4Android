package com.heqifuhou.textdrawable;

import android.graphics.Typeface;

public interface IConfigBuilder {  
    public IConfigBuilder width(int width);  

    public IConfigBuilder height(int height);  

    public IConfigBuilder textColor(int color);  

    public IConfigBuilder withBorder(int thickness);  

    public IConfigBuilder useFont(Typeface font);  

    public IConfigBuilder fontSize(int size);  

    public IConfigBuilder bold();  

    public IConfigBuilder toUpperCase();  

    public IShapeBuilder endConfig();  
}  