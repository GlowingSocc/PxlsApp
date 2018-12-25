package space.pxls.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;

import space.pxls.Pxls;

public class TitledTableHelper extends Table {
    public TitledTableHelper(String title) {
        setBackground(new NinePatchDrawable(Pxls.skin.getPatch("white-patch"))); //white-patch is a simple bordered 9patch with white background
        add(new Stack(new SolidContainer(new Color(0, 0, 0, 0.1f)), new PxlsLabel(title).setFontScaleChain(0.6f))).colspan(2).fillX().center().row(); //actual title
        add(new SolidContainer()).height(6).colspan(2).padBottom(3).fillX().row(); //bottom "border"
    }
}
