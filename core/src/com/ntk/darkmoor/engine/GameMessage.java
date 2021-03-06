package com.ntk.darkmoor.engine;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.ntk.darkmoor.config.LanguagesManager;
import com.ntk.darkmoor.config.Log;
import com.ntk.darkmoor.resource.Resources;
import com.ntk.darkmoor.stub.GameTime;

public class GameMessage {

	private static ArrayList<ScreenMessage> messages;
	private static LanguagesManager language;
	private static BitmapFont font;

	static {
		messages = new ArrayList<ScreenMessage>();
	}

	@SuppressWarnings("unchecked")
	public static boolean init() {

		// Language
		language = LanguagesManager.getInstance();// "game");
		if (language == null) {
			Log.error("ERROR !!! No StringTable defined for the game !!!");
			return false;
		}
		// language.setLanguageName(Settings.getLastLoadedInstance().getLanguage());

		font = Resources.createSharedFontAsset("inventory");

		return true;
	}

	public static void dispose() {
		Resources.unlockSharedFontAsset(font);
		font = null;
	}

	public static void clear() {
		messages.clear();
	}

	public static void update(GameTime time) {
		// TODO: ntk: careful with the removals here! Check for concurrent modification

		// Remove older screen messages and display them
		while (messages.size() > 3)
			messages.remove(0);

		for (ScreenMessage msg : messages)
			msg.setLife(msg.getLife() - time.getElapsedGameTime());

		// Remove old messages
		int i = 0;
		while (messages.size() > 0 && i < messages.size()) {
			if (messages.get(i).getLife() <= 0)
				messages.remove(i);
			else
				i++; // increment i only in case nothing was removed, otherwise stay there
		}
	}

	public static void draw(SpriteBatch batch) {

		// Display the last 3 messages
		int i = 0;
		for (ScreenMessage msg : messages) {
			font.setColor(msg.getColor());
			font.draw(batch, msg.getMessage(), 8, 360 + i * 12);
			i++;
		}
	}

	public static void buildMessage(int id, Object... args) {
		addMessage(language.buildMessage("game", id, args));
	}

	public static void addMessage(String msg) {
		addMessage(msg, GameColors.White);
	}

	public static void addMessage(String msg, Color color) {
		// Split all new lines
		String[] lines = msg.split("\n");

		// TODO: ntk: check below the substring result

		// Lines too long
		int maxlen = 47;
		for (String line : lines) {
			for (int i = 0; i < line.length(); i += maxlen)
				messages.add(new ScreenMessage(line.substring(i, i + Math.min(Math.abs(line.length() - i), line.length())), color));
		}
	}

	public static ArrayList<ScreenMessage> getMessages() {
		return messages;
	}

	public static LanguagesManager getLanguage() {
		return language;
	}

	public static BitmapFont getFont() {
		return font;
	}

}
