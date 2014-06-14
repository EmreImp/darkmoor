package com.ntk.darkmoor.resource;

import java.io.IOException;

import org.ntk.commons.StringUtils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.XmlReader.Element;
import com.badlogic.gdx.utils.XmlWriter;
import com.ntk.darkmoor.exception.LoadException;
import com.ntk.darkmoor.exception.ResourceException;

public class TextureSet implements Disposable {

	public static final String TAG = "tileset";
	private String name;
	private String textureFile;
	private Texture texture;
	private Sprite[] spriteCache;
	private Array<TextureMetadata> metadata;

	public TextureSet(int capacity) {
		metadata = new Array<TextureMetadata>(capacity);
		spriteCache = new Sprite[capacity];
	}

	private void loadTexture() {
		texture = null;
		// the file handle can be retrieved in a game, in JUnit it doesn't work
		if (Gdx.files == null)
			return;

		FileHandle fh = Gdx.files.internal("data/" + textureFile.toLowerCase());
		if (fh != null) {
			texture = new Texture(fh);
			// atlas = new TextureAtlas(fh);

			// for (TextureMetadata textureInfo : metadata) {
			// atlas.addRegion(String.valueOf(textureInfo.getId()), texture, (int) textureInfo.getRectangle().x,
			// (int) textureInfo.getRectangle().y, (int) textureInfo.getRectangle().width,
			// (int) textureInfo.getRectangle().height);
			// }
		}
	}

	public boolean load(Element xml) {
		if (xml == null || !TAG.equalsIgnoreCase(xml.getName()))
			return false;

		this.name = xml.getAttribute("name");
		String name = null;

		for (int i = 0; i < xml.getChildCount(); i++) {
			Element child = xml.getChild(i);
			name = child.getName();

			if (TextureMetadata.TAG.equalsIgnoreCase(name)) {

				TextureMetadata textureMetadata = new TextureMetadata();
				if (textureMetadata.load(child)) {
					metadata.add(textureMetadata);
				} else {
					throw new LoadException("Could not load texture metadata node: " + child.toString());
				}

			} else if ("texture".equalsIgnoreCase(name)) {
				this.textureFile = child.getAttribute("file");
			}
		}

		if (!StringUtils.isEmpty(textureFile)) {
			loadTexture();
		}

		return true;
	}

	public boolean save(XmlWriter writer) throws IOException {
		if (writer == null)
			return false;

		writer.element(TAG).attribute("name", name);
		writer.element("texture").attribute("file", textureFile).pop();

		for (TextureMetadata texture : metadata) {
			texture.save(writer);
		}

		writer.pop();
		return true;
	}

	public Sprite getSprite(int spriteId) {

		if (spriteId >= 0 && spriteId < spriteCache.length && spriteCache[spriteId] != null)
			return spriteCache[spriteId];

		if (texture == null) {
			loadTexture();
		}
		try {
			TextureMetadata textureInfo = metadata.get(spriteId);

			spriteCache[spriteId] = new Sprite(texture, (int) textureInfo.getRectangle().x,
					(int) textureInfo.getRectangle().y, (int) textureInfo.getRectangle().width,
					(int) textureInfo.getRectangle().height);

			return spriteCache[spriteId];

		} catch (Exception e) {
			throw new ResourceException(e);
		}
	}

	@Override
	public void dispose() {
		for (int i = 0; i < spriteCache.length; i++) {
			if (spriteCache[i] != null)
				spriteCache[i] = null;
		}
		if (texture != null)
			texture.dispose();
		metadata.clear();
		metadata = null;
	}

	public static String getTag() {
		return TAG;
	}

	public String getName() {
		return name;
	}

	public String getTextureAtlasFile() {
		return textureFile;
	}

	public Array<TextureMetadata> getMetadata() {
		return metadata;
	}

}