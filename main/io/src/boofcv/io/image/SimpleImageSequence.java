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

package boofcv.io.image;

import boofcv.struct.image.ImageBase;

import java.awt.image.BufferedImage;

/**
 * Simplified interface for reading in a sequence of images.  This interface hides the complexities of reading
 * from different file formats and from live video streams.
 *
 * @author Peter Abeles
 */
public interface SimpleImageSequence<T extends ImageBase> {

	/**
	 * If a new image is available.
	 *
	 * @return true if a new image is available.
	 */
	public boolean hasNext();

	/**
	 * Returns the next image available in the sequence.
	 *
	 * @return Next image in the sequence.
	 */
	public T next();

	/**
	 * Returns a BufferedImage that can be used for display purposes of the current image.
	 *
	 * @return
	 */
	public BufferedImage getGuiImage();

	/**
	 * Call when done reading the image sequence.
	 */
	public void close();

	/**
	 * Returns the number of the current frame in the sequence.
	 *
	 * @return Frame ID number.
	 */
	public int getFrameNumber();

	/**
	 * Sets if the video should loop or not
	 *
	 * @param loop true for looping forever, false for once
	 */
	public void setLoop( boolean loop );

	/**
	 * Returns the type of class used to store the output image
	 */
	public Class<T> getImageType();

	/**
	 * Start reading the sequence from the start
	 */
	public void reset();
}
