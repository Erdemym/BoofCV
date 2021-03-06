/*
 * Copyright (c) 2011-2012, Peter Abeles. All Rights Reserved.
 *
 * This file is part of BoofCV (http://boofcv.org).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package boofcv.struct.image;

/**
 * <p>
 * An image where the primitive type is a signed 16-bit short.  By default all operations treat elements
 * in this image as an unsigned bytes.
 * </p>
 *
 * @author Peter Abeles
 */
public class ImageSInt16 extends ImageInt16<ImageSInt16> {
	/**
	 * Creates a new gray scale (single band/color) image.
	 *
	 * @param width  number of columns in the image.
	 * @param height number of rows in the image.
	 */
	public ImageSInt16(int width, int height) {
		super(width, height);
	}

	/**
	 * Creates an image with no data declared and the width/height set to zero.
	 */
	public ImageSInt16() {
	}

	@Override
	public int unsafe_get(int x, int y) {
		return data[getIndex(x, y)];
	}

	@Override
	public ImageTypeInfo<ImageSInt16> getTypeInfo() {
		return ImageTypeInfo.S16;
	}

	@Override
	public ImageSInt16 _createNew(int imgWidth, int imgHeight) {
		if (imgWidth == -1 || imgHeight == -1)
			return new ImageSInt16();
		return new ImageSInt16(imgWidth, imgHeight);
	}
}
