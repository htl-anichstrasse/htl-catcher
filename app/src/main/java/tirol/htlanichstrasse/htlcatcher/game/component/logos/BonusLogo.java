package tirol.htlanichstrasse.htlcatcher.game.component.logos;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import tirol.htlanichstrasse.htlcatcher.R;
import tirol.htlanichstrasse.htlcatcher.game.component.Logo;
import tirol.htlanichstrasse.htlcatcher.game.stats.GameStatistics;

/**
 * Grants bonus points
 *
 * @author Nicolaus Rossi
 * @since 08.02.2020
 */
public class BonusLogo extends Logo {
    /**
     * Creates a new bonus-logo on the GameView canvas
     *
     * @param x the x coordinate of the logo
     * @param y the y coordinate of the logo
     */
    public BonusLogo(int x, int y, int radius) {
        super(x, y, radius);
    }

    @Override
    public void onCollided() {
        this.setAlive(false);
        GameStatistics.getInstance().increase(GameStatistics.StatisticsAction.BONUSLOGO);
    }

    @Override
    public Bitmap getLogoBitmap(Context context) {
        final Bitmap decodedResource = BitmapFactory
                .decodeResource(context.getResources(), R.mipmap.bonus_logo);
        return Bitmap.createScaledBitmap(decodedResource, this.getRadius() * 2,
                this.getRadius() * 2, false);
    }
}
