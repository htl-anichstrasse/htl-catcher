package tirol.htlanichstrasse.htlcatcher.game;

/**
 * Enumerating possible game states
 *
 * @author Joshua Winkler
 * @since 05.11.2019
 */
public enum GameState {
   START, INGAME, INGAME2, INGAME3, END;
   // there are three stages, the first is the easiest, the third is the hardest

   /**
    * Checks if the provided game state is an ingame game state
    *
    * @param gameState the game state to be checked
    * @return true if the state is an ingame state, false otherwise
    */
   public static boolean isInGame(final GameState gameState) {
      return gameState == INGAME || gameState == INGAME2 || gameState == INGAME3;
   }
}
