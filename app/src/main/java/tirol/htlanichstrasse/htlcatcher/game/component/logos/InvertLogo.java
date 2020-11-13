package tirol.htlanichstrasse.htlcatcher.game.component.logos;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import tirol.htlanichstrasse.htlcatcher.R;
import tirol.htlanichstrasse.htlcatcher.game.component.Logo;
import tirol.htlanichstrasse.htlcatcher.game.logos.LogoModeManager;
import tirol.htlanichstrasse.htlcatcher.game.stats.GameStatistics;

/**
 * A subtype of the normal logo which inverts the gravity for a short duration.
 *
 * @author Nicolaus Rossi
 * @since 08.02.2020
 */
public class InvertLogo extends Logo {
    /**
     * Creates a new invert-logo on the GameView canvas
     *
     * @param x the x coordinate of the logo
     * @param y the y coordinate of the logo
     */
    public InvertLogo(final int x, final int y, final int radius) {
        super(x, y, radius);
    }

    @Override
    public void onCollided() {
        this.setAlive(false);
        GameStatistics.getInstance().increase(GameStatistics.StatisticsAction.LOGO);
        LogoModeManager.getInstance().enableMode(LogoModeManager.Mode.INVERT);
    }

    @Override
    public Bitmap getLogoBitmap(Context context) {
        final Bitmap decodedResource = BitmapFactory
                .decodeResource(context.getResources(), R.mipmap.invertlogo);
        return Bitmap.createScaledBitmap(decodedResource, this.getRadius() * 2,
                this.getRadius() * 2, false);
    }
}