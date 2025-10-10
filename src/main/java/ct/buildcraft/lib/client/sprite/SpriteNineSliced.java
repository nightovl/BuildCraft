/*
 * Copyright (c) 2017 SpaceToad and the BuildCraft team
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0. If a copy of the MPL was not
 * distributed with this file, You can obtain one at https://mozilla.org/MPL/2.0/
 */

package ct.buildcraft.lib.client.sprite;

import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;

import ct.buildcraft.api.core.render.ISprite;
import ct.buildcraft.lib.gui.pos.IGuiArea;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/** Defines and draws a 9-sliced sprite. */
@OnlyIn(Dist.CLIENT)
public class SpriteNineSliced {
    public final ISprite sprite;
    public final double xMin, yMin, xMax, yMax;
    public final double xScale, yScale;
    
    private double transX, transY;

    public SpriteNineSliced(ISprite sprite, int xMin, int yMin, int xMax, int yMax, int textureSize) {
        this(sprite, xMin, yMin, xMax, yMax, textureSize, textureSize);
    }

    public SpriteNineSliced(ISprite sprite, int xMin, int yMin, int xMax, int yMax, int xScale, int yScale) {
        this.sprite = sprite;
        this.xMin = xMin / (double) xScale;
        this.yMin = yMin / (double) yScale;
        this.xMax = xMax / (double) xScale;
        this.yMax = yMax / (double) yScale;
        this.xScale = xScale;
        this.yScale = yScale;
    }

    public SpriteNineSliced(ISprite sprite, double xMin, double yMin, double xMax, double yMax, double scale) {
        this(sprite, xMin, yMin, xMax, yMax, scale, scale);
    }

    public SpriteNineSliced(ISprite sprite, double xMin, double yMin, double xMax, double yMax, double xScale,
        double yScale) {
        this.sprite = sprite;
        this.xMin = xMin;
        this.yMin = yMin;
        this.xMax = xMax;
        this.yMax = yMax;
        this.xScale = xScale;
        this.yScale = yScale;
    }

    public void draw(IGuiArea element) {
        draw(element.getX(), element.getY(), element.getWidth(), element.getHeight());
    }

    public void draw(double x, double y, double width, double height) {
        sprite.bindTexture();
        Tesselator tess = Tesselator.getInstance();
        BufferBuilder vb = tess.getBuilder();
        vb.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        transX = x;
        transY = y;

        double sx = xScale;
        double sy = yScale;
        double[] xa = { 0, xMin * sx, width + (xMax - 1) * sx, width };
        double[] ya = { 0, yMin * sy, height + (yMax - 1) * sy, height };

        double[] ua = { 0, xMin, xMax, 1 };
        double[] va = { 0, yMin, yMax, 1 };

        quad(vb, xa, ya, ua, va, 0, 0);
        quad(vb, xa, ya, ua, va, 0, 1);
        quad(vb, xa, ya, ua, va, 0, 2);

        quad(vb, xa, ya, ua, va, 1, 0);
        quad(vb, xa, ya, ua, va, 1, 1);
        quad(vb, xa, ya, ua, va, 1, 2);

        quad(vb, xa, ya, ua, va, 2, 0);
        quad(vb, xa, ya, ua, va, 2, 1);
        quad(vb, xa, ya, ua, va, 2, 2);

        tess.end();
        transX = 0;
        transY = 0;
    }

    private void quad(BufferBuilder vb, double[] x, double[] y, double[] u, double[] v, int xIndex, int yIndex) {
        int xis = xIndex;
        int xIB = xIndex + 1;

        int yis = yIndex;
        int yIB = yIndex + 1;

        vertex(vb, x[xis], y[yis], u[xis], v[yis]);
        vertex(vb, x[xis], y[yIB], u[xis], v[yIB]);
        vertex(vb, x[xIB], y[yIB], u[xIB], v[yIB]);
        vertex(vb, x[xIB], y[yis], u[xIB], v[yis]);
    }

    private void vertex(BufferBuilder vb, double x, double y, double texU, double texV) {
        vb.vertex(x+transX, y+transY, 0);
        vb.uv(sprite.getInterpU(texU), sprite.getInterpV(texV));
        vb.endVertex();
    }
}
