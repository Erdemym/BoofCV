/*
 * Copyright (c) 2011, Peter Abeles. All Rights Reserved.
 *
 * This file is part of BoofCV (http://www.boofcv.org).
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

package boofcv.alg.distort.impl;

import boofcv.alg.distort.ImageDistort;
import boofcv.alg.interpolate.InterpolatePixel;
import boofcv.struct.distort.PixelTransform;
import boofcv.struct.image.ImageInt8;


/**
 * <p>Implementation of {@link boofcv.alg.distort.ImageDistort}.</p>
 *
 * <p>
 * DO NOT MODIFY: Generated by {@link boofcv.alg.distort.impl.GeneratorImplImageDistort}.
 * </p>
 *
 * @author Peter Abeles
 */
@SuppressWarnings({"UnnecessaryLocalVariable"})
public class ImplImageDistort_I8<T extends ImageInt8> implements ImageDistort<T> {

	// transform from dst to src image
	private PixelTransform dstToSrc;
	// sub pixel interpolation
	private InterpolatePixel<T> interp;

	public ImplImageDistort_I8(PixelTransform dstToSrc, InterpolatePixel<T> interp) {
		this.dstToSrc = dstToSrc;
		this.interp = interp;
	}

	@Override
	public void setModel(PixelTransform dstToSrc) {
		this.dstToSrc = dstToSrc;
	}

	@Override
	public void apply( T srcImg , T dstImg ) {
		interp.setImage(srcImg);

		final float widthF = srcImg.getWidth();
		final float heightF = srcImg.getHeight();

		for( int y = 0; y < dstImg.height; y++ ) {
			int indexDst = dstImg.startIndex + dstImg.stride*y;
			for( int x = 0; x < dstImg.width; x++ , indexDst++ ) {
				dstToSrc.compute(x,y);

				final float sx = dstToSrc.distX;
				final float sy = dstToSrc.distY;

				if( sx < 0f || sx >= widthF || sy < 0f || sy >= heightF ) {
					continue;
				}

				dstImg.data[indexDst] = (byte)interp.get(sx,sy);
			}
		}
	}

	@Override
	public void apply( T srcImg , T dstImg , Number value ) {
		interp.setImage(srcImg);

		int valueF = value.intValue();

		final float widthF = srcImg.getWidth();
		final float heightF = srcImg.getHeight();

		for( int y = 0; y < dstImg.height; y++ ) {
			int indexDst = dstImg.startIndex + dstImg.stride*y;
			for( int x = 0; x < dstImg.width; x++ , indexDst++ ) {
				dstToSrc.compute(x,y);

				final float sx = dstToSrc.distX;
				final float sy = dstToSrc.distY;

				if( sx < 0f || sx >= widthF || sy < 0f || sy >= heightF ) {
					dstImg.data[indexDst] = (byte)valueF;
					continue;
				}

				dstImg.data[indexDst] = (byte)interp.get(sx,sy);
			}
		}
	}

}
